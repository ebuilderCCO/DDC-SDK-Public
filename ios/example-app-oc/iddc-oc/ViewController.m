//
//  ViewController.m
//  iddc-oc
//
//  Created by Zhihui Tang on 2017-10-11.
//  Copyright © 2017 Crafttang. All rights reserved.
//

#import "ViewController.h"
#import <iddc/iddc.h>
#import <AdSupport/ASIdentifierManager.h>


@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = [NSString stringWithFormat:@"DDC(%@)", DeviceDataCollector.versionInfo];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (IBAction)buttonPressed:(UIButton *)sender {
    
    DeviceDataCollector *ddc = [DeviceDataCollector getDefaultWithKey:@"YOUR_LICENCE_KEY"];
    
    ddc.phoneNumber = @"+1234567890";
    ddc.externalUserID = @"c23911a2-c455-4a59-96d0-c6fea09176b8";

    if ([[ASIdentifierManager sharedManager] isAdvertisingTrackingEnabled]) {
        NSUUID  *advertisingId =  [[ASIdentifierManager sharedManager] advertisingIdentifier];
        if (advertisingId != NULL) {
            ddc.advertisingID = [advertisingId UUIDString];
        }
    } else {
        ddc.advertisingID = @"00000000-0000-0000-0000-000000000000";
    }
    
    [ddc runWithCompletion:^(DdcError *error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (error == nil || error.code == 0) {
                _labelResult.text = @"ddc succeeded";
            } else {
                _labelResult.text = [NSString stringWithFormat:@"ddc failed with error: \n%@", error];
            }
        });
    }];
}
@end
