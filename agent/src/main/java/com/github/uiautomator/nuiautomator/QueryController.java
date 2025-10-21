package com.github.uiautomator.nuiautomator;


import android.app.UiAutomation;
import android.os.Build;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import com.github.uiautomator.stub.FakeInstrumentationRegistry;
import com.github.uiautomator.stub.ReflectWrapper;
import com.github.uiautomator.exceptions.UiAutomator2Exception;
import com.github.uiautomator.tools.ReflectionUtils;
import com.github.uiautomator.stub.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class QueryController {
    private static final String CLASS_QUERY_CONTROLLER = "android.support.test.uiautomator.QueryController";

    private static final String METHOD_FIND_ACCESSIBILITY_NODE = "findAccessibilityNodeInfo";

    private static final String METHOD_FIND_NODE_IN_ROOT = "translateCompoundSelector";

    private static final String METHOD_GET_ACCESSIBILITY_ROOT_NODE = "getRootNode";

    private final Object queryController;

    private UiAutomation uiautomation = FakeInstrumentationRegistry.getInstrumentation().getUiAutomation();

    public QueryController(Object paramObject) {
        this.queryController = paramObject;
    }

    public AccessibilityNodeInfo findAccessibilityNode(UiSelector paramUiSelector) throws UiAutomator2Exception {
        return (AccessibilityNodeInfo) ReflectionUtils.invoke(ReflectionUtils.method("android.support.test.uiautomator.QueryController", "findAccessibilityNodeInfo", new Class[]{UiSelector.class}), this.queryController, new Object[]{paramUiSelector});
    }

    public AccessibilityNodeInfo findNodeInRoot(UiSelector paramUiSelector, AccessibilityNodeInfo paramAccessibilityNodeInfo) throws UiAutomator2Exception {
        return (paramAccessibilityNodeInfo == null) ? null : (AccessibilityNodeInfo) ReflectionUtils.invoke(ReflectionUtils.method("android.support.test.uiautomator.QueryController", "translateCompoundSelector", new Class[]{UiSelector.class, AccessibilityNodeInfo.class, boolean.class}), this.queryController, new Object[]{paramUiSelector, paramAccessibilityNodeInfo, Boolean.valueOf(false)});
    }

    public AccessibilityNodeInfo getAccessibilityRootNode() throws UiAutomator2Exception {
        Log.d("start getAccessibilityRootNode...");
        long timeout = 100; // 100ms
        int b = "REL".equals(Build.VERSION.CODENAME) ? 0 : 1;
        if (b + Build.VERSION.SDK_INT <= 23) {
            Log.d("Low Android Version, use long timeout");
            timeout = 1000; // 1s
        }
        int retryTime = 0;
        AccessibilityNodeInfo accessibilityNodeInfo = null;
        while (retryTime < 2) {
            try {
                FutureTask<AccessibilityNodeInfo> futureTask = new FutureTask<>(new Callable<AccessibilityNodeInfo>() {
                    public AccessibilityNodeInfo call() throws Exception {
                        return (AccessibilityNodeInfo) ReflectionUtils.invoke(ReflectionUtils.method("android.support.test.uiautomator.QueryController", "getRootNode"), QueryController.this.queryController);
                    }
                });
                Thread thread = new Thread(futureTask);
                try {
                    thread.start();
                    accessibilityNodeInfo = futureTask.get(timeout, TimeUnit.MILLISECONDS);
                } catch (Exception exception1) {
                    Log.e(retryTime + ": Thread getRootNode error|" + exception1.toString());
                }
            } catch (Exception exception) {
                Log.e(retryTime + ": getRootNode error|" + exception.toString());
            }
            if (accessibilityNodeInfo != null) {
                return accessibilityNodeInfo;
            }
            if (retryTime == 0) {
                Log.d("Reconnect UiAutomation...");
                ReflectWrapper.invoke(ReflectWrapper.getMethod(this.uiautomation.getClass(), "disconnect", new Class[0]), this.uiautomation);
                ReflectWrapper.invoke(ReflectWrapper.getMethod(this.uiautomation.getClass(), "connect", new Class[0]), this.uiautomation);
                Log.d("Reconnect UiAutomation Complete!!!");
            }
            retryTime++;
        }
        throw new UiAutomator2Exception("Dump Timeout");
    }
}

