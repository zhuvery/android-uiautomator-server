package com.github.uiautomator.stub;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Instrumentation;
import android.app.UiAutomation;

import com.android.uiautomator.core.UiDevice; //旧的UiDevices

import java.lang.reflect.Field;

public class FakeInstrument extends Instrumentation {
    private UiAutomation mUiAutomation = null;

    private Object uiAutomatorBridge;

    private UiDevice uiDevice = null;

    public FakeInstrument(UiDevice paramUiDevice) {
        this.uiDevice = paramUiDevice;
    }

    private UiAutomation reflectUiAutomation() {
        UiAutomation uiAutomation2 = null;
        UiAutomation uiAutomation1 = null;
        this.uiAutomatorBridge = ReflectWrapper.getValue("mUiAutomationBridge", this.uiDevice);
        Field[] arrayOfField = this.uiAutomatorBridge.getClass().getSuperclass().getDeclaredFields();
        String str = null;
        Log.d("Find UiAutomationBridge");
        int j = arrayOfField.length;
        int i = 0;
        while (true) {
            Field field = null;
            String str1 = str;
            if (i < j) {
                Field field1 = arrayOfField[i];
                field1.setAccessible(true);
                str1 = "";
                try {
                    String str2 = field1.get(this.uiAutomatorBridge).getClass().getCanonicalName();
                    str1 = str2;
                } catch (IllegalArgumentException illegalArgumentException) {
                    illegalArgumentException.printStackTrace();
                } catch (IllegalAccessException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                }
                Log.d(str1 + "  " + field1.getName());
                if (str1.equals("android.app.UiAutomation")) {
                    Log.d("Find android.app.UiAutomation");
                    field = field1;
                } else {
                    i++;
                    continue;
                }
            }
            if (field != null) {
                field.setAccessible(true);
            }

            try {
                UiAutomation uiAutomation = (UiAutomation) field.get(this.uiAutomatorBridge);
                uiAutomation1 = uiAutomation;
                uiAutomation2 = uiAutomation;
                AccessibilityServiceInfo accessibilityServiceInfo = uiAutomation.getServiceInfo();
                uiAutomation1 = uiAutomation;
                uiAutomation2 = uiAutomation;
                accessibilityServiceInfo.eventTypes ^= 0x401000;
                uiAutomation1 = uiAutomation;
                uiAutomation2 = uiAutomation;
                Log.d("Accessibility eventTypes is " + accessibilityServiceInfo.eventTypes);
                uiAutomation1 = uiAutomation;
                uiAutomation2 = uiAutomation;
                accessibilityServiceInfo.flags |= 0x2;
                uiAutomation1 = uiAutomation;
                uiAutomation2 = uiAutomation;
                accessibilityServiceInfo.flags |= 0x10;
                uiAutomation1 = uiAutomation;
                uiAutomation2 = uiAutomation;
                Log.d("Accessibility Flag change to " + accessibilityServiceInfo.flags);
                uiAutomation1 = uiAutomation;
                uiAutomation2 = uiAutomation;
                uiAutomation.setServiceInfo(accessibilityServiceInfo);
                return uiAutomation;
            } catch (IllegalArgumentException illegalArgumentException) {
                illegalArgumentException.printStackTrace();
                return uiAutomation1;
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
                return uiAutomation2;
            }
        }
    }

    public UiAutomation getUiAutomation() {
        if (this.mUiAutomation == null)
            this.mUiAutomation = reflectUiAutomation();
        return this.mUiAutomation;
    }

    public UiAutomation getUiAutomation(int paramInt) {
        if (this.mUiAutomation == null)
            this.mUiAutomation = reflectUiAutomation();
        return this.mUiAutomation;
    }
}
