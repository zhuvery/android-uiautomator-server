/*
 * The MIT License (MIT)
 * Copyright (c) 2015 xiaocong@gmail.com, 2018 codeskyblue@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.uiautomator.stub;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.test.filters.SdkSuppress;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.Until;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.ErrorResolver;
import com.googlecode.jsonrpc4j.JsonRpcServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Use JUnit test to start the uiautomator jsonrpc server.
 *
 * @author xiaocong@gmail.com
 */
public class Stub {
    private static final int LAUNCH_TIMEOUT = 5000;
    // http://www.jsonrpc.org/specification#error_object
    private static final int CUSTOM_ERROR_CODE = -32001;

    private static final int PORT = 9008;
    private static final AutomatorHttpServer server = new AutomatorHttpServer(PORT);


    public static void main(String[] args) throws RemoteException, IOException {
        Log.d("-----start main function111-dafdsaf----");
        System.out.println("start main function111sss1");
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.wakeUp();

        JsonRpcServer jrs = new JsonRpcServer(new ObjectMapper(), new AutomatorServiceImpl(), AutomatorService.class);
        jrs.setShouldLogInvocationErrors(true);
        jrs.setErrorResolver(new ErrorResolver() {
            @Override
            public JsonError resolveError(Throwable throwable, Method method, List<JsonNode> list) {
                String data = throwable.getMessage();
                if (!throwable.getClass().equals(UiObjectNotFoundException.class)) {
                    throwable.printStackTrace();
                    StringWriter sw = new StringWriter();
                    throwable.printStackTrace(new PrintWriter(sw));
                    data = sw.toString();
                }
                return new JsonError(CUSTOM_ERROR_CODE, throwable.getClass().getName(), data);
            }
        });
        server.route("/jsonrpc/0", jrs);
        server.start();
    }
//
//    private void launchPackage(String packageName) {
//        Log.i("Launch " + packageName);
//        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
//        Context context = InstrumentationRegistry.getInstrumentation().getContext();
//        final Intent intent = context.getPackageManager()
//                .getLaunchIntentForPackage(packageName);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        context.startActivity(intent);
//
//        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), LAUNCH_TIMEOUT);
//        device.pressHome();
//    }
//
//    private void launchTestApp() throws RemoteException {
//        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
//        device.wakeUp();
//
//        // Wait for launcher
//        String launcherPackage = device.getLauncherPackageName();
//        Boolean ready = device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
//        if (!ready) {
//            Log.i("Wait for launcher timeout");
//            return;
//        }
//
//        Log.d("Launch service");
//    }
//
//    private void startMonitorService(Context context) {
//        Intent intent = new Intent("com.github.uiautomator.ACTION_START");
//        intent.setPackage("com.github.uiautomator"); // fix error: Service Intent must be explicit
//        context.startService(intent);
//    }
//
////    @After
////    public void tearDown() {
////        server.stop();
////        //Context context = InstrumentationRegistry.getContext();
////        //stopMonitorService(context);
////    }
//
//    private boolean checkAccessibilityQuery() throws InterruptedException {
//        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
//        // check if app_process still alive
//        for (int i = 3; i > 0; i--) {
//            AccessibilityNodeInfo nodeInfo = instrumentation.getUiAutomation().getRootInActiveWindow();
//            if (nodeInfo != null) {
//                return true;
//            }
//            if (i > 1) Thread.sleep(1000);
//        }
//        return false;
//    }

//    @Test
//    @LargeTest
//    public void testUIAutomatorStub() throws InterruptedException {
//        // stop the server with "am force-stop com.github.uiautomator"
//        Log.i("server started");
//        while (server.isAlive()) {
//            if (!checkAccessibilityQuery()) {
//                Log.e("uiAutomation.getRootInActiveWindow() always return null, okhttpd server quit");
//                return;
//            }
//            Thread.sleep(500);
//        }
//    }
}

