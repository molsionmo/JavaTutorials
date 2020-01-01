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

#### AbstractQueuedSynchronizer

AQS,队列同步器,解决独占与共享同步问题,ReentrantLock,ReentrantReadWriteLock,CountDownLatch,CyclicBarrier 都是基于它实现了同步功能.

```java
/****
* 独占模式下实现tryAcquire与tryRelease后就可以使用它的大部分行为方法了
* share模式下实现tryAcquireShared与tryReleaseShared后就可以使用它的大部分行为方法了
*/
public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements java.io.Serializable{
    //由子类实现;尝试一次信号获取,不成功直接返回
    protected boolean tryAcquire(int arg);
    //share模式下的同样方法
    protected int tryAcquireShared(int arg);

    //有时间限定的尝试获取信号;都基于 tryAcquire;tryAcquireShared的实现进行有限时间的重试
    public final boolean tryAcquireNanos(int arg, long nanosTimeout);
    public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout);

    //由子类实现;释放信号,与上述tryAcquire可以实现可重入锁
    protected boolean tryRelease(int arg)
    protected boolean tryReleaseShared(int arg);
    //基于上述tryRelease进行确认性的释放信号
    public final boolean release(int arg)
    public final boolean releaseShared(int arg);

    //独占模式下的阻塞式获取信号,无法阻断,类似于synchronized
    public final void acquire(int arg);
    //独占模式下的阻塞式获取信号,可以阻断
    public final void acquireInterruptibly(int arg);
    //share模式下的阻塞式信号获取;CountDownLatch就使用了acquireShared(1)来实现await()方法
    public final void acquireShared(int arg);
    public final void acquireSharedInterruptibly(int arg)
}
```

##### ReentrantLock-Sync

* FairSync(谁等得久谁就拿走锁,FIFO)
* NonfairSync(随便谁都可以拿,没界定)
* 可重入的实现其实是state增加,每次lock+1,需要多次unlock,获取到信号量的线程state=1并进行叠加,当state=0时才能继续acquire
* Sync核心是实现tryAcquire(int),tryRelease(int)
* Sync暴露的API是lock(int),parent.acquireInterruptibly(int),parent.release(int),parent.tryAcquireNanos(timeout,unit),还有AQS中的队列情况与当前线程等.
* compareAndSetState方法来自AQS,它使用的是Unsafe.compareAndSwapInt()硬件级的原子操作,只有部分类才能使用

```java
public class ReentrantLock{
    public void lock() {
        //底层是 Sync.acquire(1);
        sync.lock();
    }
    public boolean tryLock() {
        return sync.nonfairTryAcquire(1);
    }
    public boolean tryLock(long timeout, TimeUnit unit)throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }
    public void unlock() {sync.release(1);}
    public final boolean isFair() {return sync instanceof FairSync;}
}

abstract static class Sync extends AbstractQueuedSynchronizer {
    final boolean nonfairTryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
            if (compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0) // overflow
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }

    protected final boolean tryRelease(int releases) {
        int c = getState() - releases;
        if (Thread.currentThread() != getExclusiveOwnerThread())
            throw new IllegalMonitorStateException();
        boolean free = false;
        if (c == 0) {
            free = true;
            setExclusiveOwnerThread(null);
        }
        setState(c);
        return free;
    }

    protected final boolean isHeldExclusively() {
        return getExclusiveOwnerThread() == Thread.currentThread();
    }

    final ConditionObject newCondition() {
        return new ConditionObject();
    }

    // Methods relayed from outer class

    final Thread getOwner() {
        return getState() == 0 ? null : getExclusiveOwnerThread();
    }

    final int getHoldCount() {
        return isHeldExclusively() ? getState() : 0;
    }

    final boolean isLocked() {
        return getState() != 0;
    }

    /**
        * Reconstitutes the instance from a stream (that is, deserializes it).
        */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        setState(0); // reset to unlocked state
    }
}
static final class FairSync extends Sync {
    final void lock() {
        acquire(1);
    }
    protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
            if (!hasQueuedPredecessors() &&
                compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0)
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }
}
static final class NonfairSync extends Sync {
    final void lock() {
        if (compareAndSetState(0, 1))
            setExclusiveOwnerThread(Thread.currentThread());
        else
            acquire(1);
    }

    protected final boolean tryAcquire(int acquires) {
        return nonfairTryAcquire(acquires);
    }
}
```

##### CountDownLatch-Sync

```java
/**
* 初始化CountDownLatch count=10;
* 10个工作线程countDown(),将 state置为0, 主线程的await(),acquireSharedInterruptibly(1)才能解除阻塞
* ReentrantLock初始化state=0,CountDownLatch初始化state=10(state--达到0时才能acquire到)
*/
public class CountDownLatch{
    private static final class Sync extends AbstractQueuedSynchronizer {

        Sync(int count) {setState(count);}

        int getCount() {return getState();}

        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }
    public boolean await(long timeout, TimeUnit unit)
        throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }
    public void countDown() {
        sync.releaseShared(1);
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

##### ThreadPoolExecutor

* **最大可接收的线程数: maximumPoolSize + workQueue.size()**
* corePoolSize: the number of threads to keep in the pool;常规线程池持有的线程大小,真正执行的线程,传入进来的Thread只是执行了他们的run方法
* maximumPoolSize: the maximum number of threads to allow in the pool;线程池最大可以承接的线程数量;当workQueue已经满时,新建线程执行Thread
* keepAliveTime+TimeUnit: 定义在排队的thread的超时时间
* workQueue: the queue to use for holding tasks before they are executed;用于存储需要执行的线程队列
* ThreadFactory: the factory to use when the executor creates a new thread;线程工厂,可以指定线程的名称与Group相关信息,定义工厂的创造逻辑
* RejectedExecutionHandler: the handler to use when execution is blocked because the thread bounds and queue capacities are reached;当工作队列已经满时对于新的线程提交无法处理时的处理实现接口,可以异步进行通知与处理;

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
