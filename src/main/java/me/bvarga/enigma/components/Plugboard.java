package me.bvarga.enigma.components;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Plugboard extends AbstractTableModel implements Serializable {

    protected Map<Integer, Integer> PlugboardValues;
    protected List<Integer> ConnectedKeys;

    public Plugboard() {
        PlugboardValues = new HashMap<Integer, Integer>();
        ConnectedKeys = new ArrayList<>();
        for(int i=0; i<26; i++) {
            PlugboardValues.put(i, i);
        }
    }

    public void ConnectLetters(Integer left, Integer right) {
        //check if the selected letters are not connected to anything.
        if(!GetConnectedLetterCodes().contains(left) && !GetConnectedLetterCodes().contains(right)) {
            //set the connection pair.
            PlugboardValues.put(left, right);
            PlugboardValues.put(right, left);
            ConnectedKeys.add(left);
        }
    }

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

    public int GetConnectedValue(Integer inData) {
        return PlugboardValues.get(inData);
    }

    public void CopyConnections(Plugboard pb) {
        PlugboardValues = pb.PlugboardValues;
        ConnectedKeys = pb.ConnectedKeys;
    }

    public List<Integer> GetConnectedKeys() {return ConnectedKeys;}


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
