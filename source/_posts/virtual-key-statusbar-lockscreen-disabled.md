---
title: 分析和解决手机重启若干次后，虚拟按键、状态栏等失效的问题
date: 2017-09-19 13:28:20
tags:
- Java
- Android
---

Android 有种特殊的 App 叫老化测试工具，一般包括若干次重启、存储读写、音频播放、视频播放等流程，是一个预装在手机的 APK，出厂时会去掉。

测试做老化测试挂机 24 小时后，手机概率性出现问题，现象主要包括** Home 键和 Recent 键不能用、无法下拉快捷设置和通知栏、锁屏失效、开发者模式无法打开**等。复现概率很低，每台重启 50 次，10 台中出现 0 ~ 3 台。诡异的是，我这边也会每天挂机，但一台都没有复现。

这个问题严重、紧急且困难。对于用户来说，出现问题后，重启手机是不能解决的，只能恢复出厂设置。而且出现问题的都是测试使用的 user 正式版本，几天才会出一个新版本，而我们工程师使用的 userdebug 版本从未复现，这也给 debug 造成了麻烦。

<!-- more -->

# 分析过程

## 开机向导

手机出现的这些症状，经历过 Android 5.0 ROM 开发的人可能似曾相识。如果手机预装了 Google 开机向导 SetupWizard.apk，那么你一定会记得首次开机必须登录 Google 账户的脑残设定。虽然这一做法无可厚非，但由于众所周知的原因，天朝无法访问 Google 服务器，所以作为 ROM 开发人员，如果没有能稳定爬梯的 Wi-Fi，就不得不八仙过海各显神通了。

我的做法简单粗暴，刷好 userdebug 或者 eng 版本，首次开机直接`adb shell`进去删除 SetupWizard.apk，手机就直接跳过了开机向导进入 Launcher。实际上开机向导和 Launcher 本质上一样，都为默认 Activity 添加了`android.intent.category.HOME`的 filter，只不过开机向导具有更高的优先级。

但这一做法是有问题的，其症状和这次问题的现象完全一样。原因是开机向导完成时，会写入两个重要的属性：

```java
Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
```

这两个属性表示手机已经设置完成，设备处于可用状态了。如果没有写入，那么手机很多功能都不能正常工作。在没有内置 SetupWizard.apk 的 AOSP 中，这个任务由源码树下`packages/apps/Provision/`来完成。如果内置了 SetupWizard.apk，则 Provision.apk 会被覆盖。

我对问题机测试了两个属性：

```bash
$ adb shell settings get global device_provisioned
0
$ adb shell settings get secure user_setup_complete
0
```

果然属性是不对的。再将正确的值 put 进去，手机就正常了。

考虑到 Google 的开机向导完成后，我们还会显示一个定制界面，这个界面走完后 SetupWizard.apk 才会写入这两个属性。所以我首先怀疑开机向导有问题，尤其是定制部分，并推断这个 bug 不是跑老化后出现的，一定是第一次开机就有问题了，只不过测试当时没有注意。我特意叮嘱测试，刷机后第一次开机就要确认是不是就有问题。

就在我们打算去掉定制界面、进行压力测试以验证我的想法时，我被迅速打脸。20 台机器又复现了 4 台，并且测试确认第一次开机是正常的。这就非常尴尬。

## 老化测试

既然开机向导没有问题，比较有可能的就是老化测试了，它包括很多步骤，其中最令人注意的就是重启 50 次。

之前提到问题的原因是属性值不正确，从 log 中看，此时 Keyguard 会打印错误 log：

```
KeyguardViewMediator: doKeyguard: not showing because device isn't provisioned and the sim is not locked or missing
```

查看 log 发现，第一次复现的 3 台机器中，有两台是第一次重启后就报了这个错误，剩下一台是 50 次重启跑完后报了这个错误。手机出错时间肯定在 log 报错之前，于是我转向怀疑是 50 次重启过程中出了问题。

但是重启测试的核心代码非常简单，没有什么特别的地方：

```java
PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
if (pm != null) {
    pm.reboot("");
}
```

从 log 中分析关机和开机流程，也没发现可疑的地方。

## 初步规避方案

与龙哥讨论后，开始尝试规避方案，即在开机进入 Launcher 时判断这两个属性是否正确，如果不正确则重新写入。但老大否决了这个方案，原因是这个问题非常严重，应该找出问题的根源，这个根源可能会同时引起其它问题，而不仅仅是两个属性值。时间非常紧迫，看来 deadline 之前是解决不了了，但事实证明老大的想法是明智的。

与此同时我也在可疑的地方添加了 log，打印包名和调用栈信息，看到底是什么程序修改了这两个属性。Android 提供给第三方读写这两个属性的接口在`frameworks/base/core/java/android/provider/Settings.java`中，最终数据的存取是在 SettingsProvider 模块中。

但 Settings.java 中添加的 log 并没有什么卵用。

之前为了方便 debug，龙哥建议我用 QFIL 给问题机半擦烧写了一个 userdebug 的 bootimage（只是不时会有点问题）。另一同事发现这两个属性最终保存在`/data/system/users/0/`下的 XML 文件中，他将`settings_global.xml`导出，发现写入属性的值为默认，包名为`android`：

```xml
<setting id="19" name="device_provisioned" value="0" package="android" />
```

另一个属性也是类似，但保存在`settings_secure.xml`文件中。

这说明这两个错误属性值应该是由 framework 来写的，十有八九就是 SettingsProvider 本身。这意味着可能是我们的源码中出现了系统级别的错误，而不是什么开机向导或者第三方应用导致的。

初步规避方案宣告失败。

## SettingsProvider

同事把导出的 XML 文件与正常的文件做了对比，发现错误的 XML 文件总是把其它属性也写成系统默认值。也就是说，很可能是原来正常的文件丢失了，系统重新生成了一份。

我分析源码发现，Settings.java 通过 binder 机制调用 SettingsProvider 模块来完成数据的存取，最终是在 SettingsState.java 中读写 XML 文件的。

SettingsState 中维护了一个 mStatePersistFile 对象，这是一个 File 类型对象，指代当前的 XML 文件。事实上用于存取数据的 XML 文件一共有三个，每个都对应一个 SettingsState 对象：

```
/data/system/users/0/settings_system.xml
/data/system/users/0/settings_secure.xml
/data/system/users/0/settings_global.xml
```

原本这些数据保存在数据库中，从 Android 6.0 开始会迁移到 XML 文件，默认情况下还会在迁移后删除数据库文件`settings.db`。

在分析 XML 文件如何读写之前，需要先了解一下 AtomicFile。

### AtomicFile

AtomicFile 让文件的写入变为原子操作，然而阅读源码可以发现，它并不是线程安全的。

它的原理非常简单，就是在写入文件时为原文件创建一个扩展名为`.bak`的备份文件。

```java
// frameworks/base/core/java/android/util/AtomicFile.java

public class AtomicFile {
    private final File mBaseName;
    private final File mBackupName;

    /**
     * Create a new AtomicFile for a file located at the given File path.
     * The secondary backup file will be the same file path with ".bak" appended.
     */
    public AtomicFile(File baseName) {
        mBaseName = baseName;
        mBackupName = new File(baseName.getPath() + ".bak");
    }

    // ...

    /**
     * Start a new write operation on the file.  This returns a FileOutputStream
     * to which you can write the new file data.  The existing file is replaced
     * with the new data.  You <em>must not</em> directly close the given
     * FileOutputStream; instead call either {@link #finishWrite(FileOutputStream)}
     * or {@link #failWrite(FileOutputStream)}.
     *
     * <p>Note that if another thread is currently performing
     * a write, this will simply replace whatever that thread is writing
     * with the new file being written by this thread, and when the other
     * thread finishes the write the new write operation will no longer be
     * safe (or will be lost).  You must do your own threading protection for
     * access to AtomicFile.
     */
    public FileOutputStream startWrite() throws IOException {
        // Rename the current file so it may be used as a backup during the next read
        if (mBaseName.exists()) {
            if (!mBackupName.exists()) {
                if (!mBaseName.renameTo(mBackupName)) {
                    Log.w("AtomicFile", "Couldn't rename file " + mBaseName
                            + " to backup file " + mBackupName);
                }
            } else {
                mBaseName.delete();
            }
        }
        FileOutputStream str = null;
        try {
            str = new FileOutputStream(mBaseName);
        } catch (FileNotFoundException e) {
            File parent = mBaseName.getParentFile();
            if (!parent.mkdirs()) {
                throw new IOException("Couldn't create directory " + mBaseName);
            }
            FileUtils.setPermissions(
                parent.getPath(),
                FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IXOTH,
                -1, -1);
            try {
                str = new FileOutputStream(mBaseName);
            } catch (FileNotFoundException e2) {
                throw new IOException("Couldn't create " + mBaseName);
            }
        }
        return str;
    }

    /**
     * Call when you have successfully finished writing to the stream
     * returned by {@link #startWrite()}.  This will close, sync, and
     * commit the new data.  The next attempt to read the atomic file
     * will return the new file stream.
     */
    public void finishWrite(FileOutputStream str) {
        if (str != null) {
            FileUtils.sync(str);
            try {
                str.close();
                mBackupName.delete();
            } catch (IOException e) {
                Log.w("AtomicFile", "finishWrite: Got exception:", e);
            }
        }
    }

    /**
     * Call when you have failed for some reason at writing to the stream
     * returned by {@link #startWrite()}.  This will close the current
     * write stream, and roll back to the previous state of the file.
     */
    public void failWrite(FileOutputStream str) {
        if (str != null) {
            FileUtils.sync(str);
            try {
                str.close();
                mBaseName.delete();
                mBackupName.renameTo(mBaseName);
            } catch (IOException e) {
                Log.w("AtomicFile", "failWrite: Got exception:", e);
            }
        }
    }

    // ...

    /**
     * Open the atomic file for reading.  If there previously was an
     * incomplete write, this will roll back to the last good data before
     * opening for read.  You should call close() on the FileInputStream when
     * you are done reading from it.
     *
     * <p>Note that if another thread is currently performing
     * a write, this will incorrectly consider it to be in the state of a bad
     * write and roll back, causing the new data currently being written to
     * be dropped.  You must do your own threading protection for access to
     * AtomicFile.
     */
    public FileInputStream openRead() throws FileNotFoundException {
        if (mBackupName.exists()) {
            mBaseName.delete();
            mBackupName.renameTo(mBaseName);
        }
        return new FileInputStream(mBaseName);
    }

    // ...

}
```

`openRead()`方法用于读取文件，返回原文件的 FileInputStream。如果发现备份文件存在，则说明当前有写入操作未完成，直接把备份文件恢复为原文件，这样原文件中总是干净的数据。

`startWrite()`方法用于写入文件，返回原文件的 FileOutputStream。如果原文件存在，但备份文件不存在，则将创建一个备份。如果原文件存在且备份文件也存在，说明当前有写入操作未完成，直接把原文件删除。这样备份文件中总是干净的数据。当写入成功后，应调用`finishWrite()`来删除备份文件；当写入失败时，应调用`failWrite()`来将备份文件恢复为原文件。

线程安全问题由调用方考虑。

### XML 文件的读写

XML 文件的读取是在 SettingsState.java 的`readStateSyncLocked()`中：

```java
// frameworks/base/packages/SettingsProvider/src/com/android/providers/settings/SettingsState.java

private void readStateSyncLocked() {
    FileInputStream in;
    if (!mStatePersistFile.exists()) {
        Slog.i(LOG_TAG, "No settings state " + mStatePersistFile);
        addHistoricalOperationLocked(HISTORICAL_OPERATION_INITIALIZE, null);
        return;
    }
    try {
        in = new AtomicFile(mStatePersistFile).openRead();
    } catch (FileNotFoundException fnfe) {
        String message = "No settings state " + mStatePersistFile;
        Slog.wtf(LOG_TAG, message);
        Slog.i(LOG_TAG, message);
        return;
    }
    try {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, StandardCharsets.UTF_8.name());
        parseStateLocked(parser);
    } catch (XmlPullParserException | IOException e) {
        String message = "Failed parsing settings file: " + mStatePersistFile;
        Slog.wtf(LOG_TAG, message);
        throw new IllegalStateException(message , e);
    } finally {
        IoUtils.closeQuietly(in);
    }
}
```

XML 文件的写入是在`doWriteState()`中：

```java
private void doWriteState() {
    if (DEBUG_PERSISTENCE) {
        Slog.i(LOG_TAG, "[PERSIST START]");
    }

    AtomicFile destination = new AtomicFile(mStatePersistFile);

    final int version;
    final ArrayMap<String, Setting> settings;

    synchronized (mLock) {
        version = mVersion;
        settings = new ArrayMap<>(mSettings);
        mDirty = false;
        mWriteScheduled = false;
    }

    FileOutputStream out = null;
    try {
        out = destination.startWrite();

        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(out, StandardCharsets.UTF_8.name());
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        serializer.startDocument(null, true);
        serializer.startTag(null, TAG_SETTINGS);
        serializer.attribute(null, ATTR_VERSION, String.valueOf(version));

        final int settingCount = settings.size();
        for (int i = 0; i < settingCount; i++) {
            Setting setting = settings.valueAt(i);

            writeSingleSetting(mVersion, serializer, setting.getId(), setting.getName(),
                    setting.getValue(), setting.getPackageName());

            if (DEBUG_PERSISTENCE) {
                Slog.i(LOG_TAG, "[PERSISTED]" + setting.getName() + "=" + setting.getValue());
            }
        }

        serializer.endTag(null, TAG_SETTINGS);
        serializer.endDocument();
        destination.finishWrite(out);

        synchronized (mLock) {
            addHistoricalOperationLocked(HISTORICAL_OPERATION_PERSIST, null);
        }

        if (DEBUG_PERSISTENCE) {
            Slog.i(LOG_TAG, "[PERSIST END]");
        }
    } catch (Throwable t) {
        Slog.wtf(LOG_TAG, "Failed to write settings, restoring backup", t);
        destination.failWrite(out);
    } finally {
        IoUtils.closeQuietly(out);
    }
}
```

逻辑很简单，似乎没什么特别的。

我在`doWriteState()`中添加了 log。几天后测试复现问题，分析 log 发现，出错时确实走到了这里，并且写入的属性为默认值，包名为`android`，这与 XML 文件中是一致的。但是为什么 SettingsProvider 会写入默认值，仍然不清楚。这种问题只能反复添加 log、等复现、分析 log，转眼又是好几天。

AtomicFile 实际上是 Java 中 File 类的一个封装，最终仍然是通过 File 类读写 XML 文件的，于是我又在 File 类中的读写删除等方法中添加 log，打印调用栈信息，至于如何添加的又是另一个故事了：[在 Android Java 核心库 libcore 中打印 Log | Binac](https://binac.io/2017/08/30/print-log-in-libcore/)

分析 log 发现确实是在 AtomicFile 中读写删的，其他地方对这些 XML 文件没有任何操作。

问题迟迟不能解决，引起了小 boss 的注意。小 boss 拉我们开了一个小会，由于怀疑是 50 次重启过程中出了问题，他让我们在重启之前和开机时分别打印`/data/system/users/0/`下的文件。说起来几句话，实际上实现起来并不简单，完全是吃力不讨好的活——这种活当然由我来干了。

因为关机和开机不是一瞬间的事，而是有很复杂的流程，在哪里打印 log，这是需要认真考虑的。我先在调用`PowerManager#reboot()`方法前和开机启动 SettingsProvider 时打印log，发现了奇怪的现象：问题出现时，前一次关机时是没有问题的，文件没有丢失，而开机后却丢失了，并且只剩下备份文件！

这说明：问题出现的时刻，要么是在调用`PowerManager#reboot()`关机之后，要么是在开机流程走到 ProviderService 之前。

终于有了一线生机，然而事情并不那么简单，开关机流程太复杂，一一排查的话，不知要排查到什么时候。

我接着分析：

1. 理论上讲，Android 只有 SettingsProvider 会去直接读写这些文件，其他地方哪怕是 framework 也要通过它来进行
2. Google 不会傻到在其他模块去读写，因此第三方 App 的嫌疑更大
3. ProviderService 是在 System Server 中启动的，这算是开机流程相当早的时候了，而此时第三方 App 根本就没有启动
4. 与负责工模的龙哥确认，工模是不会读写这些文件的，公司内部的内置 App 也不会去读写

于是，我将目光转向

1. 内置的第三方 App，并且
2. 在关机的时候，有系统设置读写操作

## 内置的第三方 App

除 AOSP 自带和公司内置 App 外，还有一些客户提供或第三方外包公司开发的 App，这些 App 我们都没有源码。

一种方法就是把可疑的 App 列出，逐个去掉其中一个，然后跑测试。但这几乎不可行，不仅是因为以上都是推测，即使知道确实是第三方 App 搞的鬼，排查下来也要数周时间。

就在这时，真是柳暗花明又一村，我对比了几次问题出现的前一次关机的 log，发现有两三次有同一个第三方 App 疑似读写系统设置。但是为什么其他 log 中没有呢？

我调查了这个 App，它和手机保修有关，是一个印度外包公司做的。接着我又发现了诡异的现象……

开机，它都会自启；手动 kill 掉，一段时间后也会自启！这个可疑的 App，不仅不停地读写系统设置，而且开机自启，有着杀不死的进程保活机制！

我请教了龙哥，得到的回复是，即使是 T 卡 log，也会在调用`PowerManager#reboot()`不久后自动关闭，因此不是其他 log 中没有，而是根本打印不出来！

是的，这个时候连 T 卡 log 都关闭了，而这个 App 居然还活着！怪不得我们发现不了问题所在，打印不出有效的 log，怎么定位问题？

接下来的分析就顺理成章了：

1. 手机关机，关机流程，第三方 App、系统 App、特权 App、T 卡 log 等陆续关闭
2. 这个第三方 App 做了进程保活，只有它不会被关闭，而且一直在读写系统设置
3. 在某次写入系统设置时，AtomicFile 调用`startWrite()`方法，将原 XML 文件重命名为备份文件
4. 恰好在这时，Android 系统关闭，而尚未调用`finishWrite()`方法写入，也无法调用`failWrite()`方法恢复，原 XML 文件丢失，只剩下备份文件
5. 手机开机，调用 SettingsProvider 的`readStateSyncLocked()`方法，发现文件不存在，就重新创建了 XML 文件，且使用系统默认配置
6. 至此，原 XML 文件彻底丢失，所有系统设置恢复默认，手机虚拟按键、状态栏等全部失效。

那么为什么 userdebug 版本从未复现呢？我的推测是，userdebug 关机流程和 user 版本存在区别，userdebug 版本的关机 log 明显更多，流程也更长，在关机流程走完之前，这个 App 读写系统设置已经完成了。

我将此 App 从编译配置中去掉，又连续跑了几天测试都没有再复现问题。看来根因确实在这个奇葩的 App。

规避方案就很简单了，在`readStateSyncLocked()`方法中，如果发现原文件不存在，但备份文件存在，就把备份文件恢复为原文件，这样系统设置又恢复了。不过还是要让印度外包公司修复，彻底修复才是王道。

# 尾声

说到这我不得不吐槽印度人真是奇葩，我们把问题的前因后果都详细分析给印度外包公司，通知三哥赶紧确认是否有进程保活并一直读写系统配置，再三催促，人家根本不回答问题，然后突然更新了 APK，问我们——现在你们还能复现吗？

如果是我同事我可能当场就骂过去了，你他妈的不确认我们的问题，连修改了什么都不告诉我们，然后就让我们用新的 APK 复现？你知道我们花了多少人力多少时间在这个破问题上吗？你们有一点责任心吗？

至于道歉？呵呵，不存在的。

这个故事告诉我们，以后遇到阿三，一定要多留个心眼。

当然，为了防止阿三再次脑抽，我还是将规避方案加上去了。
