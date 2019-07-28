package com.mxb.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * @author moxianbin
 * @date 2019-07-27.
 */
public class ScanXan {
    public static void main(String[] args) {
        try(
                Scanner s = new Scanner(new BufferedReader(new FileReader("/tmp/source.txt")))
            ){
            while (s.hasNext()){
                System.out.println(s.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
