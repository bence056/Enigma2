package me.bvarga.enigmatests;

import me.bvarga.enigma.Enigma;
import me.bvarga.enigma.EnigmaConfig;
import me.bvarga.enigma.components.Plugboard;
import me.bvarga.enigma.components.RotorBase;
import me.bvarga.enigma.swing.EnigmaController;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the system.
 */
public class EnigmaTests {

    /**
     * Controller reference
     */
    private static EnigmaController Controller;
    /**
     * Config reference
     */
    private EnigmaConfig config = null;

    /**
     * Create the enigma controller and init the system beforehand.
     */
    @BeforeAll
    public static void setUpBeforeClass() {
        Controller = new EnigmaController();
        Controller.InitializeController();
    }


    /**
     * Test whether the machine can export data properly.
     */
    @org.junit.jupiter.api.Test
    public void testEnigmaConfSave() {
        //test whether the save data parsed is tht same as in the machine.
        config = Controller.getMachine().ParseConfig();
        assertEquals(config.SavedRotors, Controller.getMachine().GetRotors());
        assertEquals(config.PlugboardEntries, Controller.getMachine().GetPlugboard().GetPlugboardValues());
        assertEquals(config.PlugboardKeys, Controller.getMachine().GetPlugboard().GetConnectedKeys());
    }

    /**
     * Test whether the machine can import data properly.
     */
    @org.junit.jupiter.api.Test
    public void testEnigmaConfLoad() {
        //test whether the save data parsed is tht same as in the machine.
        config = Controller.getMachine().ParseConfig();
        Enigma e1 = new Enigma(config);
        Enigma e2 = new Enigma(config);
        assertEquals(e1.ParseConfig().SavedRotors, e2.ParseConfig().SavedRotors);
        assertEquals(e1.ParseConfig().PlugboardEntries, e2.ParseConfig().PlugboardEntries);
        assertEquals(e1.ParseConfig().PlugboardKeys, e2.ParseConfig().PlugboardKeys);
    }

    /**
     * Test if the machine handles invalid character input from strings.
     */
    @org.junit.jupiter.api.Test
    public void testEnigmaInvalidCharacterString() {
        assertEquals("INVALID CHARACTER", Controller.getMachine().processString(";"));
        assertNotEquals("INVALID CHARACTER", Controller.getMachine().processString("A"));
    }

    /**
     * Test if the rotors advance the way they are supposed to.
     * Also test if connected rotors advance when needed.
     */
    @org.junit.jupiter.api.Test
    public void testRotorAdvance() {
        int RotorCurrentPos = Controller.getMachine().GetRotors().get(0).GetCurrentRotorIndex();
        Controller.getMachine().GetRotors().get(0).AdvanceRotor();
        assertEquals(RotorCurrentPos+1, Controller.getMachine().GetRotors().get(0).GetCurrentRotorIndex());
        for(int i=0; i<25; i++) {
            Controller.getMachine().GetRotors().get(0).AdvanceRotor();
        }
        assertEquals(RotorCurrentPos, Controller.getMachine().GetRotors().get(0).GetCurrentRotorIndex());
        assertEquals(1, Controller.getMachine().GetRotors().get(1).GetCurrentRotorIndex());
    }

    /**
     * Test whether direct setting the rotor would cause any data mismatches.
     */
    @org.junit.jupiter.api.Test
    public void testRotorSetPosition() {
        int RotorCurrentPos = Controller.getMachine().GetRotors().get(0).GetCurrentRotorIndex();
        Controller.getMachine().GetRotors().get(0).SetRotorPosition(RotorCurrentPos + 10);
        assertEquals(RotorCurrentPos+10, Controller.getMachine().GetRotors().get(0).GetCurrentRotorIndex());
    }

    /**
     * Test the rotor config reverser function.
     */
    @org.junit.jupiter.api.Test
    public void testRotorMapReversal() {
        Map<Integer, Integer> ForwardMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> ExpectedRevMap = new HashMap<>();
        for(int i=0; i<10; i++) {
            ForwardMap.put(i, 9-i);
            ExpectedRevMap.put(9-i, i);
        }
        Map<Integer, Integer> ReturnMap = RotorBase.GetReverseMap(ForwardMap);
        for(int i=0; i<10; i++) {
            assertEquals(ExpectedRevMap.get(i), ReturnMap.get(i));
        }
    }

    /**
     * Test if rotors can be attached properly.
     */
    @org.junit.jupiter.api.Test
    public void testAttachRotors() {
        RotorBase r1 = new RotorBase();
        RotorBase r2 = new RotorBase();
        r1.SetNextRotor(r2);
        assertEquals(r1.GetNextRotor(), r2);
    }

    /**
     * Test if the default config of the plugboard contains the default pairs (1-1,2-2...26-26)
     */
    @org.junit.jupiter.api.Test
    public void testPlugboardDefaultInitPairs() {
        Plugboard pb = new Plugboard();
        for(int i=0; i<26; i++) {
            assertEquals(pb.GetPlugboardValues().get(i), i);
        }
    }

    /**
     * Tests whether the plugboard handles connecting letters properly or not.
     */
    @org.junit.jupiter.api.Test
    public void testPlugboardConnectLetters() {
        Plugboard pb = new Plugboard();
        pb.ConnectLetters(0,12);
        assertEquals(pb.GetConnectedValue(0), 12);
        assertEquals(pb.GetConnectedValue(12), 0);
    }

    /**
     * Tests whether the plugboard handles disconnecting letters properly or not.
     */
    @org.junit.jupiter.api.Test
    public void testPlugboardDisconnectLetters() {
        Plugboard pb = new Plugboard();
        pb.ConnectLetters(0,12);
        pb.DisconnectLetter(12);
        assertEquals(pb.GetConnectedValue(0), 0);
        assertEquals(pb.GetConnectedValue(12), 12);
    }



}
