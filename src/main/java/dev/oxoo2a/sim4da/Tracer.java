package dev.oxoo2a.sim4da;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;

public class Tracer {
    
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final String name;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final boolean orderedTracing; //currently unused
    private final boolean useLog4j2;
    private final PrintStream alternativeTracingDestination;
    private final Logger log4j2Logger;
    
    public Tracer(String name, boolean orderedTracing, boolean useLog4j2,
                  PrintStream alternativeTracingDestination) {
        this.name = name;
        this.orderedTracing = orderedTracing;
        this.useLog4j2 = useLog4j2;
        this.alternativeTracingDestination = alternativeTracingDestination;
        log4j2Logger = LogManager.getFormatterLogger(name);
    }
    
    public void emit(String format, Object... args) {
        if (useLog4j2) log4j2Logger.trace(format, args);
        if (alternativeTracingDestination!=null) {
            alternativeTracingDestination.printf(format, args);
            alternativeTracingDestination.println();
        }
    }
}
