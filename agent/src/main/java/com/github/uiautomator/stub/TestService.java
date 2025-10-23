package com.github.uiautomator.stub;

import android.os.RemoteException;
import android.support.test.uiautomator.UiObjectNotFoundException;

import com.github.uiautomator.exceptions.NotImplementedException;
import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;

public interface TestService {

    final static int ERROR_CODE_BASE = -32000;

    boolean playSound(String path);

    String ping();

    DeviceInfo deviceInfo();

    String dumpAllWindowHierarchy(boolean paramBoolean);

    String dumpWindowHierarchy(boolean compressed);

    String dumpWindowHierarchy(boolean compressed, int maxDepth);

    String dumpWindowHierarchyWithSelector(boolean paramBoolean, Selector paramSelector);

    @JsonRpcErrors({@JsonRpcError(code = -32002, exception = UiObjectNotFoundException.class)})
    String getText(Selector paramSelector) throws UiObjectNotFoundException;

    boolean click(int x, int y);

    public boolean click(int x, int y, long milliseconds);

    @JsonRpcErrors({@JsonRpcError(exception = NotImplementedException.class, code = ERROR_CODE_BASE - 3)})
    boolean drag(int startX, int startY, int endX, int endY, int steps) throws NotImplementedException;

    boolean swipe(int startX, int startY, int endX, int endY, int steps);

    /**
     * Performs a swipe between points in the point array
     *
     * @param segments     the point array
     * @param segmentSteps steps to inject between two points, each step lasting 5ms
     */
    boolean swipePoints(int[] segments, int segmentSteps);

    /**
     * Inject a low-level InputEvent (MotionEvent) to the input stream
     *
     * @param action    MotionEvent.ACTION_*
     * @param x         x coordinate
     * @param y         y coordinate
     * @param metaState any meta info
     */
    boolean injectInputEvent(int action, float x, float y, int metaState);

    /**
     * Take a screenshot of current window and store it as PNG The screenshot is adjusted per screen rotation
     *
     * @param filename where the PNG should be written to
     * @param scale    scale the screenshot down if needed; 1.0f for original size
     * @param quality  quality of the PNG compression; range: 0-100
     * @return the file name of the screenshot. null if failed.
     * @throws NotImplementedException
     */
    @JsonRpcErrors({@JsonRpcError(exception = NotImplementedException.class, code = ERROR_CODE_BASE - 3)})
    String takeScreenshot(String filename, float scale, int quality) throws NotImplementedException;

    /**
     * Take a screenshot of current window and store it as JPEG The screenshot is adjusted per screen rotation
     *
     * @param scale
     * @param quality
     * @return base64 encoded image data
     * @throws NotImplementedException
     */
    @JsonRpcErrors({@JsonRpcError(exception = NotImplementedException.class, code = ERROR_CODE_BASE - 3)})
    public String takeScreenshot(float scale, int quality) throws NotImplementedException;

    /**
     * Disables the sensors and freezes the device rotation at its current rotation state, or enable it.
     *
     * @param freeze true to freeze the rotation, false to unfreeze the rotation.
     * @throws RemoteException
     */
    @JsonRpcErrors({@JsonRpcError(exception = RemoteException.class, code = ERROR_CODE_BASE - 1)})
    void freezeRotation(boolean freeze) throws RemoteException;  // freeze or unfreeze rotation, see also unfreezeRotation()

    @JsonRpcErrors({@JsonRpcError(exception = RemoteException.class, code = ERROR_CODE_BASE - 1), @JsonRpcError(exception = NotImplementedException.class, code = ERROR_CODE_BASE - 3)})
    void setOrientation(String dir) throws RemoteException, NotImplementedException;

    /**
     * Opens the notification shade.
     *
     * @return true if successful, else return false
     * @throws NotImplementedException
     */
    @JsonRpcErrors({@JsonRpcError(exception = NotImplementedException.class, code = ERROR_CODE_BASE - 3)})
    boolean openNotification() throws NotImplementedException;

    /**
     * Opens the Quick Settings shade.
     *
     * @return true if successful, else return false
     * @throws NotImplementedException
     */
    @JsonRpcErrors({@JsonRpcError(exception = NotImplementedException.class, code = ERROR_CODE_BASE - 3)})
    boolean openQuickSettings() throws NotImplementedException;

    /**
     * Simulates a short press using key name.
     *
     * @param key possible key name is home, back, left, right, up, down, center, menu, search, enter, delete(or del), recent(recent apps), volume_up, volume_down, volume_mute, camera, power
     * @return true if successful, else return false
     * @throws RemoteException
     */
    @JsonRpcErrors({@JsonRpcError(exception = RemoteException.class, code = ERROR_CODE_BASE - 1)})
    boolean pressKey(String key) throws RemoteException;

    /**
     * Simulates a short press using a key code. See KeyEvent.
     *
     * @param keyCode the key code of the event.
     * @return true if successful, else return false
     */
    boolean pressKeyCode(int keyCode);

    /**
     * Simulates a short press using a key code. See KeyEvent.
     *
     * @param keyCode   the key code of the event.
     * @param metaState an integer in which each bit set to 1 represents a pressed meta key
     * @return true if successful, else return false
     */
    boolean pressKeyCode(int keyCode, int metaState);

    /**
     * This method simulates pressing the power button if the screen is OFF else it does nothing if the screen is already ON. If the screen was OFF and it just got turned ON, this method will insert a 500ms delay to allow the device time to wake up and accept input.
     *
     * @throws RemoteException
     */
    @JsonRpcErrors({@JsonRpcError(exception = RemoteException.class, code = ERROR_CODE_BASE - 1)})
    void wakeUp() throws RemoteException;

    /**
     * This method simply presses the power button if the screen is ON else it does nothing if the screen is already OFF.
     *
     * @throws RemoteException
     */
    @JsonRpcErrors({@JsonRpcError(exception = RemoteException.class, code = ERROR_CODE_BASE - 1)})
    void sleep() throws RemoteException;

    /**
     * Checks the power manager if the screen is ON.
     *
     * @return true if the screen is ON else false
     * @throws RemoteException
     */
    @JsonRpcErrors({@JsonRpcError(exception = RemoteException.class, code = ERROR_CODE_BASE - 1)})
    boolean isScreenOn() throws RemoteException;

    /**
     * Clears the existing text contents in an editable field. The UiSelector of this object must reference a UI element that is editable. When you call this method, the method first sets focus at the start edge of the field. The method then simulates a long-press to select the existing text, and deletes the selected text. If a "Select-All" option is displayed, the method will automatically attempt to use it to ensure full text selection. Note that it is possible that not all the text in the field is selected; for example, if the text contains separators such as spaces, slashes, at symbol etc. Also, not all editable fields support the long-press functionality.
     *
     * @param obj the selector of the UiObject.
     * @throws UiObjectNotFoundException
     */
    @JsonRpcErrors({@JsonRpcError(exception = UiObjectNotFoundException.class, code = ERROR_CODE_BASE - 2)})
    void clearTextField(Selector obj) throws UiObjectNotFoundException;

    @JsonRpcErrors({@JsonRpcError(exception = UiObjectNotFoundException.class, code = ERROR_CODE_BASE - 2)})
    boolean setText(Selector obj, String text) throws UiObjectNotFoundException;

    /**
     * Performs a click at the center of the visible bounds of the UI element represented by this UiObject.
     *
     * @param obj the target ui object.
     * @return true id successful else false
     * @throws UiObjectNotFoundException
     */
    @JsonRpcErrors({@JsonRpcError(exception = UiObjectNotFoundException.class, code = ERROR_CODE_BASE - 2)})
    boolean click(Selector obj) throws UiObjectNotFoundException;

    /**
     * Clicks the bottom and right corner or top and left corner of the UI element
     *
     * @param obj    the target ui object.
     * @param corner "br"/"bottomright" means BottomRight, "tl"/"topleft" means TopLeft, "center" means Center.
     * @return true on success
     * @throws UiObjectNotFoundException
     */
    @JsonRpcErrors({@JsonRpcError(exception = UiObjectNotFoundException.class, code = ERROR_CODE_BASE - 2)})
    boolean click(Selector obj, String corner) throws UiObjectNotFoundException;

    /**
     * Long clicks the center of the visible bounds of the UI element
     *
     * @param obj the target ui object.
     * @return true if operation was successful
     * @throws UiObjectNotFoundException
     */
    @JsonRpcErrors({@JsonRpcError(exception = UiObjectNotFoundException.class, code = ERROR_CODE_BASE - 2)})
    boolean longClick(Selector obj) throws UiObjectNotFoundException;

    /**
     * Long clicks bottom and right corner of the UI element
     *
     * @param obj    the target ui object.
     * @param corner "br"/"bottomright" means BottomRight, "tl"/"topleft" means TopLeft, "center" means Center.
     * @return true if operation was successful
     * @throws UiObjectNotFoundException
     */
    @JsonRpcErrors({@JsonRpcError(exception = UiObjectNotFoundException.class, code = ERROR_CODE_BASE - 2)})
    boolean longClick(Selector obj, String corner) throws UiObjectNotFoundException;

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
    @JsonRpcErrors({@JsonRpcError(exception = UiObjectNotFoundException.class, code = ERROR_CODE_BASE - 2), @JsonRpcError(exception = NotImplementedException.class, code = ERROR_CODE_BASE - 3)})
    boolean dragTo(Selector obj, Selector destObj, int steps) throws UiObjectNotFoundException, NotImplementedException;

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
    @JsonRpcErrors({@JsonRpcError(exception = UiObjectNotFoundException.class, code = ERROR_CODE_BASE - 2), @JsonRpcError(exception = NotImplementedException.class, code = ERROR_CODE_BASE - 3)})
    boolean dragTo(Selector obj, int destX, int destY, int steps) throws UiObjectNotFoundException, NotImplementedException;

    /**
     * Check if view exists. This methods performs a waitForExists(long) with zero timeout. This basically returns immediately whether the view represented by this UiObject exists or not.
     *
     * @param obj the ui object.
     * @return true if the view represented by this UiObject does exist
     */
    @JsonRpcErrors({@JsonRpcError(exception = UiObjectNotFoundException.class, code = ERROR_CODE_BASE - 2)})
    boolean exist(Selector obj);

    /**
     * Get the object info.
     *
     * @param obj the target ui object.
     * @return object info.
     * @throws UiObjectNotFoundException
     */
    @JsonRpcErrors({@JsonRpcError(exception = UiObjectNotFoundException.class, code = ERROR_CODE_BASE - 2)})
    ObjInfo objInfo(Selector obj) throws UiObjectNotFoundException;

    /**
     * Get the count of the UiObject instances by the selector
     *
     * @param obj the selector of the ui object
     * @return the count of instances.
     */
    int count(Selector obj);

    /**
     * Get the info of all instance by the selector.
     *
     * @param obj the selector of ui object.
     * @return array of object info.
     */
    ObjInfo[] objInfoOfAllInstances(Selector obj);

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
    @JsonRpcErrors({@JsonRpcError(exception = UiObjectNotFoundException.class, code = ERROR_CODE_BASE - 2), @JsonRpcError(exception = NotImplementedException.class, code = ERROR_CODE_BASE - 3)})
    boolean gesture(Selector obj, Point startPoint1, Point startPoint2, Point endPoint1, Point endPoint2, int steps) throws UiObjectNotFoundException, NotImplementedException;


    void testApi();
}
