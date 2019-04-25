package com.edafa.web2sms.campaign.statistics;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.jee.scheduler.api.ScheduledTask;
import com.edafa.jee.scheduler.model.ScheduledTaskStatus;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

@Stateless
public class CampaignStatisticsTask implements CampaignStatisticsTaskRemote {

	private static final long serialVersionUID = 553678241828927822L;

	private static final String CALL_UPDATE_COLLECTION_STATS = "{call update_collection_stats()}";

	private Logger appLogger = LogManager.getLogger(LoggersEnum.WEB2SMS_APP_UTILS.name());

	@Resource(lookup = "jdbc/smsgw")
	private DataSource ds;

	@Override
	public ScheduledTaskStatus execute(ScheduledTask arg0) {
		ScheduledTaskStatus status = ScheduledTaskStatus.SUCCESS;

		try {
			appLogger.error("CampaignStatisticsTask get connection");
			Connection connection = ds.getConnection();

			appLogger.error("CampaignStatisticsTask create CALL_UPDATE_COLLECTION_STATS statement");
			CallableStatement updateCollectionStatsTableCallableStatement = connection
					.prepareCall(CALL_UPDATE_COLLECTION_STATS);

			appLogger.error("CampaignStatisticsTask execute CALL_UPDATE_COLLECTION_STATS");
			updateCollectionStatsTableCallableStatement.execute();
			updateCollectionStatsTableCallableStatement.close();

			appLogger.error("CampaignStatisticsTask close connection");
			connection.close();
		} catch (SQLException e) {
			appLogger.error("CampaignStatisticsTask SQLException:" + e.getMessage(), e);

			status = ScheduledTaskStatus.FAILED;
		} catch (RuntimeException e) {
			appLogger.error("CampaignStatisticsTask Exception:" + e.getMessage(), e);

			status = ScheduledTaskStatus.FAILED;
		}

		return status;
	}

}
