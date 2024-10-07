package me.bvarga.enigma;

import me.bvarga.enigma.components.ReflectorBase;
import me.bvarga.enigma.components.RotorBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Enigma {

    private List<RotorBase> rotors;
    private ReflectorBase reflector;

    public Enigma(int numRotors) {
        rotors = new ArrayList<RotorBase>();
        for (int i = 0; i < numRotors; i++) {
            RotorBase rotor = new RotorBase();
            rotors.add(rotor);
        }
        if(presetReflector != null) {
            reflector = presetReflector;
        }else {
            reflector = new ReflectorBase();
        }
    }

    public Enigma(ReflectorBase presetReflector, RotorBase... Rotors) {
        rotors = new ArrayList<RotorBase>();
        rotors.addAll(Arrays.asList(Rotors));
        if(presetReflector != null) {
            reflector = presetReflector;
        }else {
            reflector = new ReflectorBase();
        }

    }

    public String processString(String input) {
        input = input.toUpperCase();
        String ReturnString = "";
        for(char c : input.toCharArray()) {
            if(Character.isLetter(c) && c >= 'A' && c <= 'Z') {
                ReturnString += ProcessSingle(c);
            }else {
                return "INVALID CHARACTER";
            }

        }
        return ReturnString;
    }

    public char ProcessSingle(char c) {

        int asNumber = c - 'A';

        for(RotorBase rotor : rotors) {

            asNumber = rotor.convertForward(asNumber);

        }

        asNumber = reflector.convertForward(asNumber);

        for(int i=rotors.size()-1; i>=0; i--) {
            RotorBase rotor = rotors.get(i);
            asNumber = rotor.convertReverse(asNumber);

        }

        return (char)('A' + asNumber);
    }

}
