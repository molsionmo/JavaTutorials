# RPC

## 定义

在分布式计算中，远程过程调用（RPC）是指计算机程序导致过程（子例程）在不同的地址空间（通常在共享网络上的另一台计算机上）执行时，其编码就好像它是正常的一样（本地）过程调用，没有程序员明确编写远程交互的细节

## 与HTTP的区别

* RPC是个更抽象的概念，聚焦在编码架构层面，远程过程调用使得程序编码就跟本地过程调用一般
* RPC实现可以使用TPC传输层，也可以使用HTTP应用层,只要解决编码与本地过程调用一般即可
* HTTP是网络应用层的一种协议,聚焦于超文本通信

## 与微服务的关系

* 微服务架构中需要选取网络请求通信模式(基于服务治理情况下)
  1. 使用RESTful风格的通信模式
  2. 使用RPC的通信模式
  3. 使用混合RPC与RESTful风格的通信模式

## 主流RPC架构实现方案

1. gRpc
   * 底层基于http2.0协议(二进制协议),采用Socket长连接
   * 底层使用Netty支持其网络通信高可用

2. Thrift
   * 它有一个代码生成器来对它所定义的IDL定义文件自动生成服务代码框架。用户只要在其之前进行二次开发就行，对于底层的RPC通讯等都是透明的
   * 不过这个对于用户来说的话需要学习特定领域语言这个特性，还是有一定成本的。

3. Dubbo
   * Dobbo是当前主流的RPC框架;

4. Hessian
