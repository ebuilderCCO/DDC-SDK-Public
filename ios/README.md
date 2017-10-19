# iOS implementation of iddc.framework

[![Build Status](https://img.shields.io/badge/Platform-iOS-lightgrey.svg)](https://www.apple.com)   [![iOS](https://img.shields.io/badge/iOS-8.0-brightgreen.svg)](https://www.apple.com) [![ide](https://img.shields.io/badge/IDE-Xcode9-lightgrey.svg)](https://img.shields.io/badge/IDE-Xcode9-lightgrey.svg)

## 1. Example

To run the example project, clone the repo, and run `pod install` from the **iddc-oc** or **iddc-swift** directory first.

## 2. Requirements

## 3. Installation

iOS DDC SDK **iddc** is available through [CocoaPods](http://cocoapods.org). To install
it, simply add the following line to your Podfile:

```ruby
pod 'iddc'
```

 * (**Objective-C project only**, if the Host App is a Swift project, please skip)Always Embed Swift Standard Libraries: Select your project in **"TARGETS"**(Not PROJECT), click **Build Settings**, Set **Always Embed Swift Standard Libraries** to **Yes** 
   ![embeded swift](./res/15071978755006.jpg "embeded swift")        


## 4. Usage

####  4.1 Use the framework in Objective-C project 

```objective-c
#import <iddc/iddc.h>

...
...

/*
initWithKey: License key for DDC
deviceId: Unique id for the device.
deviceIdType: The type for the deviceId, it could be IMEI/GAID/IDFA/PhoneNumber/InstallationId.
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

#### 4.2 Use the framework in Swift project 

```Swift
import iddc

...
...

/*
initWithKey: License key for DDC
deviceId: Unique id for the device.
deviceIdType: The type for the deviceId, it could be IMEI/GAID/IDFA/PhoneNumber/InstallationId.
*/
let manager = DdcManager(key: "YOUR-LICENSE-KEY", systemId: "YOUR-SYSTEM-ID", deviceId: "YOU-DEVICE-ID", deviceIdType: deviceIdType)
manager.run { (error) in
    if error == nil || error?.code == 0 {
        print("ddc succeed")
    } else {
        print("ddc failed with error ",error!.description)
    }
}
```


DeviceIdType in Objective-C

```objective-c
typedef SWIFT_ENUM(NSInteger, DeviceIdType) {
  DeviceIdTypeImei = 0,
  DeviceIdTypeGaid = 1,
  DeviceIdTypeIdfa = 2,
  DeviceIdTypeIccid = 3,
  DeviceIdTypePhoneNumber = 4,
  DeviceIdTypeInstallationId = 5,
};
```

DeviceIdType in Swift

```Swift
public enum DeviceIdType : Int {
    case imei
    case gaid
    case idfa
    case iccid
    case phoneNumber
    case installationId
}
```


#### 4.3 Input Parameters:

* **initWithKey**:mandatory,  license key for DDC
* **systemId**: mandatory, the name of the application using/embedding the SDK
* **deviceId**: mandatory, unique id for the device.
* **deviceIdType**: mandatory, the type for the deviceId, it could be IMEI/GAID/IDFA/PhoneNumber/InstallationId.

#### 4.4 Output:
* **[ddcManager runWithCompletion]** returns a DdcError object. If `DdcError == nil or DdcError.code == 0`, that means DDC report succeed. Otherwise you can get the failure reason from `DdcError.description`


## 5. Upload your host App to AppStore

The iddc.framework is a universal framework. The Appp with this framework can be run in both simulator and real device, but it is not allowed to be uploaded to AppStore.
So we need remove simulator architectures from the framework before we archiving and uploading to AppStore.
#### 5.1 Add [script](https://gist.github.com/zhihuitang/1046b71bf7fe6c169a29405c37f99a66) to **Archive: pre-actions**
Please remember to replace the framework name with **your own framework name**
![pre actions](./res/pre-actions.jpg "pre actions")  

#### 5.2 Add [script](https://gist.github.com/zhihuitang/69fc4784df749137b5ecf890b3c591e9) to **Archive: post-actions**
Please remember to replace the framework name with **your own framework name**
![post actions](./res/post-actions.jpg "post actions")  

> Ref: [Your executable contains unsupported architecture](https://dahuayuan.wordpress.com/2017/10/18/how-to-upload-an-app-with-universal-framework/)

#### 5.3 Then you can archive your host App as usual.





