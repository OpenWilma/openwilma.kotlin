# openwilma.kotlin
OpenWilma Kotlin library

[![](https://maven.testausserveri.fi/api/badge/latest/openwilma/org/openwilma/kotlin/?color=40c14a&name=Testausmaven&prefix=v)](https://maven.testausserveri.fi/#/openwilma/org/openwilma/kotlin) [![](https://github.com/openwilma/openwilma.kotlin/actions/workflows/gradle.yml/badge.svg)](https://github.com/OpenWilma/openwilma.kotlin/actions/workflows/gradle.yml)

## How to install

1. Add jitpack.io to root build.gradle:

```gradle
allprojects {
    repositories {
	...
	maven { url 'https://maven.testausserveri.fi/openwilma' }
    }
}
  ```
  
  
 2. Add dependency
 
 ```gradle
 dependencies {
    implementation 'org.openwilma:kotlin:0.9.8-BETA'
 }
 ```
 
 3. Start using OpenWilma!
 
 ### Android and ProGuard
 
 If you're using ProGuard in your Android project, be sure to include this snippet in your `proguard-rules.pro`:
```gradle
-keep class org.openwilma.** { *; }
-keep public class org.openwilma.** { *; }
```
