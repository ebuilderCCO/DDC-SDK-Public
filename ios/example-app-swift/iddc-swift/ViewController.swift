//
//  ViewController.swift
//  iddc-swift
//
//  Created by Zhihui Tang on 2017-10-11.
//  Copyright © 2017 Crafttang. All rights reserved.
//

import UIKit
import iddc
import AdSupport

class ViewController: UIViewController {

    @IBOutlet weak var labelResult: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "DDC(\(DeviceDataCollector.versionInfo))"
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func buttonPressed(_ sender: UIButton) {

        let ddc =  DeviceDataCollector.getDefault(key: "YOUR_LICENCE_KEY")

        ddc.externalUserID = "c23911a2-c455-4a59-96d0-c6fea09176b8"
        ddc.phoneNumber = "+1234567890"
        
        let asIdMng = ASIdentifierManager.shared()
        if asIdMng.isAdvertisingTrackingEnabled {
            ddc.advertisingID = asIdMng.advertisingIdentifier.uuidString
        } else {
            ddc.advertisingID = "00000000-0000-0000-0000-000000000000"
        }
        
        ddc.run { error in
            if let err = error {
                print("\(err.description)")
            }
        }

    }
    
}

