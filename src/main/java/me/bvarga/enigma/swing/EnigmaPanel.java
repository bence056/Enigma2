package me.bvarga.enigma.swing;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Custom JPanel, allows for the creation of the segmented titled UI elements.
 */
public class EnigmaPanel extends JPanel {

    /**
     * Custom JPanel constructor.
     * @param PanelTitle the title to use on the top of the panel.
     */
    public EnigmaPanel(String PanelTitle) {
        TitledBorder b = BorderFactory.createTitledBorder(PanelTitle);
        b.setTitleJustification(TitledBorder.CENTER);
        setBorder(b);
        setLayout((new BorderLayout()));
    }

}
