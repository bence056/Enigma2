package me.bvarga.enigma.network;

import me.bvarga.enigma.swing.EnigmaController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Network management class for multithreading p2p connections between instances.
 */
public class NetworkManager {

    /**
     * Output object for networked chat.
     */
    private PrintWriter ChatWrite;
    /**
     * Input object for networked chat.
     */
    private BufferedReader ChatRead;
    /**
     * Stored instance role.
     * @see InstanceRole
     */
    private InstanceRole instanceRole;

    /**
     * Controller reference.
     */
    private final EnigmaController controller;

    /**
     * Server side host socket.
     */
    private ServerSocket serverSocket;
    /**
     * Client side connection socket.
     */
    private Socket clientSocket;

    /**
     * Initializer for the network manager.
     * @param controller the controller from the MVC framework.
     */
    public NetworkManager(EnigmaController controller) {
        this.controller = controller;
    }

    /**
     * Method used to instantiate a client instance of the chat.
     * @param address IP address to connect to.
     * @param port port to connect to .
     */
    public void InitializeClient(String address, int port) {

        new Thread(() -> {
            try {
                clientSocket = new Socket(address, port);
                ChatWrite = new PrintWriter(clientSocket.getOutputStream(), true);
                ChatRead = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                instanceRole = InstanceRole.Client;
                controller.TriggerMessageReceived("Connected!");
                String msg;
                while ((msg = ChatRead.readLine()) != null) {
                    controller.TriggerMessageReceived(msg);
                }

            } catch (UnknownHostException e) {
                controller.TriggerMessageReceived("Host does not exist or can not be reached.");
            } catch (IOException e) {
                controller.TriggerMessageReceived("An Error Occurred.");
            }
        }).start();

    }

    /**
     * Method used to instantiate a server instance of the chat.
     * @param port port to host the server on.
     */
    public void InitializeServer(int port) {

        new Thread(() -> {

            try {
                serverSocket = new ServerSocket(port);
                instanceRole = InstanceRole.Server;
                controller.TriggerMessageReceived(String.format("[%s]: Server started. - Waiting for connection...", InetAddress.getLocalHost().getHostName()));

                clientSocket = serverSocket.accept();

                controller.TriggerMessageReceived(String.format("[%s]: Client connected.", InetAddress.getLocalHost().getHostName()));
                ChatWrite = new PrintWriter(clientSocket.getOutputStream(), true);
                ChatRead = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String msg;
                while (!clientSocket.isClosed() && (msg = ChatRead.readLine()) != null) {
                    controller.TriggerMessageReceived(msg);
                }

            } catch (IOException e) {
                controller.TriggerMessageReceived("An Error Occured.");
            }
        }).start();

    }

    /**
     * Method to send a message to the other endpoint.
     * @param msg The message
     */
    public synchronized void SendMessage(String msg) {
        if (bIsConnected() && ChatWrite != null) {
            ChatWrite.println(msg);
        }
    }

    /**
     * Information about the connection status. It is only used for UI purposes.
     * @return true if connected to endpoint or hosting, false otherwise.
     */
    public boolean bIsConnected() {

        if(instanceRole == InstanceRole.Client) {
            return (clientSocket != null && !clientSocket.isClosed());
        }else if (instanceRole == InstanceRole.Server) {
            return (serverSocket != null && !serverSocket.isClosed());
        }
        return false;
    }

    /**
     * Method to initiate disconnects.
     */
    public void DisconnectSocket() {
        controller.TriggerMessageReceived("Disconnected!");
        try {
            SendMessage(String.format("%s Disconnected!", InetAddress.getLocalHost().getHostName()));
        } catch (UnknownHostException e) {
            System.out.println("Local hostname could not be determined");
        }
        try {
            if(instanceRole == InstanceRole.Client) {
                clientSocket.close();
            }else if(instanceRole == InstanceRole.Server) {
                serverSocket.close();
            }
        }catch (IOException e) {
            System.out.println("Error closing socket!");
        }
        controller.TriggerDisconnectConfirmed();
    }

    /**
     * Getter for instanceRole
     * @return instanceRole
     */
    public InstanceRole getInstanceRole() {
        return instanceRole;
    }

}
