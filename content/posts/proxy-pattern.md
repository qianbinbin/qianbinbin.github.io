---
title: 代理模式 Proxy Pattern
date: 2020-03-10 01:19:31
tags:
- Java
- 设计模式
---

客户端不能或不想直接使用一个类时，用代理模式可以实现间接使用。

代理模式一般指的是静态代理，即在编译时实现，代理类 SubjectProxy 与被代理的真实类 RealSubject 实现同一个 Subject 接口，SubjectProxy 维护一个 RealSubject 实例，客户端通过 SubjectProxy 来间接使用 RealSubject。

Java 可以实现动态代理，在运行时定制或增强功能。

<!-- more -->

## 实例

### 静态代理

![](/images/proxy-pattern/proxy-pattern.png)

```java
public interface Subject {
    void request();
}
```

```java
public class RealSubject implements Subject {
    @Override
    public void request() {
        new Throwable().printStackTrace();
    }
}
```

```java
public class SubjectProxy implements Subject {
    private RealSubject mSubject;

    @Override
    public void request() {
        if (mSubject == null)
            mSubject = new RealSubject();
        mSubject.request();
    }
}
```

#### 测试

```java
Subject subject = new SubjectProxy();
subject.request();
```

```shell
java.lang.Throwable
	at io.binac.designpattern.proxy.RealSubject.request(RealSubject.java:6)
	at io.binac.designpattern.proxy.SubjectProxy.request(SubjectProxy.java:10)
```

#### 与装饰器模式比较

两者都封装原始对象，代理模式注重控制对象的访问，装饰器模式则注重装饰对象，而且可以多次装饰。

#### 应用

- 远程代理
  为对象在不同地址空间提供局部代表，通过序列化等手段，使客户端的代码在远程执行

- 虚拟代理
  根据需要创建开销很大的对象，直到真正需要一个对象时才创建它，创建完成前用虚拟代理来代替，例如加载网络图片前，先加载本地图片

- 保护代理
  控制对原始对象的访问

- 指针引用
  当调用真实的对象时，代理处理一些额外工作，例如引用计数，当该对象没有引用时自动释放

### Java 动态代理

在动态代理中，代理类的字节码将在运行时生成并载入当前代理的 `ClassLoader`。这里介绍 Java 自身支持的代理方式，此外还有 cglib 等第三方库。

实现 `java.lang.reflect.InvocationHandler` 接口：

```java
public class SubjectInvocationHandler implements InvocationHandler {
    private RealSubject mSubject;

    public SubjectInvocationHandler(RealSubject subject) {
        mSubject = subject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("invoking " + method);
        Object result = method.invoke(mSubject, args);
        System.out.println("invoked " + method);
        return result;
    }
}
```

动态代理是通过反射完成的，在重写 `InvocationHandler#invoke` 时定制所需的方法，不需要像静态代理那样重载被代理接口的全部方法。

#### 测试

先获取一个需要代理的实例，然后通过 `java.lang.reflect.Proxy#newProxyInstance` 来获取代理类的对象：

```java
Subject subject = new RealSubject();
Subject proxy = (Subject) Proxy.newProxyInstance(
        subject.getClass().getClassLoader(),
        subject.getClass().getInterfaces(),
        new SubjectInvocationHandler(subject)
);
proxy.request();
```

```shell
invoking public abstract void io.binac.designpattern.proxy.Subject.request()
java.lang.Throwable
	at io.binac.designpattern.proxy.RealSubject.request(RealSubject.java:6)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at io.binac.designpattern.proxy.SubjectInvocationHandler.invoke(SubjectInvocationHandler.java:16)
	at com.sun.proxy.$Proxy9.request(Unknown Source)
...
invoked public abstract void io.binac.designpattern.proxy.Subject.request()
```

需要注意的是，动态代理的类必须实现接口，使用时也要用接口的方式定义，这是因为生成的代理类继承了 `java.lang.reflect.Proxy`，而 Java 不支持多继承，只能用接口完成。如果手动生成字节码文件，可以看到定义类似这样：

```java
public final class $Proxy0 extends Proxy implements Subject {
}
```

## 源码实现

<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/designpattern/proxy>

## 参考资料

1. 《Head First 设计模式》
2. [代理模式原理及实例讲解](https://www.ibm.com/developerworks/cn/java/j-lo-proxy-pattern/index.html)
3. [为什么JDK的动态代理要基于接口实现而不能基于继承实现？ - 掘金](https://juejin.im/post/5d8a0799f265da5b7a752e7c)
