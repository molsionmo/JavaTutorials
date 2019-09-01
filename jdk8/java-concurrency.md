[TOC]

# java Concurrency

Thread使用了模板模式,父类定义了算法结构,子类实现逻辑细节.Thread.run()给子类定义逻辑，Thread.start()会直接调用run中的逻辑

[oracle-java-concurrency](https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html)

## 高级并发对象

### Lock

synchronized有2个问题

* 不能中断
* 不能设置超时
* 常规使用ReentrantLock.tryLock(long,TimeUnit)有超时时间也可以中断

锁可以在申请尝试时退出，tryLock(time,timeUtil)可以在时间申请后退出，lockInterruptibly将会退出如果另外线程发送中断信号

```java
// 类似synchronized,无法被interruput
void lock();

//可以被中断的获取锁,获取锁时阻塞线程
void lockInterruptibly() throws InterruptedException;

//不会阻塞的获取锁,立即返回
boolean tryLock();

//设定等待时间的获取锁;可以被中断
boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

void unlock();
```

```java
public class TryReentrantLock {
    public static void main(String[] args) throws InterruptedException {
        Lock lock = new ReentrantLock();

        Thread thread1 = new Thread(new TestThread(lock), "thread1");
        Thread thread2 = new Thread(new TestThread(lock), "thread2");

        thread1.start();
        thread2.start();

        TimeUnit.SECONDS.sleep(1);

        thread2.interrupt();

    }

    static class TestThread implements Runnable {
        public TestThread(Lock lock) {
            this.lock = lock;
        }

        public Lock lock;

        @Override
        public void run() {
            try {
                lock.lockInterruptibly();

                System.out.println(Thread.currentThread().getName() + ": get the lock");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println(Thread.currentThread().getName() + " interrupt");
            }
        }
    }
}
```

### Exectors

用于管理线程的创建与生命周期管理

#### Exector Interface

执行器的接口定义

##### Executor

```java
//执行指定线程
execute(Runnable command);
```

##### ExecutorService

相较Executor,sumbit方法返回了Future用于查看线程的状态;可以对当前执行器中的线程进行关闭

```java
<T> Future<T> submit(Callable<T> var1);
<T> Future<T> submit(Runnable var1, T var2);
Future<?> submit(Runnable var1);
void shutdown();
```

```java
//future可以对其中的线程进行操作处理
public interface Future<V> extends java.util.concurrent.Future<V> {
    boolean isSuccess();
    boolean isCancellable();
    Throwable cause();
    Future<V> addListener(GenericFutureListener<? extends Future<? super V>> var1);
    Future<V> addListeners(GenericFutureListener... var1);
    Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> var1);
    Future<V> removeListeners(GenericFutureListener... var1);
    Future<V> sync() throws InterruptedException;
    Future<V> syncUninterruptibly();
    Future<V> await() throws InterruptedException;
    Future<V> awaitUninterruptibly();
    boolean await(long var1, TimeUnit var3) throws InterruptedException;
    boolean await(long var1) throws InterruptedException;
    boolean awaitUninterruptibly(long var1, TimeUnit var3);
    boolean awaitUninterruptibly(long var1);
    V getNow();
    //Attempts to cancel execution of this task.  This attempt will fail if the task has already completed, has already been cancelled,or could not be cancelled for some other reason
    boolean cancel(boolean var1);
}
```

##### ScheduledExecutorService

```java

```

### Fork/Join

Fork/Join提供了并行计算的基础框架,不过计算任务如何拆解还是User自己关注的事情; oracle上的简易例子

```java
public class MyRecursiveTask extends RecursiveTask<Long> {

    private long workLoad = 0;

    public MyRecursiveTask(long workLoad) {
        this.workLoad = workLoad;
    }

    //核心方法,根据任务的重量进行切割直至可以单个完成;上级任务join等待,然后底层子任务递归返回
    @Override
    protected Long compute() {
        //if work is above threshold, break tasks up into smaller tasks
        if(this.workLoad > 16) {
            System.out.println("Splitting workLoad : " + this.workLoad);

            List<MyRecursiveTask> subtasks = new ArrayList<>();
            subtasks.addAll(createSubtasks());

            for(MyRecursiveTask subtask : subtasks){
                subtask.fork();
            }

            long result = 0;
            for(MyRecursiveTask subtask : subtasks) {
                result += subtask.join();
            }
            return result;

        } else {
            System.out.println("Doing workLoad myself: " + this.workLoad);
            return workLoad * 2;
        }
    }

    private List<MyRecursiveTask> createSubtasks() {
        List<MyRecursiveTask> subtasks = new ArrayList<>();

        MyRecursiveTask subtask1 = new MyRecursiveTask(this.workLoad / 2);
        MyRecursiveTask subtask2 = new MyRecursiveTask(this.workLoad / 2);

        subtasks.add(subtask1);
        subtasks.add(subtask2);

        return subtasks;
    }

    public static void main(String[] args) {
        MyRecursiveTask myRecursiveTask = new MyRecursiveTask(1024);

        long mergedResult = new ForkJoinPool().invoke(myRecursiveTask);

        System.out.println("mergedResult = " + mergedResult);
    }
}
```

## 守护线程

守护线程一般处理后台的一些任务，当JVM只存在守护线程时，JVM退出.垃圾回收就是典型的守护线程，在其他用户线程结束后将会自动结束生命周期。

### 什么时候使用守护线程

* 守护线程一般用于执行一些后台任务,他在某些线程关闭或者退出JVM时，此线程就能自动关闭
* 简单的一个游戏程序,其中一个线程在后台不断与服务器交互以获得玩家最新金币,武器信息,并且希望在游戏客户端关闭时，这些数据同步工作也能自动关闭,此时就能使用守护进程

## Thread方法详解

### 线程阻塞方法

下面的方法都会阻塞当前线层,也都可以被Thread.interrupt给中断

* Object.wait(),wait(long),wait(long,int)
* Thread.sleep(long),sleep(long,int);
* Thread.join,join(long),join(long,int);阻塞主线程,等待分支线程执行结束

### 线程关闭的2种设计

1. 调用Thread.interruput,直接中断线程,由线程处理中断(内置支持,不过要能处理中断),while(!isInterruput()){dosomthing}
2. 使用volatile开关控制,当前ThreadTask持有一个中断的标志,实现closeable接口中的close方法,while(!isInterruput() && !isClose()){dosomething}
3. 线程正常执行结束,正常关闭

### 线程上下文类加载器

* getContextClassLoader()获取上下文的类加载器,如果在没有修改线程上下文类加载器的情况下,则保持与父线程同样的类加载器
* setContextClassLoader可以设置线程的类加载器

## Syschronized

### JVM指令

使用javap进行反编译,会发现 monitorenter指令 与 monitorexit指令

```java
public class Mutex {
    private static final Object MUTEX = new Object();

    public void accessSource(){
        synchronized (MUTEX){
            try {
                TimeUnit.MINUTES.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        final Mutex mutex = new Mutex();

        for (int i=0;i<2;i++){
            new Thread(mutex::accessSource).start();
        }
    }
}
```

![2个线程jstack情况](https://i.loli.net/2019/08/25/bIW2udqJH5tinR7.png)

### 是否可被中断

* synchronized (MUTEX)是不可以被Thread.interrupt中断的.
* synchronized (MUTEX)是不会有超时概念的,会一直阻塞等待

## 线程间通信

### 单线程通信

* Object.wait()与Object.notify()需要在synchronized中实现,需要在获得monitor所有权后才能执行,如果没获得monitor将会抛出IllegalMonitorStateException
* wait与notify可以实现线程间的生产者消费者模型,使用的对象是queue,一边生产一边消费.

### 多线程通信

* 生产者消费者模型中常规的例子是单线程通信,一个take,一个offer。
* 多线程时需要对eventQueue进行synchronized,然后使用queue.notifyAll方法唤醒所有的线程进行争抢锁.不过synchronized锁定的是常规的核心操作流程,实际上已经被变成了单线程通信。
* 需要多线程中有可以进行并发的部分才能发挥其多线程优势

## ThreadGroup

创建线程的时候如果没有显示的指定ThreadGroup,那么新的线程会被加入与父线程相同的ThreadGroup中.

守护ThreadGroup: 若将ThreadGroup设置为daemon，也并不会影响线程的daemon属性,在group中没有任何active线程的时候该group将自动destory(在父group中将自己移除)

## Hook线程及捕获执行异常

Runtime.getRuntime.addShutdownHook()可以用于在进程结束时增加钩子线程处理额外事情;如线程启动lock文件,hook线程删掉文件以保证线程只有一个启动
