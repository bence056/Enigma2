package me.bvarga.enigma;

import me.bvarga.enigma.components.Plugboard;
import me.bvarga.enigma.components.RotorBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnigmaConfig implements Serializable {

    List<RotorBase> SavedRotors = new ArrayList<>();
    Plugboard SavedPlugboard = new Plugboard();

}
