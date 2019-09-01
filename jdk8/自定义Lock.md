# 自定义Lock

## Syschronized缺陷

* 不可中断,Thead.interrupt()无法中断
* 无超时概念,会一直占用锁,其他线程将一直等待synchronized(MUTEX)
* 常规使用ReentrantLock.tryLock(long,TimeUnit)有超时时间也可以中断

## 自定义Lock实现

```java
public interface Lock{
    //可以被中断
    void lock() throws InterruptedException;

    //可以被中断且设定超时;指定时间内获取不到将直接结束,不再阻塞等待
    void lock(long mills) throws InterruptedException,TimeoutException;

    void unlock();
}
```

```java
public class BooleanLock implements Lock {

    private Thread current;
    private boolean lock = false;
    private List<Thread> blockedThreads = new ArrayList<>();

    @Override
    public void lock() throws InterruptedException {
        //此同步快执行很快;
        //如 10个线程并发,1个线程获得锁,其他9个将逐个执行wait并进入BLOCKED状态直到notifyAll;而最终也只有一个能够获得锁,其他进入wait;
        //不会产生synchronized很久没法获得执行权的情况
        synchronized (this){
            while (lock){
                if (!blockedThreads.contains(Thread.currentThread())){
                    blockedThreads.add(Thread.currentThread());
                }

                this.wait();
            }

            //获得锁
            lock = true;
            blockedThreads.remove(Thread.currentThread());
            current = Thread.currentThread();
        }
    }

    @Override
    public void lock(long mills) throws InterruptedException, TimeoutException {
        long remainingMill = mills;
        long endMills = System.currentTimeMillis() + mills;
        synchronized (this) {
            while (lock){
                this.wait(remainingMill);
                remainingMill = endMills - System.currentTimeMillis();
                if (remainingMill <= 0){
                    throw new TimeoutException("time out and do not get the lock");
                }
            }

            lock = true;
            current = Thread.currentThread();
            blockedThreads.remove(Thread.currentThread());
        }
    }

    @Override
    public void unlock() {
        synchronized (this) {
            if (current == Thread.currentThread()) {
                current = null;
                lock = false;
                //需要synchronized获得monitor锁才能进行notifyAll
                this.notifyAll();
            }
        }

    }

    @Override
    public List<Thread> getBlockedThreads() {
        return null;
    }
}
```

```java
public class TryBooleanLock {

    public static void main(String[] args) throws InterruptedException {
        Lock lock = new BooleanLock();

        Thread thread1 = new Thread(new TestThread(lock), "thread1");
        Thread thread2 = new Thread(new TestThread(lock), "thread2");
        Thread thread3 = new Thread(new TestThread(lock), "thread3");

        thread1.start();
        TimeUnit.MILLISECONDS.sleep(1);
        thread2.start();
        thread3.start();

        //TimeUnit.SECONDS.sleep(1);
        //thread2.interrupt();

    }

    static class TestThread implements Runnable{

        private Lock lock;

        public TestThread(Lock lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                lock.lock(3000);

                System.out.println(Thread.currentThread().getName() + ": get the lock");
                TimeUnit.SECONDS.sleep(2);

            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " interrupt");
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println(Thread.currentThread().getName() + " timeout");
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

            System.out.println(Thread.currentThread().getName() + ": eixt");
        }
    }
}
```
