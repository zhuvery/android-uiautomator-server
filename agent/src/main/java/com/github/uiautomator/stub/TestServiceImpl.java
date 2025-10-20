package com.github.uiautomator.stub;

import android.media.AudioManager;
import android.media.SoundPool;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.support.test.uiautomator.UiObjectNotFoundException;

import com.github.uiautomator.exceptions.UiAutomator2Exception;
import com.github.uiautomator.tools.ReflectionUtils;
import com.github.uiautomator.tools.XMLHierarchy;
import com.github.uiautomator.stub.Device;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
//import android.support.test.uiautomator.UiDevice; //新的UiDevices

public class TestServiceImpl implements TestService {
    private final SoundPool soundPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 0);

    private com.android.uiautomator.core.UiDevice oldUiDevice;
    private android.support.test.uiautomator.UiDevice uiDevice;
    private Instrumentation instrumentation;
    private UiAutomation uiAutomation;

    public TestServiceImpl(com.android.uiautomator.core.UiDevice oldUiDevice, Instrumentation fakeInstrument) {
        Log.d("constructor TestServiceImpl");
        this.oldUiDevice = oldUiDevice;
        this.instrumentation = fakeInstrument;
        this.uiAutomation = this.instrumentation.getUiAutomation();
        this.uiDevice = android.support.test.uiautomator.UiDevice.getInstance(fakeInstrument);
        Device.getInstance().init(this.uiDevice, this.uiAutomation);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 1, 1, 1, 0, 1);
            }
        });
    }

    @Override
    public boolean playSound(String path) {
        try {
            soundPool.load(path, 1);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public String ping() {
        return "pong";
    }

    @Override
    public DeviceInfo deviceInfo() {
        return DeviceInfo.getDeviceInfo(this.oldUiDevice);
    }

    @Override
    public String dumpWindowHierarchy() {
        return dumpWindowHierarchy(true, 50);
    }

    @Override
    public String dumpWindowHierarchy(boolean compressed) {
        return dumpWindowHierarchy(compressed, 50);
    }

    @Override
    public String dumpWindowHierarchy(boolean compressed, int maxDepth) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            AccessibilityNodeInfoDumper.dumpWindowHierarchy(XMLHierarchy.getRootAccessibilityNode(), os, this.oldUiDevice, maxDepth);
            return os.toString("UTF-8");
        } catch (Exception e) {
            Log.d("dumpWindowHierarchy got Exception: " + e);
            throw new UiAutomator2Exception(e);
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                Log.d("dumpWindowHierarchy got Exception: " + e + "but ignore it");
                // ignore
            }
        }
    }

    public String getText(Selector paramSelector) throws UiObjectNotFoundException {
        return this.uiDevice.findObject(paramSelector.toUiSelector()).getText();
    }

}
