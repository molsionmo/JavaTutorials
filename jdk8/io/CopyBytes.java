package com.mxb.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author moxianbin
 * @date 2019-07-27.
 */
public class CopyBytes {
    public static void main(String[] args) {
        try(
                FileInputStream fileInputStream = new FileInputStream("/tmp/source.txt");
                FileOutputStream fileOutputStream = new FileOutputStream("/tmp/source_copy.txt");
        ){
            int c;
            while (( c = fileInputStream.read()) != -1){
                fileOutputStream.write(c);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
