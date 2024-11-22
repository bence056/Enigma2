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
    protected Map<Integer, Integer> PlugboardValuesReverse;
    protected List<Integer> ConnectedKeys;

    public Plugboard() {
        PlugboardValues = new HashMap<Integer, Integer>();
        PlugboardValuesReverse = new HashMap<Integer, Integer>();
        ConnectedKeys = new ArrayList<>();
        for(int i=0; i<26; i++) {
            PlugboardValues.put(i, i);
            PlugboardValuesReverse.put(i, i);
        }
    }

    public void ConnectLetters(Integer left, Integer right) {
        //check if the selected letters are not connected to anything.
        if(PlugboardValues.get(left).intValue() == left.intValue()) {
            //set the connection pair.
            PlugboardValues.put(left, right);
            PlugboardValuesReverse.put(right, left);
            ConnectedKeys.add(left);
        }
    }

    public int DisconnectLetter(Integer any) {

        int PVKey = -1;
        int PVRKey = -1;

        if(ConnectedKeys.contains(any)) {
            //we know that the given letter is the left one;
            PVKey = any;
            PVRKey = PlugboardValues.get(any);
        }else {
            //we know that the given letter is the right one;
            PVKey = PlugboardValuesReverse.get(any);
            PVRKey = any;
        }

        int pairValue = PlugboardValues.get(PVKey);

        PlugboardValues.put(PVKey, PVKey);
        PlugboardValuesReverse.put(PVRKey, PVRKey);

        int indexToRemove = -1;
        for(int i=0; i<ConnectedKeys.size(); i++) {
            if(ConnectedKeys.get(i).intValue() == PVKey) {
                indexToRemove = i;
                break;
            }
        }
        if(indexToRemove != -1) {
            ConnectedKeys.remove(indexToRemove);
        }
        return pairValue;
    }

    public int GetConnectedValue(Integer inData) {
        return PlugboardValues.get(inData);
    }

    public int GetReverseConnectedValue(Integer inData) {
        return PlugboardValuesReverse.get(inData);
    }

    public void CopyConnections(Plugboard pb) {
        PlugboardValues = pb.PlugboardValues;
        PlugboardValuesReverse = pb.PlugboardValuesReverse;
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
