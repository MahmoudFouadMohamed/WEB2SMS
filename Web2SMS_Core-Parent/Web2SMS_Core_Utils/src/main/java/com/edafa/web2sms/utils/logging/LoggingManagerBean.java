package com.edafa.web2sms.utils.logging;

import java.util.Iterator;
import java.util.Map;

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
import com.edafa.web2sms.utils.logging.interfaces.LoggingManagerLocal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Session Bean implementation class LoggingManagerBean
 */
@Singleton(name = "Web2SMS-Core_Utils/LoggingManagerBean")
@Startup
@DependsOn("Web2SMS-Core_Utils/ConfigsManagerBean")
public class LoggingManagerBean implements LoggingManagerLocal, ConfigsListener {
    Logger appLogger = LogManager.getLogger(LoggersEnum.WEB2SMS_APP_UTILS.name());

    @EJB(beanName = "Web2SMS-Core_Utils/ConfigsManagerBean")
    ConfigsManagerBeanLocal configsManager;

    private String logDirName;
    private final String extension = ".log";
    private final String logLevelConf = "LOG_LEVEL";
    private final String logDatePattern = ".%d{yyyy-MM-dd}";
    // private final String logDatePattern = "'.'yyyy-MM-dd";
    private final Level defaultLogLevel = Level.TRACE;
    
    private java.util.logging.Logger logger = java.util.logging.Logger.getAnonymousLogger();

    @PostConstruct
    @Override
    public void init() {

        try {
            logDirName = AppSettings.BaseDir.getEnvEntryValue() + (String) Configs.LOG_DIR_NAME.getValue();
            System.out.println("Initialize System Loggers .................");

//            LoggersEnum[] loggersEnums = LoggersEnum.values();
//
//            for (LoggersEnum loggerEnum : loggersEnums) {
//                initLogging(loggerEnum);
//            }
            configurationRefreshed();

            System.out.println("All loggers have been initialized ...............");
        } catch (Exception e) {
            e.printStackTrace();
        }

        configsManager.registerConfigsListener(this);
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
        PatternLayout patternLayout = PatternLayout.newBuilder().withPattern(layout).build();  // (PatternLayout.SIMPLE_CONVERSION_PATTERN, config, null, null,null, null);
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

    }

    @PreDestroy
    public void close() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = ctx.getConfiguration();
        // close appenders
        for (LoggersEnum loggerEnum : LoggersEnum.values()) {
            try {
                configuration.getAppender(loggerEnum.name() + "_appender").stop();
            } catch (Exception e) {
                logger.log(java.util.logging.Level.SEVERE, "ERROR during close appender : {0}_appender, message: {1}", new Object[]{loggerEnum.name(), e.getMessage()});
            }
        }
       LogManager.shutdown();
    }

    // @Override
    public void updateLoggersConfigs(Map<LoggersEnum, String> newLogLevels) {
        Iterator<Map.Entry<LoggersEnum, String>> newLogLevelPair = newLogLevels.entrySet().iterator();

        while (newLogLevelPair.hasNext()) {
            Map.Entry<LoggersEnum, String> entry = newLogLevelPair.next();

            Configurator.setLevel(entry.getKey().name(), Level.toLevel(entry.getValue()));
        }
    }

    private Level getLogLevelName(LoggersEnum loggerEnum) {
        String levelName = null;
        Level level;
        Configs conf = null;
        try {
            conf = Configs.getConfig(loggerEnum.getModule(), logLevelConf);
        } catch (Exception e) {
            System.err.println("Cannot get " + loggerEnum.getModule().getName() + " module log level configuration ["
                    + e.getCause().getMessage() + "], will use default log level: " + defaultLogLevel);
        }

        if (conf != null) {
            try {
                levelName = (String) conf.getValue();
                level = Level.toLevel(levelName);
            } catch (Exception e) {
                level = defaultLogLevel;
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
        String logMsg;
        LoggersEnum[] loggersEnums = LoggersEnum.values();
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = ctx.getConfiguration();

        for (LoggersEnum loggerEnum : loggersEnums) {
            Level level = getLogLevelName(loggerEnum);
            Level oldLevel = configuration.getLoggerConfig(loggerEnum.name()).getLevel();
            if (!level.equals(oldLevel)) {
                logMsg = "Updated log level for " + loggerEnum.name() + " from module " + loggerEnum.getModule().name()
                        + " from " + oldLevel + " to " + level;
                System.out.println(logMsg);
                appLogger.info(logMsg);
                Configurator.setLevel(loggerEnum.name(), level);
            }
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
