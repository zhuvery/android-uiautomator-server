# Purpose
[![Android CI](https://github.com/openatx/android-uiautomator-server/actions/workflows/android.yml/badge.svg)](https://github.com/openatx/android-uiautomator-server/actions/workflows/android.yml)

[UIAutomator](http://developer.android.com/tools/testing/testing_ui.html) is a
great tool to perform Android UI testing, but to do it, you have to write java
code, compile it, install the jar, and run. It's a complex steps for all
testers...

This project is to build a light weight jsonrpc server in Android device, so
that we can just write PC side script to write UIAutomator tests.

# Build

- Run command:

```bash
$ ./gradlew build
$ ./gradlew packageDebugAndroidTest
```

- Build jar
```bash
$ # PC端
$ ./gradlew packFinalJar
$ adb push agent/release/uiautomator.jar /data/local/tmp
$ adb push agent/release/StartUiServer.sh /data/local/tmp
$ # 手机端
$ chmod 0755 /data/local/tmp/StartUiServer.sh
$ ./start.sh
```

- Run the jsonrpc server on Android device

```bash
$ ./gradlew cC
$ adb forward tcp:9008 tcp:9008 # tcp forward
```

If debug apk already installed, There is no need to use gradle.

simply run the following command

```
adb forward tcp:9008 tcp:9008
adb shell am instrument -w -r -e debug false -e class com.github.uiautomator.stub.Stub \
    com.github.uiautomator.test/androidx.test.runner.AndroidJUnitRunner
```

# Run
```bash
$ curl -X POST -d '{"jsonrpc": "2.0", "id": "1f0f2655716023254ed2b57ba4198815", "method": "deviceInfo", "params": {}}' 'http://127.0.0.1:9008/jsonrpc/0'
{'currentPackageName': 'com.smartisanos.launcher',
 'displayHeight': 1920,
 'displayRotation': 0,
 'displaySizeDpX': 360,
 'displaySizeDpY': 640,
 'displayWidth': 1080,
 'productName': 'surabaya',
 'screenOn': True,
 'sdkInt': 23,
 'naturalOrientation': True}
```

# Resources
- [Google UiAutomator Tutorial](https://developer.android.com/training/testing/ui-testing/uiautomator-testing?hl=zh-cn)
- [Google UiAutomator API](https://developer.android.com/reference/kotlin/androidx/test/uiautomator/package-summary)
- [Maven repository of uiautomator](https://mvnrepository.com/artifact/androidx.test.uiautomator/uiautomator)
- [androidx.test.uiautomator release notes](https://developer.android.com/jetpack/androidx/releases/test-uiautomator)

Clipboard, Thanks to @fplust

- https://github.com/majido/clipper
- https://github.com/appium/io.appium.settings/blob/5d3bc99ff35f3b816b4342395aba1bdea82ad48f/app/src/main/java/io/appium/settings/receivers/ClipboardReceiver.java

# The buildin input method
**com.github.uiautomator/.AdbKeyboard**

Encode the text into UTF-8 and then Base64

For example:

    "Hello 你好" -> (UTF-8 && Base64) = SGVsbG8g5L2g5aW9

Send to AdbKeyboard with broadcast

> Broadcast completed: result=-1 means success
```bash
# show ime list
$ adb shell ime list -s -a
com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
com.google.android.tts/com.google.android.apps.speech.tts.googletts.settings.asr.voiceime.VoiceInputMethodService
com.github.uiautomator/.AdbKeyboard

# enable and set ime
$ adb shell ime enable com.github.uiautomator/.AdbKeyboard
$ adb shell settings put secure default_input_method com.github.uiautomator/.AdbKeyboard

# Append text to input field
$ adb shell am broadcast -a ADB_KEYBOARD_INPUT_TEXT --es text SGVsbG8g5L2g5aW9
Broadcasting: Intent { act=ADB_KEYBOARD_INPUT_TEXT flg=0x400000 (has extras) }
Broadcast completed: result=-1

# Clear text
$ adb shell am broadcast -a ADB_KEYBOARD_CLEAR_TEXT
Broadcasting: Intent { act=ADB_KEYBOARD_CLEAR_TEXT flg=0x400000 }
Broadcast completed: result=-1

# Clear text before append text
$ adb shell am broadcast -a ADB_KEYBOARD_SET_TEXT --es text SGVsbG8g5L2g5aW9
Broadcasting: Intent { act=ADB_KEYBOARD_SET_TEXT flg=0x400000 (has extras) }
Broadcast completed: result=-1

# Send Keycode or Editor code according to the InputEditor requires
$ adb shell am broadcast -a ADB_KEYBOARD_SMART_ENTER
Broadcasting: Intent { act=ADB_KEYBOARD_SMART_ENTER flg=0x400000 }
Broadcast completed: result=-1

# Send keycode, eg: ENTER
$ adb shell am broadcast -a ADB_KEYBOARD_INPUT_KEYCODE --ei code 66
# Send Editor code, eg: 2
$ adb shell am broadcast -a ADB_KEYBOARD_EDITOR_CODE --ei code 2 # IME_ACTION_GO

# Get clipboard (without data)
$ adb shell am broadcast -a ADB_KEYBOARD_GET_CLIPBOARD
Broadcasting: Intent { act=ADB_GET_CLIPBOARD flg=0x400000 }
Broadcast completed: result=0

# Get clipboard (with data, base64 encoded)
$ adb shell am broadcast -a ADB_GET_KEYBOARD_CLIPBOARD
Broadcasting: Intent { act=ADB_GET_CLIPBOARD flg=0x400000 }
Broadcast completed: result=-1, data="5LqG6Kej5Lyg57uf5paH5YyW"

# show keyboard
$ adb shell am broadcast -a ADB_KEYBOARD_SHOW
# hide keyboard
$ adb shell am broadcast -a ADB_KEYBOARD_HIDE
```

- [Editor Code](https://developer.android.com/reference/android/view/inputmethod/EditorInfo)
- [Key Event](https://developer.android.com/reference/android/view/KeyEvent)

# Change GPS mock location
You can change mock location from terminal using adb in order to test GPS on real devices.

```
adb [-s <specific device>] shell am broadcast -a send.mock [-e lat <latitude>] [-e lon <longitude>]
        [-e alt <altitude>] [-e accurate <accurate>]
```

For example:

```
adb  shell am broadcast -a send.mock -e lat 15.3 -e lon 99
```

## Show toast

```
adb shell am start -n com.github.uiautomator/.ToastActivity -e message hello
```

## Float window

```
adb shell am start -n com.github.uiautomator/.ToastActivity -e showFloatWindow true # show
adb shell am start -n com.github.uiautomator/.ToastActivity -e showFloatWindow false # hide
```

# How to use with Python

```python
import uiautomator2 as u2

d = u2.connect()

d.screen.on()
d(text="Settings").click()
d(scrollable=True).scroll.vert.forward()
```

Refer to python wrapper library [uiautomator](https://github.com/xiaocong/uiautomator).

# How to generate changelog
[conventional-changelog](https://github.com/conventional-changelog/conventional-changelog/tree/master/packages/conventional-changelog-cli)

```bash
npm install -g conventional-changelog-cli
conventional-changelog -p grunt -i CHANGELOG.md -s -r 0
```

# Notes

If you have any idea, please email codeskyblue@gmail.com or [submit tickets](https://github.com/openatx/android-uiautomator-server/issues/new).

# Dependencies

- [nanohttpd](https://github.com/NanoHttpd/nanohttpd)
- [jsonrpc4j](https://github.com/briandilley/jsonrpc4j)
- [jackson](https://github.com/FasterXML/jackson)
- [androidx.test.uiautomator](https://mvnrepository.com/artifact/androidx.test.uiautomator/uiautomator-v18)

# Added features

- [x] support unicode input

# Thanks to
- [xiaocong](https://github.com/xiaocong)
- https://github.com/willerce/WhatsInput
- https://github.com/senzhk/ADBKeyBoard
- https://github.com/amotzte/android-mock-location-for-development
- https://github.com/gladed/gradle-android-git-version
