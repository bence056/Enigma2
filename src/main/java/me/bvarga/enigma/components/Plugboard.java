package me.bvarga.enigma.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Plugboard implements Serializable {

    protected Map<Integer, Integer> PlugboardValues;

    public Plugboard() {
        PlugboardValues = new HashMap<Integer, Integer>();
        for(int i=0; i<26; i++) {
            PlugboardValues.put(i, i);
        }
    }

    public void ConnectLetters(Integer left, Integer right) {
        //check if the selected letters are not connected to anything.
        if(PlugboardValues.get(left).intValue() == left.intValue() &&
        PlugboardValues.get(right).intValue() == right.intValue()) {
            //set the connection pair.
            PlugboardValues.put(left, right);
            PlugboardValues.put(right, left);
        }
    }

    public void DisconnectLetter(Integer any) {
        Integer value = PlugboardValues.get(any);
        PlugboardValues.put(any, any);
        PlugboardValues.put(value, value);
    }

    public Map<Integer, Integer> getConnections() {
        return PlugboardValues;
    }

    public int GetConnectedValue(Integer inData) {
        return PlugboardValues.get(inData);
    }

    public void CopyConnections(Plugboard pb) {
        PlugboardValues = pb.getConnections();
    }

}
