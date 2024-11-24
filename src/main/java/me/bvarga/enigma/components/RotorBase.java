package me.bvarga.enigma.components;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class RotorBase implements Serializable {


    /**
     * Map storing the rotor inner wiring
     */
    protected Map<Integer, Integer> RotorWiring;
    /**
     * Map storing the rotor inner wiring reversed.
     */
    protected Map<Integer, Integer> RotorWiringReverse;

    /**
     * Integer storing the rotation count.
     */
    protected int RotationCount = 0;
    /**
     * The connected rotor.
     */
    private RotorBase NextRotor = null;
    protected Random rand = new Random();

    /**
     * Default constructor creating a random inner wiring for the rotor.
     */
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

    /**
     * Constructor with specified wiring.
     * @param rotorWiring The given wiring
     */
    public RotorBase(Map<Integer, Integer> rotorWiring) {
        this.RotorWiring = rotorWiring;
        RotorWiringReverse = GetReverseMap(rotorWiring);
    }

    /**
     * Static function to generate a reverse map from a given map.
     * Only works if the values are also distinct.
     * @param InMap The map to reverse.
     * @return The reversed map.
     * @param <T> Key type.
     * @param <V> Value type.
     */
    public static<T,V> Map<V,T> GetReverseMap(Map<T,V> InMap) {
        Map<V,T> ReturnMap = new HashMap<>();
        for(Map.Entry<T,V> entry : InMap.entrySet()) {
            ReturnMap.put(entry.getValue(), entry.getKey());
        }
        return ReturnMap;
    }

    /**
     * Method to convert the character using the forward wiring.
     * @param c The character to convert.
     * @return The converted character.
     */
    public int convertForward(int c) {
        return RotorWiring.get(c);
    }
    /**
     * Method to convert the character using the backward wiring.
     * @param c The character to convert.
     * @return The converted character.
     */
    public int convertReverse(int c) {
        return RotorWiringReverse.get(c);
    }

    /**
     * Method used to advance the rotor position by one and also advance the connected rotor if needed.
     */
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

    /**
     * This method allows the rotor position to be set with multiple rotor advance calls.
     * @param numPosition The targeted position.
     */
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

    /**
     * Setter for NextRotor.
     * @param NextRotor The rotor to set as next.
     */
    public void SetNextRotor(RotorBase NextRotor) {
        this.NextRotor = NextRotor;
    }

    /**
     * Getter for the connected rotor.
     * @return The connected rotor.
     */
    public RotorBase GetNextRotor() {return NextRotor;}

    /**
     * Getter for the rotor's current index.
     * @return The current indexed position of the rotor.
     */
    public int GetCurrentRotorIndex() {return RotationCount;}

}
