package com.github.uiautomator.tools;

import android.os.SystemClock;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.github.uiautomator.exceptions.UiAutomator2Exception;
import com.github.uiautomator.nuiautomator.NDevices;

import java.util.HashSet;

import com.github.uiautomator.nuiautomator.QueryController;
import com.github.uiautomator.stub.Log;

public class XMLHierarchy {
    public static AccessibilityNodeInfo[] getRootAccessibilityNode() throws UiAutomator2Exception {
        long l = SystemClock.uptimeMillis();
        HashSet<AccessibilityNodeInfo> hashSet = new HashSet<>();
        while (true) {
            for (AccessibilityWindowInfo accessibilityWindowInfo : NDevices.getInstance().getU2UiAutomation().getWindows()) {
                Log.d(String.format("current window: %s", new Object[]{accessibilityWindowInfo.toString()}));
                AccessibilityNodeInfo accessibilityNodeInfo = accessibilityWindowInfo.getRoot();
                if (accessibilityNodeInfo == null) {
                    Log.d(String.format("Skipping null root node for window: %s", new Object[]{accessibilityWindowInfo.toString()}));
                    continue;
                }
                hashSet.add(accessibilityNodeInfo);
            }
            if (!hashSet.isEmpty())
                return hashSet.toArray(new AccessibilityNodeInfo[0]);
            long l1 = l + 3000L - SystemClock.uptimeMillis();
            if (l1 < 0L)
                throw new UiAutomator2Exception(String.format("Timed out after %d milliseconds waiting for root AccessibilityNodeInfo", new Object[]{3000L}));
            SystemClock.sleep(Math.min(100L, l1));
        }
    }

    public static AccessibilityNodeInfo[] getCurstomRootAccessibilityNode() throws UiAutomator2Exception {
        HashSet<AccessibilityNodeInfo> hashSet = new HashSet<>();
        QueryController queryController = NDevices.getInstance().getQueryController();
        AccessibilityNodeInfo accessibilityNodeInfo = queryController.getAccessibilityRootNode();
        Log.d(String.format("current window: %s", new Object[]{accessibilityNodeInfo.toString()}));
        hashSet.add(accessibilityNodeInfo);
        return hashSet.toArray(new AccessibilityNodeInfo[0]);
    }
}
