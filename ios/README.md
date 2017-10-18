# iOS implementation of iddc.framework

[![Build Status](https://img.shields.io/badge/Platform-iOS-lightgrey.svg)](https://www.apple.com)   [![iOS](https://img.shields.io/badge/iOS-8.0-brightgreen.svg)](https://www.apple.com) [![ide](https://img.shields.io/badge/IDE-Xcode9-lightgrey.svg)](https://img.shields.io/badge/IDE-Xcode9-lightgrey.svg)

## Example

To run the example project, clone the repo, and run `pod install` from the **iddc-oc** or **iddc-swift** directory first.

## Requirements

## Installation

## How to integrate

iOS DDC SDK **iddc** is available through [CocoaPods](http://cocoapods.org). To install
it, simply add the following line to your Podfile:

```ruby
pod 'iddc'
```

 * (**Objective-C project only**, if the Host App is a Swift project, please skip)Always Embed Swift Standard Libraries: Select your project in **"TARGETS"**(Not PROJECT), click **Build Settings**, Set **Always Embed Swift Standard Libraries** to **Yes** 
   ![embeded swift](./res/15071978755006.jpg "embeded swift")        


## Usage

```objective-c
#import <iddc/iddc.h>

...
...

/*
initWithKey: License key for DDC
deviceId: Unique id for the device.
deviceIdType: The type for the deviceId, it could be IMEI/GAID/IDFA/PhoneNumber/InstallationId.
*/
DdcManager *ddcManager = [[DdcManager alloc] initWithKey:@"YOUR-LICENSE-KEY" deviceId: @"YOU-DEVICE-ID" deviceIdType: deviceIdType];
[ddcManager runWithCompletion:^(DdcError * error) {    
    if (error == nil || [error code] == 0) {
        NSLog(@"ddc succeed");
    } else {
        NSLog(@"ddc completed with error: %@",error);
    }
}];

```

## Use the framework in Swift project 

```Swift
import iddc

...
...

/*
initWithKey: License key for DDC
deviceId: Unique id for the device.
deviceIdType: The type for the deviceId, it could be IMEI/GAID/IDFA/PhoneNumber/InstallationId.
*/
let deviceId = UIDevice.current.identifierForVendor?.uuidString ?? "uuid-unavailable"
let manager = DdcManager(key: "YOUR-LICENSE-KEY", deviceId: deviceId, deviceIdType: .installationId)
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


## Input Parameters:

* **initWithKey**: License key for DDC
* **deviceId**: Unique id for the device.
* **deviceIdType**: The type for the deviceId, it could be IMEI/GAID/IDFA/PhoneNumber/InstallationId.

### Output:
* **[ddcManager runWithCompletion]** returns a DdcError object. If `DdcError == nil or DdcError.code == 0`, that means DDC report succeed. Otherwise you can get the failure reason from `DdcError.description`


## Done




