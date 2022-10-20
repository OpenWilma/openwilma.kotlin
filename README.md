# openwilma.kotlin
OpenWilma Kotlin library

[![](https://jitpack.io/v/OpenWilma/openwilma.kotlin.svg)](https://jitpack.io/#OpenWilma/openwilma.kotlin) [![](https://github.com/openwilma/openwilma.kotlin/actions/workflows/gradle.yml/badge.svg)](https://github.com/OpenWilma/openwilma.kotlin/actions/workflows/gradle.yml)

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
    implementation 'org.openwilma:kotlin:0.9.7-BETA'
 }
 ```
 
 3. Start using OpenWilma!
