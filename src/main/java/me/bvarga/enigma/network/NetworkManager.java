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

public class NetworkManager {

    private PrintWriter ChatWrite;
    private BufferedReader ChatRead;
    private InstanceRole instanceRole;

    private final EnigmaController controller;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    public NetworkManager(EnigmaController controller) {
        this.controller = controller;
    }


    public void InitializeClient(String address, int port) {

        new Thread(new Runnable() {
            public void run() {
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
            }
        }).start();

    }

    public void InitializeServer(int port) {

        new Thread(new Runnable() {
            public void run() {

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
            }
        }).start();

    }

    public synchronized void SendMessage(String msg) {
        if (bIsConnected() && ChatWrite != null) {
            ChatWrite.println(msg);
        }
    }

    public boolean bIsConnected() {

        if(instanceRole == InstanceRole.Client) {
            return (clientSocket != null && !clientSocket.isClosed());
        }else if (instanceRole == InstanceRole.Server) {
            return (serverSocket != null && !serverSocket.isClosed());
        }
        return false;
    }

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

    public InstanceRole getInstanceRole() {
        return instanceRole;
    }

}
