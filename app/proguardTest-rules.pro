# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class com.mycompany.chservicetime.** { *; }

# retrolambda & retrolambda
-dontwarn java.lang.invoke.*

#
#-dontwarn com.mycompany.chguardianpro.business.action.*

# http://jeroenmols.com/blog/2016/05/06/methodcount/

-dontoptimize
-dontobfuscate

# Guava
-dontwarn sun.misc.Unsafe
-dontwarn com.google.common.collect.MinMaxPriorityQueue

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

# Firebase Authentication
-keepattributes Signature
-keepattributes *Annotation*

#############################

# Proguard rules that are applied to your test apk/code.
-ignorewarnings

-keepattributes *Annotation*

-dontnote junit.framework.**
-dontnote junit.runner.**

-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**
-dontwarn org.hamcrest.**
-dontwarn com.squareup.javawriter.JavaWriter
# Uncomment this if you use Mockito
-dontwarn org.mockito.**

-keep public interface android.support.test.espresso.IdlingResource$ResourceCallback {*;}