---
title: 缩减 IMG 镜像文件
date: 2020-04-11 22:12:38
tags:
- Linux
---

折腾 Armbian 时，下载的镜像文件 `Armbian_20.05.1_Arm-64_bionic_current_5.6.2_20200408.img` 是 5000MiB，而实际文件系统只占用了 2GiB 左右，说明存在大量冗余。

这是个非官方版本的 Armbian，作者看样子是个毛子，脾气大得很。我建议他缩小镜像，他说完全没必要。2GiB 的东西硬是搞到接近 5GiB，真是传统艺能。

将镜像写入 U 盘或 SD 卡时，写入的就是镜像大小，而这本来就没必要，尤其 U 盘速度慢或者容量低的时候。

于是可以缩减镜像大小。

<!-- more -->

# 调整文件系统

开启 loopback 并寻找可用设备：

```shell
$ sudo modprobe loop
$ sudo losetup -f
/dev/loop18
```

设置 loop 设备：

```shell
$ sudo losetup /dev/loop18 Armbian_20.05.1_Arm-64_bionic_current_5.6.2_20200408.img
```

查看：

```shell
$ sudo fdisk -l /dev/loop18
Disk /dev/loop18: 4.9 GiB, 5242880000 bytes, 10240000 sectors
Units: sectors of 1 * 512 = 512 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disklabel type: dos
Disk identifier: 0x78b401bb

Device        Boot   Start      End Sectors  Size Id Type
/dev/loop18p1        32768  1081343 1048576  512M  e W95 FAT16 (LBA)
/dev/loop18p2      1081344 10239999 9158656  4.4G 83 Linux
```

可以看到每个 sector 是 512B，10239999 + 1 = 10240000 个就是 5000MiB，与镜像文件大小一致。

读取分区表：

```shell
$ sudo partprobe /dev/loop18
```

这时 `/dev/` 下出现了 `loop18p1` 和 `loop18p2`，分别是 boot 分区和 root 分区，我们的目标是 `loop18p2`，这是个 ext4 分区。

在 resize 之前先使用 `e2fsck` 检查：

```shell
$ sudo e2fsck -f /dev/loop18p2
e2fsck 1.44.1 (24-Mar-2018)
Pass 1: Checking inodes, blocks, and sizes
Pass 2: Checking directory structure
Pass 3: Checking directory connectivity
Pass 4: Checking reference counts
Pass 5: Checking group summary information
ROOTFS: 35394/286720 files (0.1% non-contiguous), 309097/1144832 blocks
```

缩减文件系统：

```shell
$ sudo resize2fs -M /dev/loop18p2 
resize2fs 1.44.1 (24-Mar-2018)
Resizing the filesystem on /dev/loop18p2 to 434677 (4k) blocks.
The filesystem on /dev/loop18p2 is now 434677 (4k) blocks long.

$ sudo resize2fs -M /dev/loop18p2 
resize2fs 1.44.1 (24-Mar-2018)
Resizing the filesystem on /dev/loop18p2 to 432231 (4k) blocks.
The filesystem on /dev/loop18p2 is now 432231 (4k) blocks long.

$ sudo resize2fs -M /dev/loop18p2 
resize2fs 1.44.1 (24-Mar-2018)
Resizing the filesystem on /dev/loop18p2 to 432227 (4k) blocks.
The filesystem on /dev/loop18p2 is now 432227 (4k) blocks long.

$ sudo resize2fs -M /dev/loop18p2 
resize2fs 1.44.1 (24-Mar-2018)
The filesystem is already 432227 (4k) blocks long.  Nothing to do!
```

参数 `-M` 是调整到最小，文件系统是需要一部分冗余空间的，多运行几次可以更极限一点。

`432227 (4k) blocks` 是指最终调整到 432227 个块，每个块 4KiB。这里 `resize2fs` 的单位不规范。

# 调整分区表

使用 `fdisk` 调整分区，先 `d` 删除，再 `n` 新建，最后 `w` 写入：

```shell
$ sudo fdisk /dev/loop18

Welcome to fdisk (util-linux 2.31.1).
Changes will remain in memory only, until you decide to write them.
Be careful before using the write command.


Command (m for help): p
Disk /dev/loop18: 4.9 GiB, 5242880000 bytes, 10240000 sectors
Units: sectors of 1 * 512 = 512 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disklabel type: dos
Disk identifier: 0x78b401bb

Device        Boot   Start      End Sectors  Size Id Type
/dev/loop18p1        32768  1081343 1048576  512M  e W95 FAT16 (LBA)
/dev/loop18p2      1081344 10239999 9158656  4.4G 83 Linux

Command (m for help): d
Partition number (1,2, default 2): 

Partition 2 has been deleted.

Command (m for help): n
Partition type
   p   primary (1 primary, 0 extended, 3 free)
   e   extended (container for logical partitions)
Select (default p): 

Using default response p.
Partition number (2-4, default 2): 
First sector (2048-10239999, default 2048): 1081344
Last sector, +sectors or +size{K,M,G,T,P} (1081344-10239999, default 10239999): +1.7G

Created a new partition 2 of type 'Linux' and of size 1.7 GiB.
Partition #2 contains a ext4 signature.

Do you want to remove the signature? [Y]es/[N]o: N

Command (m for help): w

The partition table has been altered.
Calling ioctl() to re-read partition table.
Re-reading the partition table failed.: Invalid argument

The kernel still uses the old table. The new table will be used at the next reboot or after you run partprobe(8) or kpartx(8).
```

在使用 `n` 新建时，起始 sector 与原来相同，最终 sector 可以直接输入分区大小，但要前置一个 `+` 符号。

`resize2fs` 的结果为 432227 个 4KiB 块，大小即为 432227 * 4KiB = 1728908KiB，大约为 1.65 GiB，这里要多留出一点，比如分配 1.7 GiB，否则文件系统可能会被破坏。

重新读取分区表：

```shell
$ sudo e2fsck -f /dev/loop18p2
e2fsck 1.44.1 (24-Mar-2018)
Pass 1: Checking inodes, blocks, and sizes
Pass 2: Checking directory structure
Pass 3: Checking directory connectivity
Pass 4: Checking reference counts
Pass 5: Checking group summary information
ROOTFS: 35394/114688 files (0.1% non-contiguous), 297742/432227 blocks
$ sudo partprobe /dev/loop18
$ sudo fdisk -l /dev/loop18
Disk /dev/loop18: 4.9 GiB, 5242880000 bytes, 10240000 sectors
Units: sectors of 1 * 512 = 512 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disklabel type: dos
Disk identifier: 0x78b401bb

Device        Boot   Start     End Sectors  Size Id Type
/dev/loop18p1        32768 1081343 1048576  512M  e W95 FAT16 (LBA)
/dev/loop18p2      1081344 4612095 3530752  1.7G 83 Linux
$ sudo losetup -d /dev/loop18
```

可以看到已经缩减为 1.7GiB 了。

解除设备：

```shell
$ sudo losetup -d /dev/loop18
```

# 截取 IMG 镜像

```shell
$ fdisk -l Armbian_20.05.1_Arm-64_bionic_current_5.6.2_20200408.img
Disk Armbian_20.05.1_Arm-64_bionic_current_5.6.2_20200408.img: 4.9 GiB, 5242880000 bytes, 10240000 sectors
Units: sectors of 1 * 512 = 512 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disklabel type: dos
Disk identifier: 0x78b401bb

Device                                                    Boot   Start     End Sectors  Size Id Type
Armbian_20.05.1_Arm-64_bionic_current_5.6.2_20200408.img1        32768 1081343 1048576  512M  e W95 FAT16 (LBA)
Armbian_20.05.1_Arm-64_bionic_current_5.6.2_20200408.img2      1081344 4612095 3530752  1.7G 83 Linux
```

截取：

```shell
$ truncate --size=$[(4612095+1)*512] Armbian_20.05.1_Arm-64_bionic_current_5.6.2_20200408.img
```

这里的 `--size` 是最终大小，即镜像的 sector 大小 * sector 数。

最终得到的 IMG 是 2.2GiB，还不到原来的一半。

# 参考资料

1. [Shrinking images on Linux - Softwarebakery](https://softwarebakery.com/shrinking-images-on-linux)
2. [How to Shrink an ext2/3/4 File system with resize2fs - Red Hat Customer Portal](https://access.redhat.com/articles/1196333)
3. [How to Resize a Partition using fdisk - Red Hat Customer Portal](https://access.redhat.com/articles/1190213)
