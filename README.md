# AddressPicker
地址选择器

### How to use
Step 1:   Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```
Step 2. Add the dependency
```groovy
dependencies {
    implementation 'com.github.Hsicen:AddressPicker:1.0.0'
}
```

### proguard
add the following where you used the library
```
-keep class com.lljjcoder.**{
	*;
}

-dontwarn demo.**
-keep class demo.**{*;}
-dontwarn net.sourceforge.pinyin4j.**
-keep class net.sourceforge.pinyin4j.**{*;}
-keep class net.sourceforge.pinyin4j.format.**{*;}
-keep class net.sourceforge.pinyin4j.format.exception.**{*;}
```