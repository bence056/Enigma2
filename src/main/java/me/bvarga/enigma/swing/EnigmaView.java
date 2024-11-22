package me.bvarga.enigma.swing;

import me.bvarga.enigma.Enigma;
import me.bvarga.enigma.components.Plugboard;
import me.bvarga.enigma.components.RotorBase;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;

public class EnigmaView extends JFrame {

    JComboBox<Character> FirstRotor = new JComboBox<>();
    JComboBox<Character> SecondRotor = new JComboBox<>();
    JComboBox<Character> ThirdRotor = new JComboBox<>();
    JComboBox<Character> PlugboardLeft = new JComboBox<>();
    JComboBox<Character> PlugboardRight = new JComboBox<>();
    JComboBox<Character> LetterSelector = new JComboBox<>();
    JButton PlugboardButton;
    JTable PlugboardTable;

    JLabel EncodeResultLabel;
    JTextField outputText;

    public EnigmaController Controller;

    public EnigmaView(EnigmaController Controller) {

        this.Controller = Controller;

        setTitle("Enigma");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1100, 400));
        setLayout(new GridLayout(2, 2));
        setResizable(false);

        for(int i=0; i<26; i++) {
            FirstRotor.addItem((char)('A' + i));
            SecondRotor.addItem((char)('A' + i));
            ThirdRotor.addItem((char)('A' + i));
            PlugboardLeft.addItem((char)('A' + i));
            PlugboardRight.addItem((char)('A' + i));
            LetterSelector.addItem((char)('A' + i));
        }

        EnigmaPanel EncodePanel = new EnigmaPanel("Encode");
        EncodePanel.setLayout(new BorderLayout());
        JButton EncodeButton = new JButton("Encode!");
        EncodeButton.addActionListener(e -> {
            Controller.TriggerEncode((Character)LetterSelector.getSelectedItem());
        });
        EncodeResultLabel = new JLabel("?");

        JPanel SeparatorPanel = new JPanel();
        SeparatorPanel.setLayout(new GridLayout(2, 1));

        JPanel EncodeTopPanel = new JPanel();
        EncodeTopPanel.setLayout(new FlowLayout());

        EncodeTopPanel.add(LetterSelector);
        EncodeTopPanel.add(EncodeButton);
        EncodeTopPanel.add(EncodeResultLabel);

        JPanel EncodeBottomPanel = new JPanel();
        EncodeBottomPanel.setLayout(new FlowLayout());

        outputText = new JTextField();
        outputText.setEditable(false);
        outputText.setColumns(20);
        JButton ClearButton = new JButton("Clear");
        ClearButton.addActionListener(e -> {
            Controller.TriggerClearOutput();
        });

        JButton SendButton = new JButton("Send");
        SendButton.addActionListener(e -> {
            //todo later.
        });

        EncodeBottomPanel.add(outputText);
        EncodeBottomPanel.add(ClearButton);
        EncodeBottomPanel.add(SendButton);

        SeparatorPanel.add(EncodeTopPanel);
        SeparatorPanel.add(EncodeBottomPanel);
        EncodePanel.add(SeparatorPanel, BorderLayout.CENTER);


        EnigmaPanel RotorsPanel = new EnigmaPanel("Rotors");
        RotorsPanel.setLayout(new FlowLayout());


        RotorsPanel.add(ThirdRotor);
        RotorsPanel.add(SecondRotor);
        RotorsPanel.add(FirstRotor);

        EnigmaPanel PlugboardPanel = new EnigmaPanel("Plugboard");
        PlugboardPanel.setLayout(new BorderLayout());

        PlugboardTable = new JTable();
        JScrollPane PlugboardTableScrollPane = new JScrollPane(PlugboardTable);

        PlugboardTable.setModel(Controller.getMachine().GetPlugboard());
        PlugboardTable.setDefaultRenderer(JButton.class, new JTableButtonRenderer(PlugboardTable.getDefaultRenderer(JButton.class)));


        PlugboardPanel.add(PlugboardTableScrollPane, BorderLayout.CENTER);

        JPanel PlugboardManagement = new JPanel();
        PlugboardManagement.setLayout(new FlowLayout());
        PlugboardManagement.add(PlugboardLeft);
        PlugboardManagement.add(PlugboardRight);
        PlugboardButton = new JButton("Connect/Disconnect");
        PlugboardButton.addActionListener(e -> {
            Controller.TriggerModifyPlugboardConfig();
        });
        PlugboardManagement.add(PlugboardButton);
        PlugboardPanel.add(PlugboardManagement, BorderLayout.SOUTH);




        EnigmaPanel ChatPanel = new EnigmaPanel("Chat");


        this.add(RotorsPanel);
        this.add(PlugboardPanel);
        this.add(EncodePanel);
        this.add(ChatPanel);

        //bind for actions:
        RotorsModifiedListener listener = new RotorsModifiedListener();
        FirstRotor.addActionListener(listener);
        SecondRotor.addActionListener(listener);
        ThirdRotor.addActionListener(listener);
//        PlugboardConnected.addActionListener(modifiedEvent);

    }


    public void UpdateUI(Enigma enigma) {
        List<RotorBase> rotorBaseList = enigma.GetRotors();
        FirstRotor.setSelectedIndex(rotorBaseList.get(0).GetCurrentRotorIndex());
        SecondRotor.setSelectedIndex(rotorBaseList.get(1).GetCurrentRotorIndex());
        ThirdRotor.setSelectedIndex(rotorBaseList.get(2).GetCurrentRotorIndex());

        EncodeResultLabel.setText(String.valueOf(Controller.getLastEncodedCharacter()));
        outputText.setText(Controller.getEncodedString());


    }

//    class PlugboardEntryModifiedListener implements ActionListener {
//
//        public void actionPerformed(ActionEvent ae) {
//            if(ae.getSource() == PlugboardLeft) {
//                Plugboard PBRef = Controller.getMachine().GetPlugboard();
//                List<Integer> DisallowedLetters = PBRef.GetConnectedLetterCodes();
//                if(DisallowedLetters.contains(PlugboardLeft.getSelectedIndex())) {
//                    PlugboardRight.removeAllItems();
//                    for(int i=0; i<26; i++) {
//                        PlugboardRight.addItem((char)('A' + i));
//                    }
//
//                    int pairIndex = Controller.getMachine().GetPlugboard().GetConnectedValue(PlugboardLeft.getSelectedIndex());
//                    if(pairIndex == PlugboardLeft.getSelectedIndex()) {
//                        pairIndex = Controller.getMachine().GetPlugboard().GetReverseConnectedValue(PlugboardLeft.getSelectedIndex());
//                    }
//                    PlugboardRight.setSelectedIndex(pairIndex);
//                    PlugboardRight.setEnabled(false);
//                }else {
//                    PlugboardRight.removeAllItems();
//                    for(int i=0; i<26; i++) {
//                        if(!DisallowedLetters.contains(i) && i != PlugboardLeft.getSelectedIndex()) {
//                            PlugboardRight.addItem((char)('A' + i));
//                        }
//                    }
//                    PlugboardRight.setEnabled(true);
//                }
//
//            }else if(ae.getSource() == PlugboardButton) {
//                if(bConnect) {
//
//                }else {
//
//                }
//            }
//        }
//    }

    class RotorsModifiedListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ae.getSource().equals(FirstRotor)) {
                Controller.TriggerModifyRotorConfig(0, FirstRotor.getSelectedIndex());
            }else if(ae.getSource().equals(SecondRotor)) {
                Controller.TriggerModifyRotorConfig(1, SecondRotor.getSelectedIndex());
            }else if(ae.getSource().equals(ThirdRotor)) {
                Controller.TriggerModifyRotorConfig(2, ThirdRotor.getSelectedIndex());
            }
        }
    }


    class JTableButtonRenderer implements TableCellRenderer {
        private final TableCellRenderer defaultRenderer;
        public JTableButtonRenderer(TableCellRenderer renderer) {
            defaultRenderer = renderer;
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if(value instanceof Component)
                return (Component)value;
            return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

}
