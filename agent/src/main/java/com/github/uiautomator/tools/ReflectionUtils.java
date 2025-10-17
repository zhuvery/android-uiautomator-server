package com.github.uiautomator.tools;

import com.github.uiautomator.stub.Log;
import com.github.uiautomator.exceptions.UiAutomator2Exception;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionUtils {
    public static boolean clearAccessibilityCache() throws UiAutomator2Exception {
        try {
            Object object = method(Class.forName("android.view.accessibility.AccessibilityInteractionClient"), "getInstance", new Class[0]).invoke(null, new Object[0]);
            method(object.getClass(), "clearCache", new Class[0]).invoke(object, new Object[0]);
            return true;
        } catch (IllegalAccessException illegalAccessException) {
            Log.e("Failed to clear Accessibility Node cache. ", illegalAccessException);
            return false;
        } catch (InvocationTargetException invocationTargetException) {
            Log.e("Failed to clear Accessibility Node cache. ", invocationTargetException);
            return false;
        } catch (ClassNotFoundException classNotFoundException) {
            Log.e("Failed to clear Accessibility Node cache. ", classNotFoundException);
            return false;
        }
    }

    public static Class getClass(String paramString) throws UiAutomator2Exception {
        try {
            return Class.forName(paramString);
        } catch (ClassNotFoundException classNotFoundException) {
            throw new UiAutomator2Exception(String.format("unable to find class %s", new Object[]{paramString}), classNotFoundException);
        }
    }

    public static Object getField(Class paramClass, String paramString, Object paramObject) throws UiAutomator2Exception {
        try {
            Field field = paramClass.getDeclaredField(paramString);
            field.setAccessible(true);
            return field.get(paramObject);
        } catch (Exception exception) {
            paramString = String.format("error while getting field %s from object %s", new Object[]{paramString, paramObject});
            Log.e(paramString + " " + exception.getMessage());
            throw new UiAutomator2Exception(paramString, exception);
        }
    }

    public static Object getField(String paramString, Object paramObject) throws UiAutomator2Exception {
        return getField(paramObject.getClass(), paramString, paramObject);
    }

    public static Object getField(String paramString1, String paramString2, Object paramObject) throws UiAutomator2Exception {
        return getField(getClass(paramString1), paramString2, paramObject);
    }

    public static Object invoke(Method paramMethod, Object paramObject, Object... paramVarArgs) throws UiAutomator2Exception {
        try {
            return paramMethod.invoke(paramObject, paramVarArgs);
        } catch (Exception exception) {
            String str = String.format("error while invoking method %s on object %s with parameters %s", new Object[]{paramMethod, paramObject, Arrays.toString(paramVarArgs)});
            Log.e(str + " " + exception.getMessage());
            throw new UiAutomator2Exception(str, exception);
        }
    }

    public static Method method(Class paramClass, String paramString, Class... paramVarArgs) throws UiAutomator2Exception {
        try {
            Method method = paramClass.getDeclaredMethod(paramString, paramVarArgs);
            method.setAccessible(true);
            return method;
        } catch (Exception exception) {
            String str = String.format("error while getting method %s from class %s with parameter types %s", new Object[]{paramString, paramClass, Arrays.toString((Object[]) paramVarArgs)});
            Log.e(str + " " + exception.getMessage());
            throw new UiAutomator2Exception(str, exception);
        }
    }

    public static Method method(String paramString1, String paramString2, Class... paramVarArgs) throws UiAutomator2Exception {
        return method(getClass(paramString1), paramString2, paramVarArgs);
    }
}

