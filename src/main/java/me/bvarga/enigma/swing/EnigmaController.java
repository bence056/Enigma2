package me.bvarga.enigma.swing;

import me.bvarga.enigma.Enigma;
import me.bvarga.enigma.EnigmaConfig;
import me.bvarga.enigma.components.Reflector;
import me.bvarga.enigma.components.RotorBase;

import javax.swing.*;
import java.io.*;

public class EnigmaController {

    private Enigma machine;
    private EnigmaView view;

    private char lastEncodedCharacter = '?';
    private String encodedString = "";

    public void InitializeController() {
        Reflector.InitializeReflector();

            try {
                File f = new File("./data.enigma");
                if(f.createNewFile()) {
                    machine = new Enigma(3);
                    FileOutputStream fos = new FileOutputStream("./data.enigma");
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(machine.ParseConfig());
                }else {
                    FileInputStream fis = new FileInputStream(f);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    EnigmaConfig ec = (EnigmaConfig) ois.readObject();
                    if(ec != null) {
                        machine = new Enigma(ec);
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
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
}
