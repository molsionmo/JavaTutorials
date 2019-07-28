package com.mxb.io;

import java.io.*;

/**
 * @author moxianbin
 * @date 2019-07-27.
 */
public class CopyLines {
    public static void main(String[] args) {
        try (
                BufferedReader reader = new BufferedReader(new FileReader("/tmp/source.txt"));
                PrintWriter writer = new PrintWriter(new FileWriter("/tmp/copy2.txt"))
        ) {

            String line;
            int c;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
