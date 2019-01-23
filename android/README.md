# Android

The Device Data Collector (**DDC**) for Android manual and [example implementation](./example-app).

[Compatibility](#compatibility)<br/>[Project Setup](#project-setup)<br/>	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Permissions](#permissions)<br/>	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Gradle Dependencies](#gradle-dependencies)<br/>	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Proguard](#proguard)<br/>	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Content Provider configuration](#content-provider-configuration)<br/>[Initialization](#initialization)<br/>	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Associating collected data with a user/device identity](#associating-collected-data-with-a-userdevice-identity)<br/>
[Data collection](#data-collection)<br/>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Data collection frequency](#data-collection-frequency)<br/>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[On demand trigger](#on-demand-trigger)<br/>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Scheduled triggers](#scheduled-triggers)<br/>



## Compatibility

Minimum supported [Android SDK version](https://source.android.com/source/build-numbers) is 16 (4.1.x / Jelly Bean).

## Project Setup

### Permissions
DDC SDK doesn't need any permissions in order to run (and won't ask for any). If no permissions are set by the host application, DDC will only collect data for which permissions are not required.

However, there are some permissions that improve data quality if already granted to the host application:

| Permission                                                   | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| android.permission.READ_PHONE_STATE                          | Retrieve SIM card info                                       |
| android.permission.ACCESS_WIFI_STATE                         | Retrieve WiFi status                                         |
| android.permission.ACCESS_NETWORK_STATE                      | Retrieve network status                                      |
| android.permission.BLUETOOTH                                 | Retrieve information about Bluetooth adapter and paired devices |
| android.permission.RECEIVE_BOOT_COMPLETED                    | Improves scheduler reliability upon device restart on some devices |
| android.permission.WAKE_LOCK                                 | Improves scheduler reliability on some devices               |
| android.permission.ACCESS_COARSE_LOCATION<br />or<br />android.permission.ACCESS_FINE_LOCATION | Used to collect SSID and (hashed) BSSID                      |



### Gradle Dependencies
Add the following repository:
```
allprojects {
    repositories {
        maven {
            url "https://artifacts.ebuilder.io/repository/ebuilder-external-android/"
        }
    }
}

```

Add DDC SDK:

```groovy
dependencies {
    implementation "io.ebuilder.mobile.services:ddc-sdk:${DDC_SDK_VERSION}"
}
```

**Note**: The DDC SDK has [dependencies of its own](https://artifacts-int.ebuilder.io/repository/ebuilder-maven/io/ebuilder/mobile/services/ddc-sdk/1.2.0.255/ddc-sdk-1.2.0.255.pom). If you run into build or runtime issues because your project depends on an older version of the Android support library, you can exclude the one required by DDC: 

```groovy
dependencies {
    implementation ("io.ebuilder.mobile.services:ddc-sdk:${DDC_SDK_VERSION}") {
        exclude group: "com.android.support"
    }
}
```
[SEE EXAMPLE](./android/example-app/ddc-example-app/build.gradle#L25)


### Proguard

In case your app has [proguard](https://developer.android.com/studio/build/shrink-code) rules enabled you need to ensure that DDC SDK classes are excluded from the proguard processing.


Adjust your **proguard-rules.pro** file with the following lines:

```
-keep class io.ebuilder.mobile.services.** { *; }
-keep interface io.ebuilder.mobile.services.** { *; }
```



### Content Provider configuration

The SDK is using **Content Providers** to manage access to locally stored data. For more on Content Providers and their advantages see the [Android documentation](https://developer.android.com/guide/topics/providers/content-providers). 

The host app is required to specify a unique app content uri. DDC reads this value as the attribute **data_content_provider_authority** that can be configured in *build.gradle*:

```groovy
android {
    defaultConfig {
        ...
        resValue "string", "data_content_provider_authority", "${applicationId}.ddc"
    }
    ...
}
```
[SEE EXAMPLE](./android/example-app/ddc-example-app/build.gradle#L3)

## Initialization

Import DDC:

```java
import io.ebuilder.mobile.services.DeviceDataCollector;
```

Create an instance:

```java
DeviceDataCollector ddc = DeviceDataCollector.getDefault(context, "YOUR_LICENSE_KEY");
```

DeviceDataCollector.getDefault(...) has the following mandatory arguments:
* *context* (Context) : application context
* *licenseKey* (String) : the licence key provided by eBuilder

Enable SDK logging (for debugging purposes):

```java
ddc.loggingEnabled(true); // default is false
```

#### Associating collected data with a user/device identity
The following properties can be used to optionally provide additional user/device identifiers:

| Name           | Description                                                  |
| -------------- | ------------------------------------------------------------ |
| advertisingId  | The [Android advertising ID](https://developers.google.com/android/reference/com/google/android/gms/ads/identifier/package-summary) of a device. |
| externalUserId | The host application's user identity. For example a (unique) user name, a user ID, an e-mail - or a hash thereof. |
| phoneNumber    | The user's phone number.                                     |

These can be set in any order, at any time (once there is a ddc instance) and as many time as needed.

Example:

```java
ddc.advertisingId(adID);
ddc.externalUserId("c23911a2-c455-4a59-96d0-c6fea09176b8"); 
ddc.phoneNumber("+1234567890");
```

**Note:** user data is encrypted and handled in accordance with EU GDPR.



## Data collection

Data can be collected in two ways:

- **On demand trigger**: Host app explicitly calls DDC to collect data whenever desired.
- **Scheduled triggers**: DDC schedules data collection. It uses the [Android WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager/) for this.

### Data collection frequency

The higher the frequency of data collection (DDC events), the greater the business value. The bare minimum is to trigger events on app open and/or app close using the on demand trigger, but **using scheduled data collection as well is ideal**.


### On demand trigger

Collect data:

```java
ddc.run();
```

**Note:** on demand triggering on its own is not recommended. Read below how to enable scheduling as well.

### Scheduled triggers

Scheduled jobs will in most cases continue running even when the host app is not running, or is put in background. Collection frequency is defined in the licence.

Start the scheduler:

```java
ddc.startScheduler();
```

There are use cases where you need to stop the scheduler service for specific devices. Such a use case, for instance, is compliance to the EU GDPR and the user's right to opt out of data collection.

Stop the scheduler:

```java
ddc.stopScheduler();
```

If a user choses to opt in again, simply start the scheduler again. Calling *startScheduler* is safe, even if scheduling already is enabled.

You can see the scheduled jobs via [Android Debug Bridge (adb)](https://developer.android.com/studio/command-line/adb.html):

```sh
$ adb shell dumpsys jobscheduler | grep "<YOUR PACKAGE ID>"
```

For example:

```sh
$ adb shell dumpsys jobscheduler | grep "io.ebuilder.ddc.example.app"
```
