//
//  TestFile.swift
//  iOS
//
//  Created by MOSTEFAOUIM on 19/03/2023.
//

import Foundation
import nyris

class TestIntegrationWithKMP {
    func greetings() -> Nyris {
        let config = NyrisConfig(isDebug: true, baseUrl: "", httpEngine: nil, timeout: 10, platform: NyrisPlatform.ios)
        return NyrisCompanion()
            .createInstance(apiKey: "",config: config)
    }
}
