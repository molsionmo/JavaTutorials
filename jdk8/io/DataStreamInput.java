package com.mxb.io;

import java.io.*;

/**
 * @author moxianbin
 * @date 2019-07-28.
 */
public class DataStreamInput {
    public static void main(String[] args) throws FileNotFoundException {

        double price;
        int unit;
        String desc;
        double total = 0.0;


        try (DataInputStream in = new DataInputStream(new FileInputStream("/tmp/dataStream.txt"))){
            while (true){
                price = in.readDouble();
                unit = in.readInt();
                desc = in.readUTF();
                System.out.format("You ordered %d" + " units of %s at $%.2f%n",
                        unit, desc, price);
                total += unit * price;
                System.out.println(total);
            }

        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

    }
}
