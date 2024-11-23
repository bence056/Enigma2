package me.bvarga.enigma.swing;

import me.bvarga.enigma.Enigma;
import me.bvarga.enigma.EnigmaConfig;
import me.bvarga.enigma.components.Reflector;
import me.bvarga.enigma.components.RotorBase;
import me.bvarga.enigma.network.InstanceRole;
import me.bvarga.enigma.network.NetworkManager;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnigmaController {

    private Enigma machine;
    private EnigmaView view;
    private NetworkManager networkManager;

    private char lastEncodedCharacter = '?';
    private String encodedString = "";

    public void InitializeController() {
        Reflector.InitializeReflector();
        networkManager = new NetworkManager(this);
        RandomizeNewMachine();
        view = new EnigmaView(this);
        view.UpdateUI(machine);
        view.setVisible(true);
    }

    public void TriggerEncode(Character character) {
        lastEncodedCharacter = machine.ProcessSingle(character);
        encodedString += lastEncodedCharacter;
        view.UpdateUI(machine);
    }

    public String getEncodedString() {
        return encodedString;
    }

    public char getLastEncodedCharacter() {
        return lastEncodedCharacter;
    }

    public void TriggerClearOutput() {
        encodedString = "";
        lastEncodedCharacter = '?';
        view.UpdateUI(machine);
    }

    public void TriggerModifyRotorConfig(int RotorIndex, int PositionData) {
        machine.GetRotors().get(RotorIndex).SetRotorPosition(PositionData);
        view.UpdateUI(machine);

    }

    public Enigma getMachine() {
        return machine;
    }


    public void TriggerModifyPlugboardConfig() {

        int Letter1 = view.PlugboardLeft.getSelectedIndex();
        int Letter2 = view.PlugboardRight.getSelectedIndex();

        if(Letter1 != Letter2) {
            //check if they are connected:
            boolean bDontReconnect = false;
            if(machine.GetPlugboard().GetConnectedLetterCodes().contains(Letter1)) {
                //disconnect it.
                bDontReconnect |= Letter2 == machine.GetPlugboard().DisconnectLetter(Letter1);

            }
            if(machine.GetPlugboard().GetConnectedLetterCodes().contains(Letter2)) {
                //disconnect it.
                bDontReconnect |= Letter1 == machine.GetPlugboard().DisconnectLetter(Letter2);

            }

            if(!bDontReconnect) {
                //we know they were not paired up, so we create a new pairing with the given values.
                machine.GetPlugboard().ConnectLetters(Letter1, Letter2);
            }
            }
        machine.GetPlugboard().fireTableDataChanged();
        }

    public void TriggerToggleInputMode(boolean bUseTextField) {
        view.SetInputMode(bUseTextField ? InputMode.TextField : InputMode.SingleChar);
        view.UpdateUI(machine);
    }

    public void TriggerSaveMachineConfig() {
        try {
            File f = new File("./data.enigma");
            if(!f.exists()) f.createNewFile();
            FileOutputStream fos = new FileOutputStream("./data.enigma");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(machine.ParseConfig());
            oos.close();
            view.UpdateUI(machine);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void TriggerLoadMachineConfig() {
        try {
            File f = new File("./data.enigma");
            if(f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                EnigmaConfig ec = (EnigmaConfig) ois.readObject();
                if(ec != null) {
                    machine = new Enigma(ec);
                }
                ois.close();
            }else {
                RandomizeNewMachine();
            }
            view.UpdateUI(machine);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void RandomizeNewMachine() {
        machine = new Enigma(3);
        List<Integer> LetterCodes = new ArrayList<Integer>();
        Random r = new Random();
        for(int i=0; i<26; i++) {
            LetterCodes.add(i);
        }
        for(int i=0; i<5; i++) {
            int selIndex = r.nextInt(LetterCodes.size());
            int selValue = LetterCodes.get(selIndex);
            LetterCodes.remove(selIndex);
            int selIndex2 = r.nextInt(LetterCodes.size());
            int selValue2 = LetterCodes.get(selIndex2);
            LetterCodes.remove(selIndex2);
            machine.GetPlugboard().ConnectLetters(selValue, selValue2);
        }
    }

    public void TriggerMessageReceived(String msg) {
        view.ChatOutput.append(msg + "\n");
        view.UpdateUI(machine);
    }

    public void TriggerSendMessage(String msg) {
        //add to local log as well.
        try {
            TriggerMessageReceived(String.format("[%s] %s", InetAddress.getLocalHost().getHostName(), msg));
            networkManager.SendMessage(String.format("[%s] %s", InetAddress.getLocalHost().getHostName(), msg));
        } catch (UnknownHostException e) {
            System.out.println("Error, local hostname cannot be determined");
        }
        view.UpdateUI(machine);
    }

    public boolean IsNetConnected() {
        return networkManager.bIsConnected();
    }

    public InstanceRole GetInstanceRole() {
        return networkManager.getInstanceRole();
    }

    public void TriggerDisconnect() {
        if(IsNetConnected()) {
            networkManager.DisconnectSocket();
        }
        view.ChatOutput.setText("");
    }

    public void TriggerConnect() {
        if(!IsNetConnected()) {
            try {
                int ServerPort = Integer.parseInt(view.ServerPortField.getText());
                String ServerAddr = view.ServerAddressField.getText();
                if(ServerAddr.isEmpty()) ServerAddr = "127.0.0.1";
                networkManager.InitializeClient(ServerAddr, ServerPort);
            }catch (NumberFormatException ex) {
                TriggerSendMessage("Server port is not a number");
            }
        }
    }


    public void TriggerHostServer() {
        if(!IsNetConnected()) {
            try {
                int ServerPort = Integer.parseInt(view.ServerPortField.getText());
                networkManager.InitializeServer(ServerPort);
            }catch (NumberFormatException ex) {
                TriggerSendMessage("Server port is not a number");
            }
        }
    }

    public void TriggerDisconnectConfirmed() {
        view.UpdateUI(machine);
    }

}
