package com.mxb.io.objectIo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author moxianbin
 * @date 2019-07-28.
 */
public class ObjectIoImpl implements ObjectIo, Serializable {

    private Date date;
    private Person person;

    public ObjectIoImpl(Date date, Person person) {
        this.date = date;
        this.person = person;
    }

    @Override
    public void sayHello() {
        System.out.format("hello, objectIoImpl, %s, %s, %d" , date.toString() , person.getName(), person.getAge());
    }

}
