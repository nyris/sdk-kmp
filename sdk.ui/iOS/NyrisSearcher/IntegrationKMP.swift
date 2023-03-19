//
//  IntegrationKMP.swift
//  NyrisSearcher
//
//  Created by MOSTEFAOUIM on 19/03/2023.
//

import Foundation
import nyris

class IntegrationWithKMP {
    func greetings() -> Nyris {
        let config = NyrisConfig(isDebug: true, baseUrl: "", httpEngine: nil, timeout: 10, platform: NyrisPlatform.ios)
        return NyrisCompanion()
            .createInstance(apiKey: "",config: config)
    }
}
