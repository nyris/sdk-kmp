# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in android-sdk/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
#
-keepattributes *Annotation*
-keep public class io.nyris.sdk.camera.** { public *; }
-keep public class io.nyris.sdk.camera.**$Companion { public *; }
-keep, allowobfuscation, allowoptimization class !io.nyris.sdk.camera.** { *; }
-keep class io.nyris.sdk.camera.core.BarcodeFormat
-keep class io.nyris.sdk.camera.core.CaptureMode
-keep class io.nyris.sdk.camera.core.CompressionFormat
-keep class io.nyris.sdk.camera.core.FocusMode

-dontwarn java.lang.invoke.StringConcatFactory