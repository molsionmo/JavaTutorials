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
