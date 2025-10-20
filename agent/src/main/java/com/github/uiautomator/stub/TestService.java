package com.github.uiautomator.stub;

import android.support.test.uiautomator.UiObjectNotFoundException;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;

public interface TestService {

    boolean playSound(String path);

    String ping();

    DeviceInfo deviceInfo();

    String dumpWindowHierarchy();

    String dumpWindowHierarchy(boolean compressed);

    String dumpWindowHierarchy(boolean compressed, int maxDepth);

    @JsonRpcErrors({@JsonRpcError(code = -32002, exception = UiObjectNotFoundException.class)})
    String getText(Selector paramSelector) throws UiObjectNotFoundException;
}
