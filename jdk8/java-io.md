# Java IO

<https://docs.oracle.com/javase/tutorial/essential/TOC.html>

## BASIC IO

### **Reader**

Abstract class for reading character streams. The only methods that a subclass must implement are read(char[], int, int) and close(). Most subclasses, however, will override some of the methods defined here in order to provide higher efficiency, additional functionality, or both.

#### **BufferReader**

Reads text from a character-input stream, buffering characters so as to provide for the efficient reading of characters, arrays, and lines.

##### Constructor

* BufferReader(Reader reader)
* BufferReader(Reader reader , int bufferSize)
  * BufferReader(FileReader(filePath))
    > FileReader is subclass of InputStreamReader
  * BufferReader(InputStreamReader(FileInputStream(filePath)))
    > FileInputStream is subClass of InputStream

##### Method

* String readLine()
    > Reads a line of text.
* int read()
    > Reads a single character.

#### **ObjectInputStream**

Just as data streams support I/O of primitive data types, object streams support I/O of objects. Most, but not all, standard classes support serialization of their objects. Those that do implement the marker interface Serializable.

##### Constructor

* ObjectInputStream(InputStream in)
    > FileInputStream(filePath) can be used;FileInputStream is suclass of InputStream

##### Method

* readObject()

--------

## File IO

### Path

A file is identified by its path through the file system, beginning from the root node

* Path path = Paths.get(file or dir)
* Path path = Paths.get(uri)

### Files

This class offers a rich set of static methods for reading, writing, and manipulating files and directories. The Files methods work on instances of Path objects.

* Files.readAllLines()
* Files.copy() move() delete()
* Files.newBufferedReader newBufferedWriter

## BIO

* 对应Linux下的阻塞IO
* 阻塞型IO，在网络编程中socket.getInputStream都是获取完数据后再进行业务处理的，而获取流数据就是一个阻塞操作，即写读网络数据，不能进行其他业务操作，且一个socket一个线程，这大大制约了并发，因为单位线程的容量是很大的。

```java
public class SocketExample {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9091);

        Socket socket;

        while ((socket=serverSocket.accept()) != null) {
            System.out.format(" The new connection is accepted from the client: %s \n", socket);
            new Thread(new BioHandleThread(socket)).start();
        }

    }

    static class BioHandleThread implements Runnable{

        private Socket socket;

        public BioHandleThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String msg;
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ( (msg = bufferedReader.readLine()) != null){
                    System.out.println("recv msg : "+ msg);

                    if ("exit".equals(msg)){
                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
```

## NIO

非阻塞型IO,相较于阻塞型IO，主要区分在网络IO中

* 对应Linux的non-blocking-io,很神奇,Linux下的I/O 多路复用刚好也是NIO中的selector多路复用方案,而Linux下的IO多路复用又支撑起了Java的AIO
* 阻塞型IO，BufferReader(new InputSteamReader(socket.getInputStream)).readLine(), 这个会阻塞当前线程以读到数据
* NIO,非阻塞型IO，可以配置套接字为非阻塞,有数据时调用，无数据时返回.不阻塞主线程.

socket 1->1 channel *->1 selector

```java
public class SelectorExample {

    public static void main(String[] args) throws IOException {
        // get selector
        Selector selector = Selector.open();
        System.out.println("Selector is open for making connection: " + selector.isOpen());

        // register server socket channel to selector
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8099));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, serverSocketChannel.validOps());

        for (;;){
            System.out.println("Waiting for the select operation...");
            //阻塞以获得有变化的事件channel
            int noOfKeys = selector.select();
            System.out.println("The Number of selected keys are: " + noOfKeys);

            Iterator<SelectionKey> itr = selector.selectedKeys().iterator();
            while (itr.hasNext()){
                SelectionKey selectionKey = itr.next();
                if (selectionKey.isAcceptable()) {
                    // The new client connection is accepted
                    SocketChannel client = serverSocketChannel.accept();
                    if (client != null) {
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                        System.out.println("The new connection is accepted from the client: " + client);
                    }
                }

                else if (selectionKey.isReadable()) {
                    SocketChannel client = (SocketChannel)selectionKey.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    client.read(buffer);
                    String output = new String(buffer.array()).trim();
                    System.out.println("Message read from client: " + output);
                    if (output.equals("Bye Bye")) {
                        client.close();
                        System.out.println("The Client messages are complete; close the session.");
                    }
                }
            }
        }
    }

}
```

### NIO Channel

Channel是中间桥梁或网关，将连接方与Buffer连接起来。它从连接方中读取数据并写入Buffer中

* Channel.read(Buffer) 将数据写入Buffer中

### NIO Selector

In Java NIO the selector is a multiplexor of selectable channels, which is used as a special type of channel that can be put into non-blocking mode. It can examine one or more NIO Channel's and determines which channel is ready for communication i.e. reading or writing.

### NIO Buffer

它代表着一片内存我们可以写入，读取.常用的有ByteBuffer buffer = ByteBuffer.allocate(1024);

普通文件读写操作调用的是操作系统内核提供的方法,流程如下

1. 应用程序私有内存空间,调用操作系统write(),进而将数据写入操作系统内核的内存空间,进而写入磁盘文件
2. 应用程序调用read(),同样也是得先从磁盘文件中读取到内核缓冲中,再从内核复制到应用程序私有内存空间
3. 每次调用read()/write()都会导致系统的上下文切换，性能较差，故此为了减少调用read/write,我们都会采用Buffer方式,先统一放置再缓存中再统一flush输出，对于读也是，读入缓存区再统一操作，减少系统的read/write调用

```flow
st=>operation: 应用进程的私有内存空间
pan=>operation: 磁盘文件
op1=>operation: 操作系统内核使用的内存空间

st->op1->pan

```

## AIO

NIO采用的是多路复用器(Selector),在一个线程中不断轮询所有通道。一旦某个通道可已经进行读写操作，则被选择出来进行操作，读写操作仍然是由调用者线程(Selector所在线程)执行并且是阻塞的，在选择select时阻塞。

AIO则是由操作系统在I/O完成后主动通知调用者。这意味着程序在发起读操作后总是立即返回，并可以去做其他事情。当底层的数据读取或写入操作完成时，将由操作系统通过调用相应的回调函数将已读取到的数据交给调用程序处理，写入过程同理.

AIO的实施需充分调用OS参与，IO需要操作系统支持、并发也同样需要操作系统的支持，所以**性能方面不同操作系统差异会比较明显**。因此在实际中AIO使用不是很广泛。核心原因其实还是Linux没有对AIO提供原生的支持，目前仍然使用epoll来模拟AIO功能

### 系统支持情况

* Linux : 通过epoll机制模拟实现AIO
* Windows : 通过IOCP机制(I/O Completion Port API)来实现AIO

#### Linux epoll

IO多路复用就是我们说的select，poll，epoll，有些地方也称这种IO方式为event driven IO。select/epoll的好处就在于单个process就可以同时处理多个网络连接的IO。它的基本原理就是select，poll，epoll这个function会不断的轮询所负责的所有socket，当某个socket有数据到达了，就通知用户进程。**是不是超像NIO中的设计思路**

* Linux接收到epoll读取请求后,将确认数据是否可以读取(多路复用selector选取),如果可以读取则告知application,application再调用read,OS再将数据写入内存缓冲区再交付给application进行处理

#### Windows IOCP

windows这方面其实更强大些,application请求异步IO时,OS将数据读取完成并copy至application的私有内存空间后再通知application，application直接就可以就这个读取结果进行处理，写入操作同理。这方面就比Linux强大，因为Linux只是确认有数据可以读取就通知application进行read,还需要再走一次内核缓冲与植入application内存区的操作.

## BIO/NIO/AIO总结

* BIO
  * 该方式适用于连接数较少且固定的架构。这种方式对服务器资源要求较高，并发局限在应用中
* NIO
  * NIO适用于连接数目多且轻操作(连接短)的架构，编程相对复杂些。NIO的原理是多路复用，其使用专用线程对多个channel上的IO事件进行轮询检查，当某个通道IO操作处理时间较长时，必然会影响到其他通道上的IO事件处理。这也是NIO适合轻操作架构的原因
* AIO
  * AIO方式适用于连接数目多且重操作(连接比较长)的架构，AIO充分调用操作系统参与并发操作，编程较为复杂
  * AIO需要OS参与，Linux系统还未对AIO提供原生支持，因此目前使用效果是打折扣的

名称 | 原理 | 场景 | 调用过程
:--:|:--:|:--:|:--:
NIO | 多路复用,主线程轮询IO事件 | 多连接,轻操作 |application(selector -------- channel -------- 业务)
AIO | 异步通信，协同OS;请求OS后等待IO完成回调再执行,充分使用了OS中的IO | 多连接,重操作 | application -------- OS(read)return -------- 回调application -------- 业务
BIO | 一个连接一个线程 | 少连接且稳定场景 | application -------- socket.read -------- 业务 (thread)
