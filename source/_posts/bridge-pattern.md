---
title: 桥接模式 Bridge Pattern
date: 2020-03-06 23:16:59
tags:
- Java
- 设计模式
---

当一个抽象可以有多个实现时，通常用继承来协调它们。

例如，对于表示形状的抽象类，可以有圆形、矩形等具体实现，但使用的绘图 API 实现可以不同，如果为每种形状类再定义各种 API 的版本，不仅复杂，而且抽象与实现绑定在一起，不能在运行时改变。

桥接模式可以将抽象和实现分别放在独立的类层次结构中，两者可以独立地进行扩充。其实桥接模式没那么玄乎，我们在很多时候都不经意地使用了。

<!-- more -->

# 实例

## 参与者

![](/images/bridge-pattern/bridge-pattern.png)

- Abstraction
  较高层次的抽象，维护一个 Implementor 对象，对应上面例子中的“形状”

- RefinedAbstraction
  Abstraction 的一个实现，对应“圆形”、“矩形”等

- Implementor
  较低层次的抽象，定义了 Abstraction 某种基本操作的接口，对应“绘图 API”

- ConcreteImplementor
  Implementor 的一个实现，对应具体的“绘图 API”

## 源码

```java
public interface Implementor {
    void operationImp();
}
```

```
public class ConcreteImplementorA implements Implementor {
    @Override
    public void operationImp() {
        new Throwable().printStackTrace();
    }
}
```

```java
public abstract class Abstraction {
    private Implementor mImplementor;

    public Abstraction(Implementor implementor) {
        mImplementor = implementor;
    }

    public void setImplementor(Implementor implementor) {
        mImplementor = implementor;
    }

    public void operation() {
        mImplementor.operationImp();
    }
}
```

```java
public class RefinedAbstraction extends Abstraction {
    public RefinedAbstraction(Implementor implementor) {
        super(implementor);
    }
}
```

## 测试

```java
Abstraction abstraction = new RefinedAbstraction(new ConcreteImplementorA());
abstraction.operation();

abstraction.setImplementor(() -> System.out.println("Lambda implementor"));
abstraction.operation();
```

```shell
java.lang.Throwable
	at io.binac.designpattern.bridge.ConcreteImplementorA.operationImp(ConcreteImplementorA.java:6)
	at io.binac.designpattern.bridge.Abstraction.operation(Abstraction.java:15)
...
Lambda implementor
```

## 与抽象工厂模式比较

两者都可以将多个维度的实现进行组合，但{% post_link factory-pattern 抽象工厂模式 %}是创建型模式，注重的是一个产品族产品的创建，其实现往往是编译时定义好的，而桥接模式是结构型模式，Implementor 的实现可以是在运行时变化的。

## 与策略模式比较

如果忽略 Abstraction 的扩展，则与{% post_link strategy-pattern 策略模式 %}很相似，但策略模式侧重于在运行时替换具体实现，桥接模式则是为了分离抽象和实现。

# 源码实现

<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/designpattern/bridge>

# 参考资料

1. 《设计模式：可复用面向对象软件的基础》
2. [桥接模式 - 维基百科，自由的百科全书](https://zh.wikipedia.org/wiki/%E6%A9%8B%E6%8E%A5%E6%A8%A1%E5%BC%8F)
3. [2. 桥接模式 — Graphic Design Patterns](https://design-patterns.readthedocs.io/zh_CN/latest/structural_patterns/bridge.html)
