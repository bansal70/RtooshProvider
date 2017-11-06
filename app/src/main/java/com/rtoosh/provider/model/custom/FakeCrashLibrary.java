package com.rtoosh.provider.model.custom;

/** Not a real crash reporting library! */
public final class FakeCrashLibrary {
    public static void log(int priority, String tag, String message) {
        // TODO add log entry to circular buffer.
    }

    static void logWarning(Throwable t) {
        // TODO report non-fatal warning.
    }

    static void logError(Throwable t) {
        // TODO report non-fatal error.
    }

    private FakeCrashLibrary() {
        throw new AssertionError("No instances.");
    }
}