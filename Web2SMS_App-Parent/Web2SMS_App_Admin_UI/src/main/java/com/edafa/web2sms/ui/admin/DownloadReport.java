package com.edafa.web2sms.ui.admin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.clients.XmlFileHttpClient;
import com.edafa.web2sms.service.enums.ResponseStatus;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.FileTokenResult;
import com.edafa.web2sms.ui.models.AdminReportRequest;
import com.edafa.web2sms.ui.util.WSClients;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.sun.jersey.api.client.ClientResponse;

/**
 * Servlet implementation class DownloadReport
 */
@WebServlet("/DownloadReport")
public class DownloadReport extends HttpServlet {

	private final Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI
			.name());

	InputStream input;
	// UserTrxInfo userInfo = new UserTrxInfo();
	DownloadedFileInfo result;
	@EJB
	XmlFileHttpClient xmlClient;

	@EJB
	WSClients servicePort;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadReport() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String reportToken = "";
		try {
			PrintWriter out = null;
			HttpSession session = request.getSession();
			reportToken = (String) session.getAttribute("Token");
			AdminTrxInfo trxInfo = (AdminTrxInfo) session
					.getAttribute("adminTrxInfo");

			logger.info("Get report service port, to download detailed report of file="
					+ reportToken);
			exportReport(trxInfo, reportToken, response);
		} catch (Exception e) {
			logger.error("Error while exporting campaign id=" + reportToken, e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	public void exportReport(AdminTrxInfo userInfo, String token,
			HttpServletResponse response) throws IOException {
		OutputStream output = null;
		try {
			System.out.println("url: " + (String) Configs.DOWNLOAD_REPORT_SERVER_LINK.getValue());

			result = downloadFile(userInfo.getTrxId(), token,
					(String) Configs.DOWNLOAD_REPORT_SERVER_LINK.getValue());
			System.out.println("result;" + result);

			input = result.getFileInputStream();
			System.out.println("input;" + input);
			logger.info(logTrxId(userInfo.getTrxId())
					+ "file token contains the file name is successfully returned "
					+ token);
			response.setHeader("Content-Type", "application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ result.getFileName() + "\"");
			output = response.getOutputStream();
			int octet;
			while ((octet = input.read()) != -1) {
				output.write(octet);
			}
			input.close();
			output.flush();
			output.close();
			logger.info(logTrxId(userInfo.getTrxId())
					+ "Done exporting report for file " + result.getFileName());
		} catch (Exception e) {
			e.printStackTrace();
			input.close();
			output.flush();
			output.close();
			logger.error(
					logTrxId(userInfo.getTrxId())
							+ "Error while exporting report file="
							+ result.getFileName(), e);
		}
	}

	public DownloadedFileInfo downloadFile(String trxId, String fileName,
			String url) throws IOException {
		DownloadedFileInfo result;
		AdminReportRequest request = new AdminReportRequest();
		request.setFileToken(fileName);
		request.setTrx(trxId);
		ClientResponse response = xmlClient.sendHttpXmlRequest(url, request);
		InputStream input = response.getEntityInputStream();
		if (response.getStatus() != 200 || input == null)
			throw new FileNotFoundException();
		result = new DownloadedFileInfo((String) response.getHeaders()
				.get("Content-Disposition").get(0).split("=")[1], 0,
				response.getEntityInputStream());
		return result;
	}

	public String logTrxId(String trxId) {
		StringBuilder sb = new StringBuilder();
		sb.append("AdminTrx");
		sb.append("(");
		sb.append(trxId);
		sb.append("): ");
		return sb.toString();
	}
}
