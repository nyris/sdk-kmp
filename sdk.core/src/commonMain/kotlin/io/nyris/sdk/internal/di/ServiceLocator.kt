/*
 * Copyright 2023 nyris GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UNCHECKED_CAST")

package io.nyris.sdk.internal.di

import io.nyris.sdk.NyrisConfig
import io.nyris.sdk.internal.ConfigInternal
import io.nyris.sdk.internal.util.DEFAULT
import io.nyris.sdk.internal.util.Logger
import kotlin.reflect.KClass

internal object ServiceLocator {
    internal val objectMap: MutableMap<KClass<*>, Lazy<*>> = mutableMapOf()

    inline fun <reified T> get(): Lazy<T> =
        objectMap[T::class] as? Lazy<T>
            ?: throw IllegalArgumentException("Unable to get dependency[${T::class.simpleName}]")

    fun <T : Any> put(
        kClass: KClass<T>,
        initializer: () -> T,
    ): ServiceLocator {
        objectMap[kClass] = lazy { initializer.invoke() }
        return this
    }

    // Please do not change the order!!
    fun init(
        apiKey: String,
        config: NyrisConfig,
    ) {
        // Init your private methods here
        putLogger(
            if (config.isDebug) {
                Logger.DEFAULT
            } else {
                Logger.EMPTY
            }
        )

        putConfig(
            ConfigInternal(
                apiKey = apiKey,
                isDebug = config.isDebug,
                httpEngine = config.httpEngine,
                timeout = config.timeout
            )
        )

        // Init your modules here
        NetworkModule.init(config.platform, config.baseUrl)
        ServiceModule.init()
        RepositoryModule.init()
        RequestBuilderModule.init()
    }

    private fun putLogger(logger: Logger) {
        put(Logger::class) { logger }
    }

    private fun putConfig(config: ConfigInternal) {
        put(ConfigInternal::class) { config }
    }
}
