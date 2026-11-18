# SK7 Kiosk SDK

[SUNTEK Docs](https://github.com/SUNTEK-Official/suntek-docs)

SUNTEK SK7 kiosk system API SDK. Use it on the kiosk Android OS to control device information, display, network, system, hardware peripherals, and industry-specific features.

Website: [www.suntekai.com](https://www.suntekai.com/)  
Support: [support@suntekai.com](mailto:support@suntekai.com)

Bundled API manual: `API documentation.docx` V1.3 (2025-12-05).

---

## Contents

```
.
├── smdt.jar                  # SDK library (use SmdtManagerNew)
├── SmdtApiDemo.apk           # Prebuilt demo APK for on-device trial
├── API documentation.docx    # Full API manual (parameters, returns, samples)
└── SmdtApiDemo/              # Demo source (Android Studio)
    └── SmdtApiDemo/SmdtApiDemo/
        └── app/
            ├── libs/smdt.jar
            └── src/main/java/android/app/smdt/apidemo/
```

| File | Purpose |
|------|---------|
| `smdt.jar` | SDK to add to your app |
| `SmdtApiDemo.apk` | Install on a device to try APIs without building |
| `API documentation.docx` | Per-method parameters, return values, notes |
| `SmdtApiDemo/` | Sample calls, organized by module |

---

## Requirements

- Device: SK7 kiosk (SUNTEK customized Android)
- Android Studio 3.6+ (demo uses AGP `3.6.3` + Gradle `5.6.4`)
- `compileSdk` / `targetSdk`: 30
- `minSdk`: 22
- Java 1.8

These APIs depend on device system services. **Run them on a real SK7.** Emulators and non-SUNTEK Android builds return "not supported".

---

## Quick start

### 1. Add `smdt.jar`

1. Copy `smdt.jar` from the repo root into your app's `app/libs/`
2. In Android Studio, right-click the jar and choose **Add as Library**
3. Or declare it in `app/build.gradle`:

```gradle
dependencies {
    implementation files('libs/smdt.jar')
}
```

### 2. Get the singleton

```java
import android.app.smdt.SmdtManagerNew;

SmdtManagerNew smdt = SmdtManagerNew.getInstance(context);
String version = smdt.info_getApiVersion();   // e.g. V1.0.0-release
```

All APIs go through this instance. The demo initializes it once in `BaseFragment`:

```java
smdtManagerNew = SmdtManagerNew.getInstance(mContext);
```

### 3. Minimal example

```java
SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);

// Device info
String model  = smdt.info_getModel();
String serial = smdt.info_getSerialNumber();
String mem    = smdt.info_getAvailMemory();

// Hide status / navigation bars (typical kiosk setup)
smdt.disp_setStatusBar(false);
smdt.disp_setNavigationBar(false);

// Reboot
smdt.sys_setReboot();
```

---

## Naming

Method names are **module prefix + verb + object**:

| Prefix | Module | Typical capabilities |
|--------|--------|----------------------|
| `info_` | Device info | Model, serial, versions, memory/storage, CPU, temperature |
| `disp_` | Display | Backlight, resolution, rotation, screenshot, status/nav bar, HDMI |
| `net_` | Network | Ethernet / Wi-Fi / 4G, static IP, hotspot, priority, multi-network |
| `sys_` | System | Power, watchdog, silent install, volume, GPIO, OTA, logs |
| `dev_` | Hardware | UART, I2C, SPI, CAN, USB power, private partition, RGB LED |
| `custom_` | Custom | Wiegand, relay, dialer, process, app encryption, LED strip |

Common verbs: `get` / `set` / `open` / `close` / `read` / `write` / `add` / `del` / `send` / `receive` / `do`.

The jar also contains the legacy `SmdtManager` (`SmdtManager.create(context)`). **New projects should use `SmdtManagerNew` only**, matching the official manual and the demo.

---

## Feature overview

### Device info `info_`

Board type, model, serial number, manufacturer, Android / software / hardware / kernel / MCU / NPU / WebView versions, security patch, face-detect support, total/available memory and storage, CPU ID / temperature / frequency / usage, device temperature.

### Display `disp_`

- Up to 4 screens: resolution, rotation (reboot required), overscan (80–100)
- Up to 3 backlight channels: brightness, on/off, PWM frequency
- Screenshot (Bitmap or save to a path)
- Boot logo / boot animation
- Status bar, navigation bar, pull-down bar, gesture bar, SystemUI mode
- Launcher icon hide list
- HDMI-In / HDMI-Out, 4K media mode

### Network `net_`

- ETH0 / ETH1 / Wi-Fi MAC, current network type
- DHCP / static IP, gateway, netmask, DNS
- Wi-Fi connect, default SSID/password, hotspot, RSSI
- Network guard, speed limit, network priority, multi-network coexistence
- IMEI / ICCID / IMSI (when a cellular module is present)

### System `sys_`

- Bluetooth, location, airplane mode, soft keyboard, key/touch reporting
- Language, timezone, time format, NTP, network time sync, font size
- Default launcher, boot app, daemon process
- Silent install / uninstall, black/white lists, auto-install policy
- Media volume, mute, speaker gain, audio input/output
- GPIO and peripheral power (Wi-Fi / 4G / LAN / fan / panel, etc.)
- Watchdog, daily scheduled power on/off, one-shot scheduled power on/off
- Power off / reboot / MCU reboot, factory reset
- Local firmware update (including A/B), log capture, ADB / network ADB, OTG

### Hardware `dev_`

- SD card / USB disk paths, unmount external storage
- Private partition read/write (eMMC / EEPROM)
- UART `uart0`–`uart10`: open, send, receive via callback
- Camera VID/PID, orientation, mirror, rotation
- I2C / SPI / CAN
- USB port power (USBOTG1 / USBHOST1–3)
- RGB LED: red / green / white

### Custom `custom_`

- Wiegand 26/34: send card ID, read-card callback
- Relay mode and on/off
- Headless dial / hang up
- PID by package name, kill process, clear recent tasks
- App encryption list, custom desktop shortcuts, LED-strip brightness

---

## Return values and error codes

Most `set` / `do` APIs return `int`. `0` means success; negative values are errors. Convert a code with `sys_getErrorDescription(code)`.

| Constant | Value | Meaning |
|----------|-------|---------|
| `ErrorCode.RET_API_OK` | `0` | Success |
| `ErrorCode.RET_API_ERR_NG` | `-1` | Not supported |
| `ErrorCode.RET_API_ERR_PARA` | `-2` | Invalid parameter |
| `ErrorCode.RET_API_ERR_FILE_EXISTS` | `-3` | File does not exist |
| `ErrorCode.RET_API_ERR_PERMISSION_DENIED` | `-4` | Permission denied |
| `ErrorCode.RET_API_ERR_EXCEPTION` | `-5` | Exception |
| `ErrorCode.RET_API_ERR_PROPERTIES_EXISTS` | `-6` | Property does not exist |
| `ErrorCode.RET_API_ERR_METHOD` | `-7` | Method does not exist |
| `ErrorCode.RET_API_ERR_LITE` | `-8` | Not supported on lite firmware |

Some APIs return `-1` / `-8` depending on model, OS version, or lite firmware. Confirm support on the target unit with the demo first.

Async work uses callbacks — do not block waiting for a return value:

| Callback | Use |
|----------|-----|
| `InstallCallback` | Silent install |
| `DeleteCallback` | Silent uninstall |
| `UpdateCallback` | A/B firmware update |
| `CopyCallback` | File copy |
| `DataCallback` | UART receive |
| `WiegandCallback` | Wiegand card read |
| `LogCallback` | Line-by-line logs |
| `BackUpCallback` / `RecoveryCallback` | App backup / restore |

```java
smdt.sys_doSilentInstallApp("/sdcard/app.apk", new SmdtManagerNew.InstallCallback() {
    @Override
    public void onInstallFinished(String packageName, int returnCode) {
        // returnCode == 0 means success
    }
});
```

---

## Run the demo

Prebuilt: install `SmdtApiDemo.apk` on an SK7. App name: **OS ApiDemo**.

From source:

1. Open `sample-android` in Android Studio
2. Point `sdk.dir` in `local.properties` at your Android SDK
3. Sync and Run on the device (`applicationId`: `android.app.smdt.apidemo`)

Tabs map to API modules:

| Tab | Source | Prefix |
|-----|--------|--------|
| About | `MainFragment` | `info_getApiVersion` |
| Device Information | `DeviceFragment` | `info_` |
| Display Manage | `DisplayFragment` | `disp_` |
| Network Manage | `NetFragment` | `net_` |
| System Control | `SysFragment` | `sys_` |
| Hardware Control | `HardwareFragment` | `dev_` |
| Customer | `CustomFragment` | `custom_` |

UI languages: Chinese, English, Traditional Chinese, Russian, Spanish. Long-press a result to copy it.

---

## Notes

1. **Call these APIs only on SK7 / SUNTEK OS.** Stock Android does not provide the system services.
2. Some settings take effect after reboot: screen rotation, network priority, multi-network, default Wi-Fi, audio I/O, launcher hide list, app encryption, and similar.
3. `sys_setKeyReport(false)` / `sys_setTouchReport(false)` disable input. The demo turns them back on when you open About. If you disable them by mistake, restart the APK.
4. Power off, reboot, factory reset, firmware update, watchdog, and silent install are high-risk. Validate on a test unit first.
5. Do not cut power during an update. A/B updates use `sys_doUpdatePackageAB` + `UpdateCallback`.
6. Not every SK7 exposes every API. GPIO count, UART count, HDMI, Wiegand, relay, and 4G depend on the hardware SKU.
7. For debugging, use `sys_setApiDebugLevel(0~3)` and capture logs with `sys_getSystemLog` / `sys_getProcessLogcat`.

---

## Common kiosk scenarios

```java
SmdtManagerNew smdt = SmdtManagerNew.getInstance(context);

// 1. Boot into the kiosk app: hide system UI, set default launcher and boot app
smdt.disp_setStatusBar(false);
smdt.disp_setNavigationBar(false);
smdt.disp_setStatusBarDrag(false);
smdt.sys_setDefaultLauncher("com.example.kiosk");
smdt.sys_setSystemBootApp("com.example.kiosk");

// 2. Watchdog: reboot if the app hangs and stops feeding
smdt.sys_setWatchDog(true, 60);   // 60 seconds
smdt.sys_setWatchDogFeed();

// 3. Scheduled power (daily 08:00 on, 22:00 off)
smdt.sys_setAutoPowerOnOff(true, new int[]{1,1,1,1,1,1,1}, 8, 0, 22, 0);

// 4. UART for a scanner / printer
String path = smdt.dev_getUartPath("uart1");
smdt.dev_openUart(path, 115200, 8, 1, 0, 0);
smdt.dev_receiveUart(path, data -> { /* handle received data */ });

// 5. Wiegand card + relay door
smdt.custom_readWiegandData(card -> { /* verify, then open */ });
smdt.custom_setRelayIoEnable(true);
```

Full parameters, ranges, and model differences are in [`README.md`](../README.md).


---

# SK7 Android API Reference (`smdt.jar`)

December 05, 2025
V1.3
API usage method

Get object instance method

Function name : public static SmdtManagerNew getInstance(Context context)

Description : get SmdtManagerNew A single instance of an object

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| context | Context | Context |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | SmdtManagerNew | SmdtManagerial New object for return singleton |

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>System.out.println(smdt.info_getApiVersion());<br>Output Result : V1.0.0-release |
| :--- |

#### Import JAR package

#### Android Studio

Copy smdt.ar to the project directory \ app \ libs \;

2.Right click on the jar file in the libs folder and select add as Library;

3.Then call the method after the instance object in the code.

### 1.4 API naming rules

The API consists of three parts: name, verb, and minute. The name should match the functionality of the API.

| Starting with 'name' | meaning |
| :--- | :--- |
| info | device information class |
| disp | Display management category |
| net | Network control class |
| sys | System control class |
| dev | Hardware control category |
| custom | Customer customization category |

| verb | meaning |
| :--- | :--- |
| get | get |
| set | set |
| write | write |
| read | read |
| open | open |
| close | close |
| reset | reset |
| register | register |
| unregister | Cancel register |
| check | check |
| test | test |
| change | change |
| sync | sync |
| add | add |
| del | del |
| send | send |
| receive | receive |
| do | Do |

### 1.5 Global error code

#### Error code category ErrorCode

| variable name | error code | explanation |
| :--- | :--- | :--- |
| RET_API_OK | 0 | success |
| RET_API_ERR_NG | -1 | Not supported |
| RET_API_ERR_PARA | -2 | Parameter incorrect |
| RET_API_ERR_FILE_EXISTS | -3 | File does not exist |
| RET_API_ERR_PERMISSION_DENIED | -4 | No permission |
| RET_API_ERR_EXCEPTION | -5 | Exception thrown |
| RET_API_ERR_PROPERTIES_EXISTS | -6 | Attribute does not exist |
| RET_API_ERR_METHOD | -7 | Method does not exist |
| RET_API_ERR_LITE | -8 | Simplify version not supported |

### 2. Device information

#### 2.1, version

#### 2.1.1, info_getApiVersion

Function name : public String info_getApiVersion()

Description : Get current API version number

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | API version number |

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>System.out.println(smdt.info_getApiVersion());<br>Output result : V1.0.0-release |
| :--- |

notes

null

#### 2.1.2, info_getAndroidVersion

Function name : public String info_getAndroidVersion()

Description : get Android system version

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Android system version |

#### 2.1.3, info_getSoftwareVersion

Function name : public String info_getSoftwareVersion()

Description : Getdevice software version number

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Device software version number |

#### 2.1.4, info_getHardwareVersion

Function name : public String info_getHardwareVersion()

Description : Get device hardware version parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Device hardware version |

#### 2.1.5, info_getKernelVersion

Function name : public String info_getKernelVersion()

Description : getdevice kernel version

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | kernel version |

#### 2.1.6, info_getMCUVersion

Function name : public String info_getMCUVersion()

Description : get MCU version number

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | MCU version number |

#### 2.1.7, info_getSecurityVersion

Function name : public String info_getSecurityVersion()

Description : Get Android security patch level

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | security patch date |

#### 2.1.8, info_getNPUVersion

Function name : public String info_getNPUVersion()

Description : Get NPU driver version number

parameter : null

API version : V2.3.0 or above

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | NPU driver version number |

#### 2.1.9, info_getWebViewVersion

Function name : public String info_getWebViewVersion()

Description : get WebView version

API version : V2.14.0 or above

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | WebView version |

### 2.2 Storage

#### 2.2.1, info_getTotalMemory

Function name : public String info_getTotalMemory()

Description : getdevice Total memory capacity

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Total memory capacity |

notes

Unit of return :

Remaining 1GB or more, return GB

#### Remaining 1GB or less, return MB

#### 2.2.2, info_getAvailMemory

Function name : public String info_getAvailMemory()

Description : Getdevice remaining can use memory capacity

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Remaining can use memory capacity |

notes

Return size unit :

Return GB for 1GB or more

1GB or less, return MB for 1M or more

Below 1M, return KB for 1KB or more

#### Return B below 1KB

#### 2.2.3, info_getTotalStorage

Function name : public String info_getTotalStorage()

Description : getdevice total storage capacity

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Device total storage capacity |

notes

return size unit: GB

#### 2.2.4, info_getAvailStorage

Function name : public String info_getAvailStorage()

Description : Getdevice remaining can use storage capacity

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Remaining can use storage capacity |

notes

Return size unit :

Return GB for 1GB or more

1GB or less, return MB for 1M or more

Below 1M, return KB for 1KB or more

#### Return B below 1KB

#### 2.2.5, info_getAppUsedMemory

Function name : public String info_getAppUsedMemory()

Description : Memory occupied by getAPP

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | The memory occupied by the APP |

notes

#### The unit is MB

#### 2.3, model

#### 2.3.1, info_getBoardType

Function name : public String info_getBoardType()

Description : get Board Model

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Board Model |

#### 2.3.2, info_getModel

Function name : public String info_getModel()

Description : getdevice model

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Device model |

#### 2.3.3, info_getFactoryCompany

Function name : public String info_getFactoryCompany()

Description : getdevice manufacturer

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Device manufacturer |

#### 2.4, serial number

#### 2.4.1, info_getSerialNumber

Function name : public String info_getSerialNumber()

Description : get serial number

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | serial number |

#### 2.5, CPU

#### 2.5.1, info_getCpuTemperature

Function name : public String info_getCpuTemperature()

Description : get device CPU temperature

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | CPU Current Temperature |

notes

#### The unit is ℃

#### 2.5.2, info_getCpuFrequency

Function name : public String info_getCpuFrequency()

Description : getdevice CPU frequency

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | CPU frequency |

notes

#### The unit is GHZ

#### 2.5.3, info_getCpuUsage

Function name : public String info_getCpuUsage()

Description : getdevice CPU utilization rate

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | CPU utilization rate |

notes

Unit %

### 2.6 Face recognition

#### 2.6.1, info_getFaceDetectSupport

Function name : public int info_getFaceDetectSupport()

Description : Do you support facial recognition

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: Support 0: Not supported |

#### 2.7, other

#### 2.7.1, info_getDeviceTemperature

Function name : public float info_getDeviceTemperature()

Description : Ge tdevice temperature

API version : V2.11.0 and above

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | float | Current CPU temperature |

notes

### The unit is ℃

### 3. Display management

### 3.1 Display screen

#### 3.1.1, disp_setDispParams

Function name : public int disp_setDispParams(String params)

Description : Set screen display parameters

API version : V2.9.0 or above

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| params | String | Screen parameter, refer to the cfg file of the screen parameter, input the concatenated string, and separate the parameters with ";" |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>String params = "cpu_id=3568;disp_main=lvds;disp_aux=hdmi;";<br>int result = smdt.disp_setDispParams(params); |
| :--- |

notes

#### Pass in empty 'cancelset' to restore default screen parameters

#### 3.1.2, disp_getDispParams

Function name : public int disp_getDispParams()

Description : Get screen display parameters

API version : V2.9.0 or above

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Screen display parameters |

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>String params = smdt.disp_getDispParams();<br>System.out.println(params);<br>Output result : "cpu_id=3568;disp_main=lvds;disp_aux=hdmi;" |
| :--- |

notes

#### Output empty as default, screen parameters not configured through external device

#### 3.1.3, disp_setDisplayOverScan

Function name : public int disp_setDisplayOverScan(int screen_id, String direction, int value)

Description : Set display area (screen zoom) simplify version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| screen_id | int | Screen ID 0: Main screen 1: Secondary screen |
| direction | String | Direction:<br>x: X-axis (both left and right simultaneously)<br>y: Y-axis (both up and down simultaneously)<br>left: left side<br>right: right side<br>top: top<br>bottom: bottom<br>all: all four sides |
| value | int | Distance from the boundary |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

set range 80-100

The primary and secondary screens depend on the system and screen parameter definitions, not the number of screens. For example, if the primary screen is set to LVDS and the secondary screen to HDMI, and one HDMI screen is connected, the HDMI screen also becomes the secondary screen, and the required parameter is also "secondary screen".

#### 3.1.4, disp_getDisplayOverScan

Function name : public int[] disp_getDisplayOverScan(int screen_id)

Description : Get display area value (screen zoom)

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| screen_id | int | Screen ID 0: Main screen 1: Secondary screen |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int[] | An array of four edge distances (member order: 0 left, 1 right, 2 top, 3 bottom) |

notes

Range 80-100

The primary and secondary screens depend on the system and screen parameter definitions, not the number of screens. For example, if the primary screen is set to LVDS and the secondary screen to HDMI, and one HDMI screen is connected, the HDMI screen also becomes the secondary screen, and the required parameter is also "secondary screen".

#### 3.1.5, disp_getScreenWidth

Function name : public int disp_getScreenWidth(int screen_id)

Description : get the specified display screen minute resolution-width

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| screen_id | int | Screen ID 0: Main screen 1: Secondary screen |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | width |

notes

The primary and secondary screens depend on the system and screen parameter definitions, not the number of screens. For example, if the primary screen is set to LVDS and the secondary screen to HDMI, and one HDMI screen is connected, the HDMI screen also becomes the secondary screen, requiring the parameter to be passed as "one secondary screen" .

#### 3.1.6, disp_getScreenHeight

Function name : public int disp_getScreenHeight(int screen_id)

Description : get specified display minute resolution - high

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| screen_id | int | Screen ID 0: Main screen 1: Secondary screen |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | high |

notes

The primary and secondary screens depend on the system and screen parameter definitions, not the number of screens. For example, if the primary screen is set to LVDS and the secondary screen to HDMI, and one HDMI screen is connected, the HDMI screen also becomes the secondary screen, and the required parameter is also "secondary screen".

### 3.2 Backlight

#### 3.2.1, disp_setLcdBackLight

Function name : public int disp_setLcdBackLight(int screen_id, int brightness, int frequency, boolean save)

Description : Set screen backlight brightness

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| screen_id | int | 0 First backlight channel: System default interface 1 Second backlight channel: External PWM (MCU) interface |
| brightness | int | brightness value |
| frequency | int | setting an external PWM, the frequency needs to be passed in. |
| save | boolean | the brightness value to the database. |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The frequency parameter is used for the external PWM interface . You can first get the default frequency and then set it directly .

On some minute platforms , such as the RK3568 second backlight, the system default interface is used instead of the external PWM (MCU) interface . No frequency needs to be passed in , and even if it is, it will not change.

setting , first obtain the maximum and minimum brightness values for the corresponding interface , and then set them.

After setting the system default interface , it is recommended to use the parameter save function to save the data to the database after the operation is complete. This can reduce the number of database operations.

#### 3.2.2, disp_getLcdBackLight

Function name : public int disp_getLcdBackLight(int screen_id)

Description : Get screen backlight brightness

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| screen_id | int | 0 First backlight channel: System default interface 1 Second backlight channel: External PWM (MCU) interface |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | Backlight brightness value |

#### 3.2.3, disp_getLcdPwmFrequency

Function name : public int disp_getLcdPwmFrequency(int screen_id)

Description : get backlight frequency

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| screen_id | int | 0 First backlight channel: System default interface 1 Second backlight channel: External PWM (MCU) interface |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | backlight frequency |

notes

after 11.0, if the platform is null , you need to use `setGetFrequency` ; if the interface is null , use `getFrequency`.

The backlight frequency is generally passed in when setting the backlight via the PWM interface ; it is not supported for setting it separately.

It's generally used to get the frequency of an external PWM , such as the system default interface (which is not currently supported for getting ). It's also used with devices like the RK3568, where both channels use the system default interface .

#### 3.2.4, disp_getLcdBackLightMaxMin

Function name : public int disp_getLcdBackLightMaxMin(int screen_id, String type)

Description : Get screen brightness maximum or minimum value

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| screen_id | int | 0 First backlight channel: System default interface 1 Second backlight channel: External PWM (MCU) interface |
| type | int | max: max value min: min value |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | Get screen brightness maximum or minimum value |

#### 3.2.5, disp_setLcdBackLightEnable

Function name : public int disp_setLcdBackLightEnable(int screen_id, boolean enable)

Description : Set screen backlight switch

API version : V2.16.0 or above

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| screen_id | int | 0: First backlight path; 1: Second backlight path |
| enable | boolean | true: on; false: off |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 3.2.6, disp_getLcdBackLightEnable

Function name : public int disp_getLcdBackLightEnable(int screen_id)

Description : get screen backlight switch status

API version : V2.16.0 or above

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| screen_id | int | 0: First backlight path; 1: Second backlight path |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: On 0: Off |

### 3.3 System UI

#### 3.3.1, disp_setDisplayDensity

Function name : public int disp_setDisplayDensity(int value)

Description : set display density DPI

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| value | int | DPI value |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

value of the set should be greater than or equal to 72.

#### 3.3.2, disp_getDisplayDensity

Function name : public int disp_getDisplayDensity()

Description : get current display density DPI

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | DPI value |

#### 3.3.3, disp_getScreenShotBitmap

Function name : public Bitmap disp_getScreenShotBitmap()

Description : Screenshot and return a bitmap format image.

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | Bitmap | Image data |

#### 3.3.4, disp_getScreenShot

Function name : public int disp_getScreenShot(String filepath)

Description : Take a screenshot and save it to a specified path.

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| filepath | int | Image save path |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The saved image is in PNG format.

When an empty string is passed in, a default path will be automatically saved: the Screenshots folder in the root directory.

#### 3.3.5, disp_setDisplayRotation

Function name : public int disp_setDisplayRotation(int screen_id, int degree)

Description : Set screen rotation angle

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| screen_id | int | Screen ID 0: Main screen 1: Secondary screen |
| degree | int | Rotation angle: 0/90/180/270 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

Only supports setting four angles: 0/90/180/270.

The primary and secondary screens depend on the system and screen parameter definitions, not the number of screens. For example, if the primary screen is set to LVDS and the secondary screen to HDMI, and one HDMI screen is connected, the HDMI screen also becomes the secondary screen, and the required parameter is also "secondary screen".

#### 3.3.6, disp_getDisplayRotation

Function name : public int disp_getDisplayRotation(int screen_id)

Description : get screen rotation angle

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| screen_id | int | Screen ID 0: Main screen 1: Secondary screen |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | Rotation angle: 0/90/180/270 |

notes

The primary and secondary screens depend on the system and screen parameter definitions, not the number of screens. For example, if the primary screen is set to LVDS and the secondary screen to HDMI, and one HDMI screen is connected, the HDMI screen also becomes the secondary screen, requiring the parameter to be passed as "one secondary screen" .

#### 3.3.7, disp_setBootLogo

Function name : public int disp_setBootLogo(String filepath)

Description : Replaces the power on logo

API version : V2.8.0 or above

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| filepath | String | File path |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

Large images and 4K resolution are not supported .

Passing an empty file path will result in `cancelset` , restoring the default settings.

File requirements: BMP format image, 8-bit depth (Allwinner platform has a 24-bit depth).

Setting incorrect files can easily lead to system errors.

#### 3.3.8, disp_getBootLogo

Function name : public int disp_getBootLogo()

Description : get the current power on Logo file path

API version : V2.8.0 or above

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | power on Logo: If not empty, it will be custom; otherwise, it will be the default. |

#### 3.3.9, disp_setBootAnimation

Function name : public int disp_setBootAnimation(String filepath)

Description : Replace the power on animation

API version : V2.8.0 or above

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| filepath | String | File path |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

Passing an empty file path will result in `cancelset` , restoring the default settings.

File Requirements: An animation package created according to Android requirements, in zip format.

Setting incorrect files can easily lead to system errors.

#### 3.3.10, disp_getBootAnimation

Function name : public int disp_getBootAnimation()

Description : getpower on animation file path

API version : V2.8.0 or above

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | power on Logo: If not empty, it will be custom; otherwise, it will be the default. |

#### 3.3.11, disp_setStatusBar

Function name : public int disp_setStatusBar(boolean enable)

Description : The status column displays the status.

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: show; false: hide. |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 3.3.12, disp_getStatusBar

Function name : public int disp_getStatusBar()

Description : The current status bar displays the status.

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: Show 0: Hide |

#### 3.3.13, disp_setNavigationBar

Function name : public int disp_setNavigationBar(boolean enable)

Description : Set the navigation bar to display status.

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: show; false: hide. |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 3.3.14, disp_getNavigationBar

Function name : public int disp_getNavigationBar()

Description : Get the current navigation bar status .

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: Show 0: Hide |

#### 3.3.15, disp_setStatusBarDrag

Function name : public int disp_setStatusBarDrag(boolean enable)

Description : Can the "set status" field be scrolled down?

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: Enable; false: Disable |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 3.3.16, disp_getStatusBarDrag

Function name : public int disp_getStatusBarDrag()

Description : Can the "get status" field be scrolled down?

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: Enable 0: Disable |

#### 3.3.17, disp_setGestureBar

Function name : public int disp_setGestureBar(boolean enable)

Description : Can the set gesture bring up the navigation bar?

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: Enable; false: Disable |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 3.3.18, disp_getGestureBar

Function name : public int disp_getGestureBar()

Description : Can the GET gesture bring up the navigation bar?

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: Enable 0: Disable |

#### 3.3.19, disp_setSystemUIMode

Function name : public int disp_setSystemUIMode(boolean enable)

Description : Set SystemUI mode

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: OS mode; false: native Android. |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

setting , the navigation bar needs to be hidden and then redisplayed for the changes to take effect.

In OS mode, the status bar does not support displaying notification functionality.

#### 3.3.20, disp_getSystemUIMode

Function name : public int disp_getSystemUIMode()

Description : get current SystemUI mode

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: OS mode 0: Android native |

#### 3.3.21, disp_addAppLauncherHideList

Function name : public int disp_addAppLauncherHideList(String packageName)

Description : Add the applications whose icons need to be hidden.

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | application package name |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

After setting , reboot will take effect.

#### 3.3.22, disp_getAppLauncherHideList

Function name : public List disp_getAppLauncherHideList()

Description : The application of getset to hide icons

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | All apps whose names are hidden are set to "name" . |

#### 3.3.23, disp_delAppLauncherHideList

Function name : public int disp_delAppLauncherHideList(String packageName)

Description : Delete apps whose icons need to be hidden.

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | application package name |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

Passing 'clean' will delete all applications set .

#### 3.4, HDMI

#### 3.4.1, disp_getHdmiInStatus

Function name : public int disp_getHdmiInStatus()

Description : Get hdmi in status value

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: Connected 0: Not connected |

notes

The interface is unusable and not currently supported.

#### 3.4.2, disp_setHdmiInAudio

Function name : public int disp_setHdmiInAudio(boolean enable)

Description : Set HDMI in the volume switch

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The interface is unusable and not currently supported.

#### 3.4.3, disp_getHdmiInAudio

Function name : public int disp_getHdmiInAudio

Description : get hdmi in sound switch status

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

notes

The interface is unusable and not currently supported.

#### 3.4.4, disp_setHdmiOutStatus

Function name : public int disp_setHdmiOutStatus(boolean enable)

Description : Set hdmi out switch status

API version : V2.13.0 or above

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 3.4.5, disp_getHdmiOutStatus

Function name : public int disp_getHdmiOutStatus()

Description : Get HDMI out switch status

API version : V2.13.0 or above

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

### 4. Network Management

### 4.1 Information

#### 4.1.1, net_getMacAddress

Function name : public String net_getMacAddress(String type)

Description : get the MAC address of the network card device .

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | String | network type eth0: Ethernet eth1: Ethernet 1 wlan0: WiFi |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | MAC address |

notes

API version V2.12.0+ can use Constant class

The obtained Wi-Fi MAC address is the local machine's MAC address.

#### 4.1.2, net_getCurrentNetType

Function name : public String net_getCurrentNetType()

Description : get the current network connection type

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | WIFI: WIFI<br>ETH: Ethernet<br>MOBILE: Mobile network<br>UNKNOWN:Unknown type |

notes

API version V2.12.0+ can use Constant class

#### 4.1.3, net_getNetWorkInf

Function name : public NetworkInfoData net_getNetWorkInf(String type)

Description : get current network information

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | String | network type eth0: Ethernet eth1: Ethernet 1 wlan0: WiFi |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | NetworkInfoData | An object that stores network information |

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>NetworkInfoData mNetworkInfoData = smdtManagerNew.net_getNetWorkInf("eth0");<br>if (mNetworkInfoData != null) {<br>String ip = mNetworkInfoData.getIp();<br>String gateway = mNetworkInfoData.getGateway();<br>String netmask = mNetworkInfoData.getNetmask();<br>String dns1 = mNetworkInfoData.getDns1();<br>String dns2 = mNetworkInfoData.getDns2();<br>} |
| :--- |

notes

API version V2.12.0+ can use Constant class

For specific information, please refer to the Network Information object class.

#### 4.1.4, net_getWifiRssi

Function name : public int net_getWifiRssi(int level)

Description : Get Wi-Fi connection signal

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| level | int | minute- level, for example: sending 5 minutes indicates 5 minute levels . |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | Current signal level |

#### 4.1.5, net_getImeiNumber

Function name : public String net_getImeiNumber()

Description : get IMEI number

API version : V2.14.0 or above

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | IMEI number |

#### 4.1.6, net_getIccidNumber

Function name : public String net_getIccidNumber()

Description : get ICCID number

API version : V2.14.0 or above

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | ICCID number |

#### 4.1.7, net_getIccidNumber

Function name : public String net_getImsiNumber()

Description : get IMSI number

API version : V2.14.0 or above

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | IMSI |

#### 4.2, set

#### 4.2.1, net_setNetWork

Function name : public int net_setNetWork(String type, boolean enable)

Description : setnetworkswitch status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | String | network type eth0: Ethernet eth1: Ethernet 1 wlan0: WiFi mobile: Mobile network |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

API version V2.12.0+ can use Constant class

#### 4.2.2, net_getNetWork

Function name : public int net_getNetWork(String type)

Description : getnetworkswitch status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | String | network type eth0: Ethernet eth1: Ethernet 1 wlan0: WiFi mobile: Mobile network |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

notes

API version V2.12.0+ can use Constant class

#### 4.2.3, net_setNetWorkModel

Function name : public int net_setNetWorkModel(String type, int model, String ip, String gaw, String mask, String dns1, String dns2)

Description : setnetwork connection mode

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | String | network type eth0: Ethernet eth1: Ethernet 1 wlan0: WiFi |
| model | int | Network mode 0: Dynamic; 1: Static |
| IP | String | Static mode set IP address |
| gaw | String | Static mode set gateway |
| mask | String | Static mode set subnet mask |
| dns1 | String | static mode set DNS1 |
| dns2 | String | static mode set DNS2 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

This interface modifies the currently connected Wi-Fi network. If no Wi-Fi network is connected, it will display null to switch modes.

notes

API version V2.12.0+ can use Constant class

#### 4.2.4, net_getNetWorkModel

Function name : public int net_getNetWorkModel(String type)

Description : getnetwork connection mode

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | String | network type eth0: Ethernet eth1: Ethernet 1 wlan0: WiFi |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | Network mode: 0 Dynamic 1 Static |

notes

API version V2.12.0+ can use Constant class

#### 4.2.5, net_setWifiAp

Function name : public int net_setWifiAp(boolean enable)

Description : Set Wi-Fi hotspot switch status

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

WiFi hotspot and WiFi switch are mutually exclusive; opening a hotspot will automatically close the WiFi connection .

#### 4.2.6, net_getWifiAp

Function name : public int net_getWifiAp()

Description : get WiFi hotspot switch status

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

#### 4.2.7, net_setWifiConnect

Function name : public int net_setWifiConnect(String account, String pwd, int type, int mode, NetworkInfoData info)

Description : Set WiFi connection username and password

API version : V2.6.0 or above

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| account | String | account name |
| pwd | String | password |
| type | int | Encryption method 0: null Password 1: WEP Password 2: WPA |
| mode | int | Connection mode 0: Dynamic 1: Static |
| info | NetworkInfoData | Configuration required when the connection mode is static |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

API version V2.12.0+ can use Constant class

It takes effect immediately and will connect to Wi-Fi after being invoked.

NetworkInfoData is a network information object class .

#### 4.2.8, net_setNetworkProtect

Function name : public int net_setNetworkProtect(boolean enable, int type, long time, String ip_internet, String ip_intranet, String log_path, boolean reboot)

Description : setnetwork guardian switch status

API version : V2.7.0 or above

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |
| type | int | type 0: auto; 1: Ethernet; 2: Wi-Fi; 3: Mobile network |
| time | long | Time interval between diagnosis and re-diagnosis |
| ip_internet | String | external IP address |
| ip_intranet | String | Internal network IP address |
| log_path | String | Log save path |
| reboot | boolean | After repairing a network failure , should I reboot ? `true`: reboot `false`: do not reboot |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The default value for ` type` is `auto` : This will attempt to repair all network types if the current network is detected as unusable .

time interval is 15 minutes : This is the interval between diagnostics (including repairs after problems occur) and subsequent diagnostics. It cannot be less than three minutes . Setting the time too short will result in frequent detections.

The `ip_internet` field specifies the external IP address, which defaults to Baidu. It is used to detect whether communication with the external network is possible.

The ip_intranet internal network IP address defaults to Baidu: used to detect whether communication with the external network is possible.

The default log_path path is to save logs in the system root directory/NetworkProtectLog.

to reboot after repairing a network failure . The default is `reboot` . ` reboot` : After repairing the failure , the system will reboot for re-diagnosis. Multiple consecutive reboots will cause the system to enter hibernation mode, and the waiting time for re-testing will increase after each reboot . `no reboot` : After repairing the failure , the system will enter hibernation mode.

Hibernation mode: If a network change is detected, the guardian will resume, re-diagnose, and continue hibernation if the diagnosis fails .

#### 4.2.9, net_getNetworkProtectEnable

Function name : public int net_getNetworkProtectEnable()

Description : getnetwork guardian switch status

API version : V2.7.0 or above

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

#### 4.2.10, net_getNetworkProtectEnable

Function name : public List net_getNetworkProtectConfig()

Description : getnetwork daemon configuration information

API version : V2.7.0 or above

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | List | Array order<br>: 0: type; 1: interval time; 2: external IP address;<br>3: internal IP address;<br>4: log path;<br>5: whether to re-establish network failure . |

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>List<String> config = smdt.net_getNetworkProtectConfig();<br>int type = Integer.parseInt(config.get(0));<br>long time = Long.parseLong(config.get(1));<br>String ip_internet = config.get(2);<br>String ip_intranet = config.get(3);<br>String log_path = config.get(4);<br>boolean reboot = config.get(5).equals("1"); |
| :--- |

#### 4.2.11, net_setNetworkPriority

Function name : public int net_setNetworkPriority(String[] types)

Description : set network priority

API version : V2.15.0 or above

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| types | String[] | network priority ranking |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The `types` parameter is sorted from highest to lowest order by the string `networktype` . For example: `new String[]{"eth0", "wlan0", "mobile"};` represents Ethernet/Wi-Fi/Mobile networks .

Alternatively, you can use `new String[]{ TYPE_ETH0, TYPE_WLAN, TYPE_MOBILE }` in the Constant class ;

After setting , reboot will take effect.

#### 4.2.12, net_getNetworkPriority

Function name : public String[] net_getNetworkPriority()

Description : getnetwork priority

API version : V2.15.0 or above

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String[] | network priority ranking |

notes

The return value is a string representing the networktype, sorted from highest to lowest . For example: `new String[]{"eth0", "wlan0", "mobile"}; Ethernet/WIFI/Mobile network` .

#### 4.2.13, net_setNetworkMultiEnable

Function name : public int net_setNetworkMultiEnable(boolean enable)

Description : Set multi-network coexistence switch status

API version : V2.16.0 or above

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

Note that this should be used in conjunction with network priority; the network type for external network access should be placed first.

After setting , reboot will take effect.

#### 4.2.14, net_getNetworkMultiEnable

Function name : public int net_getNetworkMultiEnable()

Description : get multi-network coexistence switch status

API version : V2.16.0 or above

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

### 5. System control

#### 5.1, set

#### 5.1.1, sys_setBluetooth

Function name : public int sys_setBluetooth(boolean enable)

Description : Set Bluetooth switch status

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.1.2, sys_getBluetooth

Function name : public int sys_getBluetooth()

Description : get Bluetooth switch status

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

#### 5.1.3, sys_setHwStack

Function name : public int sys_setHwStack(boolean enable)

Description : Set HW overlay switch status

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.1.4, sys_getHwStack

Function name : public int sys_getHwStack()

Description : get HW overlay switch status

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

#### 5.1.5, sys_setAirPlane

Function name : public int sys_setAirPlane(boolean enable)

Description : Set flight mode switch status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.1.6, sys_getAirPlane

Function name : public int sys_getAirPlane()

Description : get Flight Mode Switch Status

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

#### 5.1.7, sys_setLocation

Function name : public int sys_setLocation(int mode)

Description : set location informationswitch status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| mode | int | 0: Close 1: On 2: Power Saving Mode 3: High Precision |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.1.8, sys_getLocation

Function name : public int sys_getLocation()

Description : get location informationswitch status

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0: Close 1: On 2: Power Saving Mode 3: High Precision |

#### 5.1.9, sys_setSoftKeyboard

Function name : public int sys_setSoftKeyboard(boolean enable)

Description : Set the soft keyboard to display status.

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.1.10, sys_getSoftKeyboard

Function name : public int sys_getSoftKeyboard()

Description : Get soft keyboard displays status

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

#### 5.1.11, sys_setKeyReport

Function name : public int sys_setKeyReport(boolean enable)

Description : Can the SET button be reported?

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: Enable; false: Disable |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.1.12, sys_getKeyReport

Function name : public int sys_getKeyReport()

Description : Can the GET button report data?

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: Enable 0: Disable |

#### 5.1.13, sys_setTouchReport

Function name : public int sys_setTouchReport(boolean enable)

Description : Set whether to report touch clicks

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: Enable; false: Disable |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.1.14, sys_getTouchReport

Function name : public int sys_getTouchReport()

Description : Can touch clicks be reported?

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: Enable 0: Disable |

#### 5.1.15, sys_copyFile

Function name : public void sys_copyFile(String oldpath, String newpath, CopyCallback callback)

Description : Copy file

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| oldpath | String | Source path |
| newpath | String | Target path |
| callback | CopyCallback | Copy status callback |

return parameter Instructions null

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>smdt.sys_copyFile(oldpath, newpath, new SmdtManagerNew.CopyCallback() {<br>@Override<br>public void onCopyProgress(int progress) throws RemoteException {<br>...<br>}<br>@Override<br>public void onCopyFinished(int returnCode, String msg) throws RemoteException {<br>...<br>}<br>}); |
| :--- |

notes

Callback interface : CopyCallback

#### 5.1.16, sys_setDefInputMethod

Function name : public int sys_setDefInputMethod(String default_input_method)

Description : Set default input method

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| default_input_method | String | The input method uses the `nameand` class , for example: `com.android.inputmethod.pinyin/.LatinIME`. |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

to get the input list first and then set it.

#### 5.1.17, sys_getDefInputMethod

Function name : public String sys_getDefInputMethod()

Description : get current default input method

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | The input method uses the `nameand` class , for example: `com.android.inputmethod.pinyin/.LatinIME`. |

#### 5.1.18, sys_getDefInputMethodList

Function name : public String[] sys_getDefInputMethodList()

Description : get all input methods

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | All input methods, package nameand class name |

#### 5.1.19, sys_setSystemFontSize

Function name : public int sys_setSystemFontSize(float size)

Description : setsystem font size

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| size | float | Font size , for example, 0.85/1.00/1.15/1.30. |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.1.20, sys_getSystemFontSize

Function name : public float sys_getSystemFontSize()

Description : getsystem's current set font size

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | float | Font size , for example, 0.85/1.00/1.15/1.30. |

### 5.2 Timeand Language

#### 5.2.1, sys_setNtpServer

Function name : public int sys_setNtpServer(String url)

Description : Set the address of the NTP synchronization time server .

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| URL | String | Server URL, for example: "time.nist.gov" |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.2.2, sys_getNtpServer

Function name : public String sys_getNtpServer()

Description : Get the address of the current NTP synchronization time server.

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Server address URL |

#### 5.2.3, sys_setNationallanguage

Function name : public int sys_setNationallanguage(String national, String language)

Description : set country and language

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| national | String | Country/region, e.g., CN |
| language | String | Language, for example: zh |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

Incorrect parameters may cause system exceptions; please use with caution.

It is recommended to first iterate through the system language to get the corresponding country/region and language code, and then call the interface set.

#### 5.2.4, sys_getNationallanguage

Function name : public String sys_getNationallanguage()

Description : get the current set of country and language

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Country and language, for example: zh_CN |

notes

Return value zh_CN: Country CN, Language zh

#### 5.2.5, sys_setTimeZone

Function name : public int sys_setTimeZone(String timeZone)

Description : Set the current time zone

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| timeZone | String | Time zone, for example: Asia/Shanghai |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.2.6, sys_getTimeZone

Function name : public String sys_getTimeZone()

Description : get current time zone

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Current time zone, for example: Asia/Shanghai |

#### 5.2.7, sys_setTimeFormat

Function name : public int sys_setTimeFormat(String format)

Description : SetSystem Time (Default Format)

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| format | String | Time format: 12:12 hour system, 24:24 hour system |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.2.8, sys_getTimeFormat

Function name : public String sys_getTimeFormat()

Description : getsystem time (default format)

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | 12:12 hour system 24:24 hour system |

#### 5.2.9, sys_setTime

Function name : public int sys_setTime(long time_millisecond)

Description : Set the current system time

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| time_millisecond | long | time , unit: milliseconds |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

Before setting , you need to close system auto to synchronize time.

#### 5.2.10, sys_setNetworkTimeSync

Function name : public int sys_setNetworkTimeSync(boolean enable)

Description : Set whether to synchronize network time

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.2.11, sys_getNetworkTimeSync

Function name : public int sys_getNetworkTimeSync()

Description : get synchronize network time open status

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

### 5.3 Application

#### 5.3.1, sys_setDefaultLauncher

Function name : public int sys_setDefaultLauncher(String packageName)

Description : Set default desktop application

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | The `set` directive requires the desktop application package name and the class name (with a slash "/" in between). |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

name must be included , such as: android.app.smdt.launcher/.Launcher

#### 5.3.2, sys_getDefaultLauncher

Function name : public String sys_getDefaultLauncher()

Description : get default desktop application

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Application package nameand class name |

#### 5.3.3, sys_setSystemBootApp

Function name : public int sys_setSystemBootApp(String packageName)

Description : Set applications that require power on to start automatically.

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | Application package name (If you need to specify a class name , add a "/" followed by the class name ) |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

If the application using `set` does not have a desktop icon, then you need to specify the class name.

Example: Specify package name android.app.smdt.apidemo; specify class name android.app.smdt.apidemo/.MainActivity

#### 5.3.4, sys_getSystemBootApp

Function name : public String sys_getSystemBootApp()

Description : Get apps that need to be powered on to start automatically.

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | application package name |

#### 5.3.5, sys_setDaemonsActivity

Function name : public int sys_setDaemonsActivity(String packageName, long time_millisecond, boolean broadcast_enable)

Description : set daemon process

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | Set the application package name to be protected (if you need to specify the class name , add "/" followed by the class name ) |
| time_millisecond | long | How long after an application leaves the foreground should it be relaunched, in milliseconds? |
| broadcast_enable | boolean | Should a global broadcast be sent when the application exits? |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

If the application being protected does not have a desktop icon, you need to specify the class name.

Example: Specify package name android.app.smdt.apidemo; specify class name android.app.smdt.apidemo/.MainActivity

The global broadcast is "android.app.smdt.PROTECT_CHECK".

#### 5.3.6, sys_getDaemonsActivity

Function name : public String sys_getDaemonsActivity()

Description : get the name of the application package currently being protected

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | The application package name to be protected |

#### 5.3.7, sys_doSilentInstallApp

Function name : public void sys_doSilentInstallApp(String apkPath, InstallCallback callback)

Description : Silent application installation

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| apkPath | String | Path to the application to be installed |
| callback | InstallCallback | Installation callback |

return parameter Instructions null

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>smdt.sys_doSilentInstallApp(path, new SmdtManagerNew.InstallCallback() {<br>@Override<br>public void onInstallFinished(String packageName, int returnCode, String msg) throws RemoteException {<br>...<br>}<br>}); |
| :--- |

notes

Callback interface : InstallCallback

#### 5.3.8, sys_doSilentUninstallApp

Function name : public void sys_doSilentUninstallApp(String packageName, DeleteCallback callback)

Description : Silently uninstall application

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | name that needs to be uninstalled |
| callback | DeleteCallback | Unload callback |

return parameter Instructions null

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>smdt.sys_doSilentUninstallApp(pkg, new SmdtManagerNew.DeleteCallback() {<br>@Override<br>public void onDeleteFinished(String packageName, int returnCode, String msg) throws RemoteException {<br>...<br>}<br>}); |
| :--- |

notes

Callback interface : DeleteCallback

#### 5.3.9, sys_addBlackWhiteList

Function name : public int sys_addBlackWhiteList(String packageName, int type, int function)

Description : Add application to black and white name list

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | application package name |
| type | int | type 0: Black name single; type 1: White name single |
| function | int | Function 0: Uninstall 1: Install |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

API version V2.12.0+ can use Constant class

the black and white name lists are empty, allow all applications to be installed/uninstalled.

the white name field is not empty, the application in the white name field can be installed/uninstalled, while other applications cannot be installed/uninstalled.

the white name field is empty and the black name field is not empty, the application in the black name field cannot be installed/uninstalled, while the other applications can be installed/uninstalled.

the white and black name tags are empty, the white name tag takes effect, and the black name tag does not take effect.

#### 5.3.10, sys_getBlackWhiteList

Function name : public List sys_getBlackWhiteList(int type, int function)

Description : get black and white name list

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | type 0: Black name single; type 1: White name single |
| function | int | Function 0: Uninstall 1: Install |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | List | List of application package names |

notes

API version V2.12.0+ can use Constant class

the black and white name lists are empty, allow all applications to be installed/uninstalled.

the white name field is not empty, the application in the white name field can be installed/uninstalled, while other applications cannot be installed/uninstalled.

the white name field is empty and the black name field is not empty, the application in the black name field cannot be installed/uninstalled, while the other applications can be installed/uninstalled.

the white and black name tags are empty, the white name tag takes effect, and the black name tag does not take effect.

#### 5.3.11, sys_delBlackWhiteList

Function name : public int sys_delBlackWhiteList(String packageName, int type, int function)

Description : Remove the application from the blacklist/ whitelist .

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | application package name |
| type | int | type 0: Black name single; type 1: White name single |
| function | int | Function 0: Uninstall 1: Install |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

API version V2.12.0+ can use Constant class

The application package name is passed with special values : "clean" clears all data; "clean_type" in conjunction with the passed type clears the corresponding type name list; "clean_funtion" in conjunction with the passed function clears the corresponding function name list; "clean_type_function" in conjunction with the passed type and function clears the corresponding type and function name list.

#### 5.3.12, sys_backupApplication

Function name : public void sys_backupApplication(String packageName, BackUpCallback callback)

Description : Backup application data

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | application package name |
| callback | BackUpCallback | Backup callback |

return parameter Instructions null

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>smdt.sys_backupApplication(packageName, new SmdtManagerNew.BackUpCallback() {<br>@Override<br>public void onBackUpProgress(int progress) throws RemoteException {<br>...<br>}<br>@Override<br>public void onBackUpFinished(int returnCode, String msg) throws RemoteException {<br>...<br>}<br>@Override<br>public void onBackUpPath(String path) throws RemoteException {<br>...<br>}<br>}); |
| :--- |

notes

Callback interface : BackUpCallback

Conditions: The app must exist for backup to work / Root access required

It will back up: /data/data/package name /data/user/0/package name /data/user_de/0/package name /Android/data/package name

#### 5.3.13, sys_recoveryApplication

Function name : public void sys_recoveryApplication(String packageName, RecoveryCallback callback)

Description : Restore application data

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | application package name |
| callback | RecoveryCallback | Restore Callback |

return parameter Instructions null

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>smdt.sys_recoveryApplication(packageName, new SmdtManagerNew.RecoveryCallback() {<br>@Override<br>public void onRecoveryProgress(int progress) throws RemoteException {<br>...<br>}<br>@Override<br>public void onRecoveryFinished(int returnCode, String msg) throws RemoteException {<br>...<br>}<br>@Override<br>public void onRecoveryPath(String path) throws RemoteException {<br>...<br>}<br>}); |
| :--- |

notes

Callback interface : RecoveryCallback

Conditions: The app must exist for recovery to work / Root access required

It will restore: /data/data/package name /data/user/0/package name /data/user_de/0/package name /Android/data/package name

#### 5.3.14, sys_setAutoInstallEnable

Function name : public int sys_setAutoInstallEnable(boolean enable)

Description : Set whether to allow auto- installation of applications.

API version : V2.6.0 and later

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: Allow; false: Disallow |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.3.15, sys_getAutoInstallEnable

Function name : public int sys_getAutoInstallEnable(boolean enable)

Description : Does `get` allow auto -installation of applications?

API version : V2.6.0 and later

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: Allowed 0: Disallowed |

#### 5.3.16, sys_addAutoInstallAppList

Function name : public int sys_addAutoInstallAppList(String path, String packageName)

Description : Add auto -install application strategy configuration

API version : V2.6.0 and later

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| path | String | Installation package path |
| packageName | String | application package name |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The installation package path is a relative path, relative to external storage, such as an external USB drive. If you create an `app` directory in the root directory and place `test.apk` in that directory, then you would enter: `app/test.apk`.

Multiple package name setting strategies are allowed under the same path.

package name will clear all policies under that path.

#### 5.3.17, sys_getAutoInstallAppList

Function name : public List sys_getAutoInstallAppList()

Description : getauto application installation strategy configuration

API version : V2.6.0 and later

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | List | Auto -installation application strategy configuration list |

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>List list = smdt.sys_getAutoInstallAppList();<br>if (list != null && list.size() > 0) {<br>for (int i = 0; i < list.size(); i++) {<br>String[] inf = list.get(i).split(",");<br>String path = inf[0];<br>String packageName = "";<br>if (inf.length > 1) {<br>packageName = inf[1];<br>}<br>}<br>} |
| :--- |

notes

The return list members are composed of path + "," + package name . For example: if the path is root directory app.apk and the package name is android.app.smdt.apidemo, then the retrieved value will be : app.apk, android.app.smdt.apidemo.

#### 5.3.18, sys_delAutoInstallAppList

Function name : public int sys_delAutoInstallAppList(String path, String packageName)

Description : Remove auto- install application policy configuration

API version : V2.6.0 and later

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| path | String | Installation package path |
| packageName | String | application package name |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The path is the installation package path. A special value is passed : "clean" clears all data.

the application package name is empty, delete all policy configurations under that path.

#### 5.4, audio

#### 5.4.1 sys_setVolume

Function name : public int sys_setVolume(int value)

Description : setsystem media sound volume value

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| value | int | volume value |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

First get the maximum and minimum values of volume, then set them .

#### 5.4.2, sys_getVolume

Function name : public int sys_getVolume()

Description : getsystem media audio volume value

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | volume value |

#### 5.4.3, sys_getVolumeMaxMin

Function name : public int sys_getVolumeMaxMin(String type)

Description : getsystem media volumemax value and min value

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | String | max: max value min: min value |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | maxvolume value / minvolume value |

#### 5.4.4, sys_setVolumeMute

Function name : public int sys_setVolumeMute(boolean enable)

Description : SetSystem Media Sound Mute

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: mute false: cancel mute |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.4.5, sys_getVolumeMute

Function name : public int sys_getVolumeMute()

Description : Is the media sound muted in getsystem ?

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: Mute 0: Not Mute |

#### 5.4.6, sys_setOutVolume

Function name : public int sys_setOutVolume(int type, int value)

Description : Set the volume gain of deviceoutput

API version : V2.16.0 or above

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | devicetype 1: Speaker sound 2: Headphone sound 3: HDMI sound |
| value | int | the set 's volume gain value is: 0 <= volume <= 99 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

Currently, only setting speaker gain is supported.

#### 5.4.7, sys_getOutVolume

Function name : public int sys_getOutVolume(int type)

Description : get the volume gain of the audio device output .

API version : V2.16.0 or above

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | devicetype 1: Speaker sound 2: Headphone sound 3: HDMI sound |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | Volume gain value 0-99 |

notes

Currently only speaker gain is supported.

#### 5.4.8, sys_setAudioOutput

Function name : public int sys_setAudioOutput(int type)

Description : setaudiooutputtype

API version : V2.16.0 or above

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | audiooutputtype<br>0: auto<br>1: Speaker<br>2: Headphones<br>3: Speaker + Headphones<br>4: HDMI<br>5: Speaker + HDMI<br>6: Headphones + HDMI<br>7: Speaker + Headphones + HDMI |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

Due to hardware limitations, when selecting speaker-related configurations on the minute board, the headphones will also output sound.

#### 5.4.9, sys_getAudioOutput

Function name : public int sys_getAudioOutput()

Description : getaudiooutputtype

API version : V2.16.0 or above

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0: auto<br>1: Speaker<br>2: Headphones<br>3: Speaker + Headphones<br>4: HDMI<br>5: Speaker + HDMI<br>6: Headphones + HDMI<br>7: Speaker + Headphones + HDMI |

#### 5.4.10, sys_setAudioInput

Function name : public int sys_setAudioInput(int type)

Description : setaudioinputtype

API version : V2.16.0 or above

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | audioinputtype<br>0: auto<br>1: Microphone<br>2: Headphones |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

This interface is not currently supported.

#### 5.4.11, sys_getAudioInput

Function name : public int sys_getAudioInput()

Description : getaudioinputtype

API version : V2.16.0 or above

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | audioinputtype<br>0: auto<br>1: Microphone<br>2: Headphones |

notes

This interface is not currently supported.

#### 5.5, IO

#### 5.5.1, sys_setGpioDirection

Function name : public int sys_setGpioDirection(int io, int direction, int value)

Description : Set GPIO input/output status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| io | int | I/O ports, from 1 to 10 |
| direction | int | GPIO direction 0: input 1: output |
| value | int | setting the GPIO direction to output , set 0: low level , 1: high level. |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

API version V2.12.0+ can use Constant class

#### 5.5.2, sys_getGpioDirection

Function name : public int sys_getGpioDirection(int io)

Description : get GPIO input/output status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| io | int | I/O ports, from 1 to 10 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | GPIO direction 0: input 1: output |

notes

API version V2.12.0+ can use Constant class

#### 5.5.3, sys_getGpioValue

Function name : public int sys_getGpioValue(int io)

Description : Get GPIO level status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| io | int | I/O ports, from 1 to 10 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0: low level 1: high level |

notes

API version V2.12.0+ can use Constant class

#### 5.5.4, sys_setExGpioDirection

Function name : public int sys_setExGpioDirection(int io, int direction, int value)

Description : set external GPIO input output status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| io | int | I/O ports, from 1 to 10 |
| direction | int | GPIO direction 0: input 1: output |
| value | int | setting the GPIO direction to output , set 0: low level , 1: high level. |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

API version V2.12.0+ can use Constant class

#### 5.5.5, sys_getExGpioDirection

Function name : public int sys_getExGpioDirection(int io)

Description : get external GPIO input output status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| io | int | I/O ports, from 1 to 10 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | GPIO direction 0: input 1: output |

notes

API version V2.12.0+ can use Constant class

#### 5.5.6, sys_getExGpioValue

Function name : public int sys_getExGpioValue(int io)

Description : Get the external GPIO level status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| io | int | I/O ports, from 1 to 10 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0: low level 1: high level |

notes

API version V2.12.0+ can use Constant class

#### 5.5.7, sys_setControl

Function name : public int sys_setControl(int type, boolean enable)

Description : Other I/O related sets (motherboard status LED, module power)

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | 3:wifi power<br>4:wifi reset<br>5:led control<br>6:speak power<br>7:lvds power<br>8:lvds reset<br>9:4G power<br>10:4G reset<br>11:LAN power<br>12:LAN reset<br>13:SD power<br>14:SD reset<br>15:TP power<br>16:TP reset<br>17:GBE reset<br>18:EDP BackLight power<br>19:Fan power |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

API version V2.12.0+ can use Constant class

Typical applications only support controlling LED lights (5) and fans (19).

Other controls need to be at the system application level.

#### 5.5.8, sys_getControl

Function name : public int sys_getControl(int type)

Description : Get other I/O related statuses (motherboard status lights, module power).

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | 3:wifi power<br>4:wifi reset<br>5:led control<br>6:speak power<br>7:lvds power<br>8:lvds reset<br>9:4G power<br>10:4G reset<br>11:LAN power<br>12:LAN reset<br>13:SD power<br>14:SD reset<br>15:TP power<br>16:TP reset<br>17:GBE reset<br>18:EDP BackLight power<br>19:Fan power |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

notes

API version V2.12.0+ can use Constant class

Typical applications only support controlling LED lights (5) and fans (19).

Other controls need to be at the system application level.

#### 5.6. Timing on Shut down

#### 5.6.1, sys_setAutoPowerOnOff

Function name : public int sys_setAutoPowerOnOff(boolean enable, int[] week, int onHour, int onMinute, int offHour, int offMinute)

Description : settiming open Shut down

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |
| week | int[] | `date` , `new int[]{0,0,0,0,0,0,0}` represents a repeat within 7 days , with the first digit being Sunday. `1`: set; `0`: do not set. |
| onHour | int | Power on time ( hour ) (24- hour system) |
| onMinute | int | power on time point ( minute ) |
| off-hours | int | Shut down time point ( hour ) (24 hour system) |
| offMinute | int | Shut down time ( minutes ) |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>String offTime = "11:00";<br>String onTime = "12:00";<br>int on_hour = Integer.parseInt(onTime.split(":")[0]);<br>int on_min = Integer.parseInt(onTime.split(":")[1]);<br>int off_hour = Integer.parseInt(offTime.split(":")[0]);<br>int off_min = Integer.parseInt(offTime.split(":")[1]);<br>//int[] week = null; // set as day<br>int[] week = new int[]{1,0,0,0,0,0,0} // The first digit is Sunday, 1 for setting , 0 for not setting.<br>int result = smdt.sys_setAutoPowerOnOff(true, week, on_hour, on_min, off_hour, off_min); |
| :--- |

notes

The first element of the week parameter array is Sunday, and so on.

Passing null to the week parameter means it will only be set once, i.e., on the current day .

If you set the function repeatedly but only once , the Shutdown time of the set function must be after the current time .

The shutdown time must be at least 3 minutes apart from the current time ; the power-on time and the shutdown time must also be at least 3 minutes apart.

timing- on /shut-down error codes

#### 5.6.2, sys_getAutoPowerOnOffEnable

Function name : public int sys_getAutoPowerOnOffEnable(int type)

Description : gettiming start Shut down status

API version : V2.6.0 and later

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0: close 1: open |

notes

version 2.6.0 , the timing- activated Shutdown statusget could be accessed by calling sys_getAutoPowerOnOff.

#### 5.6.3, sys_getAutoPowerOnOff

Function name : public String sys_getAutoPowerOnOff(int type)

Description : gettiming on Shut downset power on / Shut down time

API version : V2.6.0 and later versions have changed; please refer to the notes.

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | 0: Timing Shut Down Time (24- hour format, including the colon) 1: Timing Power On Time (24- hour format, including the colon) 2: New Timing Shut Down Time ( year, month, day, hour, minute interface , milliseconds) 3: New Timing Power On Time ( year, month, day, hour , minute interface , milliseconds) |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | The timing function controls the power on / off time of the current set (in hours and minutes , 24- hour format). |

notes

version 2.6.0 , this interface could be used to determine whether the timing of Shutdown was open ; it returned a value when open and null when closed .

version 2.6.0 , time could only be obtained when timing was enabled and Shutdown was turned on.

version 2.6.0 , the get statement retrieves the value from the previous set statement , and is unrelated to the switch status null.

Version 2.24.0 and later support the new timingShut downandpower on timeinterface get method .

#### 5.6.4, sys_getAutoPowerOnOffRepeat

Function name : public int[] sys_getAutoPowerOnOffRepeat()

Description : gettiming enables duplicate status for Shut downset

API version : V2.6.0 and later versions have changed; please refer to the notes.

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | `date` , `new int[]{0,0,0,0,0,0,0}` represents 7 days , with the first digit being Sunday. `1`: set; `0`: not set. |

notes

The first element of the week parameter array is Sunday, and so on.

version 2.6.0 , the timing information could only be obtained when Shutdown was enabled ; it would be null when Shutdown was disabled .

version 2.6.0 , the get statement retrieves the value from the previous set statement , and is unrelated to the switch status null.

#### 5.6.5, sys_setAutoPowerOnOffTime

Function name : public int sys_setAutoPowerOnOffTime(boolean enable, long onTimeMillis, long offTimeMillis)

Description : `settiming` enables Shutdown ( year, month, day, hour, minute ), and supports specifying a specific day's Shutdown time and power on a specific day.

API version : V2.23.0+

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |
| onTimeMillis | long | timingpower on time ( year month day hour minute) (Time converted to milliseconds) Unit: milliseconds |
| offTimeMillis | long | timingShut down time ( year month day hour minute) (Time converted to milliseconds) Unit: milliseconds |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>String onTimeStr = "2024-03-25 12:00";<br>String offTimeStr = "2024-03-28 18:00";<br>SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");<br>Date onTimeDate = format.parse(onTimeStr);<br>Date offTimeDate = format.parse(offTimeStr);<br>long onTime = onTimeDate.getTime();<br>long offTime = offTimeDate.getTime();<br>int result = smdtManagerNew.sys_setAutoPowerOnOffTime(true, onTime, offTime); |
| :--- |

notes

You can set only the Shut-down time . Passing 0 to the power on time will set only the Shut-down time .

### 5.7 Watchdog

#### 5.7.1, sys_setWatchDog

Function name : public int sys_setWatchDog(boolean enable, int poweroff_time)

Description : set watchdog switch status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |
| poweroff_time | int | Shut down time after not feeding the dog , defaults to 60 seconds. Unit: seconds |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

parameter in the interface is not currently supported; the default is 60 seconds.

#### 5.7.2, sys_getWatchDog

Function name : public int sys_getWatchDog()

Description : get Watchdog Switch Status

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

#### 5.7.3, sys_setWatchDogFeed

Function name : public int sys_setWatchDogFeed()

Description : Opening the door to feed the dog

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.7.4, sys_getWatchDogShutDownTime

Function name : public int sys_getWatchDogShutDownTime()

Description : How long after getting the door dog and not feeding it will the shut down?

parameter :

null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | Shut down time (unit: seconds) |

notes

The interface is unusable and not currently supported.

### 5.8 Upgrade and Debugging

#### 5.8.1, sys_setPointerLoction

Function name : public int sys_setPointerLoction(boolean enable)

Description : Set pointer position switch status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.8.2, sys_getPointerLoction

Function name : public int sys_getPointerLoction()

Description : get pointer position switch status

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

#### 5.8.3, sys_setPowerOff

Function name : public int sys_setPowerOff()

Description : system Shut down

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.8.4, sys_setReboot

Function name : public int sys_setReboot()

Description : system reboot

parameter :

null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.8.5, sys_setRebootByMcu

Function name : public int sys_setReboot()

Description : System reboot (MCU reboot )

API version : V2.13.0 or above

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.8.6, sys_setDeveloperOptions

Function name : public int sys_setDeveloperOptions(boolean enable)

Description : setDeveloper option switch status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.8.7, sys_getDeveloperOptions

Function name : public int sys_getDeveloperOptions()

Description : getDeveloper option switch status

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

#### 5.8.8, sys_doUpdatePackage

Function name : public int sys_doUpdatePackage(int type, String filepath)

Description : OTA system upgrade

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | Upgrade type 0: OTA upgrade |
| filepath | String | absolute path of OTA firmware package |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.8.9, sys_doUpdatePackageAB

Function name : public int sys_doUpdatePackageAB(String filepath, UpdateCallback callback)

Description : OTA-AB minute zone upgrade, supports online upgrades without entering recovery mode.

API version : V2.25.0+

System version : Android 12+

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| filepath | int | Upgrade type 0: OTA upgrade |
| callback | UpdateCallback | Upgrade process callback |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>String path = "/sdcard/xxx.zip";<br>int result = smdt.sys_doUpdatePackageAB(path, new SmdtManagerNew.UpdateCallback() {<br>@Override<br>public void onStatusUpdate(int status, float percent) throws RemoteException {<br>if (status == UpdateEngineUtil.UpdateStatusConstants.DOWNLOADING) {<br>Log.d(TAG, "update progress: " + percent);<br>int progress = (int) (percent * 100);<br>}<br>}<br>@Override<br>public void onPayloadApplicationComplete(int errCode) throws RemoteException {<br>if (errCode == UpdateEngineUtil.ErrorCodeConstants.SUCCESS) {<br>Log.d(TAG, "Installation succeeded!");<br>} else {<br>Log.d(TAG, "Installation fail:"+errCode);<br>}<br>}<br>}); |
| :--- |

notes

This only applies to systems with AB minute zones ; the specific policy depends on whether the device firmware uses the AB minute zone policy.

status in onStatusUpdate callback For error codes related to `onPayloadApplicationComplete`, please refer to the OTA-AB upgrade error codes.

#### 5.8.10, sys_rebootRecovery

Function name : public int sys_rebootRecovery()

Description : Factory reset

parameter :

null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.8.11, sys_setSystemLog

Function name : public int sys_setSystemLog(boolean enable, int[] type, String dirpath, long fileMaxSize, boolean boot)

Description : Retrieves system logs to a specified path.

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |
| type | int[] | types to be captured are as follows : 1. Driver logs; 2. System logs; 3. Main logs; 4. Communication logs; 5. System properties ; 6. Kernel startup parameters (requires root access); 7. Memory usage; 8. Non-preinstalled APK information ; 9. MCU information; 10. Event logs; 11. Crash logs. |
| dirpath | String | The default storage directory is system root directory/SmdtLogNew/current time. |
| fileMaxSize | long | The maximum storage value (in bytes) for a single log file is 50MB by default. |
| boot | boolean | Power on starts logging: true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

Before storing logs, the system will automatically check the remaining system memory . If memory is insufficient, the log files will be cleaned up.

When a single log file reaches its maximum storage value , a new file will be automatically generated. A maximum of 10 files can be generated in a single directory. Once the limit is reached, the oldest file will be deleted.

`settype` contains 0, it means that the logs to be captured will only be captured up to the current time and will not be continuously output . After the input ends, the log capture status will change to `close` . 0, 1, and 2 represent that the driver logs and Android logs will only be captured once.

The recommended path for storing logs is ` system root directory/folder name /` + the current time .

event logs and crash logs of type `type` can be retrieved separately.

#### 5.8.12, sys_getSystemLog

Function name : public int sys_getSystemLog()

Description : getsystem logs switch status

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

#### 5.8.13, sys_getProcessLogcat

Function name : public void sys_getProcessLogcat(LogCallback callback)

Description : Line-by-line getsystem log

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| callback | LogCallback | Listener callback for each line of log |

return parameter Instructions null

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>int[] type = new int[]{2}; // 2 represents the system log, which must be present and included.<br>smdt.sys_setSystemLog(true, type, null, 0, false);<br>smdt.sys_getProcessLogcat(new SmdtManagerNew.LogCallback() {<br>@Override<br>public void onSingleLine(String log) throws RemoteException {<br>...<br>}<br>}); |
| :--- |

notes

Callback interface : LogCallback

You need to first enable the opensystem logging switch and select type 2: system log.

the `closeSystem` log switch is turned on or null is passed to the callback, the listener is released.

#### 5.8.14, sys_getProcessAnrLog

Function name : public int sys_getProcessAnrLog(String filepath)

Description : get ANR logs

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| filepath | String | Save path |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

API version V2.19.0+ The path needs to be a folder path.

#### 5.8.15, sys_setAdbDebug

Function name : public int sys_setAdbDebug(int type, boolean enable)

Description : set ADB switch status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | ADB type 0: USB 1: network |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

API version V2.12.0+ can use Constant class

#### 5.8.16, sys_getAdbDebug

Function name : public int sys_getAdbDebug(int type)

Description : get ADB switch status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | ADB type 0: USB 1: network |

Return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

notes

API version V2.12.0+ can use Constant class

#### 5.8.17, sys_setOTGMode

Function name : public int sys_setOTGMode(int mode)

Description : set OTG mode

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| mode | int | 0:HOST 1:DEVICE |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

API version V2.12.0+ can use Constant class

#### 5.8.18, sys_getOTGMode

Function name : public int sys_getOTGMode()

Description : get current OTG mode

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0:HOST 1:DEVICE |

notes

API version V2.12.0+ can use Constant class

#### 5.8.19, sys_setFloatBall

Function name : public int sys_setFloatBall(boolean enable)

Description : setSuspended ball state

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.8.20, sys_getFloatBall

Function name : public int sys_getFloatBall()

Description : getSuspended ball state

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

#### 5.8.21, sys_setUpdateExState

Function name : public int sys_setUpdateExState(boolean enabled)

Description : Sets whether external storage device cloning and system upgrades are allowed.

API version : V2.12.0 or above

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 5.8.22, sys_getUpdateExState

Function name : public int sys_getUpdateExState()

Description : Does GET allow external storage device cloning or system upgrades?

API version : V2.12.0 or above

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0: Prohibited 1: Allowed |

#### 5.8.23, sys_setApiDebugLevel

Function name : public int sys_setApiDebugLevel(int level)

Description : Set the API logging level

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| int | level | Level (0/1/2/3) Higher levels print more information . |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

null method control in JNI SO library

#### 5.8.24, sys_getApiDebugLevel

Function name : public int sys_getApiDebugLevel()

Description : Get the current API log level.

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | Print rating (0/1/2/3) |

#### 5.8.25, sys_getErrorDescription

Function name : public String sys_getErrorDescription(int errorCode)

Description : GET error code corresponding description

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| int | errorCode | Error code |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Error code description ; supports Chinese and English, and will switch the description language according to the system language. |

### 6. Hardware Control

### 6.1 Storage

#### 6.1.1, dev_getSDcardPath

Function name : public String dev_getSDcardPath()

Description : get external storage SD card path

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | External storage SD card path |

#### 6.1.2, dev_getUdiskPath

Function name : public List dev_getUdiskPath()

Description : get all USB drive paths on external storage

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | List | All USB drive paths on external storage |

#### 6.1.3, dev_unmountExternalStorage

Function name : public int dev_unmountExternalStorage(String path, boolean force, boolean removeEncryption)

Description : Unmount external storage

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| path | String | The absolute path to the external storage to be unloaded |
| force | boolean | Force uninstallation? |
| removeEncryption | boolean | Is it to remove the encrypted device ? |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 6.1.4, dev_getPublicPartitionSize

Function name : public int dev_getPublicPartitionSize(int type, int deviceId)

Description : get total size of public and private minutes

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | type 0: emmc 1: eeprom |
| deviceId | int | reading eeprom, device ID, Start from 0 , Each ID represents one EEPROM |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | size of private minute area |

#### 6.1.5, dev_readPublicPartition

Function name : public byte[] dev_readPublicPartition(int type, int deviceId, int areaId, int start_addr, int size)

Description : Read public/private minutes area

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | type 0: emmc 1: eeprom |
| deviceId | int | reading eeprom, device ID: Start from 0 , Each ID represents one EEPROM |
| areaId | int | reading the EEPROM, the Region ID starts from 1 , and each ID represents a region. |
| start_addr | int | Starting address |
| size | int | Data length |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | byte[] | Read data |

#### 6.1.6, dev_writePublicPartition

Function name : public int dev_writePublicPartition(int type, int deviceId, int areaId, int start_addr, int size, byte[] buf)

Description : Write to public/private minute area

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | type 0: emmc 1: eeprom |
| deviceId | int | reading eeprom, device ID: Start from 0 , Each ID represents one EEPROM |
| areaId | int | reading the EEPROM, the Region ID starts from 1 , and each ID represents a region. |
| start_addr | int | Starting address |
| size | int | Data length |
| buf | byte[] | Write data |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

the minute space size before writing ; if it exceeds the size, write using the null method.

### 6.2 Serial Port

#### 6.2.1, dev_getUartPath

Function name : public String dev_getUartPath(String uart)

Description : Get the absolute path of the serial port based on the port number.

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| uart | String | The port number corresponding to the serial port, for example: uart0/uart1/uart2/uart3 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | The absolute path to the serial port, for example: /dev/ttyS1. The specific path should be determined based on the actual serial port you need to operate on. |

#### 6.2.2, dev_openUart

Function name : public int dev_openUart(String uartNode, int baudrate, int databits, int stopbits, int parity, int flow_ctrl)

Description : Open serial port

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| uartNode | String | absolute path of serial port |
| baudrate | int | baud rate |
| databits | int | Data bits: 8/7/6/5 |
| stopbits | int | Stop position: 1/2 |
| parity | int | Parity bit: 0 null , 1 odd, 2 even |
| flow_ctrl | int | Flow control: 0 No flow control, 1 Hardware flow control, 2 Software flow control |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

It only supports opening one serial port and sending and receiving data .

open simultaneously require custom development.

to close the serial port after the operation , otherwise it will remain occupied.

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>String uart = "/dev/ttyS1";<br>int baudrate = 9600;<br>int databits = 8;<br>int stopbits = 1;<br>int parity = 0;<br>int flow_ctrl = 0;<br>int result = smdt.dev_openUart(uart, baudrate, databits, stopbits, parity, flow_ctrl);// open serial port<br>if (result == RET_API_OK) {<br>// Continue receiving serial port data after opensuccess<br>smdt.dev_receiveUart(uart, new SmdtManagerNew.DataCallback() {<br>@Override<br>public void onDataReceive(byte[] buffer, int size) throws RemoteException {<br>if (buffer != null) {<br>DEBUG("data size :" + size);<br>}<br>}<br>});<br>} |
| :--- |

#### 6.2.3, dev_closeUart

Function name : public int dev_closeUart(String uartNode)

Description : close serial port

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| uartNode | String | absolute path of serial port |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

It only supports opening one serial port and sending and receiving data .

open simultaneously require custom development.

to close the serial port after the operation , otherwise it will remain occupied.

#### 6.2.4, dev_sendUart

Function name : public int dev_sendUart(String uartNode, String data, boolean hex)

Description : Send serial port data

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| uartNode | String | absolute path of serial port |
| data | boolean | data |
| hex | boolean | true: hexadecimal false: decimal |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

It only supports opening one serial port and sending and receiving data .

open simultaneously require custom development.

to close the serial port after the operation , otherwise it will remain occupied.

#### 6.2.5, dev_receiveUart

Function name : public void dev_receiveUart(String uartNode, DataCallback callback)

Description : Receives serial port data

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| uartNode | String | absolute path of serial port |
| callback | DataCallback | Serial port data callback |

return parameter Instructions null

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>smdt.dev_receiveUart(uartNode, new SmdtManagerNew.DataCallback() {<br>@Override<br>public void onDataReceive(byte[] buffer, int size) throws RemoteException {<br>if (buffer != null) {<br>...<br>}<br>}<br>}); |
| :--- |

notes

Callback interface : DataCallback

It only supports opening one serial port and sending and receiving data .

open simultaneously require custom development.

to close the serial port after the operation , otherwise it will remain occupied.

### 6.3 Camera

#### 6.3.1, dev_getCameraConfig

Function name : public int[] dev_getCameraConfig(int cameraId)

Description : Get camera-related parameters

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| cameraId | int | Camera ID, for example: camera0 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int[] | An array that stores camera parameters |

notes

The meaning of the int[] array order is as follows:
First bit: Camera orientation 0: Rear 1: Front
Second bit: Camera rotation mode 0: Follow system screen 1: Locked
Third bit: Locking angle when rotation mode is locked: 0/90/180/270
Fourth bit: Whether the preview image is mirrored horizontally 0: No mirroring 1: Mirrored
Fifth bit: Whether the image is mirrored horizontally 0: No mirroring 1: Mirrored
Sixth bit: Whether the image is mirrored vertically 0: No mirroring 1: Mirrored
Seventh bit: Image rotation angle: 0/90/180/270
Eighth bit: Recording rotation angle: 0/90/180/270

#### 6.3.2, dev_setCameraDirection

Function name : public int dev_setCameraDirection(int cameraId, int cameraDirection)

Description : Set the front and rear camera orientation

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| cameraId | int | Camera ID, such as camera0 |
| cameraDirection | int | Camera orientation (front/rear): 0: Rear 1: Front |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

setting , reboot to take effect.

#### 6.3.3, dev_setCameraRotationMode

Function name : public int dev_setCameraRotationMode(int cameraId, int rotationMode, int lockRotation)

Description : Set camera rotation mode

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| cameraId | int | Camera ID, such as camera0 |
| rotationMode | int | Camera rotation mode 0: Follow system screen 1: Lock |
| lockRotation | int | When the rotation mode is the locking timing , the locking angle is: 0/90/180/270. |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

setting , reboot to take effect.

#### 6.3.4, dev_setCameraMirror

Function name : public int dev_setCameraMirror(int cameraId, boolean previewMirrorHorizontal, boolean imageMirrorHorizontal, boolean imageMirrorVertical)

Description : Set camera mirror

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| cameraId | int | Camera ID, camera0 |
| previewMirrorHorizontal | boolean | Does the preview screen need to be mirrored horizontally? False: Do not mirror; True: Mirror. |
| imageMirrorHorizontal | boolean | Does the image image need to be mirrored horizontally? False: Do not mirror; True: Mirror. |
| imageMirrorVertical | boolean | Does the image image need to be mirrored vertically? False: Do not mirror; True: Mirror. |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

Set and it will take effect immediately.

#### 6.3.5, dev_setCameraImageRotation

Function name : public int dev_setCameraImageRotation(int cameraId, int rotation)

Description : Set camera image rotation

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| path | String | Camera ID, for example, camera0 |
| rotation | boolean | Imaging rotation angle: 0/90/180/270 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

Set and it will take effect immediately.

#### 6.3.6, dev_setCameraVideoRotation

Function name : public int dev_setCameraVideoRotation(int cameraId, int rotation)

Description : Set camera recording rotation

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| cameraId | int | Camera ID, for example, camera0 |
| rotation | int | Video rotation angle: 0/90/180/270 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

Set and it will take effect immediately.

#### 6.4, I2C

#### 6.4.1, dev_openI2c

Function name : public int dev_openI2c(String i2cNode)

Description : Open I2C peripherals

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| i2cNode | String | i2c device node |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The interface is unusable and not currently supported.

#### 6.4.2, dev_closeI2c

Function name : public int dev_closeI2c(n)

Description : Close I2C peripherals

parameter :

null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The interface is unusable and not currently supported.

#### 6.4.3, dev_writeI2c

Function name : public int dev_writeI2c(int slave, int reg, byte[] data)

Description : Writing data from the device register using I2C

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| slave | int | slave addr |
| reg | int | reg addr |
| data | byte[] | Data written |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | Number of bytes written |

notes

The interface is unusable and not currently supported.

#### 6.4.4, dev_readI2c

Function name : public byte[] dev_readI2c(int slave, int reg, int len)

Description : Reading data from device register via I2C

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| slave | int | slave addr |
| reg | int | reg addr |
| len | int | Read the length of the data |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | byte[] | data |

notes

The interface is unusable and not currently supported.

#### 6.5, SPI

#### 6.5.1, dev_openSpi

Function name : public int dev_openSpi(String spiNode)

Description : Open SPI peripherals

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| spiNode | String | SPI device node |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The interface is unusable and not currently supported.

#### 6.5.2, dev_closeSpi

Function name : public int dev_closeSpi()

Description : Close SPI peripherals

parameter :

null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The interface is unusable and not currently supported.

#### 6.5.3, dev_readSpi

Function name : public byte[] dev_readSpi(int len)

Description : Read SPI device data

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| len | int | Data length |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | byte[] | data |

notes

The interface is unusable and not currently supported.

#### 6.5.4, dev_writeSpi

Function name : public int dev_writeSpi(byte[] data)

Description : Writing data to the SPI device

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| data | byte[] | Data written |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | Number of bytes written |

notes

The interface is unusable and not currently supported.

#### 6.6, Can

#### 6.6.1, dev_openCan

Function name : public int dev_openCan(String canNode, int baudrate)

Description : open can device

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| canNode | String | can device nodes, such as can0/can1 |
| baudrate | int | The device can control the baud rate, such as 125000. |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The interface is unusable and not currently supported.

#### 6.6.2, dev_closeCan

Function name : public int dev_closeCan(String canNode)

Description : closeopen CAN device

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| canNode | String | can device nodes, such as can0/can1 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The interface is unusable and not currently supported.

#### 6.6.3, dev_receiveCan

Function name : public int dev_receiveCan()

Description : Receives data from CAN device.

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The interface is unusable and not currently supported.

#### 6.6.4, dev_sendCan

Function name : public int dev_sendCan(long canid, byte[] data)

Description : Write data to CAN device

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| canid | long | The ID of the data sent corresponds to the can_id of the can_frame. |
| data | byte[] | The data sent corresponds to that sent using hwctrl_canOpen and will be written to the canOpen node. |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The interface is unusable and not currently supported.

#### 6.7, USB

#### 6.7.1, dev_setUsbPower

Function name : public int dev_setUsbPower(int type, int usbId, boolean enable)

Description : Set USB port power switch status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | 0:USBOTG 1:USBHOST |
| usbId | int | USB port ID: OTG has only 1, HOST has 1, 2, 3 |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The minute power port does not support a switch.

#### 6.7.2, dev_getUsbPower

Function name : public int dev_getUsbPower(int type, int usbId)

Description : Get USB power switch status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | String | 0:USBOTG 1:USBHOST |
| usbId | boolean | USB port ID: OTG has only 1, HOST has 1, 2, 3 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

notes

The minute power port does not support a switch.

#### 6.8, Lights

#### 6.8.1, dev_setLedLighted

Function name : public int dev_setLedLighted(String ledColor, boolean lighted)

Description : set tri-color light status

API version : V2.11.0 or above

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| ledColor | String | Red light: LED_RED Green light: LED_GREEN White light: LED_WHITE |
| lighted | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

API version V2.12.0+ can use Constant class

#### 6.8.2, dev_getLedState

Function name : public int dev_getLedState(String ledColor)

Description : get tri-color light status

API version : V2.11.0 or above

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| ledColor | String | Red light: LED_RED Green light: LED_GREEN White light: LED_WHITE |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

notes

API version V2.12.0+ can use Constant class

### 7. Industry-specific customization

### 7.1 Wiegand

#### 7.1.1, custom_sendWiegandCard

Function name : public int custom_sendWiegandCard(String idCard, int transformat)

Description : Wiegand card number

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| idCard | String | Wiegand Card |
| transformat | int | 1: Wiegand 26 2: Wiegand 34 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 7.1.2, custom_sendWiegandCardHIDPID

Function name : public int custom_sendWiegandCardHIDPID(String HID_value, String PID_value, int transformat)

Description : Wiegand sends card number using hidden code and public code.

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| HID_value | String | Hidden code |
| PID_value | String | public code |
| transformat | int | 1: Wiegen 26 2: Wiegen 34 |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 7.1.3, custom_readWiegandData

Function name : public void custom_readWiegandData(WiegandCallback callback)

Description : readWiegandinput​​

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| callback | WiegandCallback | Wiegand pullback |

return parameter Instructions null

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this);<br>smdt.custom_readWiegandData(new SmdtManagerNew.WiegandCallback() {<br>@Override<br>public void onReadData(String data) throws RemoteException {<br>...<br>}<br>}); |
| :--- |

notes

Callback interface : WiegandCallback

#### 7.1.4, custom_releaseWiegandRead

Function name : public int custom_releaseWiegandRead()

Description : Exit Wiegand input blocking

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 7.2, relay

#### 7.2.1, custom_setRelayIoMode

Function name : public int custom_setRelayIoMode(int mode, int delay)

Description : Set relay mode and delay

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| mode | int | 0: No auto closing mode;<br>1: Auto closing mode (high-active - default low level , high level for n seconds after sending the command , then low level );<br>2: Auto closing mode (low-active - default high level , low level for n seconds after sending the command , then high level ). |
| delay | int | auto closing mode |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

the setauto closed mode is seconds, with a maximum of 63 seconds.

#### 7.2.2, custom_getRelayIoMode

Function name : public int custom_getRelayIoMode

Description : getrelay current mode

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0: No auto closing mode;<br>1: Auto closing mode (high-active - default low level , high level for n seconds after sending command, then low level );<br>2: Auto closing mode (low-active - default high level , low level for n seconds after sending command , then high level). |

#### 7.2.3, custom_setRelayIoEnable

Function name : public int custom_setRelayIoEnable(boolean enable)

Description : set relayswitch status

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 7.2.4, custom_getRelayIoEnable

Function name : public int custom_getRelayIoEnable()

Description : getrelayswitch status

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

### 7.3 Application

#### 7.3.1, custom_dial

Function name : public int custom_dial(String number)

Description : Custom null interface dialing

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| number | String | telephone number |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 7.3.2, custom_endCall

Function name : public int custom_endCall()

Description : Custom null interface for hanging up phone calls

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 7.3.3, custom_addAppliesEncryption

Function name : public int custom_addAppliesEncryption(String packageName, String oldpwd, String pwd)

Description : Add applications that need encryption.

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | application package name |
| oldpwd | String | the set is used for modification and verification; if the password is null , then empty. |
| pwd | String | The password set |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

adding and changing passwords call this interface . Changing the password requires inputting the old password.

#### 7.3.4, custom_getAppliesEncryption

Function name : public List custom_getAppliesEncryption(String packageName)

Description : Get encrypted application and its password

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | application package name to find the password of the specified package name |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | List | Get encrypted application and its password |

notes

If the name parameter is empty, it returns all encrypted applications and their corresponding passwords.

#### 7.3.5, custom_delAppliesEncryption

Function name : public int custom_delAppliesEncryption(String packageName, String pwd)

Description : Remove application from encrypted list

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | application package name |
| pwd | String | Password set​ |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 7.4, system

#### 7.4.1, custom_cleanRecentTasks

Function name : public int custom_cleanRecentTasks()

Description : Clear recently run tasks from the system.

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

#### 7.4.2, custom_killPidProcess

Function name : public int custom_killPidProcess(int pid)

Description : Terminate the process based on the PID.

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| pid | int | Process PID |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

It will kill the process directly, use with caution.

#### 7.4.3, custom_getPidProcess

Function name : public int custom_getPidProcess(String packageName)

Description : Get PID based on package name

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | application package name |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | PID corresponding to application package name |

#### 7.4.4, custom_getPidProcessInfo

Function name : public String custom_getPidProcessInfo(int pid)

Description : Get the package name corresponding to the process pid.

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| pid | int | PID process |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | name corresponding to the PID process |

#### 7.4.5, custom_setDesktopApp

Function name : public int custom_setDesktopApp(String packageName)

Description : Set custom desktop shortcut icons

API version : V2.5.0 or above

Simplified version : Not supported

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | application package name |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The application needs to exist and have a desktop icon before it can be set .

This interface replaces the first icon in the OS-Launcher main interface.

Passing an empty string clears the currently bound status , restoring it to its original icon. If the icon is removed, the content will not be displayed.

#### 7.4.6, custom_getDesktopApp

Function name : public String custom_getDesktopApp()

Description : Get custom desktop shortcut icons

API version : V2.5.0 or above

Simplified version : Not supported

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | application package name |

### 8. Other

#### 8.1, Constant class

#### Constant class VariableUtil

API version Version 2.12.0 and above can use this class.

#### Network type of the connection :

Ethernet public static final CONNECT_TYPE_ETHERNET = "ETH"; WIFI public static final CONNECT_TYPE_WIFI = "WIFI"; mobile network public static final CONNECT_TYPE_MOBILE = "MOBILE";

#### networktype

Ethernet0: public static final String TYPE_ETH0 = "eth0"; Ethernet1: public static final String TYPE_ETH1 = "eth1"; WIFI: public static final String TYPE_WLAN = "wlan0"; Mobile network public static final String TYPE_MOBILE = "mobile";

#### network connection mode

Dynamic: `public static final int MODE_DHCP = 0;` Static: `public static final int MODE_STATIC = 1;`

#### WIFI encryption type

null password public static final int SECURITY_NONE = 0; WEP public static final int SECURITY_WEP = 1; WPA public static final int SECURITY_WPA = 2;

#### Application name​

` BLACK_LIST = 0;` ` WHITE_LIST = 1 ;` `UNINSTALL_LIST = 0;` ` INSTALL_LIST = 1 ;` `UNINSTALL_LIST = 0;` ` INSTALL_LIST = 1 ; `

#### GPIO

GPIO direction input public static final int GPIO_IN = 0; GPIO direction output public static final int GPIO_OUT = 1; GPIO output low level public static final int GPIO_LOW = 0; GPIO output high level public static final int GPIO_HIGH = 1;

#### ADB OTG

USB ADB public static final int ADB_USB = 0; network ADB public static final int ADB_NETWORK = 1; OTG mode HOST public static final int OTG_HOST = 0; OTG mode DEVICE public static final int OTG_DEVICE = 1;

#### storage minute area

EMMC public static final int PARTITION_EMMC = 0; EEPROM public static final int PARTITION_EEPROM = 1;

#### Camera

Rear camera: `public static final int CAMERA_FACING_BACK = 0; ` Front camera: `public static final int CAMERA_FACING_FRONT = 1;` Camera orientation follows the screen: `public static final int CAMERA_ROTATION_FOLLOW = 0;` Camera orientation lock: `public static final int CAMERA_ROTATION_LOCK = 1;`

#### lamp

White light public static final String LED_WHITE = "LED_WHITE"; Red light public static final String LED_RED = "LED_RED"; Green light public static final String LED_GREEN = "LED_GREEN";

#### Wiegand

Wiegand 26 public static final int WIEGAND_26 = 1; Wiegand 34 public static final int WIEGAND_34 = 2;

#### Log type

All logs are not continuously output , only captured once. `public static final int LOG_ONLY_ONE = 0; ` `public static final int LOG_KERNEL` (continuous output) `public static final int LOG_KERNEL = 1;` ` public static final int LOG_ANDROID = 2 ;` `public static final int LOG_MEDIA = 3; ` `public static final int LOG_MEDIA = 3;` Related information about the buffer. public static final int LOG_RADIO = 4; system attributes public static final int LOG_PROP = 5; kernel boot parameter (requires root access) public static final int LOG_CONFIG = 6; memory usage public static final int LOG_MISC = 7; non-preinstalled APK information public static final int LOG_APP = 8; Mcu information public static final int LOG_MCU = 9;

#### IO

WIFI Power: `public static final int WIFI_POWER = 3; ` WIFI Reset: `public static final int WIFI_RESET = 4; ` LED Power: `public static final int LED_CONTROL = 5; ` Speaker Power : `public static final int SPEAK_POWER = 6; LVDS Power: `public static final int LVDS_POWER = 7; LVDS Reset: `public static final int LVDS_RESET = 8; ` Mobile Network Module Power: `public static final int MOBILE_POWER = 9; ` Mobile Network Module Reset: `public static final int MOBILE_RESET = 10; ` LAN Power: `public static final int LAN_POWER = 11; LAN Reset: `public static final int LAN_RESET = 12; ` SD Power: `public static final int SD_POWER = 13; SD Reset: ` public static final int SD_RESET = 14; ` TP Power: `public static final int TP_POWER = 15;` TP Reset: `public static final int TP_RESET = 16; ` GBE Reset: `public static final int GBE_RESET = 17;` EDP Backlight Power Supply: `public static final int EDP_POWER = 18; ` Fan Power Supply: `public static final int FAN_POWER = 19;`

### 8.2 Network Information Object Class

#### Network information object class NetworkInfoData

#### get IP address

Function name : public String getIp()

Description : get the current network 's IP address

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | IP address |

#### get gateway

Function name : public String getGateway()

Description : get the gateway of the current network .

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | gateway |

#### get subnet mask

Function name : public String getNetmask()

Description : get the subnet mask of the current network .

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Subnet mask |

#### get DNS1

Function name : public String getDns1()

Description : get the current network 's DNS1

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | DNS1 |

#### get DNS2

Function name : public String getDns2()

Description : get the current network 's DNS2

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | DNS2 |

### 8.3 Timing Shutdown Error Code

#### Error code class TimerCode

| variable name | Error code | explain |
| :--- | :--- | :--- |
| SET_SUCCESS | 0 | success |
| ERROR_UNKNOWN | -1 | mistake |
| ERROR_CONFIG | -2 | The parameter is incorrect. |
| ERROR_EXCEPTION | -5 | abnormal |
| ERROR_TIME_PASSED | 100 | The set time has expired. |
| ERROR_COMPARE_ON_OFF_TIME | 101 | The power-on time and the shut-down time were not 3 minutes apart. |
| ERROR_COMPARE_NOW_OFF_TIME | 102 | current time and the Shutdown time are not 3 minutes apart. |
| ERROR_COMPARE_NOW_ON_TIME | 103 | current time and power on time are not 6 minutes apart. |

API version : V2.14.1 or above

notes

Versions prior to V2.14.1 did not return error codes starting with 10.

### 8.4 OTA-AB upgrade error codes

#### Error code class UpdateEngineUtil

#### UpdateStatusConstants class

| variable name | Error code | explain |
| :--- | :--- | :--- |
| IDLE | 0 | idle |
| CHECKING_FOR_UPDATE | 1 | Checking if update is available |
| UPDATE_AVAILABLE | 2 | Can use upgrade |
| DOWNLOADING | 3 | Downloading |
| VERIFYING | 4 | During verification |
| FINALIZING | 5 | Updating |
| UPDATED_NEED_REBOOT | 6 | Update complete, reboot required. |
| REPORTING_ERROR_EVENT | 7 | Error Report |
| ATTEMPTING_ROLLBACK | 8 | Try rolling back |
| Disabled | 9 | Disable |

#### ErrorCodeConstants class

| variable name | Error code | explain |
| :--- | :--- | :--- |
| SUCCESS | 0 | success |
| ERROR | 1 | mistake |
| FILESYSTEM_COPIER_ERROR | 4 | file copying ( system) |
| POST_INSTALL_RUNNER_ERROR | 5 | Error running scripts or programs after installation |
| PAYLOAD_MISMATCHED_TYPE_ERROR | 6 | Payload (i.e., upgrade package content ) type mismatch |
| INSTALL_DEVICE_OPEN_ERROR | 7 | The null method is used to open and install devices or related resources. |
| KERNEL_DEVICE_OPEN_ERROR | 8 | The null method opens the kernel device or related resources. |
| DOWNLOAD_TRANSFER_ERROR | 9 | Download and transfer process error |
| PAYLOAD_HASH_MISMATCH_ERROR | 10 | The fact that the payload's hash value does not match expectations may indicate that the file has been tampered with. |
| PAYLOAD_SIZE_MISMATCH_ERROR | 11 | The payload size is not as expected. |
| DOWNLOAD_PAYLOAD_VERIFICATION_ERROR | 12 | Downloaded payload verification failed. |
| NEW_ROOTFS_VERIFICATION_ERROR | 15 | The new root file system verification failed. |
| DOWNLOAD_STATE_INITIALIZATION_ERROR | 20 | Download status initialization fail |
| DOWNLOAD_METADATA_SIGNATURE_MISMATCH | 26 | Downloaded metadata tag name does not match |
| USER_CANCELLED | 48 | The upgrade process was canceled by the user. |
| PAYLOAD_TIMESTAMP_ERROR | 51 | Payload timestamp error, backward upgrade not supported. |
| UPDATED_BUT_NOT_ACTIVE | 52 | The upgrade is complete, but the new system has not yet been activated. |
| NOT_ENOUGH_SPACE | 60 | device storage space |
| DEVICE_CORRUPTED | 61 | Device corrupted or file system error |
| UPDATE_APPLY_PACKAGE_FAILED | 100 | Application upgrade package failed |

API version : V2.25.0 or above

System version : Android 12+

notes

This only applies to systems with AB minute zones ; the specific policy depends on whether the device firmware uses the AB minute zone policy.

### 8.5 InstallCallback

#### Application installation callback

Function name : public void onInstallFinished(String packageName, int returnCode, String msg);

Description : Installation complete.

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | application package name |
| returnCode | int | Reference error code |
| msg | String | information |

### 8.6 DeleteCallback

#### Application uninstallation callback

Function name : public onDeleteFinished(String packageName, int returnCode, String msg);

Description : Uninstallation complete.

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| packageName | String | application package name |
| returnCode | int | Reference error code |
| msg | String | information |

### 8.7 CopyCallback

#### for copying files and directories

Function name : public void onCopyProgress(int progress);

Description : Copy progress

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| progress | int | schedule |

Function name : public void onCopyFinished(int returnCode, String msg);

Description : Copy complete

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| returnCode | int | Reference error code |
| msg | String | information |

### 8.8 DataCallback

#### Callback for serial port data read

Function name : public void onDataReceive(byte[] buffer, int size);

Description : Serial port data reception

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| buffer | byte[] | data |
| size | int | Data length |

### 8.9 WiegandCallback

#### Wiegand read data callback

Function name : public void onReadData(String data);

Description : read data

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| data | String | Wiegand Read data |

#### 8.10, BackUpCallback

#### Callback to back up application data

Function name : public void onBackUpProgress(int progress);

Description : Backup progress

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| progress | int | schedule |

Function name : public void onBackUpFinished(int returnCode, String msg);

Description : Backup complete

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| returnCode | int | Reference error code |
| msg | String | information |

Function name : public void onBackUpPath(String path);

Description : return backup path

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| path | String | path |

### 8.11 RecoveryCallback

#### Callback to back up application data

Function name : public void onRecoveryProgress(int progress);

Description : Recovery progress

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| progress | int | schedule |

Function name : public void onRecoveryFinished(int returnCode, String msg);

Description : Recovery complete

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| returnCode | int | Reference error code |
| msg | String | information |

Function name : public void onRecoveryPath(String path);

Description : Return to the path where the backup is located.

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| path | String | path |

### 8.12 LogCallback

#### for fetching system logs line by line

Function name : public void onSingleLine(String log);

Description : Callback to each line of the system log

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| log | String | schedule |

### 8.13 UpdateCallback

#### OTA-AB upgrade callback

Function name : public void onStatusUpdate(int status, float percent);

Description : Callback to upgrade status and progress

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| status | int | status UpdateStatusConstants class |
| percent | float | schedule |

Function name : public void onPayloadApplicationComplete(int errCode);

Description : Callback upgrade complete status

parameterInstructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| errCode | int | ErrorCodeConstants class return codes |

### 9. Eliminate interfaces

### 9.1 Overview

APIs that are phased out will be marked with an updated phasing version . It is not recommended to use them after this point; if a replacement method is available, it is advised to use the new method. Generally, an interface will be completely phased out after three iterations , after which the interface will be completely unusable . The specific phasing version should be considered the one that is completely phased out .

List of deprecated APIs

| Function name | description | Elimination date | Eliminate version | Complete elimination | author |
| :--- | :--- | :--- | :--- | :--- | :--- |
| net_setWifiDefaultPassword | Set power on default WiFi connection username and password | 2022-05-19 | V2.6.0 | V2.9.0 | Xu Linrui |
| net_getWifiDefaultPassword | GetPower On - Default WiFi Connection Username and Password | 2022-05-19 | V2.6.0 | V2.9.0 | Xu Linrui |
| info_setModel | setdevice model | 2022-09-14 | V2.14.0 | V2.14.0 | Xu Linrui |
| info_setFactoryCompany | setdevice manufacturer | 2022-09-14 | V2.14.0 | V2.14.0 | Xu Linrui |
| info_setSoftwareVersion | setdevice software version number | 2022-09-14 | V2.14.0 | V2.14.0 | Xu Linrui |
| sys_setInVolume | Set device microphone switch status | 2022-10-25 | V2.16.0 | V2.16.0 | Xu Linrui |
| sys_getInVolume | getdevicemicrophoneswitch status​ | 2022-10-25 | V2.16.0 | V2.16.0 | Xu Linrui |
| disp_getScreenModel | Get dual-screen display combination | 2022-10-25 | V2.16.0 | V2.16.3 | Xu Linrui |

#### 9.2. net_setWifiDefaultPassword

Function name : public int net_setWifiDefaultPassword(String account, String pwd, int type)

Description : Set power on default WiFi connection username and password

API version : Versions prior to V2.6.0 are usable; version V2.6.0 and later are obsolete. Recommended alternative: net_setWifiConnect

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| account | String | account name |
| pwd | String | password |
| type | String | Encryption method 0: null Password 1: WEP Password 2: WPA |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

After setting , reboot will take effect.

Pass an empty cancelset to the account name .

#### 9.3. net_getWifiDefaultPassword

Function name : public String net_getWifiDefaultPassword()

Description : GetPower On - Default WiFi Connection Username and Password

API version : Versions prior to V2.6.0 are usable; version V2.6.0 and later are obsolete.

parameter :

null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | The return string contains commas: username + "", " + password + ", " + encryption method |

notes

For example, the return value :smdt,123456,1 represents the account name as smdt, the password as 123456, and the encryption type as WEP.

Encryption method 0: null Password 1: WEP Password 2: WPA

#### 9.4, info_setSoftwareVersion

Function name : public int info_setSoftwareVersion(String verison)

Description : setdevice software version number

API version : Obsolete. Please use a programming tool to program it.

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| verison | String | device software version number |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

After setting , reboot will take effect.

#### 9.5, info_setModel

Function name : public int info_setModel(String modelname)

Description : setdevice model

API version : Obsolete. Please use a programming tool to program it.

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| modelname | String | device model |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

After setting , reboot will take effect.

#### 9.6, info_setFactoryCompany

Function name : public int info_setFactoryCompany(String company)

Description : setdevice manufacturer

API version : Obsolete. Please use a programming tool to program it.

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| company | String | device manufacturers |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

After setting , reboot will take effect.

#### 9.7, sys_setInVolume

Function name : public int sys_setInVolume(int type, boolean enable)

Description : Set device microphone switch status

API version : V2.16.0 and later are obsolete. Recommended replacement method: sys_setAudioInput

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | devicetype 0: Headphone microphone; 1: Motherboard microphone; 2: USB microphone. |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes

The interface is unusable and not currently supported.

#### 9.8, sys_getInVolume

Function name : public int sys_getInVolume(int type)

Description : getdevicemicrophoneswitch status​

API version : V2.16.0 and later are obsolete. Recommended replacement method: sys_getAudioInput

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| type | int | devicetype 0: Headphone microphone; 1: Motherboard microphone; 2: USB microphone. |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: open 0: close |

notes

The interface is unusable and not currently supported.

#### 9.9, disp_getScreenModel

Function name : public int disp_getScreenModel()

Description : Get dual-screen display combination

API version : Deprecated after V2.16.0

parameter : null

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0: Single display; 4: Default dual display. |

### 10. Linux

### 10.1 Introduction

#### Introduction to Linux OS-API interfaces

The following interfaces have the same interface parameter names as Android APIs ; please search for the function names directly . Functions with different names or parameters than those in Android are listed in the "Different Interfaces" directory.

| return value | Function name | description |
| :--- | :--- | :--- |
| char* | info_getSoftwareVersion | Get software version number |
| char* | info_getKernelVersion | get kernel release information |
| char* | info_getMCUVersion | get MCU version number |
| char* | info_setFactoryCompany | get the current API version number |
| char* | info_getApiVersion | setdevice software version number |
| char* | info_getTotalMemory | getdevice total memory capacity |
| char* | info_getTotalStorage | getdevice total storage capacity |
| char* | info_getSerialNumber | get serial number |
| int | sys_getControl | Other I/O related sets (motherboard status LED, module power) |
| int | sys_getWatchDog | get Watchdog Switch Status |
| int | sys_setWatchDogFeed | Open the door and feed the dog |
| int | sys_setPowerOff | system Shut down |
| int | sys_setReboot | system reboot |
| int | custom_setRelayIoMode | Set relay mode and delay |
| int | custom_getRelayIoMode | getrelay current mode |
| int | disp_setLcdBackLight(int values) | Set backlight brightness |
| int | disp_getLcdBackLight() | get backlight brightness |
| int | disp_getLcdPwmFrequency() | get backlight frequency |
| char* | net_getMacAddress() | get Ethernet mac |
| int | sys_setGpioDirection(int direction, int value, char *pData, int lenth) | Set GPIO input/output status |
| int | sys_getGpioDirection(char *pData, int lenth) | get GPIO input/output status |
| int | sys_getGpioValue(char *pData, int lenth) | Get GPIO level status |
| int | sys_setControl(int type, int values) | Other I/O related sets (motherboard status LED, module power) |
| int | sys_setAutoPowerOnOff(char off_h, char off_m, char on_h, char on_m, char enable) | settiming open Shut down |
| int | sys_setWatchDog(char enable) | set watchdog switch status |
| int | dev_setUsbPower(int num, int values) | Set USB port power switch status |
| int | dev_getUsbPower(int num) | Get USB power switch status |
| int | dev_getUsbPower(int num) | Get USB power switch status |
| int | dev_setLedLighted(int led,int value) | set tri-color light status |
| int | dev_getLedState(int led) | get tri-color light status |
| int | custom_sendWiegandCardHIDPID(char HID_value, char PID_value, char transformat) | Wiegand sends card number using hidden code and public code. |
| char* | custom_readWiegandData() | readWiegandinput​​ |
| int | custom_releaseWiegandRead() | Exit Wiegand input blocking |

### 10.2 Version Change Log

Linux OS-API publicly released API version

| version | description | date | author |
| :--- | :--- | :--- | :--- |
| V1.0 | create | 2021-01-04 | Luo Jun |
| V2.0 | renew | 2022-10-24 | Luo Jun |

### 10.3 API Usage Methods

#### get object instance method

platform : Linux

Function name : const char * info_getApiVersion();

Description : get the current API version number

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| context | Context | Context |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | char* | String |

example

| char * str = info_getApiVersion();<br>Output result : V1.0.0-release |
| :--- |

#### Add library files

#### Linux Qt

Copy smdtio.h and libsmdtio.so to the project directory; 2. Add header files, find smdtio.h; 3. Right-click on the project and click Add Library, then add it following these steps; 4. Then, instantiate the object in your code and call the method.

### 10.1 Differences in Interface

#### 10.1.1, sys_setBluetooth

Function name : char *info_getRtcDateTime();

platform : Linux

Description : Get RTC date and time information

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| enable | boolean | true: open false: close |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | char* | call result , return RTC information |

notes

#### This interface differs from the Android interface in terms of parameters

#### 10.1.2, disp_setLcdPwmFrequency

Function name : int disp_setLcdPwmFrequency(int frequency, char ratio);

platform : Linux

Description : set Backlight screen ratio

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| frequency | int | frequency size |
| ratio | char | ratio |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result 0 success , less than 0 fail |

#### 10.1.3, gpio_export

Function name : int gpio_export(int pin)

platform : Linux

Description : set Pin export

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| pin | int | pin number |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0 success , less than 0 fail |

#### 10.1.4, gpio_unexport

Function name : int gpio_unexport(int pin)

platform : Linux

Description : cancel Pin export

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| pin | int | pin number |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0 success , less than 0 fail |

#### 10.1.5, gpio_direction

Function name : int gpio_direction(int pin, int dir)

platform : Linux

Description : set GPIO input output

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| pin | int | pin number |
| dir | int | 0 input , 1 output |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0 success , less than 0 fail |

#### 10.1.6, gpio_readdirection

Function name : int gpio_readdirection(int pin)

platform : Linux

Description : read GPIO input output

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| pin | int | pin number |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0 input , 1 output |

#### 10.1.7, gpio_write

Function name : int gpio_write(int pin,char *buffer)

platform : Linux

Description : set GPIO value

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| pin | int | pin number |
| buffer | char * | 0 low level , 1 high level |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | greater than 0 successes , 0 failures |

#### 10.1.8, gpio_read

Function name : int gpio_read(int pin);

platform : Linux

Description : read GPIO value

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| pin | int | pin number |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0 low level , 1 high level |

#### 10.1.9, sys_setTimerSwitchOffJson

Function name : int sys_setTimerSwitchOffJson();

platform : Linux

Description : getShut down time

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | Shut down time |

#### 10.1.10, sys_setTimerSwitchOnJson

Function name : int sys_setTimerSwitchOnJson();

platform : Linux

Description : get power on time

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | power on time |

#### 10.1.11, sys_setUtcPowerOnOff

Function name : int sys_setUtcPowerOnOff(int year, int mon,int offday, int offhour, int offmin, int onday,int onhour, int onmin, int enable);

platform : Linux

Description : timing UTC timeShut down

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| year | int | year |
| mon | int | month |
| offday | int | Shut down time day |
| offhour | int | Shut down time hour |
| offmin | int | Shut down time minute |
| onday | int | power on time day |
| onhour | int | power on time hour |
| onmin | int | power on time minute |
| enable | int | 1, 3 set , other value cancel |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0 success , less than 0 fail |

#### 10.1.12, sys_setUtcPowerOnOff

Function name : int sys_setUtcPowerOnOff(time_t timesoff, time_t timeson,int enable);

platform : Linux

Description : timing UTC time Shut down

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| timesoff | time_t | Shut down time struct |
| timeson | time_t | power on time struct |
| enable | int | 1, 3 can use , other value cancel |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0 success , less than 0 fail |

#### 10.1.13, dev_readEeprom

Function name : char* dev_readEeprom(int deviceId, int areaId, int start_addr, int size)

platform : Linux

Description : Read and write EEPROM content

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| deviceId | int | : device ID, Start from 0 , Each ID represents one EEPROM |
| areaId | int | Region ID , Start from 1 , Each ID represents one EEPROM A region of |
| start_addr | int | Starting address , Indicates reading data from this area |
| size | int | Read the length of the data |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | char* | Read data |

#### 10.1.14, dev_readEeprom_3399

Function name : char* dev_readEeprom_3399(int deviceId, int areaId, int start_addr, int size)

platform : Linux

Description : Read and write EEPROM content

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| deviceId | int | : device ID, Start from 0 , Each ID represents one EEPROM |
| areaId | int | Region ID , Start from 1 , Each ID represents one EEPROM A region of |
| start_addr | int | Starting address , Indicates reading data from this area |
| size | int | Read the length of the data |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | char* | Read data |

#### 10.1.15, dev_writeEeprom

Function name : int dev_writeEeprom(int deviceId, int areaId,int start_addr, int size, char * pData);

platform : Linux

Description : Read and write EEPROM content

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| deviceId | int | : device ID, Start from 0 , Each ID represents one EEPROM |
| areaId | int | Region ID , Start from 1 , Each ID represents one EEPROM A region of |
| start_addr | int | Starting address , Indicates reading data from this area |
| size | int | Read the length of the data |
| pData | char* | Read the length of the data |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | Write data length |

#### 10.1.16, dev_writeEeprom_3399

Function name : int dev_writeEeprom_3399(int deviceId, int areaId,int start_addr, int size, char * pData);

platform : Linux

Description : Read and write EEPROM content

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| deviceId | int | : device ID, Start from 0 , Each ID represents one EEPROM |
| areaId | int | Region ID , Start from 1 , Each ID represents one EEPROM A region of |
| start_addr | int | Starting address , Indicates reading data from this area |
| size | int | Read the length of the data |
| pData | char* | Read the length of the data |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | Write data length |

#### 10.1.17, custom_setRelayIoValue

Function name : int custom_setRelayIoValue(int value)

platform : Linux

Description : set relay size

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| value | int | pin number |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0 success , less than 0 fail |

#### 10.1.18, custom_getRelayIoValue

Function name : int custom_getRelayIoValue()

platform : Linux

Description : getrelay size

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| value | int | pin number |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0 success , less than 0 fail |

#### 10.1.19, dev_startFlag_on

Function name : int dev_startFlag_on()

platform : Linux

Description : MCU set

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0 success , less than 0 fail |

#### 10.1.20, dev_startFlag_off

Function name : int dev_startFlag_off()

platform : Linux

Description : MCU set

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 0 success , less than 0 fail |

#### 10.1.21, dev_getregval

Function name : unsigned char dev_getregval(unsigned char value)

platform : Linux

Description : get relay size

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| value | unsigned char | get register​ |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | unsigned char | register value​ |

#### 10.1.22, dev_setregval

Function name : int dev_setregval(unsigned char reg, unsigned char value)

platform : Linux

Description : set MCU register value

parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| reg | unsigned char | register |
| value | unsigned char | Set register value |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | unsigned char | register |

#### 10.1.23, dev_getMCUstatus

Function name : int dev_getMCUstatus()

platform : Linux

Description : getmcu status​

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | MCU Status |

#### 10.1.24, info_getArch

Function name : char *info_getArch()

platform : Linux

Description : getarchitecture

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | char* | architecture |

#### 10.1.25, sys_getSystemName

Function name : char * sys_getSystemName();

platform : Linux

Description : get system name

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | char* | system name |

#### 10.1.26, info_getRtcDateTime

Function name : char * info_getRtcDateTime();

platform : Linux

Description : get RTC date and time information

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | char* | RTC information |
