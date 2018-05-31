[![N|Solid](https://ebuilder.com/wp-content/uploads/2017/02/ebuilder-effortless-logo.png)](https://ebuilder.com/)

## Android compatibility
Minimum supported Android SDK version is 16 (4.1.x / Jelly Bean) [https://source.android.com/source/build-numbers](https://source.android.com/source/build-numbers)

## Project Setup

### Permissions
DDC SDK will not ask for any permissions; it's a host-app responsibility to request them. Library *support-v4* has been included only to check permissions state.

DDC SDK doesn't need any permissions in order to run.If no permissions are set by host-app DDC will only collect data for which permissions are not required.

Following are the permissions(along with their purpose) that can be requested:

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

In AndroidManifest.xml add the following line under application tag:
```xml
<meta-data android:name="ddc-sdk-license" android:value="<YOUR LICENSE KEY>" />
```

### Gradle Dependencies
Add following repository:
```
allprojects {
    repositories {
        maven {
            url "https://artifacts.ebuilder.io/repository/ebuilder-external-android/"
        }
    }
}

```

Add following dependencies:

```groovy
dependencies {
    implementation 'com.android.support:support-v4:27.1.1'
    implementation 'com.google.code.gson:gson:2.8.4'
    implementation 'org.apache.commons:commons-lang3:3.5'
    implementation 'io.ebuilder.mobile.services:ddc-sdk:1.2.0.156@aar'
}
```

### Proguard
In case your app has proguard rules enabled you need to ensure that DDC SDK classes are excluded from the proguard processing.
For more on proguard see [https://developer.android.com/studio/build/shrink-code](https://developer.android.com/studio/build/shrink-code)


Adjust your *proguard-rules.pro* file including the following lines:

```
-keep class io.ebuilder.mobile.services.** { *; }
-keep interface io.ebuilder.mobile.services.** { *; }
```

### Content Provider configuration
The SDK is using Content Provider to manage access to locally stored data. For more on Content Providers and their advantages see android documentation [https://developer.android.com/guide/topics/providers/content-providers](https://developer.android.com/guide/topics/providers/content-providers)

The host app is required to specify a unique app content uri. DDC reads this value as the attribute *data_content_provider_authority* that can be configured in *build.gradle*

```groovy
android {
    compileSdkVersion 26
    buildToolsVersion "26.0.2"

    defaultConfig {
        applicationId "com.company.app"
        minSdkVersion 16
        targetSdkVersion 26
        versionCode 2
        versionName "1.0"

        resValue "string", "data_content_provider_authority", "${applicationId}.ddc"
    }
}
```





## Modes supported by DDC

* **On demand trigger**: Host app explicitly calls DDC to do its work whenever needed
* **Scheduled trigger**: DDC owns the scheduling of DDC calls. Scheduling is built on GCM network manager [https://developers.google.com/cloud-messaging/network-manager](https://developers.google.com/cloud-messaging/network-manager)

## On Demand Trigger

### Initialization

```java
final ServiceTrigger serviceTrigger = DeviceDataCollectorFactory.setup(context, "SystemId", "IMEI", DeviceIdType.IMEI)
    .loggingEnabled()
    .build(context);
```

DeviceDataCollectorFactory.setup(...) has following mandatory arguments:
* *context* : application context
* *systemId* : name of your application
* *deviceId* : id of the device (for ex: device IMEI)
* *deviceIdType* : device id type. Possible values are DeviceIdType.IMEI, DeviceIdType.INSTALLATION_ID

#### Non mandatory settings
Following initialization settings can also be used using the fluent interface of *SettingsBuilder*:

| Builder function                           | Description                                                  | Default value |
| ------------------------------------------ | ------------------------------------------------------------ | ------------- |
| loggingEnabled()                           | Turn on SDK logging                                          | false         |
| wifiOnly()                                 | Transfer data only when connected to WIFI                    | false         |
| externalUserId(final String value)         | External user ID to include in the transferred data          | empty         |
| phoneNumber(final String value)            | Phone number to to include in the transferred data           | empty         |


### Usage

Obtain the ServiceTrigger reference initializing the sdk. Then invoke *run* at the key moments when the sdk should do its work.
```java
final ServiceTrigger serviceTrigger = DeviceDataCollectorFactory
	.setup(context, "SystemId", deviceId, deviceIdType).build(context);

serviceTrigger.run(context);
```


## Scheduled Trigger
You can let the SDK manage the scheduling of the DDC calls. It's only required to initialize it.
Scheduling is built on GCM network manager [https://developers.google.com/cloud-messaging/network-manager](https://developers.google.com/cloud-messaging/network-manager)
Scheduled jobs will continue to run even when the host app is not running or is put in background. Frequency is defined in the licence.

### Project Setup
Two extra dependencies are required in order to use the scheduler: *io.ebuilder.mobile.services.scheduler.gcm:ddc-gcm-scheduler* and *com.google.android.gms:play-services-gcm*

Adjust your *build.gradle* as following:

```groovy
dependencies {
    implementation 'com.android.support:support-v4:27.1.1'
    implementation 'com.google.code.gson:gson:2.8.4'
    implementation 'org.apache.commons:commons-lang3:3.5'
    implementation 'com.google.android.gms:play-services-gcm:15.0.1'
    implementation 'io.ebuilder.mobile.services:ddc-sdk:1.2.0.156@aar'
    implementation 'io.ebuilder.mobile.services.scheduler.gcm:ddc-gcm-scheduler:1.1.0.19@aar'
}
```

### Initialization
Initialization is similar to the On Demand Trigger having same mandatory and optional settings:

### Usage
Obtain a reference of the scheduler:

```java
Scheduler<ScheduledLicense> scheduler = ScheduledDDCFactory.setup(this, SYSTEM_ID, "deviceId", DeviceIdType.IMEI)
                .loggingEnabled().scheduler(this, ScheduledLicense.class);
```

Then you can schedule the jobs (*scheduler.reschedule(Context context)*):

```java
scheduler.reschedule(this);
```

 or unschedule the jobs (*scheduler.cancel(Context context)*) if you no longer want to run them:

 ```java
 scheduler.cancel(this);
 ```

 ### Debugging
 You can see the scheduled jobs via Android Debug Bridge (adb) [https://developer.android.com/studio/command-line/adb.html](https://developer.android.com/studio/command-line/adb.html)

 ```sh
 $ adb shell dumpsys activity service GcmService | grep "<YOUR PACKAGE ID>"
 ```

 For example:

 ```sh
 $ adb shell dumpsys activity service GcmService | grep "my.first.ddc.app/io.ebuilder.mobile.services.scheduler.gcm.services.CollectorService"

     (scheduled) my.first.ddc.app/io.ebuilder.mobile.services.scheduler.gcm.services.CollectorService{u=0 tag="DataCollectorService" trigger=window{period=1800s,flex=10s,earliest=221s,latest=311s} requirements=[NET_ANY] attributes=[PERSISTED,RECURRING] scheduled=-1488s last_run=N/A jid=N/A status=PENDING retries=0 client_lib=MANCHEGO_GCM-11717000}
     (finished) [my.first.ddc.app/io.ebuilder.mobile.services.scheduler.gcm.services.CollectorService:DataCollectorService,u0]
 ```
