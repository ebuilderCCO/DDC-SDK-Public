[![N|Solid](https://ebuilder.com/wp-content/uploads/2017/02/ebuilder-effortless-logo.png)](https://ebuilder.com/)

- [Android compatibility](#android-compatibility)
- [Project Setup](#project-setup)
	- [Permissions](#permissions)
	- [Licence](#licence)
	- [Gradle Dependencies](#gradle-dependencies)
	- [Proguard](#proguard)
	- [Content Provider configuration](#content-provider-configuration)
- [Initialization](#initialization)
	- [Associating collected data with a user/device identity](#associating-collected-data-with-a-userdevice-identity)
- [Modes supported by DDC](#modes-supported-by-ddc)
- [On demand trigger](#on-demand-trigger)
	- [Usage](#usage)
- [Scheduled triggers](#scheduled-triggers)
	- [Usage](#usage-1)



## Android compatibility

Minimum supported [Android SDK version](https://source.android.com/source/build-numbers) is 16 (4.1.x / Jelly Bean).

## Project Setup

### Permissions
The *Device Data Collector* (DDC) SDK will not ask for any permissions; it's a host application's responsibility to request them. Library *support-v4* has been included only to check permissions state.

DDC SDK doesn't need any permissions in order to run. If no permissions are set by the host application, DDC will only collect data for which permissions are not required.

However, there are some permissions that improve data quality if already granted to the host application:

| Permission                                | Description                                                   | Runtime |
| ----------------------------------------- | ------------------------------------------------------------  | ------- |
| android.permission.READ_PHONE_STATE       | Retrieve device ids, network operator names                   | no      |
| android.permission.ACCESS_WIFI_STATE      | Retrieve WiFi status                                          | no      |
| android.permission.ACCESS_NETWORK_STATE   | Retrieve network status                                      | no      |
| android.permission.BLUETOOTH              | Retrieve information about Bluetooth adapter and paired devices | no      |
| android.permission.INTERNET               | To enable SDK to make network call                           | no      |
| android.permission.RECEIVE_BOOT_COMPLETED | Used for scheduling jobs to run services-on-demand           | no      |
| android.permission.WAKE_LOCK              | Used for scheduling jobs to run services-on-demand           | no      |


### Licence
In order to use the SDK you need a licence key that you can request from us.

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
    implementation "io.ebuilder.mobile.services:ddc-sdk:1.2.0.255"
}
```

**Note**: The DDC SDK has [dependencies of its own](https://artifacts-int.ebuilder.io/repository/ebuilder-maven/io/ebuilder/mobile/services/ddc-sdk/1.2.0.255/ddc-sdk-1.2.0.255.pom). If you run into build or runtime issues because your project depends on an older version of the Android support library, you can exclude the one required by DDC: 

```groovy
dependencies {
    implementation ("io.ebuilder.mobile.services:ddc-sdk:1.2.0.255") {
        exclude group: "com.android.support"
    }
}
```

###  

### Proguard

In case your app has [proguard](https://developer.android.com/studio/build/shrink-code) rules enabled you need to ensure that DDC SDK classes are excluded from the proguard processing.


Adjust your **proguard-rules.pro** file with the following lines:

```
-keep class io.ebuilder.mobile.services.** { *; }
-keep interface io.ebuilder.mobile.services.** { *; }
```



### Content Provider configuration

The SDK is using **Content Providers** to manage access to locally stored data. For more on Content Providers and their advantages see the [Android documentation](https://developer.android.com/guide/topics/providers/content-providers). 

The host app is required to specify a unique app content uri. DDC reads this value as the attribute **data_content_provider_authority** that can be configured in *build.gradle*

```groovy
android {
    compileSdkVersion 27
    buildToolsVersion "27.0.3"

    defaultConfig {
        applicationId "com.company.app"
        minSdkVersion 16
        targetSdkVersion 27
        versionCode 1
        versionName "1.0"

        resValue "string", "data_content_provider_authority", "${applicationId}.ddc"
    }
}
```



## Initialization

```java
DeviceDataCollector ddc = DeviceDataCollector.getDefault(context, "YOUR_LICENSE_KEY");
```

DeviceDataCollector.getDefault(...) has the following mandatory arguments:
* *context* (Context) : application context
* *licenseKey* (String) : the licence key provided by eBuilder

Enable SDK logging:

```java
ddc.loggingEnabled(true);
```

#### Associating collected data with a user/device identity
The following instance methods can be used to optionally provide additional user/device identifiers:

| Name           | Description                                                  |      |
| -------------- | ------------------------------------------------------------ | ---- |
| advertisingID  | The [Android advertising ID](https://developers.google.com/android/reference/com/google/android/gms/ads/identifier/package-summary) of a device. |      |
| externalUserID | The host application's user identity. For example a (unique) user name, a user ID, an e-mail - or a hash thereof. |      |
| phoneNumber    | The user's phone number.                                     |      |

Example:

```java
ddc.externalUserID("c23911a2-c455-4a59-96d0-c6fea09176b8"); 
ddc.phoneNumber("+1234567890");
ddc.advertisingId(adID);
```



## Modes supported by DDC

* **On demand trigger**: Host app explicitly calls DDC to collect data whenever desired.
* **Scheduled triggers** (recommended): DDC schedules data collection. It uses the [Android WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager/) for this.



## On demand trigger

### Usage

Collect data:

```java
ddc.run();
```

**Note:** on demand triggering is not recommended. Read below how to enable scheduling.



## Scheduled triggers

Scheduled jobs will in most cases continue running even when the host app is not running, or is put in background. Collection frequency is defined in the licence.

### Usage
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

 ### Debugging

 You can see the scheduled jobs via [Android Debug Bridge (adb)](https://developer.android.com/studio/command-line/adb.html):

 ```sh
 $ adb shell dumpsys jobscheduler | grep "<YOUR PACKAGE ID>"
 ```

 For example:

 ```sh
 $ adb shell dumpsys jobscheduler | grep "io.ebuilder.ddc.example.app"
 ```
