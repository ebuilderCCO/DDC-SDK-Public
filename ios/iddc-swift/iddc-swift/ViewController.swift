//
//  ViewController.swift
//  iddc-swift
//
//  Created by Zhihui Tang on 2017-10-11.
//  Copyright © 2017 Crafttang. All rights reserved.
//

import UIKit
import iddc

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
    
    func getExternalUserID() -> String {
            return "this is my unique value that identifies a user"
    }
    
    @IBAction func buttonPressed(_ sender: UIButton) {
        print(DeviceDataCollector.getState(providedDeviceId: nil))
        let ddc =  DeviceDataCollector.getDefault(key: "eyJ1cmwiOiJodHRwczovL2FwaWd3LmVidWlsZGVyLmlvL21vY2stZW5kIiwgImxpY2Vuc2UiOiJ0aGVzeXN0ZW1zdXJyb2dhdGVwdWJsaWNrZXkiLCAiYXBpLWtleSI6ICJ0aGVtZW1vcnkyIn0K")
        ddc.debug = true
        ddc.externalUserID = "8284FFCC-8A3E-4D68-88A8-39BFAF26B76E"
        ddc.phoneNumber = "0046739698288"
        ddc.advertisingID = "8284FFCC-8A3E-4D68-88A8-39BFAF26B76E"
        ddc.run { (error) in
            DispatchQueue.main.async {
                if error == nil || error?.code == 0 {
                    self.labelResult.text = "ddc succeed"
                } else {
                    self.labelResult.text = String(format: "ddc failed with error: %@",error!.description)
                    print("ddc failed with error: ",error!.description)
                }
            }
        }
        print(DeviceDataCollector.getState(providedDeviceId: nil))
    }
    
}

