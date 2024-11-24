package me.bvarga.enigma;

import me.bvarga.enigma.components.Plugboard;
import me.bvarga.enigma.components.Reflector;
import me.bvarga.enigma.components.RotorBase;

import java.util.*;

/**
 * The enigma machine class.
 */
public class Enigma {

    /**
     * Stored plugboard config.
     */
    private final Plugboard plugboard;
    /**
     * Stored rotor config.
     */
    private final List<RotorBase> rotors;

    /**
     * Base constructor. It creates an enigma machine with 3 rotors. Currently, it is limited to 3, but it could be expanded in the future.
     * @param numRotors number of rotors to use.
     */
    public Enigma(int numRotors) {
        rotors = new ArrayList<>();
        for (int i = 0; i < numRotors; i++) {
            RotorBase rotor = new RotorBase();
            if(!rotors.isEmpty()) {
                rotors.get(rotors.size()-1).SetNextRotor(rotor);
            }
            rotors.add(rotor);
        }
        //set default plugboard.
        plugboard = new Plugboard();
    }

    /**
     * Custom constructor for an unspecified size of array, rotors as input.
     * @param Rotors The given rotors to use.
     */
    public Enigma(RotorBase... Rotors) {
        rotors = new ArrayList<>();
        rotors.addAll(Arrays.asList(Rotors));
        plugboard = new Plugboard();
    }

    /**
     * Custom constructor with a config parameter to allow for loading from a file data.
     * @param Conf The custom config.
     * @see EnigmaConfig
     */
    public Enigma(EnigmaConfig Conf) {
        rotors = new ArrayList<>();
        for(RotorBase rotorBase : Conf.SavedRotors) {
            if(!rotors.isEmpty() && rotors.get(rotors.size() - 1) != null) {
                rotors.get(rotors.size() - 1).SetNextRotor(rotorBase);
            }
            rotors.add(rotorBase);
        }
        plugboard = new Plugboard();
        plugboard.CopyConnections(Conf);
    }

    /**
     * String processor, allows whole strings to be processed with the machine.
     * @param input The string to process.
     * @return The encoded string.
     */
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

    /**
     * Character processor for the encoder.
     * @param c The character to process.
     * @return The encoded character.
     */
    public char ProcessSingle(char c) {

        int asNumber = Character.toUpperCase(c) - 'A';

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
        asNumber = plugboard.GetConnectedValue(asNumber);

        return (char)('A' + asNumber);
    }

    /**
     * Config parser for file writing.
     * @return The parsed config of the machine's current state.
     */
    public EnigmaConfig ParseConfig() {
        EnigmaConfig Config = new EnigmaConfig();
        Config.SavedRotors.addAll(rotors);
        Config.PlugboardEntries = plugboard.GetPlugboardValues();
        Config.PlugboardKeys = plugboard.GetConnectedKeys();
        return Config;
    }

    /**
     * Getter for rotors.
     * @return The assigned rotors.
     */
    public List<RotorBase> GetRotors() {return rotors;}

    /**
     * Getter for plugboard.
     * @return The assigned plugboard.
     */
    public Plugboard GetPlugboard() {return plugboard;}
}
