package me.bvarga.enigma;

import me.bvarga.enigma.components.RotorBase;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Config wrapper for the machine's configuration.
 * It makes it easier to save and load in files.
 */
public class EnigmaConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 202411240157L;

    /**
     * The saved rotor config.
     */
    public List<RotorBase> SavedRotors = new ArrayList<>();

    /**
     * The saved plugboard pair entry.
     */
    public Map<Integer, Integer> PlugboardEntries = new HashMap<>();
    /**
     * The saved plugboard key list.
     */
    public List<Integer> PlugboardKeys = new ArrayList<>();

}
