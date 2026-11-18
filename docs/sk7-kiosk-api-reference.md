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

December 05, 2025December 05, 2025V1.3V1.3API usage methodAPI usage method
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

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this); System.out.println(smdt.info_getApiVersion()); Output Result : V1.0.0-release |
| :--- |

Import JAR package
Android Studio
Copy smdt.ar to the project directory \ app \ libs \;

## 2.Right click on the jar file in the libs folder and select add as Library;


## 3.Then call the method after the instance object in the code.


## 1.4 API naming rules

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


## 1.5 Global error code

Error code category ErrorCode

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


## 2. Device information


## 2.1, version


## 2.1.1, info_getApiVersion

Function name : public String info_getApiVersion()
Description : Get current API version number
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | API version number |

example

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this); System.out.println(smdt.info_getApiVersion()); Output result : V1.0.0-release |
| :--- |

notes
null

## 2.1.2, info_getAndroidVersion

Function name : public String info_getAndroidVersion()
Description : get Android system version
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Android system version |


## 2.1.3, info_getSoftwareVersion

Function name : public String info_getSoftwareVersion()
Description : Getdevice software version number
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Device software version number |


## 2.1.4, info_getHardwareVersion

Function name : public String info_getHardwareVersion()
Description : Get device hardware version parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Device hardware version |


## 2.1.5, info_getKernelVersion

Function name : public String info_getKernelVersion()
Description : getdevice kernel version
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | kernel version |


## 2.1.6, info_getMCUVersion

Function name : public String info_getMCUVersion()
Description : get MCU version number
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | MCU version number |


## 2.1.7, info_getSecurityVersion

Function name : public String info_getSecurityVersion()
Description : Get Android security patch level
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | security patch date |


## 2.1.8, info_getNPUVersion

Function name : public String info_getNPUVersion()
Description : Get NPU driver version number
parameter : null
API version : V2.3.0 or above
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | NPU driver version number |


## 2.1.9, info_getWebViewVersion

Function name : public String info_getWebViewVersion()
Description : get WebView version
API version : V2.14.0 or above
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | WebView version |


## 2.2 Storage


## 2.2.1, info_getTotalMemory

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
Remaining 1GB or less, return MB

## 2.2.2, info_getAvailMemory

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
Return B below 1KB

## 2.2.3, info_getTotalStorage

Function name : public String info_getTotalStorage()
Description : getdevice total storage capacity
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Device total storage capacity |

notes
return size unit: GB

## 2.2.4, info_getAvailStorage

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
Return B below 1KB

## 2.2.5, info_getAppUsedMemory

Function name : public String info_getAppUsedMemory()
Description : Memory occupied by getAPP
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | The memory occupied by the APP |

notes
The unit is MB

## 2.3, model


## 2.3.1, info_getBoardType

Function name : public String info_getBoardType()
Description : get Board Model
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Board Model |


## 2.3.2, info_getModel

Function name : public String info_getModel()
Description : getdevice model
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Device model |


## 2.3.3, info_getFactoryCompany

Function name : public String info_getFactoryCompany()
Description : getdevice manufacturer
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | Device manufacturer |


## 2.4, serial number


## 2.4.1, info_getSerialNumber

Function name : public String info_getSerialNumber()
Description : get serial number
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | serial number |


## 2.5, CPU


## 2.5.1, info_getCpuTemperature

Function name : public String info_getCpuTemperature()
Description : get device CPU temperature
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | CPU Current Temperature |

notes
The unit is ℃

## 2.5.2, info_getCpuFrequency

Function name : public String info_getCpuFrequency()
Description : getdevice CPU frequency
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | CPU frequency |

notes
The unit is GHZ

## 2.5.3, info_getCpuUsage

Function name : public String info_getCpuUsage()
Description : getdevice CPU utilization rate
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | CPU utilization rate |

notes
Unit %

## 2.6 Face recognition


## 2.6.1, info_getFaceDetectSupport

Function name : public int info_getFaceDetectSupport()
Description : Do you support facial recognition
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | 1: Support 0: Not supported |


## 2.7, other


## 2.7.1, info_getDeviceTemperature

Function name : public float info_getDeviceTemperature()
Description : Ge tdevice temperature
API version : V2.11.0 and above
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | float | Current CPU temperature |

notes
The unit is ℃

## 3. Display management


## 3.1 Display screen


## 3.1.1, disp_setDispParams

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

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this); String params = "cpu_id=3568;disp_main=lvds;disp_aux=hdmi;"; int result = smdt.disp_setDispParams(params); |
| :--- |

notes
Pass in empty 'cancelset' to restore default screen parameters

## 3.1.2, disp_getDispParams

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

| SmdtManagerNew smdt = SmdtManagerNew.getInstance(this); String params = smdt.disp_getDispParams(); System.out.println(params); Output result : "cpu_id=3568;disp_main=lvds;disp_aux=hdmi;" |
| :--- |

notes
Output empty as default, screen parameters not configured through external device

## 3.1.3, disp_setDisplayOverScan

Function name : public int disp_setDisplayOverScan(int screen_id, String direction, int value)
Description : Set display area (screen zoom) simplify version : Not supported
parameter :

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| screen_id | int | Screen ID 0: Main screen 1: Secondary screen |
| direction | String | Direction: x: X-axis (both left and right simultaneously) y: Y-axis (both up and down simultaneously) left: left side right: right side top: top bottom: bottom all: all four sides |
| value | int | Distance from the boundary |

return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | call result , Reference error code |

notes
set range 80-100
The primary and secondary screens depend on the system and screen parameter definitions, not the number of screens. For example, if the primary screen is set to LVDS and the secondary screen to HDMI, and one HDMI screen is connected, the HDMI screen also becomes the secondary screen, and the required parameter is also "secondary screen".

## 3.1.4, disp_getDisplayOverScan

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

## 3.1.5, disp_getScreenWidth

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

## 3.1.6, disp_getScreenHeight

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

## 3.2 Backlight


## 3.2.1, disp_setLcdBackLight

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

## 3.2.2, disp_getLcdBackLight

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


## 3.2.3, disp_getLcdPwmFrequency

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

## 3.2.4, disp_getLcdBackLightMaxMin

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


## 3.2.5, disp_setLcdBackLightEnable

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


## 3.2.6, disp_getLcdBackLightEnable

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


## 3.3 System UI


## 3.3.1, disp_setDisplayDensity

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

## 3.3.2, disp_getDisplayDensity

Function name : public int disp_getDisplayDensity()
Description : get current display density DPI
Simplified version : Not supported
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | int | DPI value |


## 3.3.3, disp_getScreenShotBitmap

Function name : public Bitmap disp_getScreenShotBitmap()
Description : Screenshot and return a bitmap format image.
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | Bitmap | Image data |


## 3.3.4, disp_getScreenShot

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

## 3.3.5, disp_setDisplayRotation

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

## 3.3.6, disp_getDisplayRotation

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

## 3.3.7, disp_setBootLogo

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

## 3.3.8, disp_getBootLogo

Function name : public int disp_getBootLogo()
Description : get the current power on Logo file path
API version : V2.8.0 or above
parameter : null
return parameter Instructions

| parameter name | type | Instructions |
| :--- | :--- | :--- |
| return value | String | power on Logo: If not empty, it will be custom; otherwise, it will be the default. |


## 3.3.9, disp_setBootAnimation

Function name : public int disp_setBootAnimation(String filepath)
Description : Replace the power on animation
API version : V2.8.0 or above
parameter :

> *End of extracted summary — see full revision history on SUNTEK Developer Portal.*

