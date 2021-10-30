package main;

import process.Heavyweight;

public class ProcessA {
    public static void main(String args[]) {
        Heavyweight heavyweight = new Heavyweight();

        heavyweight.listen();
    }
}
