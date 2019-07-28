package com.mxb.io.objectIo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author moxianbin
 * @date 2019-07-28.
 */
public class ObjectInput {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(new FileInputStream("/tmp/object.txt"));

        ObjectIoImpl objectIo = (ObjectIoImpl)input.readObject();

        objectIo.sayHello();
    }
}
