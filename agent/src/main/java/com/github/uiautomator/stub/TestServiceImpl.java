package com.github.uiautomator.stub;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.github.uiautomator.exceptions.NotImplementedException;
import com.github.uiautomator.exceptions.UiAutomator2Exception;
import com.github.uiautomator.nuiautomator.NDevices;
import com.github.uiautomator.tools.ReflectionUtils;
import com.github.uiautomator.tools.XMLHierarchy;
import com.sun.org.apache.bcel.internal.generic.PUSH;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;


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

    /**
     * Helper method used for debugging to dump the current window's layout hierarchy.
     *
     * @param compressed use compressed layout hierarchy or not using setCompressedLayoutHeirarchy method. Ignore the parameter in case the API level lt 18.
     * @return the absolute path name of dumped file.
     */
    @Override
    public String dumpWindowHierarchy(boolean compressed) {
        return dumpWindowHierarchy(compressed, 50);
    }

    private String commonDumpWindowHierarchy(boolean compressed, int maxDepth, AccessibilityNodeInfo[] accessibilityNodeInfos, Selector selector) {
        ReflectionUtils.clearAccessibilityCache();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            if (selector == null) {
                AccessibilityNodeInfoDumper.dumpWindowHierarchy(accessibilityNodeInfos, os, maxDepth);
            } else {
                AccessibilityNodeInfoDumper.dumpWindowHierarchy(accessibilityNodeInfos, os, maxDepth, selector);
            }
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

    @Override
    public String dumpWindowHierarchyWithSelector(boolean compressed, Selector selector) {
        return this.commonDumpWindowHierarchy(compressed, 50, XMLHierarchy.getRootAccessibilityNode(), selector);
    }

    // 这里是把当前界面所有的树都dump出来了
    @Override
    public String dumpAllWindowHierarchy(boolean compressed) {
        // 默认层数是50层
        return this.commonDumpWindowHierarchy(compressed, 50, XMLHierarchy.getRootAccessibilityNode(), null);
    }


    @Override
    public String dumpWindowHierarchy(boolean compressed, int maxDepth) {
        return this.commonDumpWindowHierarchy(compressed, maxDepth, XMLHierarchy.getCurstomRootAccessibilityNode(), null);
    }

    /**
     * Reads the text property of the UI element
     *
     * @param paramSelector the selector of the UiObject.
     * @return text value of the current node represented by this UiObject
     * @throws UiObjectNotFoundException
     */
    public String getText(Selector paramSelector) throws UiObjectNotFoundException {
        return this.nDevices.getText(paramSelector);
    }

    /**
     * Perform a click at arbitrary coordinates specified by the user.
     *
     * @param x coordinate
     * @param y coordinate
     * @return true if the click succeeded else false
     */
    @Override
    public boolean click(int x, int y) {
        if (x < 0 || y < 0) {
            return false;
        }
        this.nDevices.getTouchController().touchDown(x, y);
        SystemClock.sleep(100); // normally 100ms for click
        return this.nDevices.getTouchController().touchUp(x, y);
    }

    public boolean click(int x, int y, long milliseconds) {
        if (x < 0 || y < 0) {
            return false;
        }
        this.nDevices.getTouchController().touchDown(x, y);
        SystemClock.sleep(milliseconds);
        return this.nDevices.getTouchController().touchUp(x, y);
    }

    /**
     * Performs a swipe from one coordinate to another coordinate. You can control the smoothness and speed of the swipe by specifying the number of steps. Each step execution is throttled to 5 milliseconds per step, so for a 100 steps, the swipe will take around 0.5 seconds to complete.
     *
     * @param startX X-axis value for the starting coordinate
     * @param startY Y-axis value for the starting coordinate
     * @param endX   X-axis value for the ending coordinate
     * @param endY   Y-axis value for the ending coordinate
     * @param steps  is the number of steps for the swipe action
     * @return true if swipe is performed, false if the operation fails or the coordinates are invalid
     * @throws NotImplementedException
     */
    @Override
    public boolean drag(int startX, int startY, int endX, int endY, int steps) throws NotImplementedException {
        return this.nDevices.getU2UiDevices().drag(startX, startY, endX, endY, steps);
    }

    /**
     * Performs a swipe from one coordinate to another using the number of steps to determine smoothness and speed. Each step execution is throttled to 5ms per step. So for a 100 steps, the swipe will take about 1/2 second to complete.
     *
     * @param startX X-axis value for the starting coordinate
     * @param startY Y-axis value for the starting coordinate
     * @param endX   X-axis value for the ending coordinate
     * @param endY   Y-axis value for the ending coordinate
     * @param steps  is the number of move steps sent to the system
     * @return false if the operation fails or the coordinates are invalid
     */
    @Override
    public boolean swipe(int startX, int startY, int endX, int endY, int steps) {
        return this.nDevices.getU2UiDevices().swipe(startX, startY, endX, endY, steps);
    }

    @Override
    public boolean swipePoints(int[] segments, int segmentSteps) {
        android.graphics.Point[] points = new android.graphics.Point[segments.length / 2];
        for (int i = 0; i < segments.length / 2; i++) {
            points[i] = new android.graphics.Point(segments[2 * i], segments[2 * i + 1]);
        }
        return this.nDevices.getU2UiDevices().swipe(points, segmentSteps);
    }

    // Multi touch is a little complicated
    @Override
    public boolean injectInputEvent(int action, float x, float y, int metaState) {
        TouchController touchController = this.nDevices.getTouchController();
        switch (action) {
            case MotionEvent.ACTION_DOWN: // 0
                return touchController.touchDown(x, y);
            case MotionEvent.ACTION_MOVE: // 2
                return touchController.touchMove(x, y);
            case MotionEvent.ACTION_UP: // 1
                return touchController.touchUp(x, y);
            default:
                return false;
        }
    }

    /**
     * Take a screenshot of current window and store it as PNG The screenshot is adjusted per screen rotation
     *
     * @param filename where the PNG should be written to
     * @param scale    scale the screenshot down if needed; 1.0f for original size
     * @param quality  quality of the PNG compression; range: 0-100
     * @return the file name of the screenshot. null if failed.
     * @throws NotImplementedException
     */
    @Override
    public String takeScreenshot(String filename, float scale, int quality) throws NotImplementedException {
        // 截图文件都保存在/data/local/tmp中
        File f = new File("/data/local/tmp", filename);
        this.nDevices.getU2UiDevices().takeScreenshot(f, scale, quality);
        if (f.exists()) return f.getAbsolutePath();
        return null;
    }


    @Override
    public String takeScreenshot(float scale, int quality) throws NotImplementedException {
        Bitmap screenshot = this.nDevices.getU2UiAutomation().takeScreenshot();
        if (screenshot == null) {
            return null;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            screenshot.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (IOException ioe) {
            Log.e("takeScreenshot error: " + ioe);
            return null;
        } finally {
            try {
                bos.close();
            } catch (IOException ioe) {
                // Ignore
            }
            screenshot.recycle();
        }
    }

    /**
     * Disables the sensors and freezes the device rotation at its current rotation state, or enable it.
     *
     * @param freeze true to freeze the rotation, false to unfreeze the rotation.
     * @throws RemoteException
     */
    @Override
    public void freezeRotation(boolean freeze) throws RemoteException {
        if (freeze) this.nDevices.getU2UiDevices().freezeRotation();
        else this.nDevices.getU2UiDevices().unfreezeRotation();
    }

    /**
     * Simulates orienting the device to the left/right/natural and also freezes rotation by disabling the sensors.
     *
     * @param dir Left or l, Right or r, Natural or n, case insensitive
     * @throws RemoteException
     * @throws NotImplementedException
     */
    @Override
    public void setOrientation(String dir) throws RemoteException, NotImplementedException {
        dir = dir.toLowerCase();
        if ("left".equals(dir) || "l".equals(dir))
            this.nDevices.getU2UiDevices().setOrientationLeft();
        else if ("right".equals(dir) || "r".equals(dir))
            this.nDevices.getU2UiDevices().setOrientationRight();
        else if ("natural".equals(dir) || "n".equals(dir))
            this.nDevices.getU2UiDevices().setOrientationNatural();
    }

    /**
     * Opens the notification shade.
     *
     * @return true if successful, else return false
     * @throws NotImplementedException
     */
    @Override
    public boolean openNotification() throws NotImplementedException {
        return this.nDevices.getU2UiDevices().openNotification();
    }

    /**
     * Opens the Quick Settings shade.
     *
     * @return true if successful, else return false
     * @throws NotImplementedException
     */
    @Override
    public boolean openQuickSettings() throws NotImplementedException {
        return this.nDevices.getU2UiDevices().openQuickSettings();
    }

    /**
     * Simulates a short press using key name.
     *
     * @param key possible key name is home, back, left, right, up, down, center, menu, search, enter, delete(or del), recent(recent apps), volume_up, volume_down, volume_mute, camera, power
     * @return true if successful, else return false
     * @throws RemoteException
     */
    @Override
    public boolean pressKey(String key) throws RemoteException {
        boolean result;
        key = key.toLowerCase();
        if ("home".equals(key)) result = this.nDevices.getU2UiDevices().pressHome();
        else if ("back".equals(key)) result = this.nDevices.getU2UiDevices().pressBack();
        else if ("left".equals(key)) result = this.nDevices.getU2UiDevices().pressDPadLeft();
        else if ("right".equals(key)) result = this.nDevices.getU2UiDevices().pressDPadRight();
        else if ("up".equals(key)) result = this.nDevices.getU2UiDevices().pressDPadUp();
        else if ("down".equals(key)) result = this.nDevices.getU2UiDevices().pressDPadDown();
        else if ("center".equals(key)) result = this.nDevices.getU2UiDevices().pressDPadCenter();
        else if ("menu".equals(key)) result = this.nDevices.getU2UiDevices().pressMenu();
        else if ("search".equals(key)) result = this.nDevices.getU2UiDevices().pressSearch();
        else if ("enter".equals(key)) result = this.nDevices.getU2UiDevices().pressEnter();
        else if ("delete".equals(key) || "del".equals(key))
            result = this.nDevices.getU2UiDevices().pressDelete();
        else if ("recent".equals(key)) result = this.nDevices.getU2UiDevices().pressRecentApps();
        else if ("volume_up".equals(key))
            result = this.nDevices.getU2UiDevices().pressKeyCode(KeyEvent.KEYCODE_VOLUME_UP);
        else if ("volume_down".equals(key))
            result = this.nDevices.getU2UiDevices().pressKeyCode(KeyEvent.KEYCODE_VOLUME_DOWN);
        else if ("volume_mute".equals(key))
            result = this.nDevices.getU2UiDevices().pressKeyCode(KeyEvent.KEYCODE_VOLUME_MUTE);
        else if ("camera".equals(key))
            result = this.nDevices.getU2UiDevices().pressKeyCode(KeyEvent.KEYCODE_CAMERA);
        else
            result = "power".equals(key) && this.nDevices.getU2UiDevices().pressKeyCode(KeyEvent.KEYCODE_POWER);
        return result;
    }

    /**
     * Simulates a short press using a key code. See KeyEvent.
     *
     * @param keyCode the key code of the event.
     * @return true if successful, else return false
     */
    @Override
    public boolean pressKeyCode(int keyCode) {
        return this.nDevices.getU2UiDevices().pressKeyCode(keyCode);
    }

    /**
     * Simulates a short press using a key code. See KeyEvent.
     *
     * @param keyCode   the key code of the event.
     * @param metaState an integer in which each bit set to 1 represents a pressed meta key
     * @return true if successful, else return false
     */
    @Override
    public boolean pressKeyCode(int keyCode, int metaState) {
        return this.nDevices.getU2UiDevices().pressKeyCode(keyCode, metaState);
    }

    /**
     * This method simulates pressing the power button if the screen is OFF else it does nothing if the screen is already ON. If the screen was OFF and it just got turned ON, this method will insert a 500ms delay to allow the device time to wake up and accept input.
     *
     * @throws RemoteException
     */
    @Override
    public void wakeUp() throws RemoteException {
        this.nDevices.getU1UiDevices().wakeUp();
    }

    /**
     * This method simply presses the power button if the screen is ON else it does nothing if the screen is already OFF.
     *
     * @throws RemoteException
     */
    @Override
    public void sleep() throws RemoteException {
        this.nDevices.getU1UiDevices().sleep();
    }

    /**
     * Checks the power manager if the screen is ON.
     *
     * @return true if the screen is ON else false
     * @throws RemoteException
     */
    @Override
    public boolean isScreenOn() throws RemoteException {
        return this.nDevices.getU1UiDevices().isScreenOn();
    }

    @Override
    public void clearTextField(Selector obj) throws UiObjectNotFoundException {
        this.nDevices.clearTextField(obj);
    }

    /**
     * Sets the text in an editable field, after clearing the field's content. The UiSelector selector of this object must reference a UI element that is editable. When you call this method, the method first simulates a click() on editable field to set focus. The method then clears the field's contents and injects your specified text into the field. If you want to capture the original contents of the field, call getText() first. You can then modify the text and use this method to update the field.
     *
     * @param obj  the selector of the UiObject.
     * @param text string to set
     * @return true if operation is successful
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean setText(Selector obj, String text) throws UiObjectNotFoundException {
        return this.nDevices.setText(obj, text);
    }

    /**
     * Performs a click at the center of the visible bounds of the UI element represented by this UiObject.
     *
     * @param obj the target ui object.
     * @return true id successful else false
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean click(Selector obj) throws UiObjectNotFoundException {
        return this.nDevices.click(obj, "c");
    }

    /**
     * Clicks the bottom and right corner or top and left corner of the UI element
     *
     * @param obj    the target ui object.
     * @param corner "br"/"bottomright" means BottomRight, "tl"/"topleft" means TopLeft, "center" means Center.
     * @return true on success
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean click(Selector obj, String corner) throws UiObjectNotFoundException {
        return this.nDevices.click(obj, corner);
    }

    /**
     * Long clicks the center of the visible bounds of the UI element
     *
     * @param obj the target ui object.
     * @return true if operation was successful
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean longClick(Selector obj) throws UiObjectNotFoundException {
        return this.nDevices.longClick(obj, "c");
    }

    /**
     * Long clicks bottom and right corner of the UI element
     *
     * @param obj    the target ui object.
     * @param corner "br"/"bottomright" means BottomRight, "tl"/"topleft" means TopLeft, "center" means Center.
     * @return true if operation was successful
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean longClick(Selector obj, String corner) throws UiObjectNotFoundException {
        return this.nDevices.longClick(obj, corner);
    }

    /**
     * Drags this object to a destination UiObject. The number of steps specified in your input parameter can influence the drag speed, and varying speeds may impact the results. Consider evaluating different speeds when using this method in your tests.
     *
     * @param obj     the ui object to be dragged.
     * @param destObj the ui object to be dragged to.
     * @param steps   usually 40 steps. You can increase or decrease the steps to change the speed.
     * @return true if successful
     * @throws UiObjectNotFoundException
     * @throws NotImplementedException
     */
    @Override
    public boolean dragTo(Selector obj, Selector destObj, int steps) throws UiObjectNotFoundException, NotImplementedException {
        return this.nDevices.dragTo(obj, destObj, steps);
    }

    /**
     * Drags this object to arbitrary coordinates. The number of steps specified in your input parameter can influence the drag speed, and varying speeds may impact the results. Consider evaluating different speeds when using this method in your tests.
     *
     * @param obj   the ui object to be dragged.
     * @param destX the X-axis coordinate of destination.
     * @param destY the Y-axis coordinate of destination.
     * @param steps usually 40 steps. You can increase or decrease the steps to change the speed.
     * @return true if successful
     * @throws UiObjectNotFoundException
     * @throws NotImplementedException
     */
    @Override
    public boolean dragTo(Selector obj, int destX, int destY, int steps) throws UiObjectNotFoundException, NotImplementedException {
        return this.nDevices.dragTo(obj, destX, destY, steps);
    }

    /**
     * Check if view exists. This methods performs a waitForExists(long) with zero timeout. This basically returns immediately whether the view represented by this UiObject exists or not.
     *
     * @param obj the ui object.
     * @return true if the view represented by this UiObject does exist
     */
    @Override
    public boolean exist(Selector obj) {
        return this.nDevices.exist(obj);
    }

    /**
     * Get the object info.
     *
     * @param obj the target ui object.
     * @return object info.
     * @throws UiObjectNotFoundException
     */
    @Override
    public ObjInfo objInfo(Selector obj) throws UiObjectNotFoundException {
        return this.nDevices.objInfo(obj);
    }

    /**
     * Get the count of the UiObject instances by the selector
     *
     * @param obj the selector of the ui object
     * @return the count of instances.
     */
    @Override
    public int count(Selector obj) {
        return this.nDevices.count(obj);
    }

    /**
     * Get the info of all instance by the selector.
     *
     * @param obj the selector of ui object.
     * @return array of object info.
     */
    @Override
    public ObjInfo[] objInfoOfAllInstances(Selector obj) {
        return this.nDevices.objInfoOfAllInstances(obj);
    }

    /**
     * Generates a two-pointer gesture with arbitrary starting and ending points.
     *
     * @param obj         the target ui object. ??
     * @param startPoint1 start point of pointer 1
     * @param startPoint2 start point of pointer 2
     * @param endPoint1   end point of pointer 1
     * @param endPoint2   end point of pointer 2
     * @param steps       the number of steps for the gesture. Steps are injected about 5 milliseconds apart, so 100 steps may take around 0.5 seconds to complete.
     * @return true if all touch events for this gesture are injected successfully, false otherwise
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean gesture(Selector obj, Point startPoint1, Point startPoint2, Point endPoint1, Point endPoint2, int steps) throws UiObjectNotFoundException, NotImplementedException {
        return this.nDevices.gesture(obj, startPoint1, startPoint2, endPoint1, endPoint2, steps);
    }

    /**
     * Performs a two-pointer gesture, where each pointer moves diagonally toward the other, from the edges to the center of this UiObject .
     *
     * @param obj     the target ui object.
     * @param percent percentage of the object's diagonal length for the pinch gesture
     * @param steps   the number of steps for the gesture. Steps are injected about 5 milliseconds apart, so 100 steps may take around 0.5 seconds to complete.
     * @return true if all touch events for this gesture are injected successfully, false otherwise
     * @throws UiObjectNotFoundException
     * @throws NotImplementedException
     */
    @Override
    public boolean pinchIn(Selector obj, int percent, int steps) throws UiObjectNotFoundException, NotImplementedException {
        return this.nDevices.pinch(obj, percent, steps, "in");
    }

    /**
     * Performs a two-pointer gesture, where each pointer moves diagonally opposite across the other, from the center out towards the edges of the this UiObject.
     *
     * @param obj     the target ui object.
     * @param percent percentage of the object's diagonal length for the pinch gesture
     * @param steps   the number of steps for the gesture. Steps are injected about 5 milliseconds apart, so 100 steps may take around 0.5 seconds to complete.
     * @return true if all touch events for this gesture are injected successfully, false otherwise
     * @throws UiObjectNotFoundException
     * @throws NotImplementedException
     */
    @Override
    public boolean pinchOut(Selector obj, int percent, int steps) throws UiObjectNotFoundException, NotImplementedException {
        return this.nDevices.pinch(obj, percent, steps, "out");
    }

    /**
     * Performs the swipe up/down/left/right action on the UiObject
     *
     * @param obj   the target ui object.
     * @param dir   "u"/"up", "d"/"down", "l"/"left", "r"/"right"
     * @param steps indicates the number of injected move steps into the system. Steps are injected about 5ms apart. So a 100 steps may take about 1/2 second to complete.
     * @return true of successful
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean swipe(Selector obj, String dir, int steps) throws UiObjectNotFoundException {
        return this.nDevices.swipe(obj, dir, steps);
    }


    /**
     * Performs the swipe up/down/left/right action on the UiObject
     *
     * @param obj     the target ui object.
     * @param dir     "u"/"up", "d"/"down", "l"/"left", "r"/"right"
     * @param percent expect value: percent >= 0.0F && percent <= 1.0F,The length of the swipe as a percentage of this object's size.
     * @param steps   indicates the number of injected move steps into the system. Steps are injected about 5ms apart. So a 100 steps may take about 1/2 second to complete.
     * @return true of successful
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean swipe(Selector obj, String dir, float percent, int steps) throws UiObjectNotFoundException {
        return this.nDevices.swipe(obj, dir, percent, steps);
    }

    /**
     * Performs a backwards fling action with the default number of fling steps (5). If the swipe direction is set to vertical, then the swipe will be performed from top to bottom. If the swipe direction is set to horizontal, then the swipes will be performed from left to right. Make sure to take into account devices configured with right-to-left languages like Arabic and Hebrew.
     *
     * @param obj        the selector of the scrollable object
     * @param isVertical vertical or horizontal
     * @return true if scrolled, and false if can't scroll anymore
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean flingBackward(Selector obj, boolean isVertical) throws UiObjectNotFoundException {
        return this.nDevices.flingBackward(obj, isVertical);
    }

    @Override
    public boolean flingForward(Selector obj, boolean isVertical) throws UiObjectNotFoundException {
        return this.nDevices.flingForward(obj, isVertical);
    }

    @Override
    public boolean flingToBeginning(Selector obj, boolean isVertical, int maxSwipes) throws UiObjectNotFoundException {
        return this.nDevices.flingToBeginning(obj, isVertical, maxSwipes);
    }

    @Override
    public boolean flingToEnd(Selector obj, boolean isVertical, int maxSwipes) throws UiObjectNotFoundException {
        return this.nDevices.flingToEnd(obj, isVertical, maxSwipes);
    }

    /**
     * Performs a backward scroll. If the swipe direction is set to vertical, then the swipes will be performed from top to bottom. If the swipe direction is set to horizontal, then the swipes will be performed from left to right. Make sure to take into account devices configured with right-to-left languages like Arabic and Hebrew.
     *
     * @param obj        the selector of the scrollable object
     * @param isVertical vertical or horizontal
     * @param steps      number of steps. Use this to control the speed of the scroll action.
     * @return true if scrolled, false if can't scroll anymore
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean scrollBackward(Selector obj, boolean isVertical, int steps) throws UiObjectNotFoundException {
        return this.nDevices.scrollBackward(obj, isVertical, steps);
    }

    /**
     * Performs a forward scroll with the default number of scroll steps (55). If the swipe direction is set to vertical, then the swipes will be performed from bottom to top. If the swipe direction is set to horizontal, then the swipes will be performed from right to left. Make sure to take into account devices configured with right-to-left languages like Arabic and Hebrew.
     *
     * @param obj        the selector of the scrollable object
     * @param isVertical vertical or horizontal
     * @param steps      number of steps. Use this to control the speed of the scroll action.
     * @return true on scrolled, else false
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean scrollForward(Selector obj, boolean isVertical, int steps) throws UiObjectNotFoundException {
        return this.nDevices.scrollForward(obj, isVertical, steps);
    }

    /**
     * Scrolls to the beginning of a scrollable layout element. The beginning can be at the top-most edge in the case of vertical controls, or the left-most edge for horizontal controls. Make sure to take into account devices configured with right-to-left languages like Arabic and Hebrew.
     *
     * @param obj        the selector of the scrollable object
     * @param isVertical vertical or horizontal
     * @param maxSwipes  max swipes to be performed.
     * @param steps      use steps to control the speed, so that it may be a scroll, or fling
     * @return true on scrolled else false
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean scrollToBeginning(Selector obj, boolean isVertical, int maxSwipes, int steps) throws UiObjectNotFoundException {
        return this.nDevices.scrollToBeginning(obj, isVertical, maxSwipes, steps);
    }

    /**
     * Scrolls to the end of a scrollable layout element. The end can be at the bottom-most edge in the case of vertical controls, or the right-most edge for horizontal controls. Make sure to take into account devices configured with right-to-left languages like Arabic and Hebrew.
     *
     * @param obj        the selector of the scrollable object
     * @param isVertical vertical or horizontal
     * @param maxSwipes  max swipes to be performed.
     * @param steps      use steps to control the speed, so that it may be a scroll, or fling
     * @return true on scrolled, else false
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean scrollToEnd(Selector obj, boolean isVertical, int maxSwipes, int steps) throws UiObjectNotFoundException {
        return this.nDevices.scrollToEnd(obj, isVertical, maxSwipes, steps);
    }

    /**
     * Perform a scroll forward action to move through the scrollable layout element until a visible item that matches the selector is found.
     *
     * @param obj        the selector of the scrollable object
     * @param targetObj  the item matches the selector to be found.
     * @param isVertical vertical or horizontal
     * @return true on scrolled, else false
     * @throws UiObjectNotFoundException
     */
    @Override
    public boolean scrollTo(Selector obj, Selector targetObj, boolean isVertical) throws UiObjectNotFoundException {
        return this.nDevices.scrollTo(obj, targetObj, isVertical);
    }

    @Override
    public void testApi() {
        Log.d("test api111:" + this.nDevices.getU1UiDevices().getDisplayWidth());
        Log.d("test api222:" + this.nDevices.getU1UiDevices().getDisplayHeight());
    }
}
