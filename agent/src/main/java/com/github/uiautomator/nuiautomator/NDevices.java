package com.github.uiautomator.nuiautomator;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import android.graphics.Rect;

import com.github.uiautomator.stub.Selector;
import com.github.uiautomator.stub.FakeInstrument;
import com.github.uiautomator.stub.FakeInstrumentationRegistry;
import com.github.uiautomator.stub.Log;
import com.github.uiautomator.stub.TouchController;
import com.github.uiautomator.tools.ReflectionUtils;
import com.github.uiautomator.tools.AccessibilityNodeInfoHelper;

public class NDevices {

    private com.android.uiautomator.core.UiDevice u1UiDevice = null;
    private android.support.test.uiautomator.UiDevice u2UiDevice = null;

    private UiAutomation u2UiAutomation = null;

    private QueryController queryController = null;

    private InteractionController interactionController = null;

    private AccessibilityNodeInfo lastUiInfo = null;

    private TouchController touchController = null;

    private static NDevices device = null;

    private final int API_LEVEL_ACTUAL = Build.VERSION.SDK_INT + ("REL".equals(Build.VERSION.CODENAME) ? 0 : 1);

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
        this.queryController = new QueryController(ReflectionUtils.getField(android.support.test.uiautomator.UiDevice.class, "mQueryController", this.u2UiDevice));
        this.interactionController = new InteractionController(ReflectionUtils.getField(android.support.test.uiautomator.UiDevice.class, "mInteractionController", this.u2UiDevice));
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

    public int getApiLevelActual() {
        return this.API_LEVEL_ACTUAL;
    }

    private AccessibilityNodeInfo findObject(Selector selector) {
        Log.d("findObject with lastUiInfo");
        AccessibilityNodeInfo accessibilityNodeInfo;
        accessibilityNodeInfo = this.queryController.findNodeInRoot(selector.toUiSelector(), this.lastUiInfo);
        if (accessibilityNodeInfo != null) {
            return accessibilityNodeInfo;
        } else {
            Log.e("accessibilityNodeInfo is null|cannot getText");
            return null;
        }
    }

    public String getText(Selector selector) {
        try {
            if (this.lastUiInfo == null) {
                Log.d("cannot find lastUiInfo");
                return this.u2UiDevice.findObject(selector.toUiSelector()).getText();
            } else {
                Log.d("find lastUiInfo");
                AccessibilityNodeInfo accessibilityNodeInfo = this.findObject(selector);
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

    public boolean setText(Selector selector, String text) {
        if (this.lastUiInfo == null) {
            try {
                return this.u2UiDevice.findObject(selector.toUiSelector()).setText(text);
            } catch (Exception exception) {
                Log.e("setText method error:" + exception);
                return false;
            }
        } else {
            if (text == null) text = "";
            if (this.API_LEVEL_ACTUAL > 19) {
                AccessibilityNodeInfo node = this.findObject(selector);
                if (node == null) {
                    Log.e("cannot find selector to set null text");
                    return false;
                } else {
                    Bundle args = new Bundle();
                    args.putCharSequence("ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE", text);
                    return node.performAction(2097152, args);
                }
            }
            clearTextField(selector);
            return this.interactionController.sendText(text);
        }
    }

    public void clearTextField(Selector selector) {
        if (this.lastUiInfo == null) {
            try {
                this.u2UiDevice.findObject(selector.toUiSelector()).clearTextField();
            } catch (Exception exception) {
                Log.e("clearTextField method error:" + exception);
            }
        } else {
            Log.d("find lastUiInfo");
            AccessibilityNodeInfo node = this.findObject(selector);
            if (node != null) {
                CharSequence text = node.getText();
                if (text != null && text.length() > 0) if (this.API_LEVEL_ACTUAL > 19) {
                    this.setText(selector, "");
                } else {
                    Bundle selectionArgs = new Bundle();
                    selectionArgs.putInt("ACTION_ARGUMENT_SELECTION_START_INT", 0);
                    selectionArgs.putInt("ACTION_ARGUMENT_SELECTION_END_INT", text.length());
                    boolean ret = node.performAction(1);
                    if (!ret) Log.e("ACTION_FOCUS on text field failed.");
                    ret = node.performAction(131072, selectionArgs);
                    if (!ret) Log.e("ACTION_SET_SELECTION on text field failed.");
                    this.interactionController.sendKey(67, 0);
                }
            }
        }
    }

    private AccessibilityNodeInfo getScrollableParent(AccessibilityNodeInfo node) {
        AccessibilityNodeInfo parent = node;
        while (parent != null) {
            parent = parent.getParent();
            if (parent != null && parent.isScrollable()) return parent;
        }
        return null;
    }

    private Rect getVisibleBounds(AccessibilityNodeInfo node) {
        if (node == null) return null;
        int w = this.u1UiDevice.getDisplayWidth();
        int h = this.u1UiDevice.getDisplayHeight();
        Rect nodeRect = AccessibilityNodeInfoHelper.getVisibleBoundsInScreen(node, w, h);
        AccessibilityNodeInfo scrollableParentNode = this.getScrollableParent(node);
        if (scrollableParentNode == null) return nodeRect;
        Rect parentRect = AccessibilityNodeInfoHelper.getVisibleBoundsInScreen(scrollableParentNode, w, h);
        nodeRect.intersect(parentRect);
        return nodeRect;
    }

    public boolean clickBottomRight(AccessibilityNodeInfo node) {
        if (node == null) {
            Log.d("cannot find node");
            return false;
        }
        Rect rect = this.getVisibleBounds(node);
        return this.clickBottomRight(rect);
    }

    public boolean clickTopLeft(AccessibilityNodeInfo node) {
        if (node == null) {
            Log.d("cannot find node");
            return false;
        }
        Rect rect = this.getVisibleBounds(node);
        return this.clickTopLeft(rect);
    }

    public boolean click(AccessibilityNodeInfo node) {
        if (node == null) {
            Log.d("cannot find node");
            return false;
        }
        Rect rect = this.getVisibleBounds(node);
        return this.click(rect);
    }

    public boolean click(Rect rect) {
        return this.interactionController.clickNoSync(rect.centerX(), rect.centerY());
    }

    public boolean clickTopLeft(Rect rect) {
        return this.interactionController.clickNoSync(rect.left + 5, rect.top + 5);
    }

    public boolean clickBottomRight(Rect rect) {
        return this.interactionController.clickNoSync(rect.right - 5, rect.bottom - 5);
    }

    public boolean click(Selector selector, String corner) {
        android.support.test.uiautomator.UiObject obj = null;
        AccessibilityNodeInfo node = null;
        Rect rect = null;
        if (this.lastUiInfo == null) {
            obj = this.u2UiDevice.findObject(selector.toUiSelector());
            try {
                rect = obj.getBounds();
            } catch (Exception e) {
                Log.e("cannot find node");
            }
        } else {
            node = this.findObject(selector);
        }
        if (corner == null) corner = "center";
        corner = corner.toLowerCase();
        try {
            switch (corner) {
                case "br":
                case "bottomright":
                    return rect != null ? this.clickBottomRight(rect) : this.clickBottomRight(node);
                case "tl":
                case "topleft":
                    return rect != null ? this.clickTopLeft(rect) : this.clickTopLeft(node);
                case "c":
                case "center":
                    return rect != null ? this.click(rect) : this.click(node);
            }
        } catch (Exception e) {
            Log.e("click error:" + e);
        }
        return false;
    }
}
