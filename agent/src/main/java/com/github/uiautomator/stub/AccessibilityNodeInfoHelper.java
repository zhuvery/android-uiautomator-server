/*
 * see <https://android.googlesource.com/platform//frameworks/support/+/48d9273f1facf771ff812a10d8bb46f6f8773ed4/test/uiautomator/uiautomator/src/main/java/androidx/test/uiautomator/AccessibilityNodeInfoDumper.java>
 */

package com.github.uiautomator.stub;

import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

//import androidx.annotation.DoNotInline;
import androidx.annotation.RequiresApi;

/**
 * This class contains static helper methods to work with
 * {@link AccessibilityNodeInfo}
 */
class AccessibilityNodeInfoHelper {
    private static final String TAG = AccessibilityNodeInfoHelper.class.getSimpleName();

    private AccessibilityNodeInfoHelper() {}

    /**
     * Returns the node's bounds clipped to the size of the display
     *
     * @param node
     * @param width pixel width of the display
     * @param height pixel height of the display
     * @return null if node is null, else a Rect containing visible bounds
     */
    static Rect getVisibleBoundsInScreen(AccessibilityNodeInfo node, int width, int height,
            boolean trimScrollableParent) {
        return getVisibleBoundsInScreen(node, new Rect(0, 0, width, height), trimScrollableParent);
    }

    /**
     * Returns the node's bounds clipped to the size of the display
     *
     * @param node
     * @param displayRect the display rect
     * @return null if node is null, else a Rect containing visible bounds
     */
    static Rect getVisibleBoundsInScreen(AccessibilityNodeInfo node, Rect displayRect,
            boolean trimScrollableParent) {
        if (node == null) {
            return null;
        }
        // targeted node's bounds
        Rect nodeRect = new Rect();
        node.getBoundsInScreen(nodeRect);

        if (displayRect == null) {
            displayRect = new Rect();
        }
        intersectOrWarn(nodeRect, displayRect);

        // On platforms that give us access to the node's window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Trim any portion of the bounds that are outside the window
            Rect bounds = new Rect();
            AccessibilityWindowInfo window = Api21Impl.getWindow(node);
            if (window != null) {
                Api21Impl.getBoundsInScreen(window, bounds);
                intersectOrWarn(nodeRect, bounds);
            }
        }

        // Trim the bounds into any scrollable ancestor, if required.
        if (trimScrollableParent) {
            for (AccessibilityNodeInfo ancestor = node.getParent(); ancestor != null; ancestor =
                    ancestor.getParent()) {
                if (ancestor.isScrollable()) {
                    Rect ancestorRect = getVisibleBoundsInScreen(ancestor, displayRect, true);
                    intersectOrWarn(nodeRect, ancestorRect);
                    break;
                }
            }
        }

        return nodeRect;
    }

    /**
     * Takes the intersection between the two input rectangles and stores the intersection in the
     * first one.
     *
     * @param target the targeted Rect to be clipped. The intersection result will be stored here.
     * @param bounds the bounds used to clip.
     */
    private static void intersectOrWarn(Rect target, Rect bounds) {
        if (!target.intersect(bounds)) {
            Log.v(TAG, String.format("No overlap between %s and %s. Ignoring.", target, bounds));
        }
    }

    @RequiresApi(21)
    static class Api21Impl {
        private Api21Impl() {
        }

//        @DoNotInline
        static void getBoundsInScreen(AccessibilityWindowInfo accessibilityWindowInfo,
                Rect outBounds) {
            accessibilityWindowInfo.getBoundsInScreen(outBounds);
        }

//        @DoNotInline
        static AccessibilityWindowInfo getWindow(AccessibilityNodeInfo accessibilityNodeInfo) {
            return accessibilityNodeInfo.getWindow();
        }
    }
}
