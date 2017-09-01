---
title: 使用 Android Studio 创建 Java 工程
date: 2017-05-21 22:44:42
tags:
- Android
- Java
---

1. 安装、配置所需的 JDK 和源码（不建议在普通 Java 工程中使用 Android SDK 中的 JDK）

2. 使用 Android Studio 创建一个 Android Application 工程

3. 切换视图为 Project，编辑`settings.gradle`，删除以下内容：

    ```
    include ':app'
    ```

<!-- more -->

4. 同步工程，点击工具栏按钮 Sync Project with Gradle Files

5. 删除工程根目录下的`app`目录

6. 编辑`build.gradle`，将其内容替换为：

    ```
    apply plugin: 'java'
    
    sourceCompatibility = 1.8
    version = '1.0'
    
    repositories {
        mavenCentral()
    }
    
    dependencies {
        testCompile group: 'junit', name: 'junit', version: '4.11'
    }
    ```

7. 同步工程，同步骤 4

8. 建立 Java 源码的目录层级，例如`src/main/java`，在其中建立一个主类（Main class），如`com.example.foo.HelloWorld.java`

9. 打开 Run - Edit Configurations，添加 Application 配置，设置名称和主类（Main class）

10. 打开 Edit - Project Structure，将`JDK location`设置为自己的 JDK 目录


## 参考资料

[Android Studio: create Java project with no Android dependencies - Stack Overflow](http://stackoverflow.com/questions/28957283/android-studio-create-java-project-with-no-android-dependencies)
