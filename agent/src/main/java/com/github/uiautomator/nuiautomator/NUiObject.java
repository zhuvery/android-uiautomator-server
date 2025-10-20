package com.github.uiautomator.nuiautomator;

import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiSelector;

public class NUiObject extends UiObject {

    NUiObject(UiDevice device, UiSelector selector) {
        super(device, selector);
    }
}
