package com.github.uiautomator.stub;

import android.app.Instrumentation;

public class FakeInstrumentationRegistry {
    private static Instrumentation instrumentation = null;

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public static void setInstrumentation(Instrumentation paramInstrumentation) {
        instrumentation = paramInstrumentation;
    }
}
