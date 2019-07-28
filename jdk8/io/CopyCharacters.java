package com.mxb.io;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author moxianbin
 * @date 2019-07-27.
 */
public class CopyCharacters {
    public static void main(String[] args) {
        try(
                FileReader reader = new FileReader("/tmp/source.txt");
                FileWriter writer = new FileWriter("/tmp/copy_test.txt")
                ) {

            int c;

            while ( (c=reader.read()) != -1){
                writer.write(c);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
