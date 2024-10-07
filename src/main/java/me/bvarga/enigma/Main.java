package me.bvarga.enigma;

public class Main {

    public static void main(String[] args) {
        Enigma e = new Enigma(3, null);
        Enigma d = new Enigma();
        String encode = e.processString("HelloWorld");
        System.out.println(encode);
        String decode = e.processString(encode);
        System.out.println(decode);
    }

}
