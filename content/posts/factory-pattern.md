---
title: 工厂模式 Factory Pattern
date: 2017-09-09 11:54:23
tags:
- Java
- 设计模式
---

工厂模式可分为简单工厂模式 Simple Factory Pattern、工厂方法模式 Factory Method Pattern 和 抽象工厂模式 Abstract Factory Pattern。

<!-- more -->

## 简单工厂模式

简单工厂模式实际上是对产品创建逻辑做了统一的封装，包含如下角色：

- 抽象产品
  具体产品的接口（或父类）

- 具体产品
  产品的具体实现

- 工厂
  创建所有产品实例的类

### 实例

#### 抽象产品

```java
public interface Product {
    default void use() {
        System.out.println("using " + this);
    }
}
```

#### 具体产品

```java
public class ConcreteProductA implements Product {
}
```

```java
public class ConcreteProductB implements Product {
}
```

#### 工厂

根据客户端传来的参数，创建并返回不同的产品。

```java
public class Factory {
    public static Product createProduct(ProductType type) throws IllegalArgumentException {
        switch (type) {
            case TYPE_A:
                return new ConcreteProductA();
            case TYPE_B:
                return new ConcreteProductB();
            default:
                throw new IllegalArgumentException("undefined product type");
        }
    }

    public enum ProductType {
        TYPE_A,
        TYPE_B
    }
}
```

#### 测试

```java
Product product = Factory.createProduct(Factory.ProductType.TYPE_A);
product.use();

product = Factory.createProduct(Factory.ProductType.TYPE_B);
product.use();
```

输出：

```shell
using io.binac.designpattern.factory.simplefactory.ConcreteProductA@65e2dbf3
using io.binac.designpattern.factory.simplefactory.ConcreteProductB@4f970963
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
public interface Factory {
    Product createProduct();
}
```

#### 具体工厂

```java
public class ConcreteFactoryA implements Factory {
    @Override
    public Product createProduct() {
        return new ConcreteProductA();
    }
}
```

```java
public class ConcreteFactoryB implements Factory {
    @Override
    public Product createProduct() {
        return new ConcreteProductB();
    }
}
```

#### 测试

```java
Factory factory = new ConcreteFactoryA();
Product product = factory.createProduct();
product.use();

factory = new ConcreteFactoryB();
product = factory.createProduct();
product.use();
```

输出：

```shell
using io.binac.designpattern.factory.factorymethod.ConcreteProductA@65e2dbf3
using io.binac.designpattern.factory.factorymethod.ConcreteProductB@4f970963
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
public interface ProductA {
    default void use() {
        System.out.println("using " + this);
    }
}
```

```java
public interface ProductB {
    default void use() {
        System.out.println("using " + this);
    }
}
```

#### 具体产品

```java
public class ConcreteProductA1 implements ProductA {
}
```

```java
public class ConcreteProductB1 implements ProductB {
}
```

```java
public class ConcreteProductA2 implements ProductA {
}
```

```java
public class ConcreteProductB2 implements ProductB {
}
```

#### 抽象工厂

```java
public interface Factory {
    ProductA createProductA();

    ProductB createProductB();
}
```

这里使用的是接口，也可以用抽象类。

#### 具体工厂

```java
public class ConcreteFactory1 implements Factory {
    @Override
    public ProductA createProductA() {
        return new ConcreteProductA1();
    }

    @Override
    public ProductB createProductB() {
        return new ConcreteProductB1();
    }
}
```

```java
public class ConcreteFactory2 implements Factory {
    @Override
    public ProductA createProductA() {
        return new ConcreteProductA2();
    }

    @Override
    public ProductB createProductB() {
        return new ConcreteProductB2();
    }
}
```

#### 测试

```java
Factory factory = new ConcreteFactory1();
ProductA productA = factory.createProductA();
productA.use();
ProductB productB = factory.createProductB();
productB.use();

factory = new ConcreteFactory2();
productA = factory.createProductA();
productA.use();
productB = factory.createProductB();
productB.use();
```

输出：

```shell
using io.binac.designpattern.factory.abstractfactory.ConcreteProductA1@65e2dbf3
using io.binac.designpattern.factory.abstractfactory.ConcreteProductB1@4f970963
using io.binac.designpattern.factory.abstractfactory.ConcreteProductA2@61f8bee4
using io.binac.designpattern.factory.abstractfactory.ConcreteProductB2@7b49cea0
```

### 小结

优点：

- 拥有简单工厂模式所有优点
- 便于客户端使用同一个产品族中的产品
- 增加新的具体工厂和产品族非常方便，符合开闭原则

缺点：

- 产品族增加新的产品非常麻烦，需要修改所有的工厂类，对开闭原则有倾斜性

## 实现源码

<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/designpattern/factory>

## 参考资料

1. [创建型模式 — Graphic Design Patterns](http://design-patterns.readthedocs.io/zh_CN/latest/creational_patterns/creational.html)
2. [设计模式（七）——抽象工厂模式-HollisChuang's Blog](http://www.hollischuang.com/archives/1420)
3. [23种设计模式（2）：工厂方法模式 - 卡奴达摩的专栏 - CSDN博客](http://blog.csdn.net/zhengzhb/article/details/7348707)
4. [23种设计模式（3）：抽象工厂模式 - 卡奴达摩的专栏 - CSDN博客](http://blog.csdn.net/zhengzhb/article/details/7359385)
