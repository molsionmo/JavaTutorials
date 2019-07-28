package com.mxb.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author moxianbin
 * @date 2019-07-27.
 */
public class TryWithResource {

    public static void main(String[] args) {
        try(BufferedReader reader =
                new BufferedReader(new FileReader("/tmp/source.txt"))){
            System.out.println(reader.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
