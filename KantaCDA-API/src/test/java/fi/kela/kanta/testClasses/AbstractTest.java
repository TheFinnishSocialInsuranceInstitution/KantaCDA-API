package fi.kela.kanta.testClasses;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.Before;

public abstract class AbstractTest {

    @Before
    public void setUp() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration conf = ctx.getConfiguration();
        conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.DEBUG);
        ctx.updateLoggers(conf);
    }

}
