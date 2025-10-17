package com.github.uiautomator.stub;

public interface TestService {

    boolean playSound(String path);

    String ping();

    DeviceInfo deviceInfo();

    String dumpWindowHierarchy();

    String dumpWindowHierarchy(boolean compressed);

    String dumpWindowHierarchy(boolean compressed, int maxDepth);
}
