package dev.oxoo2a.sim4da;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;

public class Tracer {
    
    public Tracer ( String name, boolean ordered, boolean enableTracing, boolean useLog4j2, PrintStream alternativeDestination ) {
        this.name = name;
        this.ordered = ordered;
        this.silent = !enableTracing;
        this.useLog4j2 = useLog4j2;
        this.alternativeDestination = alternativeDestination;

        log4j2Logger = LogManager.getRootLogger();
    }

    public void comment ( String s ) {
        if (silent) return;
        if (useLog4j2) {
            log4j2Logger.info(name+": "+s);
        }
        if (alternativeDestination != null) {
            alternativeDestination.println(s);
        }
    }

    private final String name;
    private final boolean ordered;
    private final boolean silent;
    private final boolean useLog4j2;
    private final PrintStream alternativeDestination;
    private final Logger log4j2Logger;
}
