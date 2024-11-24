package me.bvarga.enigma;

import me.bvarga.enigma.components.Plugboard;
import me.bvarga.enigma.components.RotorBase;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Config wrapper for the machine's configuration.
 * It makes it easier to save and load in files.
 */
public class EnigmaConfig implements Serializable {

    /**
     * The saved rotor config.
     */
    List<RotorBase> SavedRotors = new ArrayList<>();
    /**
     * The saved plugboard config.
     */
    Plugboard SavedPlugboard = new Plugboard();

}
