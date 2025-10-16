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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.uiautomator.stub.Log;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;
//import android.support.test.uiautomator.UiDevice;
import com.android.uiautomator.core.UiDevice;

import android.support.test.uiautomator.UiObjectNotFoundException;

import android.app.Instrumentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.List;

import com.googlecode.jsonrpc4j.ErrorResolver;
import com.googlecode.jsonrpc4j.JsonRpcServer;

public class StubJar extends UiAutomatorTestCase {

    private static final int LAUNCH_TIMEOUT = 5000;
    // http://www.jsonrpc.org/specification#error_object
    private static final int CUSTOM_ERROR_CODE = -32001;

    private static final int PORT = 9008;
    private static final AutomatorHttpServer server = new AutomatorHttpServer(PORT);

    public static void main(String[] args) {
        System.out.println("hello UiAutomatorTestCase");
    }

    public void initAutomator() throws IOException, InterruptedException {
        Log.d("init automator");
        UiDevice uiDevice = getUiDevice();
        uiDevice.pressHome();
//        this.instrumentation = new FakeInstrument(uiDevice);
//        FakeInstrumentationRegistry.setInstrumentation(this.instrumentation);
//        Log.d("Get Instrument!!");
//        UiDevice.getInstance(this.instrumentation);


//        JsonRpcServer jrs = new JsonRpcServer(new ObjectMapper(), new AutomatorServiceImpl(), AutomatorService.class);
        JsonRpcServer jrs = new JsonRpcServer(new ObjectMapper(), new TestServiceImpl(), TestService.class);
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
        while (server.isAlive()) {
            Thread.sleep(500);
        }
    }

    public void testUIAutomatorStub() throws InterruptedException {
        Log.d("start new uiautomator....");
        System.out.println("start new uiautomator....");
        try {
            this.initAutomator();
        } catch (Exception exception) {
            Log.e(" exception.getMessage()" + exception.getMessage());
            return;
        }

//        UiDevice device = getUiDevice();
//        device.pressHome();
    }
//
//    public void testClickByText() throws Exception {
//        UiDevice device = getUiDevice();
//        UiObject settings = new UiObject(new UiSelector().text("Settings"));
//        if (settings.exists()) {
//            settings.click();
//        }
//    }
}
