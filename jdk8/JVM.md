# JVM

## 简单基本概念

* JVM：一种能够运行Java字节码（Java bytecode）的虚拟机。
* 字节码：字节码是已经经过编译，但与特定机器码无关，需要解释器转译后才能成为机器码的中间代码。
* Java字节码：是Java虚拟机执行的一种指令格式。
* **解释器**：是一种电脑程序，能够把高级编程语言一行一行直接翻译运行。解释器不会一次把整个程序翻译出来，只像一位“中间人”，每次运行程序时都要先转成另一种语言再作运行，因此解释器的程序运行速度比较缓慢。它每翻译一行程序叙述就立刻运行，然后再翻译下一行，再运行，如此不停地进行下去。它会先将源码翻译成另一种语言，以供多次运行而无需再经编译。其制成品无需依赖编译器而运行，程序运行速度比较快。
* **即时编译(Just-in-time compilation: JIT)**：又叫实时编译、及时编译。是指一种在运行时期把字节码编译成原生机器码的技术，一句一句翻译源代码，但是会将翻译过的代码缓存起来以降低性能耗损。这项技术是被用来改善虚拟机的性能的。**JIT编译器是JRE的一部分。原本的Java程序都是要经过解释执行的，其执行速度肯定比可执行的二进制字节码程序慢。为了提高执行速度，引入了JIT。在运行时，JIT会把翻译过来的机器码保存起来，以备下次使用**

## 运行时数据区

Java虚拟机定义了在程序执行期间使用的各种运行时数据区域。其中一些数据区域是在Java虚拟机启动时创建的，仅在Java虚拟机退出时销毁。其他数据区域是每个线程。线程数据区域是在线程创建时创建, 线程退出时销毁

### Java虚拟机堆栈

每个Java虚拟机线程都有一个私有Java虚拟机堆栈，与线程同时创建。Java虚拟机堆栈类似于传统语言的堆栈，例如C：它保存局部变量和部分结果，并在方法调用和返回中起作用。由于除了推送和弹出帧之外，永远不会直接操作Java虚拟机堆栈，因此可以对堆进行堆分配。Java虚拟机堆栈的内存不需要是连续的。

Java虚拟机实现可以为程序员或用户提供对Java虚拟机堆栈的初始大小的控制，以及在动态扩展或收缩Java虚拟机堆栈的情况下，控制最大和最小大小。

以下异常条件与Java虚拟机堆栈相关联：

* **如果线程中的计算需要比允许的更大的Java虚拟机堆栈，则Java虚拟机会抛出一个StackOverflowError。**
* 如果可以动态扩展Java虚拟机堆栈，并且尝试进行扩展但可以使内存不足以实现扩展，或者可以使内存不足以为新线程创建初始Java虚拟机堆栈，则Java Virtual机器抛出一个OutOfMemoryError。

### 堆

Java虚拟机具有在所有Java虚拟机线程之间共享的堆。堆是运行时数据区，从中分配所有类实例和数组的内存。

**堆是在虚拟机启动时创建的**。对象的堆存储由自动存储管理系统（称为垃圾收集器）回收 ; 对象永远不会被显式释放。Java虚拟机假设没有特定类型的自动存储管理系统，可以根据实现者的系统要求选择存储管理技术。堆可以具有固定大小，或者可以根据计算的需要进行扩展，并且如果不需要更大的堆，则可以收缩。堆的内存不需要是连续的。

Java虚拟机实现可以为程序员或用户提供对堆的初始大小的控制，以及如果可以动态扩展或收缩堆，则控制最大和最小堆大小。

* **如果计算需要的堆量超过自动存储管理系统可用的堆，则Java虚拟机会抛出一个 OutOfMemoryError。**

### 方法区

Java虚拟机具有在所有Java虚拟机线程之间共享的方法区域。方法区域类似于传统语言的编译代码的存储区域或类似于操作系统进程中的“文本”段。它存储每类结构，例如运行时常量池，字段和方法数据，以及方法和构造函数的代码，包括类和实例初始化以及接口初始化中使用的特殊方法。

**方法区域是在虚拟机启动时创建的**。虽然方法区域在逻辑上是堆的一部分，但是简单的实现可能选择不垃圾收集或压缩它。本规范未规定方法区域的位置或用于管理编译代码的策略。方法区域可以是固定大小的，或者可以根据计算的需要进行扩展，并且如果不需要更大的方法区域，则可以收缩方法区域。

* **如果方法区域中的内存无法满足分配请求，则Java虚拟机会抛出一个OutOfMemoryError**

### 运行时常量池

每个运行时常量池都是从Java虚拟机的方法区域（第2.5.4节）中分配的。当Java虚拟机创建类或接口（第5.3节）时，将构造类或接口的运行时常量池。

* **在创建类或接口时，如果运行时常量池的构造需要的内存比Java虚拟机的方法区域中可用的内存多，则Java虚拟机会抛出一个OutOfMemoryError。**

### 本机方法堆栈

Java虚拟机的实现可以使用常规堆栈（俗称“C堆栈”）来支持native方法（用Java编程语言以外的语言编写的方法。

* **如果线程中的计算需要比允许的更大的本机方法堆栈，则Java虚拟机会抛出一个StackOverflowError。**
* **如果可以动态扩展本机方法堆栈并尝试进行本机方法堆栈扩展，但可以使内存不足，或者如果没有足够的内存可用于为新线程创建初始本机方法堆栈，则Java虚拟机会抛出OutOfMemoryError。**

## 类加载机制

class文件格式是JVM规范向外提供的统一结构, 而JVM只是需要class文件的二进制流, 至于"通过一个类的全限定名来获取定义此类的二进制字节流"JVM没有指明其方式,包括不限定于:

* 从zip包中获取,JAR,WAR包
* 从网络中获取
* 运行时计算生成，动态代理技术;java.lang.reflect.Proxy,CGLIB,Javassist
* 由其他文件生成,例如jsp中的class类
* ···

### 加载

### 连接

### 初始化

### 类与类加载器

对于任意一个类,都需要加载它的类加载器和这个类本身一同确立其在JVM中的唯一性,每一个类加载器,都拥有一个独立的类名称空间.JVM提供的一个接口去接收到加载器加载到的class文件二进制字节流.

从JVM角度来讲,只存在2种不同的类加载器

* 一种是启动类加载器(Bootstrap ClassLoader), 这个类加载器是使用C++语言实现(这里只限HotSpot虚拟机)
* 另一种就是所有其他的类加载器,这些类加载器都是Java语言实现,独立于JVM之外,且全部都继承自抽象类java.lang.ClassLoader

从Java开发人员角度看,类加载器还可以划分更细致些,绝大部分Java程序都会使用以下3种系统提供的类加载器

* 启动类加载器(Bootstrap ClassLoader), 这个类加载器负载将存放在<JAVA_HOME>\lib目录中的类加载在虚拟机内存中.启动类加载器无法被Java程序直接引用
* 扩展类加载器(Extension ClassLoader), 这个加载器由sun.misc.Launcher$ExtClassLoader实现,它负责<JAVA_HOME>\lib\ext目录或者java.ext.dirs系统变量所指定的路径的所有类库
* 应用程序类库(Application ClassLoader), 这个加载器由sun.misc.Launcher$AppClassLoader实现.由于这个类加载器是ClassLoader中的getSystemClassLoader()方法的返回值,所以一般也称它为系统类加载器

#### 双亲委派模式

双亲委派并不是一个强制的约束模型,而是Java设计者推荐给开发者的一种类加载器实现方式,其工作过程是

1. 如果一个类加载器收到类加载的请求,首先不会自己加载这个类,而是把这个请求委派给父类加载器去完成.
2. 每一层都是如此,因此所有的加载请求最终都传送到顶层的启动类加载器中,只有父加载器反馈无法完成此加载,子加载器才会自己尝试去加载

#### 破坏双亲委派模式

1. 第一次破坏是对JDK1.2之前的兼容, 1.2提出双亲委派机制, ClassLoader中增加一个protected的findClass()方法,不建议直接重写loadClass()方法,loadClass方法当前的逻辑就是尝试父类加载,失败再调用子类中的findClass方法
2. 第二次是JDBC,JNDI标准出现时,JDBC的代码由启动类加载器去加载(处于rt.jar中),JDBC需要去加载外部产商实现的类(impl Driver),这时启动类加载器是没办法加载到的, 所以引进了线程上下文类加载器(Thread Context ClassLoader).JDBC使用SPI完成实现类的加载.
   1. **如果线程创建时没有设置的话,它将会从父线程中继承一个**
   2. 如果应用程序全局范围没有设置过的话,那这个类加载器默认就是AppClassLoader
   3. **ServiceLoader.load(Class service)方法中使用的就是Thread.currentThread().getContextClassLoader()**, 线程上下文类加载器由于是用户自己定义的,是可以破坏双亲委派,直接优先加载自己URL中的类.
   4. Tomcat中有个CommonClassLoader,加载所有webApp公共的类(Spring),自己的webApp有一个自己的WebAppClassLoader(应用实现类),故Spring进行加载应用实现类时也是使用了线程上下文加载类
3. 第三次破坏是由于用户对程序动态性的追求而导致的,代码热替换,模块热部署

#### JDBC-SPI机制破坏双亲委派机制过程

1. BootstrapClassLoader 加载DriverManager类
2. DriverManager 类中有static块(初始化), loadInitialDrivers(),里面的代码其实就是调用ServiceLoader.load(Driver.class),而SPI机制调用的是当前线程的上下文类加载器,其实就是AppClassLoader,**也就是说DriverManager类初始化需要AppClassLoader的协助,这个已经是依赖倒挂了,破坏了双亲委派机制**
3. DriverManager.loadInitialDrivers代码中调用了SPI.load后,还将返回的driversIterator进行了hasNext遍历, hasNext中实现了Class.forName()加载,自此Driver实现类com.mysql.jdbc.Driver就加载到AppClassLoader中了
4. com.mysql.jdbc.Driver初始化也有段static块,DriverManager.registerDriver(new com.lcy.mysql.Driver()),将一个mysqlDriver实体注册到DriverManager上

```java
public static void main(String[] args){
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    Driver driver;
    while (drivers.hasMoreElements())
    {
        driver = drivers.nextElement();
        System.out.println(driver.getClass() + "------" + driver.getClass().getClassLoader());
    }
    System.out.println(DriverManager.class+ "------" + DriverManager.class.getClassLoader());
}

/**输出结果:

class com.mysql.jdbc.Driver------sun.misc.Launcher$AppClassLoader@14dad5dc
class com.mysql.fabric.jdbc.FabricMySQLDriver------sun.misc.Launcher$AppClassLoader@14dad5dc
class java.sql.DriverManager------null

**/
```

#### URLClassLoader

* **一般来说,即使破坏双亲委派,基础类还是由BootstrapClassLoader,应用classPath的类由AppClassLoader,其他的再由线程自己决定,防止出现ClassCastExecption**
* 这个类可以在JVM运行时额外在线程中加载另外的jar包,既不属于JDK的核心包,也不属于这个应用的ClassPath包.它重写了findClass方法,loadClass方法使用的还是ClassLoader类里面的实现(双亲委派)
* 不同线程可以设置不同的类加载器,但是Luncher$AppClassLoader,BootstrapClassLoader在同一个JVM中还是有且只有一个, 故加载Object-class时是同一个, AppClassLoader中加载的ApplicationContext也是一个
* 额外URL中增加的类如TransactionException,不同线程间就不一样了,因为URLClassLoader是不一样的,命名空间不一样那么累也就不一样了
* 我们可以继承实现自己的MyURLClassLoader, 实现childFirst的加载, 即如果Luncher$AppClassLoader,BootstrapClassLoader有同样的类, 我也使用MyURLCLassLoader优先加载,重载loadClass即可(Flink就是这样实现的),如果只是重载findClass,那么还是至少会先从BootstrapClassLoader开始
