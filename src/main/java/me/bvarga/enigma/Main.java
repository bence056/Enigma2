package me.bvarga.enigma;

import me.bvarga.enigma.components.Reflector;

import java.io.*;

public class Main {

    public static void main(String[] args) {

        Reflector.InitializeReflector();
        Enigma e = new Enigma(3);
        File f = new File("./data.enigma");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(e.ParseConfig());
        } catch (Exception ex) {

        }

        Enigma e2 = null;

        try {
            FileInputStream fis = new FileInputStream("./data.enigma");
            ObjectInputStream ois = new ObjectInputStream(fis);
            EnigmaConfig ec = (EnigmaConfig) ois.readObject();
            if(ec != null) {
                e2 = new Enigma(ec);
            }
        } catch (Exception ex) {

        }

        String encode = e.processString("ABC");
        System.out.println(encode);

        if(e2 != null) {
            String encode2 = e2.processString(encode);
            System.out.println(encode2);
        }

    }

}
