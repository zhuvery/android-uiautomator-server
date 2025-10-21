package com.github.uiautomator.stub;

import android.media.AudioManager;
import android.media.SoundPool;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.view.accessibility.AccessibilityNodeInfo;

import com.github.uiautomator.exceptions.UiAutomator2Exception;
import com.github.uiautomator.nuiautomator.NDevices;
import com.github.uiautomator.tools.ReflectionUtils;
import com.github.uiautomator.tools.XMLHierarchy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class TestServiceImpl implements TestService {
    private final SoundPool soundPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 0);

    private NDevices nDevices = null;

    public TestServiceImpl(NDevices ndevices) {
        Log.d("constructor TestServiceImpl");
        this.nDevices = ndevices;
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
        return DeviceInfo.getDeviceInfo(this.nDevices.getU1UiDevices());
    }

    @Override
    public String dumpWindowHierarchy() {
        return dumpWindowHierarchy(true, 50);
    }

    @Override
    public String dumpWindowHierarchy(boolean compressed) {
        return dumpWindowHierarchy(compressed, 50);
    }

    private String commonDumpWindowHierarchy(boolean compressed, int maxDepth, AccessibilityNodeInfo[] accessibilityNodeInfos) {
        ReflectionUtils.clearAccessibilityCache();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            AccessibilityNodeInfoDumper.dumpWindowHierarchy(accessibilityNodeInfos, os, maxDepth);
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

    // 这里是把当前界面所有的树都dump出来了
    @Override
    public String dumpAllWindowHierarchy(boolean compressed) {
        // 默认层数是50层
        return this.commonDumpWindowHierarchy(compressed, 50, XMLHierarchy.getRootAccessibilityNode());
    }


    @Override
    public String dumpWindowHierarchy(boolean compressed, int maxDepth) {

        return this.commonDumpWindowHierarchy(compressed, maxDepth, XMLHierarchy.getCurstomRootAccessibilityNode());
    }

    public String getText(Selector paramSelector) throws UiObjectNotFoundException {
        return NDevices.getInstance().getText(paramSelector);
    }

    public String testApi() {
        return "test api";
    }

}
