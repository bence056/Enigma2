package me.bvarga.enigma;

import me.bvarga.enigma.network.NetworkManager;
import me.bvarga.enigma.swing.EnigmaController;

/**
 * Main class.
 */
public class Main {

    /**
     * Entry point
     * @param args program arguments. Not used.
     */
    public static void main(String[] args) {

        EnigmaController controller = new EnigmaController();
        controller.InitializeController();

    }

}
