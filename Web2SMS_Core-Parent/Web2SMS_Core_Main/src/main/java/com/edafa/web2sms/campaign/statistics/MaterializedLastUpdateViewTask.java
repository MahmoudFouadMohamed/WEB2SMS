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
public class MaterializedLastUpdateViewTask implements CampaignStatisticsTaskRemote {

	private static final long serialVersionUID = -197452449846506559L;

	private static final String CALL_UPDATE_COLLECTION_STATS_MV = "BEGIN DBMS_SNAPSHOT.REFRESH('COLLECTION_STATS_LU_MV'); END;";

	private Logger appLogger = LogManager.getLogger(LoggersEnum.WEB2SMS_APP_UTILS.name());

	@Resource(lookup = "jdbc/smsgw")
	private DataSource ds;

	@Override
	public ScheduledTaskStatus execute(ScheduledTask arg0) {
		ScheduledTaskStatus status = ScheduledTaskStatus.SUCCESS;

		try {
			appLogger.error("MaterializedLastUpdateViewTask get connection");
			Connection connection = ds.getConnection();

			appLogger.error("MaterializedLastUpdateViewTask create CALL_UPDATE_COLLECTION_STATS_MV statement");
			CallableStatement updateCollectionStatsMvCallableStatement = connection
					.prepareCall(CALL_UPDATE_COLLECTION_STATS_MV);

			appLogger.error("MaterializedLastUpdateViewTask execute CALL_UPDATE_COLLECTION_STATS_MV");
			updateCollectionStatsMvCallableStatement.execute();
			updateCollectionStatsMvCallableStatement.close();

			appLogger.error("MaterializedLastUpdateViewTask close connection");
			connection.close();
		} catch (SQLException e) {
			appLogger.error("MaterializedLastUpdateViewTask SQLException:" + e.getMessage(), e);

			status = ScheduledTaskStatus.FAILED;
		} catch (RuntimeException e) {
			appLogger.error("MaterializedLastUpdateViewTask Exception:" + e.getMessage(), e);

			status = ScheduledTaskStatus.FAILED;
		}

		return status;
	}

}
