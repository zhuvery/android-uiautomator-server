package com.github.uiautomator.nuiautomator;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import android.graphics.Rect;
import android.view.MotionEvent;


import com.github.uiautomator.stub.ObjInfo;
import com.github.uiautomator.stub.Point;
import com.github.uiautomator.stub.Selector;
import com.github.uiautomator.stub.FakeInstrument;
import com.github.uiautomator.stub.FakeInstrumentationRegistry;
import com.github.uiautomator.stub.Log;
import com.github.uiautomator.stub.TouchController;
import com.github.uiautomator.tools.ReflectionUtils;
import com.github.uiautomator.tools.AccessibilityNodeInfoHelper;

import android.support.test.uiautomator.Until;
import android.support.test.uiautomator.UiSelector;

import java.util.Objects;

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

    public AccessibilityNodeInfo findObject(Selector selector) {
        Log.d("use lastUiInfo to find node");
        AccessibilityNodeInfo accessibilityNodeInfo;
        accessibilityNodeInfo = this.queryController.findNodeInRoot(selector.toUiSelector(), this.lastUiInfo);
        if (accessibilityNodeInfo != null) {
            return accessibilityNodeInfo;
        } else {
            Log.e("accessibilityNodeInfo is null");
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

    public Rect getVisibleBounds(AccessibilityNodeInfo node) {
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

    public boolean longClickBottomRight(AccessibilityNodeInfo node) {
        if (node == null) {
            Log.d("cannot find node");
            return false;
        }
        Rect rect = this.getVisibleBounds(node);
        return this.longClickBottomRight(rect);
    }

    public boolean longClickTopLeft(AccessibilityNodeInfo node) {
        if (node == null) {
            Log.d("cannot find node");
            return false;
        }
        Rect rect = this.getVisibleBounds(node);
        return this.longClickTopLeft(rect);
    }

    public boolean longClick(AccessibilityNodeInfo node) {
        if (node == null) {
            Log.d("cannot find node");
            return false;
        }
        Rect rect = this.getVisibleBounds(node);
        return this.longClick(rect);
    }

    public boolean longClick(Rect rect) {
        return this.interactionController.longClickNoSync(rect.centerX(), rect.centerY());
    }

    public boolean longClickTopLeft(Rect rect) {
        return this.interactionController.longClickNoSync(rect.left + 5, rect.top + 5);
    }

    public boolean longClickBottomRight(Rect rect) {
        return this.interactionController.longClickNoSync(rect.right - 5, rect.bottom - 5);
    }

    public boolean longClick(Selector selector, String corner) {
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
                    return rect != null ? this.longClickBottomRight(rect) : this.longClickBottomRight(node);
                case "tl":
                case "topleft":
                    return rect != null ? this.longClickTopLeft(rect) : this.longClickTopLeft(node);
                case "c":
                case "center":
                    return rect != null ? this.longClick(rect) : this.longClick(node);
            }
        } catch (Exception e) {
            Log.e("click error:" + e);
        }
        return false;
    }

    public boolean dragTo(Rect srcRect, Rect dstRect, int steps) {
        return this.interactionController.swipe(srcRect.centerX(), srcRect.centerY(), dstRect.centerX(), dstRect.centerY(), steps, true);
    }

    public boolean dragTo(Rect srcRect, int destX, int destY, int steps) {
        return this.interactionController.swipe(srcRect.centerX(), srcRect.centerY(), destX, destY, steps, true);
    }

    public boolean dragTo(AccessibilityNodeInfo srcNode, AccessibilityNodeInfo dstNode, int steps) {
        if (srcNode == null || dstNode == null) {
            Log.d("cannot find node");
            return false;
        }
        Rect srcRect = this.getVisibleBounds(srcNode);
        Rect dstRect = this.getVisibleBounds(dstNode);
        return this.dragTo(srcRect, dstRect, steps);
    }

    public boolean dragTo(AccessibilityNodeInfo srcNode, int destX, int destY, int steps) {
        if (srcNode == null) {
            Log.d("cannot find node");
            return false;
        }
        Rect srcRect = this.getVisibleBounds(srcNode);
        return this.dragTo(srcRect, destX, destY, steps);
    }

    public boolean dragTo(Selector selector, Selector destSelector, int steps) {
        android.support.test.uiautomator.UiObject obj = null;
        android.support.test.uiautomator.UiObject destObj = null;
        AccessibilityNodeInfo node = null;
        AccessibilityNodeInfo desNode = null;
        Rect rect = null;
        Rect desRect = null;
        if (this.lastUiInfo == null) {
            obj = this.u2UiDevice.findObject(selector.toUiSelector());
            destObj = this.u2UiDevice.findObject(destSelector.toUiSelector());
            try {
                rect = obj.getBounds();
                desRect = destObj.getBounds();
            } catch (Exception e) {
                Log.e("cannot find node");
            }
        } else {
            node = this.findObject(selector);
            desNode = this.findObject(destSelector);
        }
        return rect != null && desRect != null ? this.dragTo(rect, desRect, steps) : this.dragTo(node, desNode, steps);
    }

    public boolean dragTo(Selector selector, int destX, int destY, int steps) {
        android.support.test.uiautomator.UiObject obj = null;
        AccessibilityNodeInfo node = null;
        Rect rect = null;
        Rect desRect = null;
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
        return rect != null ? this.dragTo(rect, destX, destY, steps) : this.dragTo(node, destX, destY, steps);
    }

    public boolean exist(Selector selector) {
        if (this.lastUiInfo == null) {
            if ((selector.getChildOrSibling()).length == 0 && selector.toBySelector() != null) {
                return ((Boolean) this.u2UiDevice.wait(Until.hasObject(selector.toBySelector()), 0L)).booleanValue();
            }
            return this.u2UiDevice.findObject(selector.toUiSelector()).exists();
        } else {
            AccessibilityNodeInfo node = this.findObject(selector);
            return node != null;
        }
    }

    public ObjInfo objInfo(Selector obj) {
        if (this.lastUiInfo == null) {
            try {
                return ObjInfo.getObjInfo(this.u2UiDevice.findObject(obj.toUiSelector()));
            } catch (Exception e) {
                Log.e("ObjInfo.getObjInfo error: " + e);
                return null;
            }
        } else {
            AccessibilityNodeInfo node = this.findObject(obj);
            if (node == null) {
                Log.e("objInfo error, node is null");
                return null;
            } else {
                try {
                    return ObjInfo.getObjInfo(node);
                } catch (Exception e) {
                    Log.e("AccessibilityNodeInfo ObjInfo.getObjInfo error: " + e);
                    return null;
                }
            }

        }
    }

    public int count(Selector obj) {
        if (this.lastUiInfo == null) {
            if ((obj.deepSelector().getMask() & Selector.MASK_INSTANCE) > 0) {
                if (this.u2UiDevice.findObject(obj.toUiSelector()).exists()) return 1;
                else return 0;
            } else {
                UiSelector sel = obj.toUiSelector();
                if (!this.u2UiDevice.findObject(sel).exists()) return 0;
                int low = 1;
                int high = 2;

                // Note: can not use `sel = sel.instance(high -1)`
                // because this will change first selector in chain not last.
                sel = obj.toUiSelector(high - 1);
                while (this.u2UiDevice.findObject(sel).exists()) {
                    low = high;
                    high = high * 2;
                    sel = obj.toUiSelector(high - 1);
                }
                while (high > low + 1) {
                    int mid = (low + high) / 2;
                    sel = obj.toUiSelector(mid - 1);
                    if (this.u2UiDevice.findObject(sel).exists()) low = mid;
                    else high = mid;
                }
                return low;
            }
        } else {
            if ((obj.deepSelector().getMask() & Selector.MASK_INSTANCE) > 0) {
                AccessibilityNodeInfo node = this.findObject(obj);
                if (node != null)
                    return 1;
                else return 0;
            } else {
                if (this.findObject(obj) == null)
                    return 0;
                int oldInstance = obj.getInstance();
                long oldMask = obj.getMask();
                int low = 1;
                int high = 2;
                Selector sel = obj.toSelector(high - 1);
                while (this.findObject(sel) != null) {
                    low = high;
                    high = high * 2;
                    sel = obj.toSelector(high - 1);
                }
                while (high > low + 1) {
                    int mid = (low + high) / 2;
                    sel = obj.toSelector(mid - 1);
                    if (this.findObject(sel) != null) low = mid;
                    else high = mid;
                }
                obj.toSelector(oldInstance, oldMask);
                return low;
            }
        }
    }

    public ObjInfo[] objInfoOfAllInstances(Selector obj) {
        int total = count(obj);
        ObjInfo[] objects = new ObjInfo[total];
        if (this.lastUiInfo == null) {
            if ((obj.getMask() & Selector.MASK_INSTANCE) > 0 && total > 0) {
                try {
                    objects[0] = objInfo(obj);
                } catch (Exception e) {
                    Log.e("UiObjectNotFoundException1:" + e);
                }
            } else {
                UiSelector sel = obj.toUiSelector();
                for (int i = 0; i < total; i++) {
                    try {
                        objects[i] = ObjInfo.getObjInfo(this.u2UiDevice.findObject(sel.instance(i)));
                        Log.d("3");
                    } catch (Exception e) {
                        Log.e("UiObjectNotFoundException2:" + i + ":" + e);
                    }
                }
            }
        } else {
            int oldInstance = obj.getInstance();
            long oldMask = obj.getMask();
            for (int i = 0; i < total; i++) {
                try {
                    Selector sel = obj.toSelector(i);
                    AccessibilityNodeInfo node = this.findObject(sel);
                    if (node != null) {
                        objects[i] = ObjInfo.getObjInfo(node);
                    } else {
                        objects[i] = null;
                    }
                } catch (Exception e) {
                    Log.e("UiObjectNotFoundException2:" + i + ":" + e);
                }
            }
            obj.toSelector(oldInstance, oldMask);
        }
        return objects;
    }

    public boolean performTwoPointerGesture(android.graphics.Point startPoint1, android.graphics.Point startPoint2, android.graphics.Point endPoint1, android.graphics.Point endPoint2, int steps) {
        if (steps == 0)
            steps = 1;
        float stepX1 = ((float) (endPoint1.x - startPoint1.x) / steps);
        float stepY1 = ((float) (endPoint1.y - startPoint1.y) / steps);
        float stepX2 = ((float) (endPoint2.x - startPoint2.x) / steps);
        float stepY2 = ((float) (endPoint2.y - startPoint2.y) / steps);
        int eventX1 = startPoint1.x;
        int eventY1 = startPoint1.y;
        int eventX2 = startPoint2.x;
        int eventY2 = startPoint2.y;
        MotionEvent.PointerCoords[] points1 = new MotionEvent.PointerCoords[steps + 2];
        MotionEvent.PointerCoords[] points2 = new MotionEvent.PointerCoords[steps + 2];
        for (int i = 0; i < steps + 1; i++) {
            MotionEvent.PointerCoords pointerCoords1 = new MotionEvent.PointerCoords();
            pointerCoords1.x = eventX1;
            pointerCoords1.y = eventY1;
            pointerCoords1.pressure = 1.0F;
            pointerCoords1.size = 1.0F;
            points1[i] = pointerCoords1;
            MotionEvent.PointerCoords pointerCoords2 = new MotionEvent.PointerCoords();
            pointerCoords2.x = eventX2;
            pointerCoords2.y = eventY2;
            pointerCoords2.pressure = 1.0F;
            pointerCoords2.size = 1.0F;
            points2[i] = pointerCoords2;
            eventX1 = (int) (eventX1 + stepX1);
            eventY1 = (int) (eventY1 + stepY1);
            eventX2 = (int) (eventX2 + stepX2);
            eventY2 = (int) (eventY2 + stepY2);
        }
        MotionEvent.PointerCoords p1 = new MotionEvent.PointerCoords();
        p1.x = endPoint1.x;
        p1.y = endPoint1.y;
        p1.pressure = 1.0F;
        p1.size = 1.0F;
        points1[steps + 1] = p1;
        MotionEvent.PointerCoords p2 = new MotionEvent.PointerCoords();
        p2.x = endPoint2.x;
        p2.y = endPoint2.y;
        p2.pressure = 1.0F;
        p2.size = 1.0F;
        points2[steps + 1] = p2;
        return this.performMultiPointerGesture(new MotionEvent.PointerCoords[][]{points1, points2});
    }

    public boolean performMultiPointerGesture(MotionEvent.PointerCoords[]... touches) {
        return this.interactionController.performMultiPointerGesture(touches);
    }

    public boolean gesture(Selector obj, Point startPoint1, Point startPoint2, Point endPoint1, Point endPoint2, int steps) {
        if (this.lastUiInfo == null) {
            return this.u2UiDevice.findObject(obj.toUiSelector()).performTwoPointerGesture(startPoint1.toPoint(),
                    startPoint2.toPoint(),
                    endPoint1.toPoint(),
                    endPoint2.toPoint(),
                    steps);
        } else {
            AccessibilityNodeInfo node = this.findObject(obj);
            if (node != null) {
                return this.performTwoPointerGesture(
                        startPoint1.toPoint(),
                        startPoint2.toPoint(),
                        endPoint1.toPoint(),
                        endPoint2.toPoint(), steps);
            } else {
                Log.e("cannot find node");
                return false;
            }
        }
    }

    public boolean pinch(Selector obj, int percent, int steps, String corner) {
        if (this.lastUiInfo == null) {
            return this.pinch(new com.android.uiautomator.core.UiObject(obj.toU1UiSelector()), percent, steps, corner);
        } else {
            percent = (percent < 0) ? 0 : (Math.min(percent, 100));
            float percentage = percent / 100.0F;
            AccessibilityNodeInfo node = this.findObject(obj);
            if (node == null) {
                Log.e("cannot find node");
                return false;
            }
            Rect rect = this.getVisibleBounds(node);
            if (rect.width() <= 40)
                throw new IllegalStateException("Object width is too small for operation");
            android.graphics.Point startPoint1 = new android.graphics.Point(rect.centerX() - (int) (((float) rect.width() / 2) * percentage), rect.centerY());
            android.graphics.Point startPoint2 = new android.graphics.Point(rect.centerX() + (int) (((float) rect.width() / 2) * percentage), rect.centerY());
            android.graphics.Point endPoint1 = new android.graphics.Point(rect.centerX() - 20, rect.centerY());
            android.graphics.Point endPoint2 = new android.graphics.Point(rect.centerX() + 20, rect.centerY());
            if (Objects.equals(corner, "in")) {
                return this.performTwoPointerGesture(startPoint1, startPoint2, endPoint1, endPoint2, steps);
            } else {
                return this.performTwoPointerGesture(endPoint1, endPoint2, startPoint1, startPoint2, steps);
            }

        }
    }

    private boolean pinch(com.android.uiautomator.core.UiObject obj, int percent, int steps, String corner) {
        try {
            if (Objects.equals(corner, "in")) {
                return obj.pinchIn(percent, steps);
            } else {
                return obj.pinchOut(percent, steps);
            }
        } catch (Exception e) {
            Log.e("pinchIn error:" + e);
        }
        return false;
    }
}
