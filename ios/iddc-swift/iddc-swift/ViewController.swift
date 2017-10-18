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
        self.title = "DDC(\(DdcManager.versionInfo))"
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func buttonPressed(_ sender: UIButton) {
        let deviceId = UIDevice.current.identifierForVendor?.uuidString ?? "uuid-unavailable"
        let manager = DdcManager(key: "YOUR-LICENSE-KEY", systemId: "YOUR-SYSTEM-ID", deviceId: deviceId, deviceIdType: .installationId)
        manager.run { (error) in
            DispatchQueue.main.async {
                if error == nil || error?.code == 0 {
                    self.labelResult.text = "ddc succeed"
                } else {
                    self.labelResult.text = String(format: "ddc failed with error: %@",error!.description)
                    print("ddc failed with error: ",error!.description)
                }
            }
        }
    }
    
}

