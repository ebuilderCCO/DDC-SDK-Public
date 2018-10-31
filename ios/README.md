# iddc.framework

[![Build Status](https://img.shields.io/badge/Platform-iOS-lightgrey.svg)](https://www.apple.com)   [![iOS](https://img.shields.io/badge/iOS-8.0-brightgreen.svg)](https://www.apple.com) [![Xcode](https://img.shields.io/badge/Xcode-9.4-brightgreen.svg)](https://img.shields.io/badge/Xcode-9.4-brightgreen.svg)
[![Xcode](https://img.shields.io/badge/Xcode-10.0-brightgreen.svg)](https://img.shields.io/badge/Xcode-10.0-brightgreen.svg)

## On this page:
- [1. Example](#1-example)
- [2. Requirements](#2-requirements)
- [3. Installation](#3-installation)
- [4. Usage](#4-usage)
- [5. Trigger event on push notification](#5-trigger-event-on-push-notification)
    - [5.1 How it works](#51-how-it-works)
    - [5.2 Shared Data with App Groups](#52-shared-data-with-app-groups)
    - [5.3 Packaging](#53-packaging)
    - [5.4 Create and configure a Notification Service Extension](#54-create-and-configure-a-notification-service-extension)
    - [5.5 SDK Usage](#55-sdk-usage)
    - [5.6 Push notification requirements](#56-push-notification-requirements)
    - [5.7 Sample app](#57-sample-app)

---

## 1. Example

To run the example project, clone the repo, and run `pod update` from the **iddc-oc** or **iddc-swift** directory first.
```ruby
pod update
```

After installing the Cocoapods library,  double click `*.xcworkspace`(NOT `*.xcodeproj`) to open the project in your Xcode, build & run it.

## 2. Requirements

| iddc.framework  | Xcode |
| --------------- | ----- |
| iddc-xcode9.4   | 9.4   |
| iddc-xcode10.0  | 10.0  |


## 3. Installation

iOS DDC SDK **iddc** is available through [CocoaPods](http://cocoapods.org). To install
it, simply add the following line to your Podfile:

```ruby
pod 'iddc-xcode10.0', '0.1.189'
# or
# pod 'iddc-xcode9.4', '0.1.189'
```

* To install iddc.framework, run the script from command-line:
```ruby
pod install
```

* To upgrade iddc.framework, run the script from command-line:

```ruby
pod update
```

You can get available versions information of the framework by

```
pod trunk info iddc-xcode9.4
```

or
```
pod trunk info iddc-xcode10.0
```

## 4. Usage

#### 4.1 Add NSBluetoothPeripheralUsageDescription to Info.list
Since **iddc.framework** need Bluetooth permission to read its status(on/off), it needs to contain an NSBluetoothPeripheralUsageDescription key with a string value explaining to the user how the app uses this data.
But actually, the **iddc.framwork** won't show the permission dialogue at run time.
```xml
<key>NSBluetoothPeripheralUsageDescription</key>
<string>REASON-WHY-NEED-PERMISSION</string>
```
> Please replace **REASON-WHY-NEED-PERMISSION** with some meaningful words.

#### 4.2 Use the framework in Objective-C project
 * (Objective-C project only)Select your project in **"TARGETS"**(Not PROJECT), click **Build Settings**, Set **Always Embed Swift Standard Libraries** to **Yes**
   ![embed-swift](./res/embed-swift.png "embed-swift")

```objective-c
#import <iddc/iddc.h>

...
...

/*
 initWithKey: License key for DDC
    systemId: the name of the application using/embedding the SDK
    deviceId: Unique id for the device.
deviceIdType: The type for the deviceId, it could be IMEI/IDFA/PhoneNumber/InstallationId.
*/
DdcManager *ddcManager = [[DdcManager alloc] initWithKey:@"YOUR-LICENSE-KEY" systemId: @"YOUR-SYSTEM-ID" deviceId: @"YOU-DEVICE-ID" deviceIdType: deviceIdType];
[ddcManager runWithCompletion:^(DdcError * error) {
    if (error == nil || [error code] == 0) {
        NSLog(@"ddc succeed");
    } else {
        NSLog(@"ddc completed with error: %@",error);
    }
}];

```

> In develop environment, the DDC log output can be enabled by setting:
> ```
> ddcManager.debug = true;
> ```

DeviceIdType in Objective-C

```objective-c
typedef SWIFT_ENUM(NSInteger, DeviceIdType) {
  DeviceIdTypeImei = 0,
  DeviceIdTypeIdfa = 2,
  DeviceIdTypeIccid = 3,
  DeviceIdTypePhoneNumber = 4,
  DeviceIdTypeInstallationId = 5,
};
```


#### 4.3 Use the framework in Swift project

```Swift
import iddc

...
...

/*
         key: License key for DDC
    systemId: the name of the application using/embedding the SDK
    deviceId: Unique id for the device.
deviceIdType: The type for the deviceId, it could be IMEI/IDFA/PhoneNumber/InstallationId.
*/
let manager = DdcManager(key: "YOUR-LICENSE-KEY", systemId: "YOUR-SYSTEM-ID", deviceId: "YOU-DEVICE-ID", deviceIdType: deviceIdType)
manager.run { (error) in
    guard let ddcError = error else {
        print("DDC succeed")
        return
    }

    switch ddcError.code {
    case DdcErrorCode.succeed.rawValue:
        print("DDC succeed")
    case DdcErrorCode.tooManyRequests.rawValue:
        print("DDC failed due to too many requests in short time")
    case DdcErrorCode.ddcIsRunning.rawValue:
        print("DDC failed due to last requst is still running")
    default:
        // failed, other reason
        print("failed: \(ddcError)")
    }
}
```


DeviceIdType in Swift

```Swift
public enum DeviceIdType : Int {
    case imei
    case idfa
    case iccid
    case phoneNumber
    case installationId
}
```

#### 4.4 Output:
* **DdcManager.run()** returns a DdcError object. If `DdcError == nil or DdcError.code == 0`, that means DDC report succeed. Otherwise you can get the failure reason from `DdcError.description`

#### 4.5 Alternatives for triggering DDC
Generally, it is the responsibility of the host-app to trigger the DDC.
Every time the host-app triggers the DDC - the DDC will collect and upload a DDC event.
In order for the collected data to be of maximum business use a certain number of DDC events must be collected over time.
Fewer DDC events collected means less value can be realised.

Best practise is to trigger DDC based on location updates - however this requires the host-app to have a reason (use case) to subscribe to location updates from iOS.

Putting the DDC in the system-callback (e.g:`didUpdateToLocation`) event directly(e.g.: `didUpdateToLocation` ->
`DDC`) ensures separation of concerns and eliminate any risk of impacting other features, and vice versa.

```objective-c
- (void)locationManager:(CLLocationManager * )manager didUpdateToLocation:(CLLocation * )newLocation fromLocation:(CLLocation * )oldLocation {
    DdcManager * ddcManager = [[DdcManager alloc] initWithKey:@"YOUR-LICENSE-KEY" systemId: @"YOUR-SYSTEM-ID" deviceId: @"YOU-DEVICE-ID" deviceIdType: deviceIdType];
    [ddcManager runWithCompletion:^(DdcError * error) {
        if (error == nil || [error code] == 0) {
            // succeed
            NSLog(@"ddc succeed");
        } else if( [error code] == DdcErrorCodeTooManyRequests ){
            // failed due to too many requests in short time
        } else if( [error code] == DdcErrorCodeDdcIsRunning ){
            // failed due to last request is still running
        } else {
            // failed, other reason
            NSLog(@"ddc completed with error: %@",error);
        }
    }];
}
```

Other options include tying triggering of DDC to:
* Background fetch
* Audio process

As a last resort, DDC can be triggered when application state changes, e.g. when `func applicationDidEnterBackground(_ application: UIApplication)`, `func applicationWillEnterForeground(_ application: UIApplication)`

......

In our demo [Swift Demo App](./iddc-swift/iddc-swift/ViewController.swift), the DDC collections were triggered when a user tapped a Button.

```swift
@IBAction func buttonPressed(_ sender: UIButton) {
    let deviceId = UIDevice.current.identifierForVendor?.uuidString ?? "uuid-unavailable"
    let manager = DdcManager(key: "YOUR-LICENSE-KEY", systemId: "YOUR-SYSTEM-ID", deviceId: deviceId, deviceIdType: .installationId)
    manager.run { (error) in
        // result handling
    }
}
```

#### 4.6 User ID
In order to predict churn events you need to provide an external user ID. This ID will be used to correlate user data across devices. It is important that the user ID remains consistent on all devices. For example a login ID (username). Here is an example of how to set this:
```swift
// you need to implement a function that fetches a user ID that is unique to the user.
func getExternalUserID() -> String {
    return "this is my unique value that identifies this user"
}

@IBAction func buttonPressed(_ sender: UIButton) {
    let deviceId = UIDevice.current.identifierForVendor?.uuidString ?? "uuid-unavailable"
    let manager = DdcManager(key: "YOUR-LICENSE-KEY", systemId: "YOUR-SYSTEM-ID", deviceId: deviceId, deviceIdType: .installationId)

    manager.externalUserID = getExternalUserID()

    manager.run { (error) in
        // result handling
    }
}
```
This data is of course encrypted and handled in accordance with GDPR in EU.


## 5. Trigger event on push notification

You can trigger events on a push notification delivery.
To achieve  this functionality you need to implement a notification service extension.(https://developer.apple.com/documentation/usernotifications/unnotificationserviceextension)

### 5.1 How it works
Although bundled and published within your app, a notification service extension runs as a separate entity. Upon a push notification delivery the notification service is launched to perform needed work such as modify or enrich the content of the notification message. A lifecycle method is called to perform the work and we'll use same call to trigger an event.

#### 5.2 Shared data with App Groups
The app and the extension run in their own separate sandboxes therefore not sharing data or objects in memory.
However, App Groups feature allows data sharing between apps or between an app and the contained extension(s).

The SDK will be integrated in both the app and the extension and needs to synchronise the state between the two entities.
Once App Groups has been enabled in both the app and the extension the DDC SDK gets to know the path to shared location by getting a hang of the App Group ID.

App Group ID can be any id that is prefixed with "group." and must be declared as a variable named APP_GROUP_ID in Info.plist files of both the app and the extension.

Example:
```
APP_GROUP_ID = group.com.mycompany.myapp
```

To better visualize the role of the shared app group location see the images bellow.

![ios_ddc_without_notification_extension](./res/ios_ddc_without_notification_extension.png "ios_ddc_without_notification_extension")

In the figure above we can see that without enabling shared App Groups the DDC is using the sandboxed local storage for persisting settings and events.
Since the extension runs in its own sandbox it doesn't have access to this storage.

As shown in the following figure, the App Groups feature solved the inter-app common storage problem.

![ios_ddc_with_notification_extension](./res/ios_ddc_with_notification_extension.png "ios_ddc_with_notification_extension")

The initialization of the DDC  happens only in the app. As we'll see in a section bellow, the extension's DDC will only retrieve the settings from the local storage. Only the app has access to certain user specific settings and therefore only the app can retrieve such data.

If the triggering of events has been implemented in both the app and the extension the local storage serves also the purpose of bundling together the events originating from the app and extension, saving this way extra payload requests.

#### 5.3 Packaging
Although the app and the extension behave as separate entities at runtime, having minimal inter communication channels, having separate provisioning profiles, separate bundle IDs,  they are packaged together.
There is only one ipa archive file that is published to appstore.

This is depicted in the figure bellow:

![ios_ddc_with_notification_extension_packaging](./res/ios_ddc_with_notification_extension_packaging.png "ios_ddc_with_notification_extension_packaging")


#### 5.4 Create and configure a notification service extension
The notification service extension is created as a new target in the same Xcode project.
It also requires its own provisioning profile.

##### 5.4.1 Create a new target for Notification Service extension
Follow the steps bellow in Xcode to add the extension to your project.

###### Step1: File > New > Target ...
![extension_create_1_file_new_target](./res/extension_create_1_file_new_target.png "extension_create_1_file_new_target")

###### Step2: Choose Notification Service Extension template
![extension_create_2_choose_template](./res/extension_create_2_choose_template.png "extension_create_2_choose_template")

###### Step3: Choose target options
![extension_create_3_choose_target_options](./res/extension_create_3_choose_target_options.png "extension_create_3_choose_target_options")

The new Notification Service Extension target is created:
![extension_create_4_target_created](./res/extension_create_4_target_created.png "extension_create_4_target_created")



##### 5.4.2 Configure App Groups
AppGroups have to be configured for both the app and app extension. Follow the steps bellow.

###### Step1: Choose the group id
Let's assume the id is: *group.com.ebuilder.iddcdemo.swift.iddc-swift*

###### Step2: Enable the App Groups Capability
App Groups Capability needs to be enabled in both the app and the extension.
For each of the two targets go to Capabilities > App Groups and switch on the capability. Then specify the group id.
![add_app_groups_to_project.png](./res/add_app_groups_to_project.png "add_app_groups_to_project")

###### Step3: Add APP_GROUP_ID property to Info.plist
DDC SDK needs to know also the group id in order to discover the shared location. By convention DDC looks in Info.plist for a property called APP_GROUP_ID.
Since the DDC is integrated independently in both the app and the extension we need to specify the property in both targets.

Add following snippet to both Info.plist's:
```
<key>APP_GROUP_ID</key>
<string>group.com.ebuilder.iddcdemo.swift.iddc-swift</string>
```


##### 5.4.3 Provisioning profile
The extension needs its own provisioning profile. Follow the usual steps to configure the provisioning profile in Apple Developer console. (https://help.apple.com/xcode/mac/current/#/dev3a05256b8)

#### 5.5 SDK Usage
Upon the creation of the Notification Service Extension, Xcode creates also a service class with a basic implementation of the callback methods.

##### 5.5.1 Install DDC dependencies
Follow the steps bellow:

###### Step1: Add extension target
In your POD file add a new target:

```
target 'iddcSwiftNotification' do
    pod 'iddc-xcode10.0', '0.1.189'
    # or
    # pod 'iddc-xcode9.4', '0.1.189'
    use_frameworks!
end
```

###### Step2: Install
Execute the following commands:
```
pod repo update
pod install
```

Now you should see a new dependency added to your extension under *General > Linked Frameworks and Libraries*:

![dependencies_after_pod_install.png](./res/dependencies_after_pod_install.png "dependencies_after_pod_install")

##### 5.5.2 Trigger DDC Event

Xcode has created following default bare implementation that you need to adjust based on your needs:

```swift
import UserNotifications

class NotificationService: UNNotificationServiceExtension {

    var contentHandler: ((UNNotificationContent) -> Void)?
    var bestAttemptContent: UNMutableNotificationContent?

    override func didReceive(_ request: UNNotificationRequest, withContentHandler contentHandler: @escaping (UNNotificationContent) -> Void) {
        self.contentHandler = contentHandler
        bestAttemptContent = (request.content.mutableCopy() as? UNMutableNotificationContent)

        if let bestAttemptContent = bestAttemptContent {
            // Modify the notification content here...
            bestAttemptContent.title = "\(bestAttemptContent.title) [modified]"

            contentHandler(bestAttemptContent)
        }
    }

    override func serviceExtensionTimeWillExpire() {
        // Called just before the extension will be terminated by the system.
        // Use this as an opportunity to deliver your "best attempt" at modified content, otherwise the original push payload will be used.
        if let contentHandler = contentHandler, let bestAttemptContent =  bestAttemptContent {
            contentHandler(bestAttemptContent)
        }
    }

}
```

To integrate DDC follow the steps:

###### Step1: Import DDC
```swift
import iddc
```

###### Step2: Trigger the event in the callback method

```swift
override func didReceive(_ request: UNNotificationRequest, withContentHandler contentHandler: @escaping (UNNotificationContent) -> Void) {

        self.contentHandler = contentHandler
        bestAttemptContent = (request.content.mutableCopy() as? UNMutableNotificationContent)

        DdcManager.run { (error) in
            if let err = error {
                print("\(err.description)")
            }
        }

        if let bestAttemptContent = bestAttemptContent {
            // Modify the notification content here...
            // At the moment no modification is required
            contentHandler(bestAttemptContent)
        }
    }

```

#### 5.6 Push notification requirements
As described in Apple's documentation (https://developer.apple.com/documentation/usernotifications/unnotificationserviceextension) the push notification must conform to following requirements in order for the extension to be triggered on notification arrival:

```
  - The remote notification is configured to display an alert
  - The remote notification's aps dictionary includes the mutable-content key with the value set to 1
```
  ```json
  {"aps": {"mutable-content": 1}}
  ```


#### 5.7 Sample app
For a code sample and project configuration check the sample app available in this repository.
To setup and test Push Notifications check Apple's documentation: https://developer.apple.com/documentation/usernotifications

In brief, following actions and settings need to be performed:

  1. Enable Push Notifications capability in your app

    ![enable_push_notifications_capability.png](./res/enable_push_notifications_capability.png "enable_push_notifications_capability")

  2. Ask permission to use notifications

      https://developer.apple.com/documentation/usernotifications/asking_permission_to_use_notifications

  3. Register your app with APN, retrieve app's device token ID and send it to your provider server

      As a server you can also use a service provider such as https://www.pushwoosh.com

      More details here: https://developer.apple.com/documentation/usernotifications/registering_your_app_with_apns
