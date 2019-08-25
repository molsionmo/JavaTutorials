# 自定义Lock

## Syschronized缺陷

* 不可中断,Thead.interrupt()无法中断
* 无超时概念,会一直占用锁,其他线程将一直等待synchronized(MUTEX)

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
