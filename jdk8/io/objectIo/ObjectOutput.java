package com.mxb.io.objectIo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * @author moxianbin
 * @date 2019-07-28.
 */
public class ObjectOutput {
    public static void main(String[] args) throws IOException {
        ObjectIoImpl objectIo = new ObjectIoImpl(new Date(), new Person("name", 16));

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("/tmp/object.txt"));

        out.writeObject(objectIo);
    }
}
