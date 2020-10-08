---
title: 状态模式 State Pattern
date: 2020-03-21 15:07:51
tags:
- Java
- 设计模式
---

状态模式允许对象在内部状态改变时改变它的行为，对象看起来好像修改了它的类。

《Head First 设计模式》举的例子是糖果机：糖果机定义了投币、退币、转动曲柄、发放糖果等行为，也有未投币、已投币、售出、已售罄等状态，可以使用 `int` 常量或枚举来标识这些状态。在客户端对糖果机发出请求时，糖果机内部每种行为都要使用大量的 `if` 语句进行状态判断和切换，不仅混乱，而且不利于扩展，例如新增一个状态就要大量修改原有代码。

状态模式将“状态”抽象出来，将每种状态下的行为下放到具体状态类，这对客户端是透明的。网上有些文章把状态的构建和切换放到了客户端，这就违背了初衷。

## 实例

### 参与者

![](/images/state-pattern/state-pattern.png)

- Context
  环境，定义客户端所需的接口，维护一个 State 状态对象

- State
  抽象状态，定义与 Context 的一个特定状态相关行为的接口，任何状态都实现这个接口，状态之间可以互相替换

- ConcreteState subclasses
  具体状态子类，处理来自 Context 的请求，每个类都提供了自己对请求的实现，所以当 Context 状态改变时行为也跟着改变

### 源码

```java
public class Context {
    private State mState;
    private State mStateA;
    private State mStateB;

    void setState(State state) {
        mState = state;
    }

    State getStateA() {
        return mStateA;
    }

    State getStateB() {
        return mStateB;
    }

    public Context() {
        mStateA = new ConcreteStateA(this);
        mStateB = new ConcreteStateB(this);
        mState = mStateA;
    }

    public void request() {
        mState.handle();
    }
}
```

```java
public interface State {
    void handle();
}
```

```java
public class ConcreteStateA implements State {
    private Context mContext;

    public ConcreteStateA(Context context) {
        mContext = context;
    }

    @Override
    public void handle() {
        new Throwable().printStackTrace();
        mContext.setState(mContext.getStateB());
    }
}
```

```java
public class ConcreteStateB implements State {
    private Context mContext;

    public ConcreteStateB(Context context) {
        mContext = context;
    }

    @Override
    public void handle() {
        new Throwable().printStackTrace();
        mContext.setState(mContext.getStateA());
    }
}
```

状态模式可以有不同的写法，这里是在 `Context` 中为每种状态维护了一个实例，也可以把具体状态写成单例模式。

### 测试

```java
Context context = new Context();
context.request();
context.request();
```

```shell
java.lang.Throwable
	at io.binac.designpattern.state.ConcreteStateA.handle(ConcreteStateA.java:12)
	at io.binac.designpattern.state.Context.request(Context.java:27)
...
java.lang.Throwable
	at io.binac.designpattern.state.ConcreteStateB.handle(ConcreteStateB.java:12)
	at io.binac.designpattern.state.Context.request(Context.java:27)
...
```

### 与策略模式比较

两者都可以在运行时改变行为，但策略模式中客户端通常会主动指定所需的策略对象，而状态模式中状态对象对客户端是透明的，客户不需要了解状态。

## 源码实现

<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/designpattern/state>

## 参考资料

1. 《Head First 设计模式》
2. [State pattern - Wikipedia](https://en.wikipedia.org/wiki/State_pattern)
