//
//  ViewController.m
//  iddc-oc
//
//  Created by Zhihui Tang on 2017-10-11.
//  Copyright © 2017 Crafttang. All rights reserved.
//

#import "ViewController.h"
#import <iddc/iddc.h>


@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = [NSString stringWithFormat:@"DDC(%@)", DdcManager.versionInfo];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (IBAction)buttonPressed:(UIButton *)sender {
    NSString *uniqueIdentifier = [[[UIDevice currentDevice] identifierForVendor] UUIDString];
    DdcManager *ddcManager = [[DdcManager alloc] initWithKey:@"YOUR-LICENSE-KEY" deviceId: uniqueIdentifier deviceIdType: DeviceIdTypeInstallationId];
    [ddcManager runWithCompletion:^(DdcError * error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (error == nil || error.code == 0) {
                _labelResult.text = @"🤡 ddc succeed";
            } else {
                _labelResult.text = [NSString stringWithFormat:@"😟 ddc completed with error: \n%@", error];
            }
        });
    }];
}
@end
