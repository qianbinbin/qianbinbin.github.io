---
title: Decorator Pattern 装饰器模式
date: 2017-06-22 23:35:42
tags:
- Java
- 设计模式
---

在面向对象编程中，要扩展一个类或对象的功能，可以使用继承机制。

举例来说，咖啡店可能会提供拿铁、卡布奇诺、美式咖啡、意式浓缩等供客人选择，我们可以先定义一个 Coffee 类，并在其中声明价格、成分等方法，然后为每种咖啡继承它：Latte、Cappuccino、Americano、Espresso 等，每个类中都实现自己的一套价格、成分信息，这些类都是预先定义好的。

我们可以像这样调用：

```java
Coffee coffee = new Latte();
System.out.println("Cost: " + getCost() + "; Ingredients: " + getIngredients());
```

然而在遇到有定制化需求的客人时，我们遇到了麻烦。他可能会要求把浓缩咖啡、牛奶、奶油、巧克力、甚至酒等进行组合，混合为一种新的咖啡。这就需要在编译时静态地定义这种新的咖啡类型，而定制咖啡的方式有太多种，为每一种组合都定义一个子类不太现实。

装饰器模式（或装饰模式、修饰模式），是面向对象编程领域中，一种动态地往一个特定对象中添加新的行为的设计模式。装饰模式相比生成子类更为灵活，这样可以给某个对象而不是整个类添加一些功能。

<!-- more -->

## 模式结构

装饰器模式包含如下角色：

- Component：抽象组件，定义一个接口，以规范准备动态添加新的行为的对象
- ConcreteComponent：具体组件，实现 Component 接口，是被装饰对象的基础类
- Decorator：抽象装饰器，是实现 Component 的抽象类，也是所有装饰器的父类，并在其中维护了一个 Component 对象，Decorator 把 Component 对象的行为封装（装饰）起来
- ConcreteDecorator：具体装饰器，继承 Decorator 类，根据需要添加一些特定的行为

UML 类图如下（图片来自 <https://en.wikipedia.org/wiki/Decorator_pattern/>）：

![](https://upload.wikimedia.org/wikipedia/commons/thumb/e/e9/Decorator_UML_class_diagram.svg/757px-Decorator_UML_class_diagram.svg.png)

## 实例

本文涉及源码：
<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/decorator/>

### 抽象组件 Component

仍然以咖啡为例，先定义一个 Coffee 接口作为抽象组件 Component：

```java
public interface Coffee {
    double getCost();

    String getIngredients();
}
```

它具有获取价格和成分的行为。

### 具体组件 ConcreteComponent

很多咖啡都是以意式浓缩 Espresso 为基础进行调制的，那么被装饰的基础类当然就是：

```java
public class Espresso implements Coffee {
    @Override
    public double getCost() {
        return 22;
    }

    @Override
    public String getIngredients() {
        return "espresso";
    }

    @Override
    public String toString() {
        return "Cost: " + getCost() + "; Ingredients: " + getIngredients();
    }
}
```

### 抽象装饰器 Decorator

```java
public abstract class CoffeeDecorator implements Coffee {
    private final Coffee mCoffee;

    public CoffeeDecorator(Coffee coffee) {
        this.mCoffee = coffee;
    }

    @Override
    public double getCost() {
        return mCoffee.getCost();
    }

    @Override
    public String getIngredients() {
        return mCoffee.getIngredients();
    }

    @Override
    public String toString() {
        return "Cost: " + getCost() + "; Ingredients: " + getIngredients();
    }
}
```

它维护了一个私有 Coffee 对象，通过构造方法传入，然后将 Coffee 对象的行为封装起来。

### 具体装饰器 ConcreteDecorator

咖啡可以加奶，那么牛奶就可以作为一种装饰器：

```java
public class WithMilk extends CoffeeDecorator {
    public WithMilk(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getCost() {
        return super.getCost() + 6;
    }

    @Override
    public String getIngredients() {
        return super.getIngredients() + ", milk";
    }
}
```

在重写方法时可以自定义一些行为，例如加牛奶后价格和成分都会变化。

也可以定义巧克力粉作为一种装饰器：

```java
public class WithSprinkles extends CoffeeDecorator {
    public WithSprinkles(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getCost() {
        return super.getCost() + 3;
    }

    @Override
    public String getIngredients() {
        return super.getIngredients() + ", sprinkles";
    }
}
```

同样可以定义其它装饰器，这里不再赘述。

### 测试

```java
public class Main {
    public static void main(String[] args) {
        Coffee coffee = new Espresso();
        System.out.println(coffee);
        coffee = new WithMilk(coffee);
        System.out.println(coffee);
        coffee = new WithSprinkles(coffee);
        System.out.println(coffee);
    }
}
```

输出：

```
Cost: 22.0; Ingredients: espresso
Cost: 28.0; Ingredients: espresso, milk
Cost: 31.0; Ingredients: espresso, milk, sprinkles
```

显然，我们成功地在运行时动态扩展了对象的行为。
