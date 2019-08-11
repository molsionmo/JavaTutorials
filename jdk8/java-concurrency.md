<!-- @import "[TOC]" {cmd="toc" depthFrom=1 depthTo=6 orderedList=false} -->

[TOC]

# java Concurrency

[oracle-java-concurrency](https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html)

## 高级并发对象

### Lock

锁可以在申请尝试时退出，tryLock(time,timeUtil)可以在时间申请后退出，lockInterruptibly将会退出如果另外线程发送中断信号

```java
private final Lock lock = new ReentrantLock();
lock.tryLock();
lock.unlock();
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
