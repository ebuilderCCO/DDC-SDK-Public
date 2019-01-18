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
    self.title = [NSString stringWithFormat:@"DDC(%@)", DeviceDataCollector.versionInfo];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (IBAction)buttonPressed:(UIButton *)sender {
    
     DeviceDataCollector *ddc = [DeviceDataCollector getDefaultWithKey:@"YOUR_LICENCE_KEY"];
    
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
