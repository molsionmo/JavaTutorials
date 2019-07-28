package com.mxb.io;

import java.io.*;
import java.util.Arrays;

/**
 * @author moxianbin
 * @date 2019-07-27.
 */
public class DataStreamOutput {
    static final String dataFile = "invoicedata";

    static final double[] prices = { 19.99, 9.99, 15.99, 3.99, 4.99 };
    static final int[] units = { 12, 8, 13, 29, 50 };
    static final String[] descs = {
            "Java T-shirt",
            "Java Mug",
            "Duke Juggling Dolls",
            "Java Pin",
            "Java Key Chain"
    };

    public static void main(String[] args) {
        try (
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("/tmp/dataStream.txt")))
                ) {

            for (int i=0;i<prices.length;i++){
                out.writeDouble(prices[i]);
                out.writeInt(units[i]);
                out.writeUTF(descs[i]);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
