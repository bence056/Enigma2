package me.bvarga.enigma.components;

import java.io.Serializable;
import java.util.*;

public class RotorBase implements Serializable {

    protected Map<Integer, Integer> RotorWiring;
    protected Map<Integer, Integer> RotorWiringReverse;

    protected int RotationCount = 0;
    private RotorBase NextRotor = null;
    protected Random rand = new Random();

    public RotorBase() {
        RotorWiring = new HashMap<Integer, Integer>();
        RotorWiringReverse = new HashMap<Integer, Integer>();
        //generate random wiring pairs;
        List<Integer> AllowedLettersIn = new ArrayList<Integer>();
        List<Integer> AllowedLettersOut = new ArrayList<Integer>();
        for(int i=0; i<26; i++) {
            AllowedLettersIn.add(i);
            AllowedLettersOut.add(i);
        }

        while(!AllowedLettersIn.isEmpty() && !AllowedLettersOut.isEmpty()) {
            int pairFirstIndex = rand.nextInt(AllowedLettersIn.size());
            int pairFirst = AllowedLettersIn.get(pairFirstIndex);
            AllowedLettersIn.remove(pairFirstIndex);
            int pairSecondIndex = rand.nextInt(AllowedLettersOut.size());
            int pairSecond = AllowedLettersOut.get(pairSecondIndex);
            AllowedLettersOut.remove(pairSecondIndex);

            RotorWiring.put(pairFirst, pairSecond);
            RotorWiringReverse.put(pairSecond, pairFirst);
        }

    }

    public RotorBase(Map<Integer, Integer> rotorWiring) {
        this.RotorWiring = rotorWiring;
        RotorWiringReverse = GetReverseMap(rotorWiring);
    }

    public static<T,V> Map<V,T> GetReverseMap(Map<T,V> InMap) {
        Map<V,T> ReturnMap = new HashMap<>();
        for(Map.Entry<T,V> entry : InMap.entrySet()) {
            ReturnMap.put(entry.getValue(), entry.getKey());
        }
        return ReturnMap;
    }

    public int convertForward(int c) {
        return RotorWiring.get(c);
    }

    public int convertReverse(int c) {
        return RotorWiringReverse.get(c);
    }

    public void AdvanceRotor() {

        List<Integer> OutValuesOriginal = RotorWiring.values().stream().toList();
        int index = 0;
        for(Map.Entry<Integer, Integer> entry : RotorWiring.entrySet()) {
            entry.setValue(OutValuesOriginal.get((index+1)%26));
            //change the reverted map as well:
            RotorWiringReverse.put(entry.getValue(), entry.getKey());
            index++;
        }
        RotationCount++;
        if(RotationCount == 26) {
            RotationCount = 0;
            if(NextRotor != null) {
                NextRotor.AdvanceRotor();
            }
        }

    }

    public void SetRotorPosition(int numPosition) {

        int numAdvance = (numPosition - RotationCount + 26);
        List<Integer> OutValuesOriginal = RotorWiring.values().stream().toList();
        int index = 0;
        for(Map.Entry<Integer, Integer> entry : RotorWiring.entrySet()) {
            entry.setValue(OutValuesOriginal.get((index+numAdvance)%26));
            //change the reverted map as well:
            RotorWiringReverse.put(entry.getValue(), entry.getKey());
            index++;
        }
        RotationCount = numPosition % 26;
    }

    public void SetNextRotor(RotorBase NextRotor) {
        this.NextRotor = NextRotor;
    }

    public int GetCurrentRotorIndex() {return RotationCount;}

}
