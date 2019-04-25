package com.edafa.web2sms.utils.configs.logging;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.edafa.web2sms.utils.configs.enums.AppSettings;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.configs.enums.ModulesEnum;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsListener;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsManagerBeanLocal;
import com.edafa.web2sms.utils.configs.logging.interfaces.LoggingManagerLocal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Session Bean implementation class LoggingManagerBean
 */
@Singleton
@Startup
@DependsOn("ConfigsManagerBean")
public class LoggingManagerBean implements LoggingManagerLocal, ConfigsListener {

    @EJB
    ConfigsManagerBeanLocal configsManager;
    private String logDirName;
    private final String extension = ".log";
    private final String logLevelConf = "LOG_LEVEL";
    private final String logDatePattern = ".%d{yyyy-MM-dd}";

//    private final String pattern = "yyyy-MM-dd_HH-mm-ss";
    private final Level defaultLogLevel = Level.TRACE;

    //private java.util.logging.Logger logger = java.util.logging.Logger.getAnonymousLogger();
    Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

    @PostConstruct
    @Override
    public void init() {
        configsManager.registerConfigsListener(this);

        try {
            //
//            String serverNameSystem = System.getProperty("weblogic.Name");
//			System.out.println("serverNameSystem: " + serverNameSystem);
//			if (serverNameSystem.equals("Web2SMS-SMS-API_mServer")) {
//				logDirName = AppSettings.APPSMSBaseDir.getEnvEntryValue() + (String) Configs.LOG_DIR_NAME.getValue();
//				
//			} else {
//				logDirName = AppSettings.BaseDir.getEnvEntryValue() + (String) Configs.LOG_DIR_NAME.getValue();
//			}
//			System.out.println("LogDirName: " + logDirName);
//            //
        	String serverNameSystem = System.getProperty("weblogic.Name");
//			System.out.println("serverNameSystem      : " + serverNameSystem);
//			System.out.println("AppServerBaseDir      : " + Configs.AppServerBaseDir.getValue().toString());
//			System.out.println("ReportingServerBaseDir: " + Configs.ReportingServerBaseDir.getValue());

            if (serverNameSystem.equals("Web2SMS-APP_Reporting_mServer")) {
                AppSettings.BaseDir.setEnvEntryValue(Configs.ReportingServerBaseDir.getValue().toString());
            } else if (serverNameSystem.equals("Web2SMS-SMS-API_mServer")) {
                // @May
//                logDirName = AppSettings.APPSMSBaseDir.getEnvEntryValue() + (String) Configs.LOG_DIR_NAME.getValue();
                //TODO add smsapi base dir to configration 
                AppSettings.BaseDir.setEnvEntryValue(Configs.AppSMSAPIServerBaseDir.getValue().toString());
            }
            else {
                    AppSettings.BaseDir.setEnvEntryValue(Configs.AppServerBaseDir.getValue().toString());
                }
            logDirName = AppSettings.BaseDir.getEnvEntryValue() + (String) Configs.LOG_DIR_NAME.getValue();

            System.out.println("Initialize System Loggers .................");

//            LoggersEnum[] loggersEnums = LoggersEnum.values();
//
//            for (LoggersEnum loggerEnum : loggersEnums) {
//                appLogger.debug("Init logger " + loggerEnum.name());
//                initLogging(loggerEnum);
//            }
            configurationRefreshed();

            appLogger.info("All loggers have been initialized ...............");
        } catch (Exception e) {
            appLogger.error("Failed to init loggers");
        }
    }

    private void initLogging(LoggersEnum loggerEnum) {
        String loggerName = loggerEnum.name();
        String appenderName = loggerName + "_appender";
        String logFileName = logDirName + loggerEnum.getLogFileName() + extension;
        Level level = getLogLevelName(loggerEnum);
        String logLayoutInfo = (String) Configs.LOG_LAYOUT_INFO.getValue();
        String logLayoutDebug = (String) Configs.LOG_LAYOUT_DEBUG.getValue();

        String layout;
        if ((level == Level.DEBUG) || (level == Level.TRACE)) {
            layout = logLayoutDebug;
        } else {
            layout = logLayoutInfo;
        }

        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        //---------- Create the AppenderComponentBuilder
        PatternLayout patternLayout = PatternLayout.newBuilder().withPattern(layout).build();
        RollingRandomAccessFileAppender appender = RollingRandomAccessFileAppender.createAppender(logFileName, logFileName + logDatePattern, "true", appenderName, null, null, TimeBasedTriggeringPolicy.createPolicy(null, null), null, patternLayout, null, null, null, null, config);
        appender.start();
        config.addAppender(appender);
        //--------------------------
        AppenderRef ref = AppenderRef.createAppenderRef(appenderName, null, null);
        AppenderRef[] refs = new AppenderRef[]{ref};
        LoggerConfig loggerConfig = AsyncLoggerConfig.createLogger(false, level, loggerName, null, refs, null, config, null);
        loggerConfig.addAppender(appender, null, null);
        config.addLogger(loggerName, loggerConfig);
        //--------------------------
        ctx.updateLoggers();
        appLogger.info("Success init logger " + loggerName + " with level " + level + " to log at file " + logFileName);

    }

    @PreDestroy
    // @Override
    public void close() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = ctx.getConfiguration();
        // close appenders
        try {
            for (LoggersEnum loggerEnum : LoggersEnum.values()) {
                if (loggerEnum != LoggersEnum.APP_UTILS) {
                    appLogger.info("Closing appender " + loggerEnum.name() + "_appender");
                    // configuration.getLoggerConfig(loggerEnum.name()).stop();
                    configuration.getAppender(loggerEnum.name() + "_appender").stop();
                    appLogger.info("Success close appender " + loggerEnum.name() + "_appender");
                }
            }
            appLogger.info("Closing appender " + LoggersEnum.APP_UTILS.name() + "_appender");
            configuration.getLoggerConfig(LoggersEnum.APP_UTILS.name()).stop();
            configuration.getAppender(LoggersEnum.APP_UTILS.name() + "_appender").stop();
            appLogger.info("Success close appender " + LoggersEnum.APP_UTILS.name() + "_appender");
        } catch (Exception e) {
            appLogger.error("Failed to colse appenders");
        }
        Configurator.shutdown(ctx);
    }

    private Level getLogLevelName(LoggersEnum loggerEnum) {
        String levelName = null;
        Level level;
        Configs conf = null;
        try {
            // conf = configDao.findBykey(logLevelConf,
            // loggerEnum.getModule().getName());
            conf = Configs.getConfig(loggerEnum.getModule(), logLevelConf);

            // } catch (DBException dbe) {
            // System.err.println("Cannot get " +
            // loggerEnum.getModule().getName()
            // + " module log level configuration from database [" +
            // dbe.getCause().getMessage()
            // + "], will use default log level: " + defaultLogLevel);
        } catch (Exception e) {
            System.err.println("Cannot get " + loggerEnum.getModule().getName() + " module log level configuration ["
                    + e.getMessage() + "], will use default log level: " + defaultLogLevel);
        }

        if (conf != null) {
            try {
                levelName = (String) conf.getValue();
                level = Level.toLevel(levelName);
            } catch (Exception e) {
                level = defaultLogLevel;
                System.err.println("Cannot get " + loggerEnum.getModule().getName()
                        + " module log level configuration [" + e.getMessage() + "], will use default log level: "
                        + defaultLogLevel);
            }
        } else {
            System.err.println("Didn't find " + loggerEnum.getModule().getName()
                    + " module log level configuration in database, will use default log level: " + defaultLogLevel);
            level = defaultLogLevel;
        }
        return level;
    }

    @Override
    public void configurationRefreshed() {
        LoggersEnum[] loggersEnums = LoggersEnum.values();
        for (LoggersEnum loggerEnum : loggersEnums) {
            Level level = getLogLevelName(loggerEnum);
            Configurator.setLevel(loggerEnum.name(), level);
            appLogger.info("Success update logger " + loggerEnum.name() + " with level " + level);
        }
    }

    @Override
    public void configurationRefreshed(ModulesEnum modulesEnum) {
        configurationRefreshed();
    }

    @Override
    public void resetLoggerFiles() {
    }
}
