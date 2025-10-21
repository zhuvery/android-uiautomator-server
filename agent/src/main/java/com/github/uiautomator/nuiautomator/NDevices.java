package com.github.uiautomator.nuiautomator;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.view.accessibility.AccessibilityNodeInfo;


import com.github.uiautomator.stub.Selector;
import com.github.uiautomator.stub.FakeInstrument;
import com.github.uiautomator.stub.FakeInstrumentationRegistry;
import com.github.uiautomator.stub.Log;
import com.github.uiautomator.nuiautomator.QueryController;
import com.github.uiautomator.stub.TouchController;
import com.github.uiautomator.tools.ReflectionUtils;

import android.support.test.uiautomator.UiSelector;

public class NDevices {

    private com.android.uiautomator.core.UiDevice u1UiDevice = null;
    private android.support.test.uiautomator.UiDevice u2UiDevice = null;

    private UiAutomation u2UiAutomation = null;

    private QueryController queryController = null;

    private AccessibilityNodeInfo lastUiInfo = null;

    private TouchController touchController = null;

    private static NDevices device = null;

    public static NDevices getInstance() {
        if (device == null) {
            Log.d("Creating a new NDevices...");
            device = new NDevices();
        }
        return device;
    }

    public NDevices() {
        Log.d("need to execute init to instantiation member later");
    }

    public void init(com.android.uiautomator.core.UiDevice u1UiDevice) {
        Instrumentation instrumentation = new FakeInstrument(u1UiDevice);
        FakeInstrumentationRegistry.setInstrumentation(instrumentation);
        this.touchController = new TouchController(instrumentation);
        this.u1UiDevice = u1UiDevice;
        this.u2UiDevice = android.support.test.uiautomator.UiDevice.getInstance(instrumentation);
        this.u2UiAutomation = instrumentation.getUiAutomation();
        this.queryController = new QueryController(
                ReflectionUtils.getField(
                        android.support.test.uiautomator.UiDevice.class,
                        "mQueryController", this.u2UiDevice
                ));
    }

    public void refreshUI(AccessibilityNodeInfo accessibilityNodeInfo) {
        this.lastUiInfo = accessibilityNodeInfo;
    }

    public com.android.uiautomator.core.UiDevice getU1UiDevices() {
        return this.u1UiDevice;
    }

    public android.support.test.uiautomator.UiDevice getU2UiDevices() {
        return this.u2UiDevice;
    }

    public TouchController getTouchController() {
        return this.touchController;
    }

    public UiAutomation getU2UiAutomation() {
        return this.u2UiAutomation;
    }

    public QueryController getQueryController() {
        return this.queryController;
    }

    public String getText(Selector selector) {
        try {
            if (this.lastUiInfo == null) {
                Log.d("cannot find lastUiInfo");
                return this.u2UiDevice.findObject(selector.toUiSelector()).getText();
            } else {
                Log.d("find lastUiInfo");
                AccessibilityNodeInfo accessibilityNodeInfo;
                accessibilityNodeInfo = this.queryController.findNodeInRoot(
                        selector.toUiSelector(),
                        this.lastUiInfo);
                if (accessibilityNodeInfo != null) {
                    return accessibilityNodeInfo.getText().toString();
                } else {
                    Log.e("accessibilityNodeInfo is null|cannot getText");
                    return "";
                }

            }
        } catch (Exception e) {
            Log.e("getText exception|" + e);
        }
        return "";
    }
}
