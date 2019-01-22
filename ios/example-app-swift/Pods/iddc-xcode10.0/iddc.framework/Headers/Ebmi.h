//
//  MemoryInfo.h
//  iddc
//
//  Created by Zhihui Tang on 2017-10-10.
//  Copyright © 2017 eBuilder. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Ebmi : NSObject


+ (double)frm:(BOOL)inPercent;

+ (double)usdm:(BOOL)inPercent;

+ (double)atm:(BOOL)inPercent;

+ (double)iatm:(BOOL)inPercent;

+ (double)wdm:(BOOL)inPercent;

@end
