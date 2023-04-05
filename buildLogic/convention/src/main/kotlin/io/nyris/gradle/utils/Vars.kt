package io.nyris.gradle.utils

object Vars {
    val IS_CI = System.getenv().containsKey("CI")
    val PUBLIC_API_KEY: String? = System.getenv("PUBLIC_API_KEY")
    val NYRIS_BOT_USER: String? = System.getenv("NYRIS_BOT_USER")
    val NYRIS_BOT_TAP: String? = System.getenv("NYRIS_BOT_TAP")
    val LIB_VERSION_NAME: String? = System.getenv("LIB_VERSION_NAME")
}
