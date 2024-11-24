package me.bvarga.enigma.swing;

import me.bvarga.enigma.Enigma;
import me.bvarga.enigma.components.Plugboard;
import me.bvarga.enigma.components.RotorBase;
import me.bvarga.enigma.network.InstanceRole;
import org.w3c.dom.Text;

import javax.crypto.Mac;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;

/**
 * Main view control. Generates and propagates the user interface.
 */
public class EnigmaView extends JFrame {

    JComboBox<Character> FirstRotor = new JComboBox<>();
    JComboBox<Character> SecondRotor = new JComboBox<>();
    JComboBox<Character> ThirdRotor = new JComboBox<>();
    JComboBox<Character> PlugboardLeft = new JComboBox<>();
    JComboBox<Character> PlugboardRight = new JComboBox<>();
    JComboBox<Character> LetterSelector = new JComboBox<>();
    JTextField MessageInput = new JTextField();
    JButton PlugboardButton;
    JTable PlugboardTable;
    JButton EncodeButton;
    JTextArea ChatOutput;

    JTextField ServerAddressField;
    JTextField ServerPortField;
    JButton ChatConnectButton;
    JButton ChatHostButton;

    private InputMode TextInputMode = InputMode.SingleChar;

    JLabel EncodeResultLabel;
    JTextField outputText;

    public EnigmaController Controller;

    /**
     * Constructor for the UI.
     *
     * @param Controller Reference to the controller from the MVC framework.
     */
    public EnigmaView(EnigmaController Controller) {

        this.Controller = Controller;

        setTitle("Enigma");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1100, 400));
        setLayout(new GridLayout(2, 2));
        setResizable(false);

        for (int i = 0; i < 26; i++) {
            FirstRotor.addItem((char) ('A' + i));
            SecondRotor.addItem((char) ('A' + i));
            ThirdRotor.addItem((char) ('A' + i));
            PlugboardLeft.addItem((char) ('A' + i));
            PlugboardRight.addItem((char) ('A' + i));
            LetterSelector.addItem((char) ('A' + i));
        }

        EnigmaPanel EncodePanel = new EnigmaPanel("Encode");
        EncodePanel.setLayout(new BorderLayout());
        EncodeButton = new JButton("Encode!");
        EncodeButton.addActionListener(e -> {
            Controller.TriggerEncode((Character) LetterSelector.getSelectedItem());
        });
        EncodeResultLabel = new JLabel("?");

        JPanel SeparatorPanel = new JPanel();
        SeparatorPanel.setLayout(new GridLayout(2, 1));

        JPanel EncodeTopPanel = new JPanel();
        EncodeTopPanel.setLayout(new FlowLayout());

        JCheckBox TextFieldMode = new JCheckBox("Prefer Text field");
        TextFieldMode.addActionListener(e -> {
            Controller.TriggerToggleInputMode(TextFieldMode.isSelected());
        });
        EncodeTopPanel.add(TextFieldMode);
        EncodeTopPanel.add(LetterSelector);
        MessageInput.addKeyListener(new MessageInputAdapter());
        MessageInput.setColumns(1);
        EncodeTopPanel.add(MessageInput);
        LetterSelector.setVisible(TextInputMode == InputMode.SingleChar);
        MessageInput.setVisible(TextInputMode == InputMode.TextField);
        EncodeButton.setVisible(TextInputMode == InputMode.SingleChar);

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
            Controller.TriggerSendMessage(outputText.getText());
            outputText.setText("");
        });

        JButton SaveConfigButton = new JButton("Save");
        SaveConfigButton.addActionListener(e -> {
            Controller.TriggerSaveMachineConfig();
        });

        JButton LoadConfigButton = new JButton("Load");
        LoadConfigButton.addActionListener(e -> {
            Controller.TriggerLoadMachineConfig();
        });

        EncodeBottomPanel.add(SaveConfigButton);
        EncodeBottomPanel.add(LoadConfigButton);
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
        ChatPanel.setLayout(new GridLayout(2, 1));
        JPanel ChatTextPanel = new JPanel();
        ChatTextPanel.setLayout(new BorderLayout());
        JPanel ChatControlPanel = new JPanel();
        ChatControlPanel.setLayout(new GridLayout(2, 1));

        JLabel MessageLabel = new JLabel("Message:");
        JTextField MessageInput = new JTextField(18);
        JButton SendMessageButton = new JButton("Send");
        SendMessageButton.addActionListener(e -> {
            Controller.TriggerSendMessage(MessageInput.getText());
        });

        JPanel ChatSenderPanel = new JPanel();
        ChatSenderPanel.setLayout(new FlowLayout());
        ChatSenderPanel.add(MessageLabel);
        ChatSenderPanel.add(MessageInput);
        ChatSenderPanel.add(SendMessageButton);

        JPanel ChatConfigPanel = new JPanel();
        ChatConfigPanel.setLayout(new FlowLayout());

        JLabel IpLabel = new JLabel("IP:");
        ServerAddressField = new JTextField(12);
        JLabel PortLabel = new JLabel(":");
        ServerPortField = new JTextField(3);
        ChatConnectButton = new JButton("Join");
        ChatConnectButton.addActionListener(e -> {
            if (Controller.IsNetConnected()) {
                Controller.TriggerDisconnect();
            } else {
                Controller.TriggerConnect();
            }
        });
        ChatHostButton = new JButton("Host");
        ChatHostButton.addActionListener(e -> {
            Controller.TriggerHostServer();
        });

        ChatConfigPanel.add(IpLabel);
        ChatConfigPanel.add(ServerAddressField);
        ChatConfigPanel.add(PortLabel);
        ChatConfigPanel.add(ServerPortField);
        ChatConfigPanel.add(ChatConnectButton);
        ChatConfigPanel.add(ChatHostButton);

        ChatControlPanel.add(ChatSenderPanel);
        ChatControlPanel.add(ChatConfigPanel);


        ChatOutput = new JTextArea();
        ChatOutput.setEditable(false);
        JScrollPane ChatOutputScrollPane = new JScrollPane(ChatOutput);
        ChatOutputScrollPane.setAutoscrolls(true);
        ChatTextPanel.add(ChatOutputScrollPane, BorderLayout.CENTER);
        ChatPanel.add(ChatTextPanel);
        ChatPanel.add(ChatControlPanel);


        this.add(RotorsPanel);
        this.add(PlugboardPanel);
        this.add(EncodePanel);
        this.add(ChatPanel);

        //bind for actions:
        RotorsModifiedListener listener = new RotorsModifiedListener();
        FirstRotor.addActionListener(listener);
        SecondRotor.addActionListener(listener);
        ThirdRotor.addActionListener(listener);

    }

    /**
     * Setter for the text input mode.
     * @param InputMode Whether to use dropdown or text field.
     */
    public void SetInputMode(InputMode InputMode) {
        this.TextInputMode = InputMode;
    }


    /**
     * This method updates the UI and refreshes propagated data from the model's dataset.
     * @param enigma The enigma machine to get the data from.
     */
    public void UpdateUI(Enigma enigma) {
        List<RotorBase> rotorBaseList = enigma.GetRotors();
        FirstRotor.setSelectedIndex(rotorBaseList.get(0).GetCurrentRotorIndex());
        SecondRotor.setSelectedIndex(rotorBaseList.get(1).GetCurrentRotorIndex());
        ThirdRotor.setSelectedIndex(rotorBaseList.get(2).GetCurrentRotorIndex());

        EncodeResultLabel.setText(String.valueOf(Controller.getLastEncodedCharacter()));
        outputText.setText(Controller.getEncodedString());

        LetterSelector.setVisible(TextInputMode == InputMode.SingleChar);
        MessageInput.setVisible(TextInputMode == InputMode.TextField);
        EncodeButton.setVisible(TextInputMode == InputMode.SingleChar);

        PlugboardTable.setModel(Controller.getMachine().GetPlugboard());
        Controller.getMachine().GetPlugboard().fireTableDataChanged();

        ChatConnectButton.setText(Controller.IsNetConnected() ? (Controller.GetInstanceRole() == InstanceRole.Server ? "Terminate" : "Disconnect") : "Connect");
        ChatHostButton.setVisible(!Controller.IsNetConnected());
        ServerAddressField.setVisible(!Controller.IsNetConnected());
        ServerPortField.setVisible(!Controller.IsNetConnected());


    }

    /**
     * Inherited KeyAdapter for the custom text input field.
     */
    private class MessageInputAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (Character.isAlphabetic(e.getKeyChar())) {
                Controller.TriggerEncode(e.getKeyChar());
            }
            MessageInput.setText("");
        }
    }

    /**
     * Custom action listener for the rotor modification.
     */
    class RotorsModifiedListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource().equals(FirstRotor)) {
                Controller.TriggerModifyRotorConfig(0, FirstRotor.getSelectedIndex());
            } else if (ae.getSource().equals(SecondRotor)) {
                Controller.TriggerModifyRotorConfig(1, SecondRotor.getSelectedIndex());
            } else if (ae.getSource().equals(ThirdRotor)) {
                Controller.TriggerModifyRotorConfig(2, ThirdRotor.getSelectedIndex());
            }
        }
    }

}
