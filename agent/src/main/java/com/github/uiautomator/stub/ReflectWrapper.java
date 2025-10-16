package com.github.uiautomator.stub;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectWrapper {
    private static final String TAG = "UIAutomatorStub.Reflect";

    public static Class<?> getClass(String paramString) {
        try {
            return Class.forName(paramString);
        } catch (ClassNotFoundException classNotFoundException) {
            Log.e(TAG, "ClassNotFoundException", classNotFoundException);
            return null;
        }
    }

    public static Field getField(Class<?> paramClass, String paramString) {
        try {
            Field field = getFieldRecursiveImpl(paramClass, paramString);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException noSuchFieldException) {
            Log.e(TAG, "NoSuchFieldException", noSuchFieldException);
            return null;
        }
    }

    public static Field getField(String paramString1, String paramString2) {
        Class<?> clazz = getClass(paramString1);
        return (clazz == null) ? null : getField(clazz, paramString2);
    }

    private static Field getFieldRecursiveImpl(Class<?> paramClass, String paramString) throws NoSuchFieldException {
        try {
            return paramClass.getDeclaredField(paramString);
        } catch (NoSuchFieldException noSuchFieldException) {
            while (true) {
                paramClass = paramClass.getSuperclass();
                if (paramClass == null || paramClass.equals(Object.class))
                    throw noSuchFieldException;
                try {
                    return paramClass.getDeclaredField(paramString);
                } catch (NoSuchFieldException noSuchFieldException1) {
                }
            }
        }
    }

    public static Method getMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs) {
        try {
            return getMethodRecursiveImpl(paramClass, paramString, paramVarArgs);
        } catch (NoSuchMethodException noSuchMethodException) {
            return null;
        }
    }

    public static Method getMethod(String paramString1, String paramString2) {
        return getMethod(paramString1, paramString2, new Class[0]);
    }

    public static Method getMethod(String paramString1, String paramString2, Class<?>... paramVarArgs) {
        return getMethod(getClass(paramString1), paramString2, paramVarArgs);
    }

    private static Method getMethodRecursiveImpl(Class<?> paramClass, String paramString, Class<?>... paramVarArgs) throws NoSuchMethodException {
        try {
            return paramClass.getDeclaredMethod(paramString, paramVarArgs);
        } catch (NoSuchMethodException noSuchMethodException) {
            while (true) {
                Log.i(TAG, "try in " + paramClass.getSimpleName());
                paramClass = paramClass.getSuperclass();
                if (paramClass == null || paramClass.equals(Object.class))
                    throw noSuchMethodException;
                try {
                    return paramClass.getDeclaredMethod(paramString, paramVarArgs);
                } catch (NoSuchMethodException ignored) {
                }
            }
        }
    }

    public static Object getStaticValue(String paramString1, String paramString2) {
        return getValue(paramString1, paramString2, null);
    }

    public static Object getValue(String paramString, Object paramObject) {
        try {
            Field field = getField(paramObject.getClass(), paramString);
            return (field == null) ? null : getValue(field, paramObject);
        } catch (Exception exception) {
            Log.e(TAG, "", exception);
            return null;
        }
    }

    public static Object getValue(String paramString1, String paramString2, Object paramObject) {
        try {
            Class<?> clazz = getClass(paramString1);
            if (clazz == null)
                return null;
            Field field = getField(clazz, paramString2);
            if (field != null)
                return getValue(field, paramObject);
        } catch (Exception exception) {
            Log.e(TAG, "", exception);
        }
        return null;
    }

    public static Object getValue(Field paramField, Object paramObject) {
        try {
            paramField.setAccessible(true);
            return paramField.get(paramObject);
        } catch (Exception exception) {
            Log.e(TAG, "", exception);
            return null;
        }
    }

    public static Object invoke(String paramString, Object paramObject) {
        return invoke(getMethod(paramObject.getClass(), paramString, new Class[0]), paramObject, new Object[0]);
    }

    public static Object invoke(String paramString, Object paramObject, Object... paramVarArgs) {
        if (paramVarArgs.length == 0)
            return invoke(paramString, paramObject);
        Class[] arrayOfClass = new Class[paramVarArgs.length];
        int i = 0;
        int j = paramVarArgs.length;
        while (i < j) {
            arrayOfClass[i] = paramVarArgs[i].getClass();
            i++;
        }
        return invoke(getMethod(paramObject.getClass(), paramString, arrayOfClass), paramObject, paramVarArgs);
    }

    public static Object invoke(Method paramMethod, Object paramObject) {
        return invoke(paramMethod, paramObject, new Object[0]);
    }

    public static Object invoke(Method paramMethod, Object paramObject, Object... paramVarArgs) {
        try {
            paramMethod.setAccessible(true);
            return paramMethod.invoke(paramObject, paramVarArgs);
        } catch (Exception exception) {
            Log.e(TAG, "", exception);
            return null;
        }
    }

    public static void setStaticValue(String paramString1, String paramString2, Object paramObject) {
        try {
            Class<?> clazz = getClass(paramString1);
            if (clazz == null)
                return;
            Field field = getField(clazz, paramString2);
            if (field != null) {
                field.setAccessible(true);
                field.set(null, paramObject);
                return;
            }
        } catch (Exception exception) {
            Log.e(TAG, "", exception);
        }
    }

    public static void setValue(String paramString1, String paramString2, Object paramObject1, Object paramObject2) {
        try {
            Class<?> clazz = getClass(paramString1);
            if (clazz == null)
                return;
            Field field = getField(clazz, paramString2);
            if (field != null) {
                field.setAccessible(true);
                field.set(paramObject1, paramObject2);
                return;
            }
        } catch (Exception exception) {
            Log.e(TAG, "", exception);
        }
    }
}
