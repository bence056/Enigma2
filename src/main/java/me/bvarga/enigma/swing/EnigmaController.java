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

/**
 * MVC Controller class
 */
public class EnigmaController {

    /**
     * The machine object.
     */
    private Enigma machine;
    /**
     * The view object
     */
    private EnigmaView view;
    /**
     * Network Manager object used for handling chat.
     */
    private NetworkManager networkManager;

    /**
     * The last known encoded character from the machine.
     */
    private char lastEncodedCharacter = '?';
    /**
     * The full encoded string since clearing the output.
     */
    private String encodedString = "";

    /**
     * Initializes the controller, creates the view manager, the UI and the machine backend.
     */
    public void InitializeController() {
        Reflector.InitializeReflector();
        networkManager = new NetworkManager(this);
        RandomizeNewMachine();
        view = new EnigmaView(this);
        view.UpdateUI(machine);
        view.setVisible(true);
    }

    /**
     * Triggered when a new character is sent from the UI to encode.
     *
     * @param character The character to encode.
     */
    public void TriggerEncode(Character character) {
        lastEncodedCharacter = machine.ProcessSingle(character);
        encodedString += lastEncodedCharacter;
        view.UpdateUI(machine);
    }

    /**
     * Getter for encodedString
     *
     * @return encodedString
     */
    public String getEncodedString() {
        return encodedString;
    }

    /**
     * Getter for lastEncodedCharacter
     *
     * @return lastEncodedCharacter
     */
    public char getLastEncodedCharacter() {
        return lastEncodedCharacter;
    }

    /**
     * Triggered when the view requests to clear the encoded output.
     */
    public void TriggerClearOutput() {
        encodedString = "";
        lastEncodedCharacter = '?';
        view.UpdateUI(machine);
    }

    /**
     * Triggered when the rotor configuration is modified to update the UI.
     *
     * @param RotorIndex   The index of the rotor to update.
     * @param PositionData The new position data to assign.
     */
    public void TriggerModifyRotorConfig(int RotorIndex, int PositionData) {
        machine.GetRotors().get(RotorIndex).SetRotorPosition(PositionData);
        view.UpdateUI(machine);

    }

    /**
     * Getter for the model.
     *
     * @return The model. (Enigma machine).
     */
    public Enigma getMachine() {
        return machine;
    }


    /**
     * Triggered when the plugboard configuration is modified to update the data and the UI.
     */
    public void TriggerModifyPlugboardConfig() {

        int Letter1 = view.PlugboardLeft.getSelectedIndex();
        int Letter2 = view.PlugboardRight.getSelectedIndex();

        if (Letter1 != Letter2) {
            //check if they are connected:
            boolean bDontReconnect = false;
            if (machine.GetPlugboard().GetConnectedLetterCodes().contains(Letter1)) {
                //disconnect it.
                bDontReconnect |= Letter2 == machine.GetPlugboard().DisconnectLetter(Letter1);

            }
            if (machine.GetPlugboard().GetConnectedLetterCodes().contains(Letter2)) {
                //disconnect it.
                bDontReconnect |= Letter1 == machine.GetPlugboard().DisconnectLetter(Letter2);

            }

            if (!bDontReconnect) {
                //we know they were not paired up, so we create a new pairing with the given values.
                machine.GetPlugboard().ConnectLetters(Letter1, Letter2);
            }
        }
        machine.GetPlugboard().fireTableDataChanged();
    }

    /**
     * Triggered when the input mode is modified. Updates the UI accordingly.
     * @param bUseTextField Whether to use the text field or the letter dropdown.
     */
    public void TriggerToggleInputMode(boolean bUseTextField) {
        view.SetInputMode(bUseTextField ? InputMode.TextField : InputMode.SingleChar);
        view.UpdateUI(machine);
    }

    /**
     * Triggered when the machine's state is requested to be saved onto disk.
     */
    public void TriggerSaveMachineConfig() {
        try {
            File f = new File("./data.enigma");
            if (!f.exists()) f.createNewFile();
            FileOutputStream fos = new FileOutputStream("./data.enigma");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(machine.ParseConfig());
            oos.close();
            view.UpdateUI(machine);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Triggered when the machine's state is requested to be loaded from the file.
     */
    public void TriggerLoadMachineConfig() {
        try {
            File f = new File("./data.enigma");
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                EnigmaConfig ec = (EnigmaConfig) ois.readObject();
                if (ec != null) {
                    machine = new Enigma(ec);
                }
                ois.close();
            } else {
                RandomizeNewMachine();
            }
            view.UpdateUI(machine);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Randomizer for a random enigma configuration.
     */
    private void RandomizeNewMachine() {
        machine = new Enigma(3);
        List<Integer> LetterCodes = new ArrayList<Integer>();
        Random r = new Random();
        for (int i = 0; i < 26; i++) {
            LetterCodes.add(i);
        }
        for (int i = 0; i < 5; i++) {
            int selIndex = r.nextInt(LetterCodes.size());
            int selValue = LetterCodes.get(selIndex);
            LetterCodes.remove(selIndex);
            int selIndex2 = r.nextInt(LetterCodes.size());
            int selValue2 = LetterCodes.get(selIndex2);
            LetterCodes.remove(selIndex2);
            machine.GetPlugboard().ConnectLetters(selValue, selValue2);
        }
    }

    /**
     * Triggered when a new message is received (either locally as an error or from the client).
     * @param msg The received message.
     */
    public void TriggerMessageReceived(String msg) {
        view.ChatOutput.append(msg + "\n");
        view.UpdateUI(machine);
    }

    /**
     * Triggered when the local instance wants to send a message to the remote endpoint.
     * @param msg The message.
     */
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

    /**
     * Network state getter.
     * @return whether we are connected to a remote endpoint or not.
     */
    public boolean IsNetConnected() {
        return networkManager.bIsConnected();
    }

    /**
     * Getter for the net instance role.
     * @return Server or Client
     */
    public InstanceRole GetInstanceRole() {
        return networkManager.getInstanceRole();
    }

    /**
     * Triggered when the local instance wants to disconnect from the remote endpoint.
     */
    public void TriggerDisconnect() {
        if (IsNetConnected()) {
            networkManager.DisconnectSocket();
        }
        view.ChatOutput.setText("");
    }

    /**
     * Triggered when the local instance wants to connect to a remote endpoint.
     */
    public void TriggerConnect() {
        if (!IsNetConnected()) {
            try {
                int ServerPort = Integer.parseInt(view.ServerPortField.getText());
                String ServerAddr = view.ServerAddressField.getText();
                if (ServerAddr.isEmpty()) ServerAddr = "127.0.0.1";
                networkManager.InitializeClient(ServerAddr, ServerPort);
            } catch (NumberFormatException ex) {
                TriggerSendMessage("Server port is not a number");
            }
        }
    }


    /**
     * Triggered when the local instance wants to host a new server endpoint.
     */
    public void TriggerHostServer() {
        if (!IsNetConnected()) {
            try {
                int ServerPort = Integer.parseInt(view.ServerPortField.getText());
                networkManager.InitializeServer(ServerPort);
            } catch (NumberFormatException ex) {
                TriggerSendMessage("Server port is not a number");
            }
        }
    }

    /**
     * Callback trigger, runs when the local instance disconnects from the endpoint or the server is terminated.
     */
    public void TriggerDisconnectConfirmed() {
        view.UpdateUI(machine);
    }

}
