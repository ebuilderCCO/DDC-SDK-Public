[![N|Solid](https://ebuilder.com/wp-content/uploads/2017/02/ebuilder-effortless-logo.png)](https://ebuilder.com/)

### About the SDK
Minimum supported SDK for DDC SDK is [16](https://source.android.com/source/build-numbers)

### How to init the SDK?

```java
DeviceDataCollectorFactory.setup(context, "SystemId", "IMEI", SettingsBuilder.DeviceIdType.IMEI)
    .loggingEnabled()
    .build(context);
```
* The **SystemId** is mandatory and is the name of the application using/embedding the SDK


By calling `setup(...).build(context)` the DDC SDK would start using internal scheduler. If you're prefer not to use DDC scheduler please use [OnDemandTrigger](#ondemand-trigger).

Once the setup is performed it is enough to call init when the app starts. You can use setup every time as well if prefered.

```java
DeviceDataCollectorFactory.init(context); //not required
```
### How to define license?
 In AndroidManifest.xml manifest add the following line under application tag:
```xml
<meta-data android:name="ddc-sdk-license" android:value="<YOUR LICENSE KEY>" />
```
Replace <YOUR LICENSE KEY> with your unique key which you will receive from eBuilder.

### Gradle Integration

eBuilder maven repository:
```
allprojects {
    repositories {
        maven {
            url "https://artifacts.ebuilder.io/repository/ebuilder-external-android/"
            credentials {
                username <mavenUser>
                password <mavenPassword>
            }
        }
    }
}

```

Add the compile dependency to your build.gradle

```groovy
dependencies {
    compile ("io.ebuilder.mobile.services:ddc-sdk:x.y.z@aar")
    compile 'org.apache.commons:commons-lang3:3.5'
    compile 'com.google.code.gson:gson:2.7'
    compile 'com.squareup.okhttp3:okhttp:3.8.0'
    // Added only for checking for runtime 
    // permissions has been set or not
    compile 'com.android.support:support-v4:25.3.1'
}
```
Replace “x.y.z” with current version of eBuilder DDC

**Please note**: DDC SDK will not ask for any permissions, it's a host-app responsibility to request it. Library: *support-v4* added just to check does permission has been set or not.

### All parameter used
DDC SDK supports the following parameters.

| Builder function                      | Description                              | Default value | Mandatory |
| ------------------------------------- | ---------------------------------------- | ------------- | --------- |
| loggingEnabled()                      | Turn on android logging. DDC SDK will dump a lot useful messages for debugging | false         |           |
| wifiOnly()                            | Send data to eBuilder backend only when connected to wifi | false         |           |
| collectGoogleAccounts()               | Give a permissions to DDC to collect device accounts | false         |           |
| externalUserId(final String value)    | External user ID to send in event        | empty         |           |
| sampleTime(final int value)           | Time in seconds how often to read data from device | 300 seconds   |           |
| sendEventSchedule(final int value)    | Time in seconds how often to send data to eBuilder backend | 300 seconds   |           |
| staticData(final Map<String, ?> data) | static data to add into the event. Could be anything. | empty         |           |

#### Example
```java
DeviceDataCollectorFactory.setup(this, "Name of the app", "IMEI", SettingsBuilder.DeviceIdType.IMEI)
    .loggingEnabled() //Not recommended for production
    .wifiOnly() // Only send data when connected via Wifi
    .phoneNumber(getPhonenumber()) //The phonenumber of the user
    .externalUserId(getUserId())
    .build(this);
```

### Permissions used

Permissions are removed from DDC SDK and it doesn't required anymore any permissions to run. By default if no permissions are set by host-app DDC will only collect data which are not required any permissions to be gathered. But if you need to collect information about bluetooth adapter, for example, the host-app must include `android.permission.BLUETOOTH` permission to be able DDC SDK collect such information.

| Permission                               | Description                              | Runtime |
| ---------------------------------------- | ---------------------------------------- | ------- |
| android.permission.ACCESS_WIFI_STATE     | Used for getting WiFi status to be included in event | no      |
| android.permission.ACCESS_NETWORK_STATE  | Used for getting current network status to be included in event | no      |
| android.permission.BLUETOOTH             | To gather information about Bluetooth adapter | no      |
| android.permission.INTERNET              | To enable SDK to make network call       | no      |
| android.permission.RECEIVE_BOOT_COMPLETED | Used for scheduling jobs to run services-on-demand | no      |
| android.permission.WAKE_LOCK             | Used for scheduling jobs to run services-on-demand | no      |
| android.permission.GET_ACCOUNTS          | Used to request information about available accounts on device | *yes*   |

### OnDemand trigger
If you wish to turn off DDC scheduler and use your own way to run the jobs in sequence please use on-demand trigger while doing a setup:
```java
final OnDemandTrigger onDemandTrigger = DeviceDataCollectorFactory
	.setup(context, "SystemId", deviceId, deviceIdType).onDemandTrigger(context);
onDemandTrigger.run(context);
```
**Developer note**: `onDemandTrigger.run(context)` will run inside a pooled thread created using by Executors API. Please be aware of it.
