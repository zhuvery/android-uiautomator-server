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

    /**
     * Reads the text property of the UI element
     *
     * @param paramSelector the selector of the UiObject.
     * @return text value of the current node represented by this UiObject
     * @throws UiObjectNotFoundException
     */
    public String getText(Selector paramSelector) throws UiObjectNotFoundException {
        return NDevices.getInstance().getText(paramSelector);
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
        NDevices.getInstance().getTouchController().touchDown(x, y);
        SystemClock.sleep(100); // normally 100ms for click
        return NDevices.getInstance().getTouchController().touchUp(x, y);
    }

    public boolean click(int x, int y, long milliseconds) {
        if (x < 0 || y < 0) {
            return false;
        }
        NDevices.getInstance().getTouchController().touchDown(x, y);
        SystemClock.sleep(milliseconds);
        return NDevices.getInstance().getTouchController().touchUp(x, y);
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
        return NDevices.getInstance().getU2UiDevices().drag(startX, startY, endX, endY, steps);
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
        return NDevices.getInstance().getU2UiDevices().swipe(startX, startY, endX, endY, steps);
    }

    @Override
    public boolean swipePoints(int[] segments, int segmentSteps) {
        android.graphics.Point[] points = new android.graphics.Point[segments.length / 2];
        for (int i = 0; i < segments.length / 2; i++) {
            points[i] = new android.graphics.Point(segments[2 * i], segments[2 * i + 1]);
        }
        return NDevices.getInstance().getU2UiDevices().swipe(points, segmentSteps);
    }

    // Multi touch is a little complicated
    @Override
    public boolean injectInputEvent(int action, float x, float y, int metaState) {
        TouchController touchController = NDevices.getInstance().getTouchController();
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
        NDevices.getInstance().getU2UiDevices().takeScreenshot(f, scale, quality);
        if (f.exists()) return f.getAbsolutePath();
        return null;
    }


    @Override
    public String takeScreenshot(float scale, int quality) throws NotImplementedException {
        Bitmap screenshot = NDevices.getInstance().getU2UiAutomation().takeScreenshot();
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
        if (freeze) NDevices.getInstance().getU2UiDevices().freezeRotation();
        else NDevices.getInstance().getU2UiDevices().unfreezeRotation();
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
            NDevices.getInstance().getU2UiDevices().setOrientationLeft();
        else if ("right".equals(dir) || "r".equals(dir))
            NDevices.getInstance().getU2UiDevices().setOrientationRight();
        else if ("natural".equals(dir) || "n".equals(dir))
            NDevices.getInstance().getU2UiDevices().setOrientationNatural();
    }

    /**
     * Opens the notification shade.
     *
     * @return true if successful, else return false
     * @throws NotImplementedException
     */
    @Override
    public boolean openNotification() throws NotImplementedException {
        return NDevices.getInstance().getU2UiDevices().openNotification();
    }

    /**
     * Opens the Quick Settings shade.
     *
     * @return true if successful, else return false
     * @throws NotImplementedException
     */
    @Override
    public boolean openQuickSettings() throws NotImplementedException {
        return NDevices.getInstance().getU2UiDevices().openQuickSettings();
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
        if ("home".equals(key)) result = NDevices.getInstance().getU2UiDevices().pressHome();
        else if ("back".equals(key)) result = NDevices.getInstance().getU2UiDevices().pressBack();
        else if ("left".equals(key))
            result = NDevices.getInstance().getU2UiDevices().pressDPadLeft();
        else if ("right".equals(key))
            result = NDevices.getInstance().getU2UiDevices().pressDPadRight();
        else if ("up".equals(key)) result = NDevices.getInstance().getU2UiDevices().pressDPadUp();
        else if ("down".equals(key))
            result = NDevices.getInstance().getU2UiDevices().pressDPadDown();
        else if ("center".equals(key))
            result = NDevices.getInstance().getU2UiDevices().pressDPadCenter();
        else if ("menu".equals(key)) result = NDevices.getInstance().getU2UiDevices().pressMenu();
        else if ("search".equals(key))
            result = NDevices.getInstance().getU2UiDevices().pressSearch();
        else if ("enter".equals(key)) result = NDevices.getInstance().getU2UiDevices().pressEnter();
        else if ("delete".equals(key) || "del".equals(key))
            result = NDevices.getInstance().getU2UiDevices().pressDelete();
        else if ("recent".equals(key))
            result = NDevices.getInstance().getU2UiDevices().pressRecentApps();
        else if ("volume_up".equals(key))
            result = NDevices.getInstance().getU2UiDevices().pressKeyCode(KeyEvent.KEYCODE_VOLUME_UP);
        else if ("volume_down".equals(key))
            result = NDevices.getInstance().getU2UiDevices().pressKeyCode(KeyEvent.KEYCODE_VOLUME_DOWN);
        else if ("volume_mute".equals(key))
            result = NDevices.getInstance().getU2UiDevices().pressKeyCode(KeyEvent.KEYCODE_VOLUME_MUTE);
        else if ("camera".equals(key))
            result = NDevices.getInstance().getU2UiDevices().pressKeyCode(KeyEvent.KEYCODE_CAMERA);
        else
            result = "power".equals(key) && NDevices.getInstance().getU2UiDevices().pressKeyCode(KeyEvent.KEYCODE_POWER);
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
        return NDevices.getInstance().getU2UiDevices().pressKeyCode(keyCode);
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
        return NDevices.getInstance().getU2UiDevices().pressKeyCode(keyCode, metaState);
    }

    /**
     * This method simulates pressing the power button if the screen is OFF else it does nothing if the screen is already ON. If the screen was OFF and it just got turned ON, this method will insert a 500ms delay to allow the device time to wake up and accept input.
     *
     * @throws RemoteException
     */
    @Override
    public void wakeUp() throws RemoteException {
        NDevices.getInstance().getU1UiDevices().wakeUp();
    }

    /**
     * This method simply presses the power button if the screen is ON else it does nothing if the screen is already OFF.
     *
     * @throws RemoteException
     */
    @Override
    public void sleep() throws RemoteException {
        NDevices.getInstance().getU1UiDevices().sleep();
    }

    /**
     * Checks the power manager if the screen is ON.
     *
     * @return true if the screen is ON else false
     * @throws RemoteException
     */
    @Override
    public boolean isScreenOn() throws RemoteException {
        return NDevices.getInstance().getU1UiDevices().isScreenOn();
    }
}
