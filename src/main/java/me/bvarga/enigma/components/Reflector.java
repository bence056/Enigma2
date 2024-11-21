package me.bvarga.enigma.components;

import java.util.HashMap;
import java.util.Map;

public class Reflector {

    private static Map<Integer, Integer> STATIC_REFLECTOR = new HashMap<Integer, Integer>();
    public static void InitializeReflector() {
        for(int i=0; i<13; i++) {
            STATIC_REFLECTOR.put(i, 25-i);
            STATIC_REFLECTOR.put(25-i, i);
        }
    }

    public static int convert(int c) {
        return STATIC_REFLECTOR.get(c);
    }

}
