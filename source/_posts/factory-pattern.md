---
title: Factory Pattern 工厂模式
date: 2017-09-09 11:54:23
tags:
- Java
- 设计模式
---
工厂模式可分为 Simple Factory Pattern 简单工厂模式、Factory Method Pattern 工厂方法模式 和 Abstract Factory Pattern 抽象工厂模式。

本文涉及源码：
<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/factory>

<!-- more -->

## 简单工厂模式

简单工厂模式实际上是对产品创建逻辑做了统一的封装，包含如下角色：
- 抽象产品：具体产品的接口（或父类）
- 具体产品：产品的具体实现
- 工厂：创建所有产品实例的类

### 实例

#### 抽象产品

```java
public interface IProduct {
    void use();
}
```

#### 具体产品

```java
public class ConcreteProductA implements IProduct {
    @Override
    public void use() {
        System.out.println("using an instance of " + this.getClass());
    }
}
```

```java
public class ConcreteProductB implements IProduct {
    @Override
    public void use() {
        System.out.println("using an instance of " + this.getClass());
    }
}
```

#### 工厂

根据客户端传来的参数，创建并返回不同的产品。

```java
public class ConcreteSimpleFactory {

    public static IProduct createProduct(ProductType type) throws IllegalArgumentException {
        switch (type) {
            case TYPE_A:
                return new ConcreteProductA();
            case TYPE_B:
                return new ConcreteProductB();
            default:
                throw new IllegalArgumentException("undefined product type");
        }
    }

    enum ProductType {
        TYPE_A,
        TYPE_B
    }
}
```

#### 测试

```java
IProduct product = ConcreteSimpleFactory.createProduct(ProductType.TYPE_A);
product.use();
```

输出：

```
using an instance of class io.binac.factory.ConcreteProductA
```

### 小结

由于创建产品的方法是静态的，简单工厂模式也称为静态工厂模式。

优点：
- 对客户端隐藏产品实例的创建过程，符合迪米特法则
- 对客户端隐藏具体类，以便在后续版本修改实现
- 如果创建实例时需要先初始化各种参数，在客户端多次创建时，可避免重复编码

缺点：
- 增加产品时，需要修改工厂类，违反开闭原则
- 产品过多时，难以维护

## 工厂方法模式

工厂方法模式是对简单工厂模式的改进，把工厂角色分为抽象工厂和具体工厂，即包含抽象产品、具体产品、抽象工厂、具体工厂四个角色，每个具体产品对应一个具体工厂。

### 实例

抽象产品和具体产品代码同简单工厂模式。

#### 抽象工厂

```java
public interface IFactory {
    IProduct createProduct();
}
```

#### 具体工厂

```java
public class ConcreteFactoryA implements IFactory {
    @Override
    public IProduct createProduct() {
        return new ConcreteProductA();
    }
}
```

```java
public class ConcreteFactoryB implements IFactory {
    @Override
    public IProduct createProduct() {
        return new ConcreteProductB();
    }
}
```

#### 测试

```java
IFactory factory = new ConcreteFactoryB();
IProduct product = factory.createProduct();
product.use();
```

输出：

```
using an instance of class io.binac.factory.ConcreteProductB
```

### 小结

优点：
- 拥有简单工厂模式所有优点
- 当需要增加产品时，只需要增加产品类和相应的工厂类即可，符合开闭原则

缺点：
- 增加产品时，类的个数成对增加，增加了系统复杂性

## 抽象工厂模式

在工厂方法模式中，一个具体产品对应一个具体工厂。如果一个具体工厂可以提供多种相关的产品，称这些产品属于同一个产品族。

例如，A 系列手机配套的是 3.5 mm 接口耳机、Micro-USB 数据线，B 系列手机配套的是蓝牙耳机、USB Type-C 数据线，每种系列的一套配件可以看作是一个产品族。抽象工厂模式通常用于提供成套的产品。

抽象工厂模式同样包含抽象产品、具体产品、抽象工厂、具体工厂四个角色。

### 实例

#### 抽象产品

```java
public interface IProductA {
    void use();
}
```

```java
public interface IProductB {
    void use();
}
```

#### 具体产品

```java
public class ConcreteProductA1 implements IProductA {
    @Override
    public void use() {
        System.out.println("using an instance of " + this.getClass());
    }
}
```

```java
public class ConcreteProductB1 implements IProductB {
    @Override
    public void use() {
        System.out.println("using an instance of " + this.getClass());
    }
}
```

```java
public class ConcreteProductA2 implements IProductA {
    @Override
    public void use() {
        System.out.println("using an instance of " + this.getClass());
    }
}
```

```java
public class ConcreteProductB2 implements IProductB {
    @Override
    public void use() {
        System.out.println("using an instance of " + this.getClass());
    }
}
```

#### 抽象工厂

```java
public abstract class AbstractFactory {
    public abstract IProductA createProductA();

    public abstract IProductB createProductB();
}
```

这里使用的是抽象类，也可以用接口。

#### 具体工厂

```java
public class ConcreteFactory1 extends AbstractFactory {
    @Override
    public IProductA createProductA() {
        return new ConcreteProductA1();
    }

    @Override
    public IProductB createProductB() {
        return new ConcreteProductB1();
    }
}
```

```java
public class ConcreteFactory2 extends AbstractFactory {
    @Override
    public IProductA createProductA() {
        return new ConcreteProductA2();
    }

    @Override
    public IProductB createProductB() {
        return new ConcreteProductB2();
    }
}
```

#### 测试

```java
AbstractFactory factory1 = new ConcreteFactory1();
IProductA productA = factory1.createProductA();
IProductB productB = factory1.createProductB();
productA.use();
productB.use();
```

输出：

```
using an instance of class io.binac.factory.abstractfactory.ConcreteProductA1
using an instance of class io.binac.factory.abstractfactory.ConcreteProductB1
```

### 小结

优点：
- 拥有简单工厂模式所有优点
- 便于客户端使用同一个产品族中的产品
- 增加新的具体工厂和产品族非常方便，符合开闭原则

缺点：
- 产品族增加新的产品非常麻烦，需要修改所有的工厂类，对开闭原则有倾斜性

## 参考资料

[创建型模式 — Graphic Design Patterns](http://design-patterns.readthedocs.io/zh_CN/latest/creational_patterns/creational.html)
[设计模式（七）——抽象工厂模式-HollisChuang's Blog](http://www.hollischuang.com/archives/1420)
[23种设计模式（2）：工厂方法模式 - 卡奴达摩的专栏 - CSDN博客](http://blog.csdn.net/zhengzhb/article/details/7348707)
[23种设计模式（3）：抽象工厂模式 - 卡奴达摩的专栏 - CSDN博客](http://blog.csdn.net/zhengzhb/article/details/7359385)
