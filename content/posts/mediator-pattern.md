---
title: 中介者模式 Mediator Pattern
date: 2020-03-19 13:20:51
tags:
- Java
- 设计模式
---

在没有中介者的情况下，所有的对象都要认识其他对象，有了中介者后，对象只需要与中介者通信，对象之间彻底解耦。但是如果设计不当，中介者本身会过于复杂。

中介者常被用来协调相关的 GUI 组件。例如，一个按钮被点击以后，其他控件应该如何处理。

## 实例

### 参与者

![](/images/mediator-pattern/mediator-pattern.png)

- Mediator
  定义与各 Colleague 对象通信的接口

- ConcreteMediator
  具体中介者，维护并协调各 Colleague 对象，实现协作行为

- Colleague class
  同事类，每个同事类都知道它的中介者对象，每个同事对象都通过中介者与其他同事进行通信

### 源码

抽象中介者：

```java
interface Mediator {
    void onColleagueUpdated(Colleague colleague);
}
```

抽象同事类：

```java
public abstract class Colleague {
    protected Mediator mMediator;

    public Colleague(Mediator mediator) {
        mMediator = mediator;
    }

    public void update() {
        mMediator.onColleagueUpdated(this);
    }
}
```

使用一个 `Mediator` 对象作为构造方法的参数，使同事必须包含一个中介者。

一旦调用同事类的 `update` 方法，就会触发中介者的 `onColleagueUpdated` 方法，在其中可以对不同的同事对象进行处理。

由于要维护一个 `Mediator` 对象，只能用类而不能用接口，可能会由于 Java 不支持多继承而造成不便，但在实践中 GUI 组件一般都是继承关系，而不是实现关系，因此影响不大。

具体同事类：

```java
public class ConcreteColleague1 extends Colleague {
    public ConcreteColleague1(Mediator mediator) {
        super(mediator);
    }
}
```

```java
public class ConcreteColleague2 extends Colleague {
    public ConcreteColleague2(Mediator mediator) {
        super(mediator);
    }
}
```

具体中介者：

```java
class ConcreteMediator implements Mediator {
    ConcreteColleague1 mColleague1 = new ConcreteColleague1(this);

    ConcreteColleague2 mColleague2 = new ConcreteColleague2(this);

    @Override
    public void onColleagueUpdated(Colleague colleague) {
        if (colleague == mColleague1) {
            System.out.println("Colleague1 changed");
        } else if (colleague == mColleague2) {
            System.out.println("Colleague2 changed");
        }
    }

    @Test
    void test() {
        mColleague1.update();
        mColleague2.update();
    }
}
```

这里写成了 JUnit 单元测试类。

### 测试

```shell
Colleague1 changed
Colleague2 changed
```

## 实现源码

<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/designpattern/mediator>

## 参考资料

1. 《设计模式：可复用面向对象软件的基础》
2. 《Head First 设计模式》
