package com.edafa.web2sms.sms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.utils.IdGenerator;
import com.edafa.utils.loadbalancer.entity.Entity;
import com.edafa.utils.loadbalancer.exception.NoSuchEntityException;
import com.edafa.utils.loadbalancer.exception.NoSuchGroupException;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.encyrptionutil.Interfaces.EncyrptionUtilInterface;
import com.edafa.web2sms.sms.caching.CacheLimitException;
import com.edafa.web2sms.sms.caching.CachingManagementBeanLocal;
import com.edafa.web2sms.sms.caching.SMSapiUtils;
import com.edafa.web2sms.sms.enums.ResultStatus;
import com.edafa.web2sms.sms.enums.SMSResponseStatus;
import com.edafa.web2sms.sms.file.WritingCountMaxReachedException;
import com.edafa.web2sms.sms.model.EnquireCampaignByIdDetailedRequest;
import com.edafa.web2sms.sms.model.EnquireCampaignByIdRequest;
import com.edafa.web2sms.sms.model.EnquireCampaignByIdResponse;
import com.edafa.web2sms.sms.model.EnquireSMSByIdListDetailedRequest;
import com.edafa.web2sms.sms.model.EnquireSMSByIdListRequest;
import com.edafa.web2sms.sms.model.EnquireSMSByIdRequest;
import com.edafa.web2sms.sms.model.EnquireSMSByIdResponse;
import com.edafa.web2sms.sms.model.EnquireSMSsByDatesAndMSISDNResponse;
import com.edafa.web2sms.sms.model.EnquireSMSsByDatesDetailedRequest;
import com.edafa.web2sms.sms.model.EnquireSMSsByDatesRequest;
import com.edafa.web2sms.sms.model.EnquireSMSsByDatesResponse;
import com.edafa.web2sms.sms.model.SMSDetails;
import com.edafa.web2sms.sms.model.SMSResponseBySmsId;
import com.edafa.web2sms.sms.model.SubmitCampaignRequest;
import com.edafa.web2sms.sms.model.SubmitCampaignResponse;
import com.edafa.web2sms.sms.model.SubmitDetailedCampaignRequest;
import com.edafa.web2sms.sms.model.SubmitDetailedSMSRequest;
import com.edafa.web2sms.sms.model.SubmitSMSRequest;
import com.edafa.web2sms.sms.model.SubmitSMSResponse;
import com.edafa.web2sms.sms.model.SubmitSMSResponseBySmsId;
import com.edafa.web2sms.sms.utils.lb.LoadBalancerLocal;
import com.edafa.web2sms.utils.TrxId;
import com.edafa.web2sms.utils.XmlHttpClient;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.sun.jersey.api.client.ClientResponse;

@Stateless
@LocalBean
@Path("/submit")
@DependsOn("LoggingManagerBean")
public class SMSAPI {

	Logger appLogger;
	Logger smsLogger;

	@EJB
	LoadBalancerLocal loadBalancer;

	@EJB
	XmlHttpClient httpClient;

	@EJB
	AppErrorManagerAdapter appErrorManagerAdapter;

	@EJB
	CachingManagementBeanLocal cachingManagementBean;
	
	@EJB
	EncyrptionUtilInterface encyrptionUtil;
	
	private String groupId;
	private boolean plainData;
	@PostConstruct
	public void init() {
		appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
		smsLogger = LogManager.getLogger(LoggersEnum.SMS_API.name());
		groupId = (String) Configs.SMS_API_SERVICE_LB_GROUP_ID.getValue();
		plainData = (boolean) Configs.ENCYRPTION_FLAG.getValue();

	}

	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	public SubmitSMSResponse submitSMSRquest(@Context HttpServletRequest contextRequest, SubmitSMSRequest request) {
		String ipAddress = contextRequest.getHeader("X-FORWARDED-FOR");
		// TODO .. count requests (total, per IP) to raise alarm if exceeds configurable threshold!
		String ip = contextRequest.getRemoteAddr();
		String trxId = TrxId.getTrxId();
		String logId =  trxId + " | ";
		String sMSidPrefix = (String) Configs.SMS_ID_PREFIX.getValue();
		int maxSMS = (int) Configs.MAX_OCCURS_FOR_SMS.getValue();
		SubmitSMSResponse response = new SubmitSMSResponse();
		smsLogger.info(logId + "Received new SMSRequest from IP: " + ip + " ,IP form header is: " + ipAddress);
		if (request.getSMSs() != null && request.getSMSs().size() > maxSMS) {
			smsLogger.warn(logId + "Invalid request, can't send " + request.getSMSs().size() + " in one request.");
			appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.EXCEED_REQUEST_MAX_SMS,
					"Invalid request, Exceed number of SMSs per request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(
					"Invalid request, it has (" + request.getSMSs().size() + "), exceeds the limts (" + maxSMS + ").");
			return response;
		}
		request.setPlainToString(plainData);

		if (validateRequest(request)) {
			SubmitDetailedSMSRequest detailReq;
			Entity selectedEntity = null;
			if (ipAddress != null) {
				detailReq = new SubmitDetailedSMSRequest(request, ipAddress, sMSidPrefix, trxId);
			} else {
				detailReq = new SubmitDetailedSMSRequest(request, ip, sMSidPrefix, trxId);
			}
			try {
				if (smsLogger.isDebugEnabled()) {
					String encyrptedRequest=encyrptionUtil.encrypt(request.toString());
					smsLogger.debug(logId + "Valid request{" + encyrptedRequest + "}");
				}
				if (((boolean) Configs.CACHE_REQUEST.getValue()) & ((boolean) Configs.CACHE_REQUEST_DIRECT.getValue())
						& loadBalancer.isTotalOutage()) {
					try {
						cachingManagementBean.cacheSmsRequest(detailReq, true);
					} catch (CacheLimitException | WritingCountMaxReachedException ex) {
						smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, "
								+ ex.getClass().getSimpleName());
						response.setResultStatus(ResultStatus.GENERIC_ERROR);
						response.setDescription("Unable to Submit SMSRequest");
						return response;
					}
					response = SmsApiResponseGenerator.generateSubmitSMSFakeResponse(detailReq);
					smsLogger.info(logId + "Request cached, returning to the client with response " + response);
					return response;
				}

				selectedEntity = loadBalancer.selectEntity(groupId);
				if (selectedEntity == null) {
					if (((boolean) Configs.CACHE_REQUEST.getValue())) {
						try {
							cachingManagementBean.cacheSmsRequest(detailReq, true);
						} catch (CacheLimitException | WritingCountMaxReachedException ex) {
							smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, " + ex.getClass().getSimpleName());
							appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.GENERAL_ERROR, "Cache limit exceed");
							response.setResultStatus(ResultStatus.GENERIC_ERROR);
							response.setDescription("Unable to Submit SMSRequest");
							return response;
						}
					response = SmsApiResponseGenerator.generateSubmitSMSFakeResponse(detailReq);
					smsLogger.info(logId + "Request cached, returning to the client with response " + response);
						return response;
					} else {
						response.setResultStatus(ResultStatus.GENERIC_ERROR);
						response.setDescription("Unable to Submit SMSRequest");
						return response;
					}
				}

				String path = (String) Configs.SMS_API_SUBMIT_SMS_SERVICE_PATH.getValue();
				ClientResponse cr = sendHttpRequest(detailReq, selectedEntity, path, logId);

				if (cr != null) {
					response = cr.getEntity(SubmitSMSResponse.class);
					if (smsLogger.isDebugEnabled()) {
						smsLogger.debug(logId + response);
					}
                                        
					if (response.getResultStatus() == ResultStatus.INTERNAL_SERVER_ERROR || response.getResultStatus() == ResultStatus.RESEND_CACHE_REQUEST_FAILED) {
						if (response.getResultStatus() == ResultStatus.RESEND_CACHE_REQUEST_FAILED) {
							smsLogger.warn(logId + response.getResultStatus() + " in main flow!");
						}
						if (selectedEntity != null) {
							try {
								loadBalancer.reportFailure(groupId, selectedEntity);
							} catch (NoSuchGroupException | NoSuchEntityException ex) {
								appLogger.error(logId + "Error during report failure");
							}
						}
						if (((boolean) Configs.CACHE_REQUEST.getValue())) {
							boolean cacheAll = false;
							if (response.getSmsStatus() != null && !response.getSmsStatus().isEmpty()) {
								for (int i = 0; i < response.getSmsStatus().size(); i++) {
									SMSResponseStatus smsStatus = response.getSmsStatus().get(i);
									if (SMSapiUtils.checkSMSFailed(smsStatus)) {
										detailReq.getSMSs().get(i).setCachedSMS(true);
									}
								}
							} else {
								cacheAll = true;
							}
							try {
								cachingManagementBean.cacheSmsRequest(detailReq, cacheAll);
							} catch (CacheLimitException | WritingCountMaxReachedException ex) {
								smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, " + ex.getClass().getSimpleName());
								appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.GENERAL_ERROR, "Cache limit exceed");
								response.setResultStatus(ResultStatus.GENERIC_ERROR);
								response.setDescription("Unable to Submit SMSRequest");
								return response;
							}
							response = SmsApiResponseGenerator.generateSubmitSMSFakeResponse(detailReq, response.getSmsStatus());
							smsLogger.info(logId + "Request cached, returning to the client with response " + response);
						} else {
							List<SMSResponseStatus> newSmsStatuses = null;
							if (response.getSmsStatus() != null && !response.getSmsStatus().isEmpty()) {
								newSmsStatuses = new ArrayList<SMSResponseStatus>(response.getSmsStatus().size());
								for (SMSResponseStatus smsStatus : response.getSmsStatus()) {
									if (smsStatus == SMSResponseStatus.SYSTEM_FAILURE || smsStatus == SMSResponseStatus.CACHE_LIMIT_EXCEEDED) {
										newSmsStatuses.add(SMSResponseStatus.FAILED_TO_SUBMITTED);
									} else {
										newSmsStatuses.add(smsStatus);
									}
								}
							}
							response.setSmsStatus(newSmsStatuses);
							if (newSmsStatuses == null) {
								response.setResultStatus(ResultStatus.GENERIC_ERROR);
								response.setDescription("Unable to Submit SMSRequest");
							} else {
								response.setResultStatus(ResultStatus.SUCCESS);
								response.setDescription(null);
							}
						}
					} else {
						if (selectedEntity != null) {
							try {
								loadBalancer.reportSuccess(groupId, selectedEntity);
							} catch (NoSuchGroupException | NoSuchEntityException ex) {
								appLogger.error(logId + "Error during report failure");
							}
						}
					}
					smsLogger.info(logId + "Returned from SMSAPI service, returning to the client with status " + response.getResultStatus());

				} else {
					smsLogger.error(logId + "Invalid client response");
					appErrorManagerAdapter.raiseError(request.logAccountId(), trxId,
							AppErrors.FAILED_TO_FORWARD_REQUEST, "Invalid client response");
                                    if (selectedEntity != null) {
                                        try {
                                            loadBalancer.reportFailure(groupId, selectedEntity);
                                        } catch (NoSuchGroupException | NoSuchEntityException ex) {
                                            appLogger.error(logId + "Error during report failure");
                                        }
                                    }
					response.setResultStatus(ResultStatus.GENERIC_ERROR);
					response.setDescription("Unable to Submit SMSRequest");
					if (((boolean) Configs.CACHE_REQUEST.getValue())) {
						try {
							cachingManagementBean.cacheSmsRequest(detailReq, true);
						} catch (CacheLimitException | WritingCountMaxReachedException ex) {
							smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, "
									+ ex.getClass().getSimpleName());
							appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.GENERAL_ERROR,
									"Cache limit exceed");
							response.setResultStatus(ResultStatus.GENERIC_ERROR);
							response.setDescription("Unable to Submit SMSRequest");
							return response;
						}
						response = SmsApiResponseGenerator.generateSubmitSMSFakeResponse(detailReq);
						smsLogger.info(logId + "Request cached, returning to the client with response " + response);
					}
				}
			} catch (Exception e) {
				// TODO .. raise alarm with low threshold like 5
				// TODO .. raise special alarm for timed out requests (here or in app) ?
				smsLogger.error(logId + "Unhandled Exception in SMSRequest " + e.getMessage());
				appLogger.error(logId + "Unhandled Exception in SMSRequest: ", e);
				if (selectedEntity != null) {
					try {
						loadBalancer.reportFailure(groupId, selectedEntity);
					} catch (NoSuchGroupException | NoSuchEntityException ex) {
						appLogger.error(logId + "Error during report failure");
					}
				}
				response.setResultStatus(ResultStatus.GENERIC_ERROR);
				response.setDescription("Unable to Submit SMSRequest");
				if (((boolean) Configs.CACHE_REQUEST.getValue())) {
					try {
						cachingManagementBean.cacheSmsRequest(detailReq, true);
					} catch (CacheLimitException | WritingCountMaxReachedException ex) {
						smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, "
								+ ex.getClass().getSimpleName());
						response.setResultStatus(ResultStatus.GENERIC_ERROR);
						response.setDescription("Unable to Submit SMSRequest");
						return response;
					}

					response = SmsApiResponseGenerator.generateSubmitSMSFakeResponse(detailReq);
					smsLogger.info(logId + "Request cached, returning to the client with response " + response);
				}
				return response;
			}
		} else {
			String encyrptedRequest = encyrptionUtil.encrypt(request.toString());
			smsLogger.warn(logId + "Invalid request{" + encyrptedRequest + "}");
			appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.INVALID_REQUEST,
					"Received invalid request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription("One of mandatory fields is empty.");
			return response;
		}
		if (smsLogger.isTraceEnabled()) {
			smsLogger.trace(logId + "Returning to the client with " + response);
		}
		return response;
	}

	private boolean validateRequest(SubmitSMSRequest request) {
		if (request.getAccountId() != null && request.getPassword() != null && request.getSecureHash() != null
				&& request.getSMSs() != null)
			if (!request.getAccountId().isEmpty() && !request.getPassword().isEmpty()
					&& !request.getSecureHash().isEmpty() && !request.getSMSs().isEmpty())
				return true;
		return false;
	}

	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	@Path("/Campaign")
	public SubmitCampaignResponse submitCampaignRquest(@Context HttpServletRequest contextRequest,
			SubmitCampaignRequest request) {
		String ipAddress = contextRequest.getHeader("X-FORWARDED-FOR");
		String ip = contextRequest.getRemoteAddr().toString();
		String trxId = TrxId.getTrxId();
		String logId =  trxId + " | ";
		String campaignId = IdGenerator.generateId("API");
		int maxSMSforCampaign = (int) Configs.MAX_OCCURS_FOR_SMS_IN_CAMP.getValue();
		SubmitCampaignResponse response = new SubmitCampaignResponse();
		response.setCampaignId(campaignId);

		smsLogger.info(logId + "Received new CampaignRequest from IP: " + ip + " ,IP form header is: " + ipAddress);

		if (request.getMsisdns() != null && request.getMsisdns().getReceiverMSISDN().size() > maxSMSforCampaign) {
			smsLogger.warn(logId + " Invalid request, can't send " + request.getMsisdns().getReceiverMSISDN().size()
					+ " in single request.");
			appErrorManagerAdapter.raiseError(request.logId(), trxId, AppErrors.EXCEED_REQUEST_MAX_SMS,
					"Invalid request, Exceed number of SMSs per request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
                        response.setCampaignId(null);
			response.setDescription(
					"Invalid request, please ensure that number of MSISDNs in campaign less than " + maxSMSforCampaign);
			return response;
		}
		request.setPlainToString(plainData);

		boolean valid = validateRequest(request);
		if (valid) {
			Entity selectedEntity = null;
			SubmitDetailedCampaignRequest detailReq;
			if (ipAddress != null) {
				detailReq = new SubmitDetailedCampaignRequest(request, ipAddress, trxId, campaignId);
			} else {
				detailReq = new SubmitDetailedCampaignRequest(request, ip, trxId, campaignId);
			}

			try {
				if (smsLogger.isDebugEnabled()) {
					String encyrptedRequest=encyrptionUtil.encrypt(request.toString());
					smsLogger.debug(logId + "Valid request{" + encyrptedRequest + "}");
				}
				if (((boolean) Configs.CACHE_REQUEST.getValue()) & ((boolean) Configs.CACHE_REQUEST_DIRECT.getValue())
						& loadBalancer.isTotalOutage()) {
					try {
						cachingManagementBean.cacheCampaignRequest(detailReq);
					} catch (CacheLimitException | WritingCountMaxReachedException ex) {
						smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, "
								+ ex.getClass().getSimpleName());
						response.setResultStatus(ResultStatus.GENERIC_ERROR);
                                                response.setCampaignId(null);
						response.setDescription("Unable to Submit campaign request");
						return response;
					}
					response = SmsApiResponseGenerator.generateSubmitCampaignFakeResponse(detailReq, campaignId);
					smsLogger.info(logId + "Request cached, returning to the client with response " + response);
					return response;
				}

				selectedEntity = loadBalancer.selectEntity(groupId);
				if (selectedEntity == null) {
                                    if (((boolean) Configs.CACHE_REQUEST.getValue())) {
					try {
						cachingManagementBean.cacheCampaignRequest(detailReq);
					} catch (CacheLimitException | WritingCountMaxReachedException ex) {
						smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, "
								+ ex.getClass().getSimpleName());
						appErrorManagerAdapter.raiseError(request.logId(), trxId, AppErrors.GENERAL_ERROR,
								"Cache limit exceed");
						response.setResultStatus(ResultStatus.GENERIC_ERROR);
                                                response.setCampaignId(null);
						response.setDescription("Unable to Submit campaign request");
						return response;
					}
					response = SmsApiResponseGenerator.generateSubmitCampaignFakeResponse(detailReq, campaignId);
					smsLogger.info(logId + "Request cached, returning to the client with response " + response);
					return response;
                                    } else {
                                        response.setResultStatus(ResultStatus.GENERIC_ERROR);
                                        response.setCampaignId(null);
                                        response.setDescription("Unable to Submit campaign request");
                                        return response;
                                    }
				}

				String path = (String) Configs.SMS_API_SUBMIT_CAMPAIGN_SERVICE_PATH.getValue();
				ClientResponse cr = sendHttpRequest(detailReq, selectedEntity, path, logId);
				if (cr != null) {
					response = cr.getEntity(SubmitCampaignResponse.class);
					if (smsLogger.isDebugEnabled()) {
						smsLogger.debug(logId + response);
					}
                                        
                                    if (response.getResultStatus() == ResultStatus.INTERNAL_SERVER_ERROR) {
                                        if (selectedEntity != null) {
                                            try {
                                                loadBalancer.reportFailure(groupId, selectedEntity);
                                            } catch (NoSuchGroupException | NoSuchEntityException ex) {
                                                appLogger.error(logId + "Error during report failure");
                                            }
                                        }
                                        if (((boolean) Configs.CACHE_REQUEST.getValue())) {
                                            try {
                                                cachingManagementBean.cacheCampaignRequest(detailReq);
                                            } catch (CacheLimitException | WritingCountMaxReachedException ex) {
                                                smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, " + ex.getClass().getSimpleName());
                                                appErrorManagerAdapter.raiseError(request.logId(), trxId, AppErrors.GENERAL_ERROR, "Cache limit exceed");
                                                response.setResultStatus(ResultStatus.GENERIC_ERROR);
                                                response.setCampaignId(null);
                                                response.setDescription("Unable to Submit campaign request");
                                                return response;
                                            }
                                            response = SmsApiResponseGenerator.generateSubmitCampaignFakeResponse(detailReq, campaignId);
                                            smsLogger.info(logId + "Request cached, returning to the client with response " + response);
                                        } else {
                                            response.setResultStatus(ResultStatus.GENERIC_ERROR);
                                            response.setCampaignId(null);
                                            response.setDescription("Unable to Submit campaign request");
                                        }
                                    } else {
                                        if (selectedEntity != null) {
                                            try {
                                                loadBalancer.reportSuccess(groupId, selectedEntity);
                                            } catch (NoSuchGroupException | NoSuchEntityException ex) {
                                                appLogger.error(logId + "Error during report failure");
                                            }
                                        }
                                    }
                                    smsLogger.info(logId + "Returned from SMSAPI service, returning to the client with status " + response.getResultStatus());

				} else {
					smsLogger.error(logId + "Invalid client response");
					appErrorManagerAdapter.raiseError(request.logId(), trxId, AppErrors.FAILED_TO_FORWARD_REQUEST,
							"Invalid client response");
					response.setResultStatus(ResultStatus.GENERIC_ERROR);
                                        response.setCampaignId(null);
					response.setDescription("Unable to Submit campaign request");
					if (((boolean) Configs.CACHE_REQUEST.getValue())) {
						try {
							cachingManagementBean.cacheCampaignRequest(detailReq);
						} catch (CacheLimitException | WritingCountMaxReachedException ex) {
							smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, "
									+ ex.getClass().getSimpleName());
							response.setResultStatus(ResultStatus.GENERIC_ERROR);
                                                        response.setCampaignId(null);
							response.setDescription("Unable to Submit campaign request");
							return response;
						}
						response = SmsApiResponseGenerator.generateSubmitCampaignFakeResponse(detailReq, campaignId);
						smsLogger.info(logId + "Request cached, returning to the client with response " + response);
                                            if (selectedEntity != null) {
                                                try {
                                                    loadBalancer.reportFailure(groupId, selectedEntity);
                                                } catch (NoSuchGroupException | NoSuchEntityException ex) {
                                                    appLogger.error(logId + "Error during report failure");
                                                }
                                            }
					}
				}
			} catch (Exception e) {
				smsLogger.error(logId + "Unhandled Exception in Campaign Request");
				appLogger.error(logId + "Unhandled Exception in Campaign Request: ", e);
				response.setResultStatus(ResultStatus.GENERIC_ERROR);
                                response.setCampaignId(null);
				response.setDescription("Unable to Submit campaign request");
				if (((boolean) Configs.CACHE_REQUEST.getValue())) {
					try {
						cachingManagementBean.cacheCampaignRequest(detailReq);
					} catch (CacheLimitException | WritingCountMaxReachedException ex) {
						smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, "
								+ ex.getClass().getSimpleName());
						response.setResultStatus(ResultStatus.GENERIC_ERROR);
                                                response.setCampaignId(null);
						response.setDescription("Unable to Submit campaign request");
						return response;
					}
                                    if (selectedEntity != null) {
                                        try {
                                            loadBalancer.reportFailure(groupId, selectedEntity);
                                        } catch (NoSuchGroupException | NoSuchEntityException ex) {
                                            appLogger.error(logId + "Error during report failure");
                                        }
                                    }
					response = SmsApiResponseGenerator.generateSubmitCampaignFakeResponse(detailReq, campaignId);
					smsLogger.info(logId + "Request cached, returning to the client with response " + response);
				}
				return response;
			}
		} else {
			String encyrptedRequest=encyrptionUtil.encrypt(request.toString());
			smsLogger.warn(logId + "Invalid request{" + encyrptedRequest + "}");
			appErrorManagerAdapter.raiseError(request.logId(), trxId, AppErrors.INVALID_REQUEST,
					"Received invalid request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
                        response.setCampaignId(null);
			response.setDescription("Invalid request, please ensure there is no null or empty fields.");
			return response;
		}
		if (smsLogger.isTraceEnabled()) {
			smsLogger.trace(logId + "Returning to the client with " + response);
		}
		return response;
	}

	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	@Path("/CampaignInquiry")
	public EnquireCampaignByIdResponse submitCampaignInquiryRquest(@Context HttpServletRequest contextRequest,
			EnquireCampaignByIdRequest request) {
		String ipAddress = contextRequest.getHeader("X-FORWARDED-FOR");
		String ip = contextRequest.getRemoteAddr().toString();
		String trxId = TrxId.getTrxId();
		String logId =  trxId + " | ";

		EnquireCampaignByIdResponse response = new EnquireCampaignByIdResponse();
		smsLogger.info(
				logId + "Received new CampaignInquiry request from IP: " + ip + " ,IP form header is: " + ipAddress);
		request.setPlainToString(plainData);

		boolean valid = validateRequest(request);
		if (valid) {
			try {
				if (smsLogger.isDebugEnabled()) {
					String encyrptedRequest=encyrptionUtil.encrypt(request.toString());
					smsLogger.debug(logId + "Valid request{" + encyrptedRequest + "}");
				}
				if (((boolean) Configs.CACHE_REQUEST.getValue()) & loadBalancer.isTotalOutage()) {
					response = SmsApiResponseGenerator.generateEnquireCampaignByIdFakeResponse(request);
					smsLogger.info(logId + "Request cached, returning to the client with response " + response);
					return response;
				}

				String smsServiceURI = (String) Configs.SMS_API_CAMPAIGN_INQUIRY_SERVICE_URI.getValue();
//				if (smsLogger.isTraceEnabled())
//					smsLogger.trace(logId + " Will call SMS API send campaign service URI:" + smsServiceURI
//							+ ", will send : " + request);

				EnquireCampaignByIdDetailedRequest detailReq;
				if (ipAddress != null) {
					detailReq = new EnquireCampaignByIdDetailedRequest(request, ipAddress, trxId);
				} else {
					detailReq = new EnquireCampaignByIdDetailedRequest(request, ip, trxId);
				}
				ClientResponse cr = httpClient.sendHttpXmlRequest(smsServiceURI, detailReq, "SMS");
				if (cr != null) {
					response = cr.getEntity(EnquireCampaignByIdResponse.class);
					if (smsLogger.isDebugEnabled()) {
						smsLogger.debug(logId + response);
					}
					smsLogger.info(logId + "Returned from SMS API campaign inquiry service with status = "
							+ response.getResultStatus());
				} else {
					smsLogger.error(logId + " Client response is : " + cr);
					appErrorManagerAdapter.raiseError(request.logId(), trxId, AppErrors.FAILED_TO_FORWARD_REQUEST,
							"Invalid client response");
					response.setResultStatus(ResultStatus.GENERIC_ERROR);
					response.setDescription("Unable to inquire about the campaigns");
				}
			} catch (Exception e) {
				smsLogger.error(logId + "Unhandled Exception in Inquire campaigns", e);
				response.setResultStatus(ResultStatus.GENERIC_ERROR);
				response.setDescription("Unable to inquire about the campaigns");
				return response;
			}
		} else {
			String encyrptedRequest=encyrptionUtil.encrypt(request.toString());
			smsLogger.warn(logId + "Invalid request{" + encyrptedRequest + "}");
			appErrorManagerAdapter.raiseError(request.logId(), trxId, AppErrors.INVALID_REQUEST,
					"Received invalid request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription("Invalid request, please ensure there is no null or empty fields.");
			return response;
		}
		smsLogger.info(logId + "Returned from SMS API service with status = " + response.getResultStatus());
		if (smsLogger.isTraceEnabled()) {
			smsLogger.trace(logId + "Returning to the client with " + response);
		}

		return response;
	}

	private boolean validateRequestWithSmsId(EnquireSMSByIdRequest request) {
		if (request.getAccountId() != null && request.getPassword() != null && request.getSecureHash() != null
				&& request.getSmsId() != null) {
			if (!request.getAccountId().isEmpty() && !request.getPassword().isEmpty()
					&& !request.getSecureHash().isEmpty() && !request.getSmsId().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private boolean validateRequestWithSmsIdList(EnquireSMSByIdListRequest request) {
		if (request.getAccountId() != null && request.getPassword() != null && request.getSecureHash() != null
				&& request.getSmsIdList() != null && !request.getSmsIdList().isEmpty()) {
			if (!request.getAccountId().isEmpty() && !request.getPassword().isEmpty()
					&& !request.getSecureHash().isEmpty() && !request.getSmsIdList().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private int validateRequestWithDatesAndMsisdn(EnquireSMSsByDatesRequest request) {
		if (request.getAccountId() != null && request.getPassword() != null && request.getSecureHash() != null
				&& request.getReceiverMSISDN() != null && request.getStartDate() != null
				&& request.getEndDate() != null)
			if (!request.getAccountId().isEmpty() && !request.getPassword().isEmpty()
					&& !request.getSecureHash().isEmpty() && !request.getReceiverMSISDN().isEmpty()
					&& !request.getStartDate().isEmpty() && !request.getEndDate().isEmpty())
                            if (validateDate(request.getStartDate()) && validateDate(request.getEndDate()))
				return 0;
                            else
                                return 1;
		return 2;
	}

	private int validateRequestWithDatesOnly(EnquireSMSsByDatesRequest request) {
		if (request.getAccountId() != null && request.getPassword() != null && request.getSecureHash() != null
				&& request.getStartDate() != null && request.getEndDate() != null)
			if (!request.getAccountId().isEmpty() && !request.getPassword().isEmpty()
					&& !request.getSecureHash().isEmpty() && !request.getStartDate().isEmpty()
					&& !request.getEndDate().isEmpty())
                            if (validateDate(request.getStartDate()) && validateDate(request.getEndDate()))
				return 0;
                            else
                                return 1;
		return 2;
	}

        private final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
	private boolean validateDate(String date) {
		try {
			String[] dateParameters = date.split("/");
			int day = Integer.parseInt(dateParameters[0]);
			int month = Integer.parseInt(dateParameters[1]);
			int year = Integer.parseInt(dateParameters[2]);

                        boolean yearAndMonthPassed = validateRange(currentYear - (int) Configs.SMSAPI_INQUIRE_YEARS.getValue(), currentYear, year) && validateRange(1, 12, month);
                        if(yearAndMonthPassed) {
                            Calendar calender = Calendar.getInstance();
                            calender.set(Calendar.YEAR, year);
                            calender.set(Calendar.MONTH, month);
                            return validateRange(1, calender.getActualMaximum(Calendar.DAY_OF_MONTH), day);
                        } else {
                            return false;
                        }
		} catch (Exception e) {
			return false;
		}
	}

	private boolean validateRange(int min, int max, int num) {
		return num >= min && num <= max;
	}

	private boolean validateRequest(SubmitCampaignRequest request) {
		if (request.getAccountId() != null && request.getPassword() != null && request.getSecureHash() != null
				&& request.getCampaignName() != null && request.getSenderName() != null && request.getSMSText() != null
				&& request.getMsisdns() != null)
			if (!request.getAccountId().isEmpty() && !request.getPassword().isEmpty()
					&& !request.getSecureHash().isEmpty() && !request.getCampaignName().isEmpty()
					&& !request.getSenderName().isEmpty() && !request.getSMSText().isEmpty()
					&& !request.getMsisdns().getReceiverMSISDN().isEmpty())
				return true;
		return false;
	}

	private boolean validateRequest(EnquireCampaignByIdRequest request) {
		if (request.getAccountId() != null && request.getPassword() != null && request.getSecureHash() != null
				&& request.getCampaignId() != null)
			if (!request.getAccountId().isEmpty() && !request.getPassword().isEmpty()
					&& !request.getSecureHash().isEmpty() && !request.getCampaignId().isEmpty())
				return true;
		return false;
	}

	/*
	 * Web Method with new Response include(SMSID) :
	 * 
	 * This Method Send SMS and retrun response of SMS Details info. including
	 * each sms ID
	 */
	@Path("/submitAndgetSmsId")
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	public SubmitSMSResponseBySmsId submitSmsRequestAndGetSmsId(@Context HttpServletRequest contextRequest,
			SubmitSMSRequest request) {
		String ipAddress = contextRequest.getHeader("X-FORWARDED-FOR");
		String ip = contextRequest.getRemoteAddr();
		String trxId = TrxId.getTrxId();
		String logId =  trxId + " | ";
		String sMSidPrefix = (String) Configs.SMS_ID_PREFIX.getValue();
		int maxSMS = (int) Configs.MAX_OCCURS_FOR_SMS.getValue();
		SubmitSMSResponseBySmsId response = new SubmitSMSResponseBySmsId();
		smsLogger.info(
				logId + "Received new SmsRequestAndGetSmsId from IP: " + ip + " ,IP form header is: " + ipAddress);

		if (request.getSMSs() != null && request.getSMSs().size() > maxSMS) {
			smsLogger.warn(logId + "Invalid request, can't send " + request.getSMSs().size() + " in one request.");
			appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.EXCEED_REQUEST_MAX_SMS,
					"Invalid request, Exceed number of SMSs per request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(
					"Invalid request, it has (" + request.getSMSs().size() + "), exceeds the limts (" + maxSMS + ").");
			return response;
		}
		request.setPlainToString(plainData);

		if (validateRequest(request)) {
			SubmitDetailedSMSRequest detailReq;
			Entity selectedEntity = null;
			if (ipAddress != null) {
				detailReq = new SubmitDetailedSMSRequest(request, ipAddress, sMSidPrefix, trxId);
			} else {
				detailReq = new SubmitDetailedSMSRequest(request, ip, sMSidPrefix, trxId);
			}
			try {
				if (smsLogger.isDebugEnabled()) {
					String encyrptedRequest=encyrptionUtil.encrypt(request.toString());
					smsLogger.debug(logId + "Valid request{" + encyrptedRequest + "}");
				}
				if (((boolean) Configs.CACHE_REQUEST.getValue()) & ((boolean) Configs.CACHE_REQUEST_DIRECT.getValue())
						& loadBalancer.isTotalOutage()) {
					try {
						cachingManagementBean.cacheSmsRequest(detailReq, true);
					} catch (CacheLimitException | WritingCountMaxReachedException ex) {
						smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, "
								+ ex.getClass().getSimpleName());
						response.setResultStatus(ResultStatus.GENERIC_ERROR);
						response.setDescription("Unable to Submit SMS Request and get SMS Id");
						return response;
					}
					response = SmsApiResponseGenerator.generateSubmitSMSBySmsIdFakeResponse(detailReq);
					smsLogger.info(logId + "Request cached, returning to the client with response " + response);
					return response;
				}

				selectedEntity = loadBalancer.selectEntity(groupId);
				if (selectedEntity == null) {
                                    if (((boolean) Configs.CACHE_REQUEST.getValue())) {
					try {
						cachingManagementBean.cacheSmsRequest(detailReq, true);
					} catch (CacheLimitException | WritingCountMaxReachedException ex) {
						smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, "
								+ ex.getClass().getSimpleName());
						appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.GENERAL_ERROR,
								"Cache limit exceed");
						response.setResultStatus(ResultStatus.GENERIC_ERROR);
						return response;
					}
					response = SmsApiResponseGenerator.generateSubmitSMSBySmsIdFakeResponse(detailReq);
					smsLogger.info(logId + "Request cached, returning to the client with response " + response);
					return response;
                                    } else {
                                        response.setResultStatus(ResultStatus.GENERIC_ERROR);
                                        response.setDescription("Unable to Submit SMS Request and get SMS Id");
                                        return response;
                                    }
				}

				String path = (String) Configs.WEB_METHOD_TO_GET_SMSID_PATH.getValue();
				ClientResponse cr = sendHttpRequest(detailReq, selectedEntity, path, logId);

				if (cr != null) {
					response = cr.getEntity(SubmitSMSResponseBySmsId.class);
					if (smsLogger.isTraceEnabled()) {
						smsLogger.trace(logId + response);
					}
					if (response.getsmsResponseList() != null) {
						for (SMSResponseBySmsId sMSResponseBySmsId : response.getsmsResponseList()) {
							if (sMSResponseBySmsId != null
									&& sMSResponseBySmsId.getSmsStatus() != SMSResponseStatus.SUBMITTED) {
								sMSResponseBySmsId.setSmsId(null);
							}
						}
					}

                                    if (response.getResultStatus() == ResultStatus.INTERNAL_SERVER_ERROR || response.getResultStatus() == ResultStatus.RESEND_CACHE_REQUEST_FAILED) {
                                        if(response.getResultStatus() == ResultStatus.RESEND_CACHE_REQUEST_FAILED) {
                                            smsLogger.warn(logId + response.getResultStatus() + " in main flow!");
                                        }
                                        if (selectedEntity != null) {
                                            try {
                                                loadBalancer.reportFailure(groupId, selectedEntity);
                                            } catch (NoSuchGroupException | NoSuchEntityException ex) {
                                                appLogger.error(logId + "Error during report failure");
                                            }
                                        }
                                        if (((boolean) Configs.CACHE_REQUEST.getValue())) {
                                            boolean cacheAll;
                                            if (response.getsmsResponseList() != null && !response.getsmsResponseList().isEmpty()) {
                                                cacheAll = false;
                                                for (int i = 0; i < response.getsmsResponseList().size(); i++) {
                                                    SMSResponseStatus smsStatus = response.getsmsResponseList().get(i).getSmsStatus();
                                                    if (SMSapiUtils.checkSMSFailed(smsStatus)) {
                                                        String smsId = response.getsmsResponseList().get(i).getSMSId();
                                                        for (SMSDetails sMSDetails : detailReq.getSMSs()) {
                                                            if (sMSDetails.getSMSId().equals(smsId)) {
                                                                sMSDetails.setCachedSMS(true);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                cacheAll = true;
                                            }
                                            try {
                                                cachingManagementBean.cacheSmsRequest(detailReq, cacheAll);
                                            } catch (CacheLimitException | WritingCountMaxReachedException ex) {
                                                smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, " + ex.getClass().getSimpleName());
                                                appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.GENERAL_ERROR, "Cache limit exceed");
                                                response.setResultStatus(ResultStatus.GENERIC_ERROR);
                                                response.setDescription("Unable to Submit SMS Request and get SMS Id");
                                                return response;
                                            }
                                            response = SmsApiResponseGenerator.generateSubmitSMSBySmsIdFakeResponse(detailReq, response.getsmsResponseList());
                                            smsLogger.info(logId + "Request cached, returning to the client with response " + response);
                                        } else {
                                            if (response.getsmsResponseList() != null && !response.getsmsResponseList().isEmpty()) {
                                                for (SMSResponseBySmsId smsStatus : response.getsmsResponseList()) {
                                                    if (smsStatus.getSmsStatus() == SMSResponseStatus.SYSTEM_FAILURE || smsStatus.getSmsStatus() == SMSResponseStatus.CACHE_LIMIT_EXCEEDED) {
                                                        smsStatus.setSmsStatus(SMSResponseStatus.FAILED_TO_SUBMITTED);
                                                    }
                                                }
                                                response.setResultStatus(ResultStatus.SUCCESS);
                                                response.setDescription(null);
                                            } else {
                                                response.setResultStatus(ResultStatus.GENERIC_ERROR);
                                                response.setDescription("Unable to Submit SMSRequest");
                                            }
                                        }
                                    } else {
                                        if (selectedEntity != null) {
                                            try {
                                                loadBalancer.reportSuccess(groupId, selectedEntity);
                                            } catch (NoSuchGroupException | NoSuchEntityException ex) {
                                                appLogger.error(logId + "Error during report failure");
                                            }
                                        }
                                    }
                                    smsLogger.info(logId + "Returned from SMSAPI service, returning to the client with status " + response.getResultStatus());
                                                
				} else {
					smsLogger.error(logId + "Invalid client response");
					appErrorManagerAdapter.raiseError(request.logAccountId(), trxId,
							AppErrors.FAILED_TO_FORWARD_REQUEST, "Invalid client response");
					response.setResultStatus(ResultStatus.GENERIC_ERROR);
					response.setDescription("Unable to Submit SMS Request and get SMS Id");
					if (((boolean) Configs.CACHE_REQUEST.getValue())) {
						try {
							cachingManagementBean.cacheSmsRequest(detailReq, true);
						} catch (CacheLimitException | WritingCountMaxReachedException ex) {
							smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, "
									+ ex.getClass().getSimpleName());
							appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.GENERAL_ERROR,
									"Cache limit exceed");
							response.setResultStatus(ResultStatus.GENERIC_ERROR);
							response.setDescription("Unable to Submit SMS Request and get SMS Id");
							return response;
						}
						response = SmsApiResponseGenerator.generateSubmitSMSBySmsIdFakeResponse(detailReq);
						smsLogger.info(logId + "Request cached, returning to the client with response " + response);
                                            if (selectedEntity != null) {
                                                try {
                                                    loadBalancer.reportFailure(groupId, selectedEntity);
                                                } catch (NoSuchGroupException | NoSuchEntityException ex) {
                                                    appLogger.error(logId + "Error during report failure");
                                                }
                                            }
					}
				}
			} catch (Exception e) {
				smsLogger.error(logId + "Unhandled Exception in SMSRequestAndGetSmsId: " + e.getMessage());
				appLogger.error(logId + "Unhandled Exception in SMSRequestAndGetSmsId", e);
				response.setResultStatus(ResultStatus.GENERIC_ERROR);
				response.setDescription("Unable to Submit SMS Request and get SMS Id");
				if (((boolean) Configs.CACHE_REQUEST.getValue())) {
					try {
						cachingManagementBean.cacheSmsRequest(detailReq, true);
					} catch (CacheLimitException | WritingCountMaxReachedException ex) {
						smsLogger.warn(logId + "Can NOT cache request due to cache limit exceed, "
								+ ex.getClass().getSimpleName());
						response.setResultStatus(ResultStatus.GENERIC_ERROR);
						response.setDescription("Unable to Submit SMS Request and get SMS Id");
						return response;
					}
					response = SmsApiResponseGenerator.generateSubmitSMSBySmsIdFakeResponse(detailReq);
					smsLogger.info(logId + "Request cached, returning to the client with response " + response);
					if (selectedEntity != null) {
						try {
							loadBalancer.reportFailure(groupId, selectedEntity);
						} catch (NoSuchGroupException | NoSuchEntityException ex) {
							appLogger.error(logId + "Error during report failure");
						}
					}
				}
				return response;
			}
		} else {
			String encyrptedRequest=encyrptionUtil.encrypt(request.toString());
			smsLogger.warn(logId + "Invalid request{" + encyrptedRequest + "}");
			appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.INVALID_REQUEST,
					"Received invalid request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription("One of mandatory fields is empty.");
			return response;
		}
		if (smsLogger.isTraceEnabled()) {
			smsLogger.trace(logId + "Returning to the client with " + response);
		}
		return response;
	}// end of web method submitSmsRequestAndGetSmsId

	/*
	 * Web Method to inquire with SMSID
	 * 
	 * This Method inquire in the DataBase for SMS's with this SMSid
	 */
	/**	@Path("/inquireBySmsId")
		@POST
		@Consumes("application/xml")
		@Produces("application/xml")
		public EnquireSMSByIdResponse inquireWithSmsId(@Context HttpServletRequest contextRequest,
				EnquireSMSByIdRequest request) {
			String ipAddress = contextRequest.getHeader("X-FORWARDED-FOR");
	        String ip = contextRequest.getRemoteAddr();
			String trxId = TrxId.getTrxId();
	        String logId = request.logAccountId() + trxId + " | ";
			EnquireSMSByIdResponse response = new EnquireSMSByIdResponse();
	            smsLogger.info(logId + "Received new inquireWithSmsId request  from IP: " + ip + " ,IP form header is: " + ipAddress);
	        if (validateRequestWithSmsId(request)) {
	            try {
	                if (smsLogger.isDebugEnabled()) {
	                    smsLogger.debug(logId + "Valid request{" + request + "}");
	                }
	                if (((boolean) Configs.CACHE_REQUEST.getValue()) & loadBalancer.isTotalOutage()) {
	                    response = SmsApiResponseGenerator.generateEnquireSMSByIdFakeResponse(request);
	                    smsLogger.info(logId + "Request cached, returning to the client with response " + response);
	                    return response;
	                }
	                
	    EnquireSMSByIdDetailedRequest detailedRequest;
			if (ipAddress != null) {
				detailedRequest = new EnquireSMSByIdDetailedRequest(request, ipAddress,trxId);
			} else {
				detailedRequest = new EnquireSMSByIdDetailedRequest(request, ip,trxId);
			}
					String smsServiceURI = (String) Configs.WEB_METHOD_TO_GET_SMSDates_URI.getValue();
	                smsLogger.info(logId + "Will call SMSAPI service");
	                if (smsLogger.isTraceEnabled()) {
	                    smsLogger.trace(logId + "SMSAPI service URI:" + smsServiceURI + ", will send : " + request);
	                }
					ClientResponse cr = httpClient.sendHttpXmlRequest(smsServiceURI, detailedRequest, "SMS");
					if (cr != null) {
						response = cr.getEntity(EnquireSMSByIdResponse.class);
	                                if (smsLogger.isDebugEnabled()) {
	                                    smsLogger.debug(logId + response);
	                                }
	                                smsLogger.info(logId + "Returned from SMSAPI service, returning to the client with status " + response.getResultStatus());
					} else {
						smsLogger.error(logId + "Client response is : " + cr);
	                                appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.FAILED_TO_FORWARD_REQUEST, "Invalid client response");
	                                    response.setResultStatus(ResultStatus.GENERIC_ERROR);
						response.setDescription("Unable to inquire about the SMSs");
					}
				} catch (Exception e) {
	                smsLogger.error(logId + "Unhandled Exception in Inquire SMSs " + e.getMessage());
	                appLogger.error(logId + "Unhandled Exception in Inquire campaigns", e);
					response.setResultStatus(ResultStatus.GENERIC_ERROR);
					response.setDescription("Unable to inquire about the SMSs");
					return response;
				}
			} else {
	                    smsLogger.warn(logId + "Invalid request{" + request + "}");
	                    appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.INVALID_REQUEST, "Received invalid request");
				response.setResultStatus(ResultStatus.INVALID_REQUEST);
	            response.setDescription("One of mandatory fields is empty.");
				return response;
			}
	            if(smsLogger.isTraceEnabled()){
	                smsLogger.trace(logId + "Returning to the client with " + response);
	            }
			return response;
		}// end of web method inquireWithSmsId
	*/

	/*
	* Web Method to inquire with SMSID
	* 
	* This Method inquire in the DataBase for SMS's with this SMSid
	*/
	@Path("/inquireBySmsId")
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	public EnquireSMSByIdResponse inquireBySmsIdList(@Context HttpServletRequest contextRequest,
			EnquireSMSByIdListRequest request) {
		String ipAddress = contextRequest.getHeader("X-FORWARDED-FOR");
		String ip = contextRequest.getRemoteAddr();
		String trxId = TrxId.getTrxId();
		String logId =  trxId + " | ";
		EnquireSMSByIdResponse response = new EnquireSMSByIdResponse();
		smsLogger.info(logId + "Received new inquireBySmsId request " + request.toString() + " from IP: " + ip
				+ ",IP form header is: " + ipAddress);

		int maxSMS = (int) Configs.MAX_OCCURS_FOR_SMS_INQUIRE.getValue();
		if (request.getSmsIdList() != null && request.getSmsIdList().size() > maxSMS) {
			smsLogger.warn(
					logId + "Invalid request, can't inquire " + request.getSmsIdList().size() + "SMS in one request.");
			appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.EXCEED_REQUEST_MAX_SMS,
					"Invalid request, Exceed number of inquired SMSs per request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription("Invalid request, it has (" + request.getSmsIdList().size()
					+ ") SMS id, exceeds the limts of (" + maxSMS + ").");
			return response;
		}
		request.setPlainToString(plainData);

		if (validateRequestWithSmsIdList(request)) {
			try {
				if (smsLogger.isDebugEnabled()) {
					String encyrptedRequest=encyrptionUtil.encrypt(request.toString());
					smsLogger.debug(logId + "Valid request { "+encyrptedRequest+"}");
				}
				EnquireSMSByIdListDetailedRequest detailedRequest;
				if (ipAddress != null) {
					detailedRequest = new EnquireSMSByIdListDetailedRequest(request, ipAddress, trxId);
				} else {
					detailedRequest = new EnquireSMSByIdListDetailedRequest(request, ip, trxId);
				}
				String smsServiceURI = (String) Configs.WEB_METHOD_TO_GET_SMSDates_URI.getValue();
				/*smsLogger.info(logId + "Will call SMSAPI service");
				if (smsLogger.isTraceEnabled()) {
					smsLogger.trace(logId + "SMSAPI service URI:" + smsServiceURI);
				}*/
				ClientResponse cr = httpClient.sendHttpXmlRequest(smsServiceURI, detailedRequest, "SMS");
				if (cr != null) {
					response = cr.getEntity(EnquireSMSByIdResponse.class);
					if (smsLogger.isDebugEnabled()) {
						smsLogger.debug(logId + response);
					}
					smsLogger.info(logId + "Returned from SMSAPI service, returning to the client with status "
							+ response.getResultStatus());
				} else {
					smsLogger.error(logId + "Client response is null");
					appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.FAILED_TO_FORWARD_REQUEST, "Unable to inquire about the SMSs by SMSIds");
					response.setResultStatus(ResultStatus.GENERIC_ERROR);
					response.setDescription("Unable to inquire about the SMSs by SMSIds");
				}
			} catch (Exception e) {
				smsLogger.error(logId + "Unable to inquire about the SMSs by SMSIds");
				appLogger.error(logId + "Unable to inquire about the SMSs by SMSIds", e);
				response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
				response.setDescription("Unable to inquire about the SMSs by SMSIds");
				return response;
			}
		} else {
			smsLogger.warn(logId + "Invalid request");
			appErrorManagerAdapter.raiseError(request.logAccountId(), trxId, AppErrors.INVALID_REQUEST,
					"Received invalid request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription("One of mandatory fields is empty.");
			return response;
		}
		return response;
	}// end of web method inquireWithSmsId

	/*
	 * Web Method to inquire with start, end date, receiver MSISDN [up to 100
	 * SMS with this smsID]
	 * 
	 * this Method get from DB all the SMS's for this MSISDN in this Period from
	 * [Start Date , End Date]
	 */

	@Path("/inquireByRecieverAndDate")
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	public EnquireSMSsByDatesAndMSISDNResponse inquireByDateAndReciever(@Context HttpServletRequest contextRequest,
			EnquireSMSsByDatesRequest request) {
		String ipAddress = contextRequest.getHeader("X-FORWARDED-FOR");
		String ip = contextRequest.getRemoteAddr().toString();
		String trxId = TrxId.getTrxId();
		String logId =  trxId + " | ";
		EnquireSMSsByDatesAndMSISDNResponse response = new EnquireSMSsByDatesAndMSISDNResponse();
		smsLogger.info(logId + "Received new inquireByDateAndReciever request from IP: " + ip + " ,IP form header is: "
				+ ipAddress);

		request.setPlainToString(plainData);

		// validate Request
		int valid = validateRequestWithDatesAndMsisdn(request);
            switch (valid) {
                case 0:
			try {
				if (smsLogger.isDebugEnabled()) {
					String encyrptedRequest=encyrptionUtil.encrypt(request.toString());
					smsLogger.debug(logId + "Valid request{" + encyrptedRequest + "}");
				}
				if (((boolean) Configs.CACHE_REQUEST.getValue()) & loadBalancer.isTotalOutage()) {
					response = SmsApiResponseGenerator.generateEnquireSMSsByDatesAndMSISDNFakeResponse(request);
					smsLogger.info(logId + "Request cached, returning to the client with response " + response);
					return response;
				}

				String smsServiceURI = (String) Configs.WEB_METHOD_TO_GET_SMSDatesWithMSISDN_URI.getValue();
//				if (smsLogger.isTraceEnabled())
//					smsLogger.trace(
//							logId + "Will call SMS API service URI:" + smsServiceURI + ", will send : " + request);

				EnquireSMSsByDatesDetailedRequest detailedRequest;
				if (ipAddress != null) {

					detailedRequest = new EnquireSMSsByDatesDetailedRequest(request, ipAddress, trxId);
				} else {
					detailedRequest = new EnquireSMSsByDatesDetailedRequest(request, ip, trxId);
				}
				ClientResponse cr = httpClient.sendHttpXmlRequest(smsServiceURI, detailedRequest, "SMS");
				if (cr != null) {
					response = cr.getEntity(EnquireSMSsByDatesAndMSISDNResponse.class);
					smsLogger.info(logId + "Returned from SMS API service with status = " + response.getResultStatus());

					if (smsLogger.isDebugEnabled())
						smsLogger.debug(logId + response);

				} else {
					smsLogger.error(logId + "Client response is : " + cr);
					appErrorManagerAdapter.raiseError(request.logId(), trxId, AppErrors.FAILED_TO_FORWARD_REQUEST,
							"Invalid client response");
					response.setResultStatus(ResultStatus.GENERIC_ERROR);
					response.setDescription("Unable to inquire about the SMSs by date and reciever");
				}
			} catch (Exception e) {
				smsLogger.error(logId + "Unhandled Exception in inquireByDateAndReciever " + e.getMessage());
				appLogger.error(logId + "Unhandled Exception in inquireByDateAndReciever", e);
				response.setResultStatus(ResultStatus.GENERIC_ERROR);
				response.setDescription("Unable to inquire about the SMSs by date and reciever");
				return response;
			}
                    break;
                case 1:
			String encyrptedRequest=encyrptionUtil.encrypt(request.toString());
			smsLogger.warn(logId + "Invalid request{" + encyrptedRequest + "}");
			appErrorManagerAdapter.raiseError(request.logId(), trxId, AppErrors.INVALID_REQUEST, "Received invalid request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription("Invalid request, please ensure values of dates");
			return response;
                default:
        	encyrptedRequest=encyrptionUtil.encrypt(request.toString());
			smsLogger.warn(logId + "Invalid request{" + encyrptedRequest + "}");
			appErrorManagerAdapter.raiseError(request.logId(), trxId, AppErrors.INVALID_REQUEST,
					"Received invalid request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription("Invalid request, please ensure there is no null or empty fields.");
			return response;
		}
		if (smsLogger.isTraceEnabled()) {
			smsLogger.trace(logId + "Returning to the client with " + response);
		}
		return response;
	}

	/*
	 * Web Method to inquire with start, end date
	 * 
	 * this Method get Statistics from DB all the SMS's (Delivered,UnDelivered)
	 * in this Period from [Start Date , End Date]
	 */
	@Path("/inquireByDate")
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	public EnquireSMSsByDatesResponse inquireByDate(@Context HttpServletRequest contextRequest,
			EnquireSMSsByDatesRequest request) {

		//		String req = getXmlString(request);
		String ipAddress = contextRequest.getHeader("X-FORWARDED-FOR");
		String ip = contextRequest.getRemoteAddr().toString();
		String trxId = TrxId.getTrxId();
		String logId =  trxId + "| ";
		EnquireSMSsByDatesResponse response = new EnquireSMSsByDatesResponse();
		smsLogger.info(
				logId + "Received new inquireByDate request from IP: " + ip + " ,IP form header is: " + ipAddress);
		int returnStatus;
		// validate Request
		request.setPlainToString(plainData);

		int valid = validateRequestWithDatesOnly(request);
            switch (valid) {
                case 0:
			try {
				if (smsLogger.isDebugEnabled()) {
					String encyrptedRequest=encyrptionUtil.encrypt(request.toString());
					smsLogger.debug(logId + "Valid request{" + encyrptedRequest + "}");
				}
				if (((boolean) Configs.CACHE_REQUEST.getValue()) & loadBalancer.isTotalOutage()) {
					response = SmsApiResponseGenerator.generateEnquireSMSsByDatesFakeResponse(request);
					smsLogger.info(logId + "Request cached, returning to the client with response " + response);
					return response;
				}

				String smsServiceURI = (String) Configs.WEB_METHOD_TO_GET_SMSWithinRange_URI.getValue();
//				if (smsLogger.isTraceEnabled())
//					smsLogger.trace(
//							logId + "Will call SMS API service URI:" + smsServiceURI + ", will send : " + request);
				EnquireSMSsByDatesDetailedRequest detailedRequest;
				if (ipAddress != null) {
					detailedRequest = new EnquireSMSsByDatesDetailedRequest(request, ipAddress, trxId);
				} else {
					detailedRequest = new EnquireSMSsByDatesDetailedRequest(request, ip, trxId);
				}
				// send request to the service
				ClientResponse cr = httpClient.sendHttpXmlRequest(smsServiceURI, detailedRequest, "SMS");

				if (cr != null) {
					returnStatus = cr.getClientResponseStatus().getStatusCode();
					response = cr.getEntity(EnquireSMSsByDatesResponse.class);
					if (smsLogger.isDebugEnabled())
						smsLogger.debug(logId + response);

					smsLogger.info(logId + "Returned from SMS API service with status = " + returnStatus);
				} else {
					smsLogger.error(logId + "Client response is : " + cr);
					appErrorManagerAdapter.raiseError(request.logId(), trxId, AppErrors.FAILED_TO_FORWARD_REQUEST,
							"Invalid client response");
					response.setResultStatus(ResultStatus.GENERIC_ERROR);
					response.setDescription("Unable to inquire about the SMSs by date");
				}
			} catch (Exception e) {
				smsLogger.error("Unhandled Exception in inquireByDate " + e.getMessage());
				appLogger.error(logId + "Unhandled Exception in inquireByDate", e);
				response.setResultStatus(ResultStatus.GENERIC_ERROR);
				response.setDescription("Unable to inquire about the SMSs by date");
				return response;

			}
                    break;
                case 1:
			String encyrptedRequest=encyrptionUtil.encrypt(request.toString());
			smsLogger.warn(logId + "Invalid request{" + encyrptedRequest + "}");
			appErrorManagerAdapter.raiseError(request.logId(), trxId, AppErrors.INVALID_REQUEST, "Received invalid request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription("Invalid request, please ensure values of dates");
			return response;
                default:
            encyrptedRequest=encyrptionUtil.encrypt(request.toString());
			smsLogger.warn(logId + "Invalid request{" + encyrptedRequest + "}");
			appErrorManagerAdapter.raiseError(request.logId(), trxId, AppErrors.INVALID_REQUEST,
					"Received invalid request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription("Invalid request, please ensure there is no null or empty fields.");
			return response;
		}
		if (smsLogger.isTraceEnabled()) {
			smsLogger.trace(logId + "Returning to the client with " + response);
		}
		return response;
	}

	private ClientResponse sendHttpRequest(Object request, Entity selectedEntity, String path, String logId) {
		String smsServiceURI = selectedEntity.getEntityData() + path;
//		smsLogger.info(logId + "Will call SMSAPI service");
//
//		if (smsLogger.isTraceEnabled()) {
//			smsLogger.trace(logId + "SMSAPI service URI: " + smsServiceURI + ", will send : " + request);
//		}

		ClientResponse cr = httpClient.sendHttpXmlRequest(smsServiceURI, request, "SMS");

		if (smsLogger.isTraceEnabled()) {
			smsLogger.trace(logId + "SMS API service replay with response : " + cr);
		}

		if (cr != null && cr.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			smsLogger.warn(logId + "ClientResponse.Status : " + cr.getStatus());
			return null;
		}

		return cr;
	}
}
