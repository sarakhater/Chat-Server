package controller;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import javaprojectserver.*;

import java.rmi.registry.*;
import java.rmi.RemoteException;
import view.ServerFrame;

public class Controller {

    private static final int PORT = 1025;
    private static final String SERVICE_NAME = "Chat Service";

    private ServerFrame serverFrame = new ServerFrame(this);
    private ServerImp serverImp;

    private Registry registry;

    public Controller() {
        try {
            registry = LocateRegistry.createRegistry(PORT);
            serverImp = new ServerImp(this);

            serverFrame.setVisible(true);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    // Start
    public void start() throws RemoteException {
        registry.rebind(SERVICE_NAME, serverImp);
        serverImp.startService();
        serverFrame.setStarted(true);
    }

    // Stop
    public void stop() throws RemoteException, AccessException, NotBoundException {
        registry.unbind(SERVICE_NAME);
        serverImp.stopService();
        serverFrame.setStarted(false);
    }
    
    // Send System Message
    public void sendSystemMessage(String message) {
        serverImp.sendSystemMessage(message);
    }
    
    // Send Advertisement
    public void sendAdvertisement(String text) {
        serverImp.sendAdvertisement(text);
    }

    // Update View
    public void updateView(int total, int online) {
        serverFrame.updateInfo(total, online);
    }

    // Main Methid
    public static void main(String[] args) {
        new Controller();
    }

}
