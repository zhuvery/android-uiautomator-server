package com.github.uiautomator.stub;

import android.app.UiAutomation;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import android.view.Display;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.annotation.RequiresApi;
import androidx.test.filters.SdkSuppress;
import androidx.test.platform.app.InstrumentationRegistry;

import android.support.test.uiautomator.UiDevice;

import com.github.uiautomator.exceptions.UiAutomator2Exception;
import com.github.uiautomator.nuiautomator.NDevices;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class AccessibilityNodeInfoDumper {

    private static final String TAG = AccessibilityNodeInfoDumper.class.getSimpleName();
    private static final String[] NAF_EXCLUDED_CLASSES = new String[]{
            android.widget.GridView.class.getName(), android.widget.GridLayout.class.getName(),
            android.widget.ListView.class.getName(), android.widget.TableLayout.class.getName()
    };

    private AccessibilityNodeInfoDumper() {
    }

    public static void dumpWindowHierarchy(AccessibilityNodeInfo[] accessibilityNodeInfos, OutputStream out, int maxDepth) throws UiAutomator2Exception, IOException {
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        serializer.setOutput(out, "UTF-8");

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "hierarchy"); // TODO(allenhair): Should we use a namespace?
        com.android.uiautomator.core.UiDevice u1UiDevice = NDevices.getInstance().getU1UiDevices();
        NDevices.getInstance().refreshUI(accessibilityNodeInfos[0]);
        if (Build.VERSION.SDK_INT >= 18) {
            int i = u1UiDevice.getDisplayWidth() > u1UiDevice.getDisplayHeight() ? 1 : 0;
            serializer.attribute("", "rotation", Integer.toString(i));
        }
        for (AccessibilityNodeInfo root : accessibilityNodeInfos) {
            dumpNodeRec(root, serializer, 0, u1UiDevice.getDisplayWidth(),
                    u1UiDevice.getDisplayHeight(), maxDepth);
        }
    }

    public static void dumpWindowHierarchy(UiDevice device, OutputStream out, int maxDepth) throws IOException {
//        try (Section ignored = Traces.trace("AccessibilityNodeInfoDumper.dumpWindowHierarchy")) {
//            XmlSerializer serializer = Xml.newSerializer();
//            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
//            serializer.setOutput(out, "UTF-8");
//
//            serializer.startDocument("UTF-8", true);
//            serializer.startTag("", "hierarchy"); // TODO(allenhair): Should we use a namespace?
//            serializer.attribute("", "rotation", Integer.toString(device.getDisplayRotation()));
//
//            for (AccessibilityNodeInfo root : getWindowRoots(device)) {
//                dumpNodeRec(root, serializer, 0, device.getDisplayWidth(),
//                            device.getDisplayHeight(), maxDepth);
//            }
//
//            serializer.endTag("", "hierarchy");
//            serializer.endDocument();
//        }

        XmlSerializer serializer = Xml.newSerializer();
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        serializer.setOutput(out, "UTF-8");

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "hierarchy"); // TODO(allenhair): Should we use a namespace?
        serializer.attribute("", "rotation", Integer.toString(device.getDisplayRotation()));

        for (AccessibilityNodeInfo root : getWindowRoots(device)) {
            dumpNodeRec(root, serializer, 0, device.getDisplayWidth(),
                    device.getDisplayHeight(), maxDepth);
        }

        serializer.endTag("", "hierarchy");
        serializer.endDocument();
    }

    static AccessibilityNodeInfo[] getWindowRoots(UiDevice device) {
        device.waitForIdle();

        Set<AccessibilityNodeInfo> roots = new HashSet<>();
        UiAutomation uiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation();

        // Ensure the active window root is included.
        AccessibilityNodeInfo activeRoot = uiAutomation.getRootInActiveWindow();
        if (activeRoot != null) {
            roots.add(activeRoot);
        } else {
            Log.w(TAG, "Active window root not found.");
        }
        // Support multi-window searches for API level 21 and up.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (final AccessibilityWindowInfo window : getWindows(uiAutomation)) {
                final AccessibilityNodeInfo root = window.getRoot(); // AccessibilityNodeInfoHelper.Api21Impl.getRoot(window);
                if (root == null) {
                    Log.w(TAG, "Skipping null root node for window: " + window);
                    continue;
                }
                roots.add(root);
            }
        }
        return roots.toArray(new AccessibilityNodeInfo[0]);
    }

    @SdkSuppress(minSdkVersion = 21)
    private static List<AccessibilityWindowInfo> getWindows(UiAutomation uiAutomation) {
        // Support multi-display searches for API level 30 and up.
        if (Build.VERSION.SDK_INT >= 30) {
            final List<AccessibilityWindowInfo> windowList = new ArrayList<>();
            final SparseArray<List<AccessibilityWindowInfo>> allWindows =
                    uiAutomation.getWindowsOnAllDisplays();
            //AccessibilityNodeInfoHelper.Api30Impl.getWindowsOnAllDisplays(uiAutomation);
            for (int index = 0; index < allWindows.size(); index++) {
                windowList.addAll(allWindows.valueAt(index));
            }
            return windowList;
        }
        return uiAutomation.getWindows();
        //return AccessibilityNodeInfoHelper.Api21Impl.getWindows(uiAutomation);
    }

    private static void dumpNodeRec(AccessibilityNodeInfo node, XmlSerializer serializer, int index,
                                    int width, int height, int maxDepth) throws IOException {
        serializer.startTag("", "node");
        if (!nafExcludedClass(node) && !nafCheck(node))
            serializer.attribute("", "NAF", Boolean.toString(true));
        serializer.attribute("", "index", Integer.toString(index));
        try {
            serializer.attribute("", "text", safeCharSeqToString(node.getText()));
            serializer.attribute("", "resource-id", safeCharSeqToString(node.getViewIdResourceName()));
            serializer.attribute("", "class", safeCharSeqToString(node.getClassName()));
            serializer.attribute("", "package", safeCharSeqToString(node.getPackageName()));
            serializer.attribute("", "content-desc", safeCharSeqToString(node.getContentDescription()));
        } catch (IllegalArgumentException e) {
            // java.lang.IllegalArgumentException: Illegal character (U+0)
            // TODO: maybe the best way is to update safeCharSeqToString
            e.printStackTrace();
        }
        serializer.attribute("", "checkable", Boolean.toString(node.isCheckable()));
        serializer.attribute("", "checked", Boolean.toString(node.isChecked()));
        serializer.attribute("", "clickable", Boolean.toString(node.isClickable()));
        serializer.attribute("", "enabled", Boolean.toString(node.isEnabled()));
        serializer.attribute("", "focusable", Boolean.toString(node.isFocusable()));
        serializer.attribute("", "focused", Boolean.toString(node.isFocused()));
        serializer.attribute("", "scrollable", Boolean.toString(node.isScrollable()));
        serializer.attribute("", "long-clickable", Boolean.toString(node.isLongClickable()));
        serializer.attribute("", "password", Boolean.toString(node.isPassword()));
        serializer.attribute("", "selected", Boolean.toString(node.isSelected()));
        serializer.attribute("", "visible-to-user", Boolean.toString(node.isVisibleToUser()));
        serializer.attribute("", "bounds", AccessibilityNodeInfoHelper.getVisibleBoundsInScreen(
                node, width, height, false).toShortString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            serializer.attribute("", "drawing-order",
                    Integer.toString(Api24Impl.getDrawingOrder(node)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serializer.attribute("", "hint", safeCharSeqToString(Api26Impl.getHintText(node)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            serializer.attribute("", "display-id",
                    Integer.toString(Api30Impl.getDisplayId(node)));
        }
        if (maxDepth > 0) {
            int count = node.getChildCount();
            for (int i = 0; i < count; i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child != null) {
                    if (child.isVisibleToUser()) {
                        dumpNodeRec(child, serializer, i, width, height, maxDepth - 1);
                        child.recycle();
                    } else {
                        Log.i(TAG, String.format("Skipping invisible child: %s", child));
                    }
                } else {
                    Log.i(TAG, String.format("Null child %d/%d, parent: %s", i, count, node));
                }
            }
        }
        serializer.endTag("", "node");
    }

    /**
     * The list of classes to exclude my not be complete. We're attempting to
     * only reduce noise from standard layout classes that may be falsely
     * configured to accept clicks and are also enabled.
     *
     * @param node
     * @return true if node is excluded.
     */
    private static boolean nafExcludedClass(AccessibilityNodeInfo node) {
        String className = safeCharSeqToString(node.getClassName());
        for (String excludedClassName : NAF_EXCLUDED_CLASSES) {
            if (className.endsWith(excludedClassName))
                return true;
        }
        return false;
    }

    /**
     * We're looking for UI controls that are enabled, clickable but have no
     * text nor content-description. Such controls configuration indicate an
     * interactive control is present in the UI and is most likely not
     * accessibility friendly. We refer to such controls here as NAF controls
     * (Not Accessibility Friendly)
     *
     * @param node
     * @return false if a node fails the check, true if all is OK
     */
    private static boolean nafCheck(AccessibilityNodeInfo node) {
        boolean isNaf = node.isClickable() && node.isEnabled()
                && safeCharSeqToString(node.getContentDescription()).isEmpty()
                && safeCharSeqToString(node.getText()).isEmpty();

        if (!isNaf)
            return true;

        // check children since sometimes the containing element is clickable
        // and NAF but a child's text or description is available. Will assume
        // such layout as fine.
        return childNafCheck(node);
    }

    /**
     * This should be used when it's already determined that the node is NAF and
     * a further check of its children is in order. A node maybe a container
     * such as LinerLayout and may be set to be clickable but have no text or
     * content description but it is counting on one of its children to fulfill
     * the requirement for being accessibility friendly by having one or more of
     * its children fill the text or content-description. Such a combination is
     * considered by this dumper as acceptable for accessibility.
     *
     * @param node
     * @return false if node fails the check.
     */
    private static boolean childNafCheck(AccessibilityNodeInfo node) {
        int childCount = node.getChildCount();
        for (int x = 0; x < childCount; x++) {
            AccessibilityNodeInfo childNode = node.getChild(x);
            if (childNode == null) {
                continue;
            }
            if (!safeCharSeqToString(childNode.getContentDescription()).isEmpty()
                    || !safeCharSeqToString(childNode.getText()).isEmpty()) {
                return true;
            }

            if (childNafCheck(childNode)) {
                return true;
            }
        }
        return false;
    }

    private static String safeCharSeqToString(CharSequence cs) {
        return cs == null ? "" : stripInvalidXMLChars(cs);
    }

    private static String stripInvalidXMLChars(CharSequence cs) {
        StringBuilder ret = new StringBuilder();
        char ch;
        for (int i = 0; i < cs.length(); i++) {
            ch = cs.charAt(i);
            // http://www.w3.org/TR/xml11/#charsets
            if ((ch >= 0x0 && ch <= 0x8)
                    || (ch >= 0xB && ch <= 0xC)
                    || (ch >= 0xE && ch <= 0x1F)
                    || (ch >= 0x7F && ch <= 0x84)
                    || (ch >= 0x86 && ch <= 0x9F)
                    || (ch >= 0xD800 && ch <= 0xDFFF)
                    || (ch >= 0xFDD0 && ch <= 0xFDDF)
                    || (ch >= 0xFFFE && ch <= 0xFFFF)) {
                ret.append(".");
            } else {
                ret.append(ch);
            }
        }
        return ret.toString();
    }

    @RequiresApi(24)
    static class Api24Impl {
        private Api24Impl() {
        }

        //        @DoNotInline
        static int getDrawingOrder(AccessibilityNodeInfo accessibilityNodeInfo) {
            return accessibilityNodeInfo.getDrawingOrder();
        }
    }

    @RequiresApi(26)
    static class Api26Impl {
        private Api26Impl() {
        }

        //        @DoNotInline
        static String getHintText(AccessibilityNodeInfo accessibilityNodeInfo) {
            CharSequence chars = accessibilityNodeInfo.getHintText();
            return chars != null ? chars.toString() : null;
        }
    }

    @RequiresApi(30)
    static class Api30Impl {
        private Api30Impl() {
        }

        //        @DoNotInline
        static int getDisplayId(AccessibilityNodeInfo accessibilityNodeInfo) {
            AccessibilityWindowInfo accessibilityWindowInfo = accessibilityNodeInfo.getWindow();
            return accessibilityWindowInfo == null ? Display.DEFAULT_DISPLAY :
                    accessibilityWindowInfo.getDisplayId();
        }
    }
}