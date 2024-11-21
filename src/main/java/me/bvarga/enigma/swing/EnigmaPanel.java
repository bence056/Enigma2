package me.bvarga.enigma.swing;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class EnigmaPanel extends JPanel {

    public EnigmaPanel(String PanelTitle) {
        TitledBorder b = BorderFactory.createTitledBorder(PanelTitle);
        b.setTitleJustification(TitledBorder.CENTER);
        setBorder(b);
        setLayout((new BorderLayout()));
    }

}
