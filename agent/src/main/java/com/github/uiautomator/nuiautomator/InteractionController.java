package com.github.uiautomator.nuiautomator;

import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.InputEvent;
import android.view.KeyCharacterMap;
import android.view.MotionEvent;

import com.github.uiautomator.tools.ReflectionUtils;

public class InteractionController {

    private final Object interactionController;

    public InteractionController(Object paramObject) {
        this.interactionController = paramObject;
    }

    private boolean injectEventSync(InputEvent event) {
        return (boolean) ReflectionUtils.invoke(
                ReflectionUtils.method("android.support.test.uiautomator.InteractionController", "injectEventSync", InputEvent.class),
                this.interactionController,
                event
        );
    }

    public boolean sendKey(int keyCode, int metaState) {
        long eventTime = SystemClock.uptimeMillis();
        KeyEvent downEvent = new KeyEvent(eventTime, eventTime, 0, keyCode, 0, metaState, -1, 0, 0, 257);
        if (injectEventSync((InputEvent) downEvent)) {
            KeyEvent upEvent = new KeyEvent(eventTime, eventTime, 1, keyCode, 0, metaState, -1, 0, 0, 257);
            if (injectEventSync((InputEvent) upEvent))
                return true;
        }
        return false;
    }


    public boolean sendText(String text) {
        KeyCharacterMap mKeyCharacterMap = KeyCharacterMap.load(-1);
        KeyEvent[] events = mKeyCharacterMap.getEvents(text.toCharArray());
        if (events != null) {
            long keyDelay = 1; //delay 1ms
            for (KeyEvent event2 : events) {
                KeyEvent event = KeyEvent.changeTimeRepeat(event2, SystemClock.uptimeMillis(), 0);
                if (!injectEventSync((InputEvent) event)) return false;
                SystemClock.sleep(keyDelay);
            }
        }
        return true;
    }

    public boolean clickNoSync(int paramInt1, int paramInt2) {
        return ((Boolean) ReflectionUtils.invoke(ReflectionUtils.method("android.support.test.uiautomator.InteractionController", "clickNoSync", new Class[]{int.class, int.class}), this.interactionController, new Object[]{Integer.valueOf(paramInt1), Integer.valueOf(paramInt2)})).booleanValue();
    }

    public boolean longClickNoSync(int paramInt1, int paramInt2) {
        return ((Boolean) ReflectionUtils.invoke(ReflectionUtils.method("android.support.test.uiautomator.InteractionController", "longTapNoSync", new Class[]{int.class, int.class}), this.interactionController, new Object[]{Integer.valueOf(paramInt1), Integer.valueOf(paramInt2)})).booleanValue();
    }

    public boolean swipe(int downX, int downY, int upX, int upY, int steps) {
        return ((Boolean) ReflectionUtils.invoke(ReflectionUtils.method("android.support.test.uiautomator.InteractionController", "swipe", new Class[]{int.class, int.class, int.class, int.class, int.class}), this.interactionController, new Object[]{Integer.valueOf(downX), Integer.valueOf(downY), Integer.valueOf(upX), Integer.valueOf(upY), Integer.valueOf(steps)})).booleanValue();
    }

    public boolean swipe(int downX, int downY, int upX, int upY, int steps, boolean drag) {
        return ((Boolean) ReflectionUtils.invoke(
                ReflectionUtils.method(
                        "android.support.test.uiautomator.InteractionController",
                        "swipe", new Class[]
                                {
                                        int.class, int.class, int.class, int.class, int.class, boolean.class
                                }
                ), this.interactionController,
                new Object[]{Integer.valueOf(downX), Integer.valueOf(downY), Integer.valueOf(upX), Integer.valueOf(upY), Integer.valueOf(steps), Boolean.valueOf(drag)})).booleanValue();
    }

    public boolean performMultiPointerGesture(MotionEvent.PointerCoords[]... touches) {
        return ((Boolean) ReflectionUtils.invoke(
                ReflectionUtils.method(
                        "android.support.test.uiautomator.InteractionController",
                        "performMultiPointerGesture", new Class[]
                                {
                                        MotionEvent.PointerCoords[][].class
                                }
                ),
                this.interactionController,
                new Object[]{touches})).booleanValue();
    }
}
