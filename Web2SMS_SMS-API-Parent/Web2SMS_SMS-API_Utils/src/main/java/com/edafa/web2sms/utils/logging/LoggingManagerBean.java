package com.edafa.web2sms.utils.logging;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.edafa.web2sms.utils.configs.enums.AppSettings;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.configs.enums.ModulesEnum;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsListener;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsManagerBeanLocal;
import com.edafa.web2sms.utils.logging.interfaces.LoggingManagerLocal;
import java.util.Map;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * Session Bean implementation class LoggingManagerBean
 */
@Singleton
@Startup
@LocalBean
@DependsOn("ConfigsManagerBean")
public class LoggingManagerBean implements LoggingManagerLocal, ConfigsListener {

    @EJB(beanName = "ConfigsManagerBean")
    ConfigsManagerBeanLocal configsManager;

    private String logDirName;
    private final String extension = ".log";
    private final String logLevelConf = "LOG_LEVEL";
    private final String logDatePattern = ".%d{yyyy-MM-dd}";
    private final Level defaultLogLevel = Level.TRACE;
    
    private java.util.logging.Logger logger = java.util.logging.Logger.getAnonymousLogger();
    
    @PostConstruct
    public void init() {
        configsManager.registerConfigsListener(this);
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

//        //---------- Init the Context configuration
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
    }

    private Level getLogLevelName(LoggersEnum loggerEnum) {
        String levelName = null;
        Level level;
        Configs conf = null;
        try {
            conf = Configs.getConfig(loggerEnum.getModule(), logLevelConf);
        } catch (Exception e) {
            //TODO .. invalid log msg.
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

    @PreDestroy
    @Override
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
        Configurator.shutdown(ctx);
    }

    @Override
    public void configurationRefreshed() {
        LoggersEnum[] loggersEnums = LoggersEnum.values();
        for (LoggersEnum loggerEnum : loggersEnums) {
            Level level = getLogLevelName(loggerEnum);
            Configurator.setLevel(loggerEnum.name(), level);
        }
    }

    @Override
    public void configurationRefreshed(ModulesEnum modulesEnum) {
        configurationRefreshed();
    }

    @Override
    public void resetLoggerFiles() {
    }

    @Override
    public void refreshLoggers() {
        configurationRefreshed();
    }

    @Override
    public void updateLoggersConfigs(Map<LoggersEnum, String> newLogLevels) {
        LoggersEnum[] loggersEnums = LoggersEnum.values();
        for (LoggersEnum loggerEnum : loggersEnums) {
            String levelS = newLogLevels.get(loggerEnum);
            Level level = Level.toLevel(levelS);
            Configurator.setLevel(loggerEnum.name(), level);
        }
    }
}
