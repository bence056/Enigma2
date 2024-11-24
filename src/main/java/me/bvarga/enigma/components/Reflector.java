package me.bvarga.enigma.components;

import java.util.HashMap;
import java.util.Map;

/**
 * Special class for emulating a special type of rotor.
 * It creates a static rotor configuration that is used for all instances and serves as the reflector in the enigma machine.
 * @see <a href="https://en.wikipedia.org/wiki/Reflector_(cipher_machine)">Rotor (cipher)</a>
 */
public class Reflector {

    /**
     * Static map of integer pairs to store the constant values in.
     */
    private static Map<Integer, Integer> STATIC_REFLECTOR = new HashMap<Integer, Integer>();

    /**
     * Init method for the reflector.
     */
    public static void InitializeReflector() {
        for(int i=0; i<13; i++) {
            STATIC_REFLECTOR.put(i, 25-i);
            STATIC_REFLECTOR.put(25-i, i);
        }
    }

    /**
     * Converter function similar to the rotor conversion.
     * @param c the character to convert.
     * @see RotorBase
     * @return the converted character.
     */
    public static int convert(int c) {
        return STATIC_REFLECTOR.get(c);
    }

}
