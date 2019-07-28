package com.mxb.io.objectIo;

import java.io.Serializable;

/**
 * @author moxianbin
 * @date 2019-07-28.
 */
public class Person implements Serializable {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
