---
title: 观察者模式 Observer Pattern
date: 2020-03-15 22:02:09
tags:
- Java
- 设计模式
---

对象之间常有依赖关系，观察者模式可以让我们方便地监听一个对象，当它发生改变时，其他对象会自动收到通知并作出相应的反应。

<!-- more -->

## 实例

```java
public class Subject {
    private int mState;

    private List<Observer> mObservers = new ArrayList<>();

    public void setState(int state) {
        mState = state;
        notifyObservers();
    }

    @Override
    public String toString() {
        return "State: " + mState;
    }

    public boolean addObserver(Observer o) {
        return mObservers.add(o);
    }

    public boolean removeObserver(Observer o) {
        return mObservers.remove(o);
    }

    private void notifyObservers() {
        for (Observer o : mObservers) {
            o.update(this);
        }
    }

    public interface Observer {
        void update(Subject subject);
    }
}
```

### 测试

```java
Subject subject = new Subject();
subject.setState(0);

Subject.Observer so = System.out::println;
subject.addObserver(so);
subject.setState(1);

subject.removeObserver(so);
subject.setState(2);
```

```shell
State: 1
```

## 实现源码

<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/designpattern/observer>

## 参考资料

1. [Observer pattern - Wikipedia](https://en.wikipedia.org/wiki/Observer_pattern)
