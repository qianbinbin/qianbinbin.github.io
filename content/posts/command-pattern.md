---
title: 命令模式 Command Pattern
date: 2020-03-13 23:12:05
tags:
- Java
- 设计模式
---

命令模式将“请求”封装成对象，以便使用不同的请求、队列或者日志来参数化其他对象。

对这个设计模式的解读，网上一些文章存在误区。

<!-- more -->

## 实例

### 参与者

![](/images/command-pattern/command-pattern.png)

- Receiver
  接收者，知道如何实施与执行一个请求相关的操作。命名有误导性，有些文章把 Receiver 设计成接口，把它当成了类似观察者的角色，事实上 Receiver 应该是最终执行操作的类，任何类都可能作为一个接收者

- Command
  声明执行命令的接口，一般包括一个“执行”方法，还可以按需声明“撤销”、“重做”等方法

- ConcreteCommand
  具体命令，实现 Command 接口，与一个 Receiver 绑定并调用其相关操作

- Invoker
  命令调用者，可以按需实现历史记录等功能。有些文章根本就没有设置传入命令参数的方法，客户端无法将命令实例发送给调用者，这就没有理解调用者的作用，调用者失去了存在的意义

这样就将命令抽象出来，调用者与接收者分离。有些文章混淆了客户端与调用者，称客户端与接收者解耦，事实上客户端仍然需要创建 Receiver 实例，真正解耦的是调用者和接受者。

举个例子，工具栏是调用者，它负责调用命令、记录历史等，不关心最终这些命令是如何执行的（Receiver 是如何实现的）。

### 源码

```java
public class Receiver {
    public void action() {
        new Throwable().printStackTrace();
    }
}
```

```java
public interface Command {
    void execute();
}
```

```java
public class ConcreteCommand implements Command {
    private Receiver mReceiver;

    public ConcreteCommand(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    public void execute() {
        mReceiver.action();
    }
}
```

```java
public class Invoker {
    void call(Command command) {
        command.execute();
    }
}
```

这里调用者只负责执行命令，它可以实现更复杂的功能。

### 测试

```java
Receiver receiver = new Receiver();
Command command = new ConcreteCommand(receiver);
Invoker invoker = new Invoker();
invoker.call(command);
```

```shell
java.lang.Throwable
	at io.binac.designpattern.command.Receiver.action(Receiver.java:5)
	at io.binac.designpattern.command.ConcreteCommand.execute(ConcreteCommand.java:12)
	at io.binac.designpattern.command.Invoker.call(Invoker.java:5)
```

## 源码实现

<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/designpattern/command>

## 参考资料

1. 《Head First 设计模式》
2. [命令模式 - 维基百科，自由的百科全书](https://zh.wikipedia.org/wiki/%E5%91%BD%E4%BB%A4%E6%A8%A1%E5%BC%8F)
3. [1. 命令模式 — Graphic Design Patterns](https://design-patterns.readthedocs.io/zh_CN/latest/behavioral_patterns/command.html)
