package com.github.uiautomator.stub;

import com.github.uiautomator.stub.Log;

import android.support.test.uiautomator.UiDevice;
import android.app.UiAutomation;

public class Device {
    private UiAutomation uiAutomation = null;
    private UiDevice uiDevice = null;
    private static Device device = null;

    public static Device getInstance() {
        if (device == null) {
            Log.d("Creating a new device...");
            device = new Device();
        }
        return device;
    }

    public UiAutomation getUiAutomation() {
        return this.uiAutomation;
    }

    public UiDevice getUiDevice() {
        return this.uiDevice;
    }

    public void init(UiDevice paramUiDevice, UiAutomation paramUiAutomation) {
        Log.d("start init device class");
        this.uiDevice = paramUiDevice;
        this.uiAutomation = paramUiAutomation;
        //todo to fill property code here
        //edit by weihong
    }
}
