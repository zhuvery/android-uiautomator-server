package com.github.uiautomator.tools;

import android.view.accessibility.AccessibilityNodeInfo;
import android.graphics.Rect;

import com.github.uiautomator.nuiautomator.NDevices;

public class AccessibilityNodeInfoHelper {
    public static Rect getVisibleBoundsInScreen(AccessibilityNodeInfo node, int width, int height) {
        if (node == null)
            return null;
        Rect nodeRect = new Rect();
        node.getBoundsInScreen(nodeRect);
        Rect displayRect = new Rect();
        displayRect.top = 0;
        displayRect.left = 0;
        displayRect.right = width;
        displayRect.bottom = height;
        nodeRect.intersect(displayRect);
        if (NDevices.getInstance().getApiLevelActual() >= 21) {
            Rect window = new Rect();
            if (node.getWindow() != null) {
                node.getWindow().getBoundsInScreen(window);
                nodeRect.intersect(window);
            }
        }
        return nodeRect;
    }
}
