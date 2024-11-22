package me.bvarga.enigma;

import me.bvarga.enigma.components.Plugboard;
import me.bvarga.enigma.components.Reflector;
import me.bvarga.enigma.components.RotorBase;

import java.util.*;

public class Enigma {

    private final Plugboard plugboard;
    private final List<RotorBase> rotors;

    public Enigma(int numRotors) {
        rotors = new ArrayList<>();
        for (int i = 0; i < numRotors; i++) {
            RotorBase rotor = new RotorBase();
            if(rotors.size() >= 1) {
                rotors.get(rotors.size()-1).SetNextRotor(rotor);
            }
            rotors.add(rotor);
        }
        //set default plugboard.
        plugboard = new Plugboard();
    }

    public Enigma(RotorBase... Rotors) {
        rotors = new ArrayList<>();
        rotors.addAll(Arrays.asList(Rotors));
        plugboard = new Plugboard();
    }

    public Enigma(EnigmaConfig Conf) {
        rotors = new ArrayList<>();
        for(RotorBase rotorBase : Conf.SavedRotors) {
            if(!rotors.isEmpty() && rotors.get(rotors.size() - 1) != null) {
                rotors.get(rotors.size() - 1).SetNextRotor(rotorBase);
            }
            rotors.add(rotorBase);
        }
        plugboard = new Plugboard();
        plugboard.CopyConnections(Conf.SavedPlugboard);
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

        //first feed through plugboard.
        asNumber = plugboard.GetConnectedValue(asNumber);

        for(RotorBase rotor : rotors) {

            asNumber = rotor.convertForward(asNumber);

        }

        asNumber = Reflector.convert(asNumber);

        for(int i=rotors.size()-1; i>=0; i--) {
            RotorBase rotor = rotors.get(i);
            asNumber = rotor.convertReverse(asNumber);

        }

        //now advance the first rotor as one character entered.
        if(rotors.get(0) != null) {
            rotors.get(0).AdvanceRotor();
        }

        //feed through the plugboard again.
        asNumber = plugboard.GetReverseConnectedValue(asNumber);

        return (char)('A' + asNumber);
    }

    public EnigmaConfig ParseConfig() {
        EnigmaConfig Config = new EnigmaConfig();
        Config.SavedRotors.addAll(rotors);
        Config.SavedPlugboard = plugboard;
        return Config;
    }

    public List<RotorBase> GetRotors() {return rotors;}
    public Plugboard GetPlugboard() {return plugboard;}
}
