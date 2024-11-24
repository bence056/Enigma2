package me.bvarga.enigma.components;

import me.bvarga.enigma.EnigmaConfig;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.List;


/**
 * This class defines the Enigma machine's plugboard.
 */
public class Plugboard extends AbstractTableModel {


    /**
     * The mapping of the plugboard pairs.
     * By default, it pairs up each of the letters of the alphabet (their respective integer values from 0-25) to themselves.
     */
    protected Map<Integer, Integer> PlugboardValues;
    /**
     * This list helps identify connected pairs. it contains the keys of the PlugboardValues map, but only for the pairs where the key and the value are not the same.
     */
    protected List<Integer> ConnectedKeys;

    /**
     * Plugboard constructor. Initializes the default plugboard configuration.
     */
    public Plugboard() {
        PlugboardValues = new HashMap<Integer, Integer>();
        ConnectedKeys = new ArrayList<>();
        for(int i=0; i<26; i++) {
            PlugboardValues.put(i, i);
        }
    }

    /**
     * This method pairs up two letters on the plugboard
     * @param left One of the letters to pair.
     * @param right The other letter to pair it with.
     */
    public void ConnectLetters(Integer left, Integer right) {
        //check if the selected letters are not connected to anything.
        if(!GetConnectedLetterCodes().contains(left) && !GetConnectedLetterCodes().contains(right)) {
            //set the connection pair.
            PlugboardValues.put(left, right);
            PlugboardValues.put(right, left);
            ConnectedKeys.add(left);
        }
    }

    /**
     * This method disconnects a paired letter and resets it to the default connection (connected to itself)
     * It also resets the paired letter's data.
     * @param any The selected letter to disconnect from its pair.
     * @return The letter the given letter was originally paired to before disconnection.
     */
    public int DisconnectLetter(Integer any) {
        int Key1 = -1;
        int Key2 = -1;
        if(ConnectedKeys.contains(any)) {
            //we know that the given letter is the left one;
            Key1 = any;
            Key2 = PlugboardValues.get(any);
        }
        PlugboardValues.put(Key1, Key1);
        PlugboardValues.put(Key2, Key2);

        int indexToRemove = -1;
        for(int i=0; i<ConnectedKeys.size(); i++) {
            if(ConnectedKeys.get(i) == Key1) {
                indexToRemove = i;
                break;
            }
        }
        if(indexToRemove != -1) {
            ConnectedKeys.remove(indexToRemove);
        }
        return Key2;
    }

    /**
     * Getter for the interconnected plugboard pair.
     * @param inData The letter (code) to get the pair for.
     * @return The connected pair.
     */
    public int GetConnectedValue(Integer inData) {
        return PlugboardValues.get(inData);
    }

    /**
     * Helper function to copy data from the config
     * It is mainly used when the states are loaded in from EnigmaConfig
     * @param conf The config object to copy from.
     */
    public void CopyConnections(EnigmaConfig conf) {
        PlugboardValues = conf.PlugboardEntries;
        ConnectedKeys = conf.PlugboardKeys;
    }

    /**
     * Getter for the ConnectedKeys array.
     * @return ConnectedKeys
     */
    public List<Integer> GetConnectedKeys() {return ConnectedKeys;}

    /**
     * Getter for the PlugboardValues array.
     * @return PlugboardValues
     */
    public Map<Integer, Integer> GetPlugboardValues() {return PlugboardValues;}


    @Override
    public int getRowCount() {
        return ConnectedKeys.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int i, int i1) {
        int left = ConnectedKeys.get(i);
        int right = PlugboardValues.get(left);
        switch (i1) {
            case 0: return (char) (left + 'A');
            case 1: return (char) (right + 'A');
            default:;
        }
        return null;
    }


    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * This method generates a list of all interconnected pairs (the ones that are not connected to themselves by default)
     * @return list of the connected letter codes.
     */
    public List<Integer> GetConnectedLetterCodes() {
        List<Integer> returnList = new ArrayList<>(ConnectedKeys);
        for(Integer key : ConnectedKeys) {
            returnList.add(PlugboardValues.get(key));
        }
        return returnList;
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> "Letter #1";
            case 1 -> "Letter #2";
            default -> "";
        };
    }
}
