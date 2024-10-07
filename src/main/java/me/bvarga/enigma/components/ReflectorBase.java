package me.bvarga.enigma.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectorBase extends RotorBase {

    public ReflectorBase() {


        RotorWiring = new HashMap<Integer, Integer>();
        RotorWiringReverse = new HashMap<Integer, Integer>();
        //generate random wiring pairs;
        List<Integer> AllowedLetters = new ArrayList<Integer>();
        for(int i=0; i<26; i++) {
            AllowedLetters.add(i);
        }

        while(!AllowedLetters.isEmpty()) {
            int pairFirstIndex = rand.nextInt(AllowedLetters.size());
            int pairFirst = AllowedLetters.get(pairFirstIndex);
            AllowedLetters.remove(pairFirstIndex);
            int pairSecondIndex = rand.nextInt(AllowedLetters.size());
            int pairSecond = AllowedLetters.get(pairSecondIndex);
            AllowedLetters.remove(pairSecondIndex);

            RotorWiring.put(pairFirst, pairSecond);
            RotorWiring.put(pairSecond, pairFirst);
        }


    }

    public ReflectorBase(Map<Integer, Integer> PresetReflector) {
        this.RotorWiring = PresetReflector;
    }

    @Override
    public int convertReverse(int c) {
        return super.convertForward(c);
    }

    @Override
    public int convertForward(int c) {
        return RotorWiring.get(c);
    }
}
