package javaprojectserver;

import common.ClientInterface;
import common.Contact;
import common.Group;
import common.ServerInterface;
import java.rmi.*;
import java.rmi.server.*;
import controller.Controller;
import java.util.Vector;
import java.util.HashMap;
import database.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ServerImp extends UnicastRemoteObject implements ServerInterface {

    // Constants
    private static final int CLIENTS_CHECK_TIME_SECONDS = 5;
    private static final int OFFLINE_CLIENTS_REMOVE_TIME_SECONDS = 1;

    private static final BufferedImage[] ADVERTISEMENTS;

    private int advertisementsCounter;

    private Controller controller;

    private DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandler();

    private volatile HashMap<String, ClientInterface> clients = new HashMap<String, ClientInterface>();

    private volatile HashMap<Integer, Group> groups = new HashMap<Integer, Group>();

    private volatile Vector<String> offlineContacts = new Vector<String>();

    private int groupsCounter;

    private int total, online;
    private int online_temp;

    // Static Intializer
    static {
        File[] files = new File("Images\\Ads\\").listFiles();
        ADVERTISEMENTS = new BufferedImage[files.length];
        for (int i = 0; i < ADVERTISEMENTS.length; i++) {
            try {
                ADVERTISEMENTS[i] = ImageIO.read(files[i]);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Constructors
    public ServerImp(Controller controller) throws RemoteException {
        this.controller = controller;
        total = databaseHandler.getContactsCount();
        controller.updateView(total, online);
        checkClients();
    }

    @Override
    public void ping() throws RemoteException {
    }

    @Override
    public Contact signIn(ClientInterface client, String userName, String password) throws RemoteException {
        // Validate user name & password
        if (databaseHandler.validateContact(userName, password) != DatabaseHandler.VALID_CONTACT) {
            return null;
        }

        // If the user is already signed in, sign him/her out
        ClientInterface clientInterface = clients.get(userName);
        if (clientInterface != null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        clientInterface.stopService();
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }.start();
            signOut(userName);
        }

        // Return the data of the contact from the database
        Contact contact = databaseHandler.getContact(userName);
        setStatus(userName, contact.getStatus().ordinal());
        clients.put(userName, client);
        controller.updateView(total, online);

        // Set status with (offline) to all the contact's offline friends
        for (Contact friend : contact.getFriends()) {
            if (!clients.containsKey(friend.getUserName())) {
                friend.setStatus(Contact.Status.OFFLINE);
            }
        }

        return contact;
    }

    @Override
    public synchronized void signOut(String userName) throws RemoteException {
        clients.remove(userName);
        controller.updateView(total, online);
        sendNotifications(userName, Contact.Status.OFFLINE.ordinal());
    }

    @Override
    public boolean signUp(ClientInterface client, Contact contact) throws RemoteException {
        // Check whether the user name of the contact exists in the database
        if (databaseHandler.addContact(contact)) {
            clients.put(contact.getUserName(), client);
            total++;
            controller.updateView(total, online);
            return true;
        }
        return false;
    }

    @Override
    public void setStatus(String userName, int status) throws RemoteException {
        databaseHandler.updateContactStatus(userName, status);
        sendNotifications(userName, status);
    }

    // Send Notifications
    private void sendNotifications(String userName, int status) {
        // Inform contact's online friends of his/her new status
        for (Contact contact : databaseHandler.getFriends(userName)) {
            new Thread() {
                @Override
                public void run() {
                    ClientInterface clientInterface = clients.get(contact.getUserName());
                    // Check whether the contact is online or offline
                    if (clientInterface != null) {
                        try {
                            clientInterface.statusChanged(userName, status);
                        } catch (RemoteException ex) {
                            // If the contact is offline, put him/her in the offline queue
                            if (!offlineContacts.contains(contact.getUserName())) {
                                offlineContacts.add(contact.getUserName());
                            }
                        }
                    }
                }
            }.start();
        }
    }

    @Override
    public int sendRequest(String sender, String receiver) throws RemoteException {
        int response = databaseHandler.addRequest(sender, receiver);
        if (response == DatabaseHandler.REQUEST_SENT_SUCCESSFULLY) {
            ClientInterface clientInterface = clients.get(receiver);
            // Check whether the contact is online or offline
            if (clientInterface != null) {
                try {
                    clientInterface.receiveRequest(databaseHandler.getContact(sender));
                } catch (RemoteException ex) {
                    // If the contact is offline, put him/her in the offline queue
                    if (!offlineContacts.contains(receiver)) {
                        offlineContacts.add(receiver);
                    }
                }
            }
        }
        return response;
    }

    @Override
    public Contact confirmRequest(String sender, String receiver) throws RemoteException {
        int response = databaseHandler.confirmRequest(sender, receiver);
        if (response == DatabaseHandler.CONTACT_ADDED_SUCCESSFULLY) {
            ClientInterface clientInterface = clients.get(receiver);
            // Check whether the contact is online or offline
            if (clientInterface != null) {
                try {
                    clientInterface.requestConfirmed(databaseHandler.getContact(sender));
                } catch (RemoteException ex) {
                    // If the contact is offline, put him/her in the offline queue
                    if (!offlineContacts.contains(receiver)) {
                        offlineContacts.add(receiver);
                    }
                }
            }
            return databaseHandler.getContact(receiver);
        }
        return null;
//        return response;
    }

    @Override
    public void cancelRequest(String sender, String receiver) throws RemoteException {
        databaseHandler.removeRequest(sender, receiver);
    }

//    @Override
//    public Group createGroup(String creator, Vector<String> contacts) throws RemoteException {
//        int groupID = databaseHandler.addGroup(creator, contacts);
//        Group group = databaseHandler.getGroup(groupID);
//        for (String contact : contacts) {
//            ClientInterface clientInterface = clients.get(contact);
//            // Check whether the contact is online or offline
//            if (clientInterface != null) {
//                try {
//                    clientInterface.addToGroup(group);
//                } catch (RemoteException ex) {
//                    // If the contact is offline, put him/her in the offline queue
//                    if (!offlineContacts.contains(contact)) {
//                        offlineContacts.add(contact);
//                    }
//                }
//            }
//        }
//        return group;
//    }
    @Override
    public Group createGroup(String creator, Vector<String> contacts) throws RemoteException {
        Group group = new Group(++groupsCounter, databaseHandler.getContact(creator));

        // Add contacts to the group
        for (String contact : contacts) {
            group.addContact(databaseHandler.getContact(contact));
        }

        contacts.add(creator);
        for (String contact : contacts) {
            new Thread() {
                @Override
                public void run() {
                    ClientInterface clientInterface = clients.get(contact);
                    // Check whether the contact is online or offline
                    if (clientInterface != null) {
                        try {
                            clientInterface.addToGroup(group);
                        } catch (RemoteException ex) {
                            // If the contact is offline, put him/her in the offline queue
                            if (!offlineContacts.contains(contact)) {
                                offlineContacts.add(contact);
                            }
                        }
                    }
                }
            }.start();
        }

        groups.put(groupsCounter, group);
        return group;
    }

//    @Override
//    public void sendMessage(String sender, int groupID, String message, Font font, Color color) throws RemoteException {
//        // Send the message to all the group members including the sender
//
//        for (Contact contact : databaseHandler.getContacts(groupID)) {
//            ClientInterface clientInterface = clients.get(contact.getUserName());
//            // Check whether the contact is online or offline
//            if (clientInterface != null) {
//                try {
//                    clientInterface.receiveMessage(sender, groupID, message, font, color);
//                } catch (RemoteException ex) {
//                    // If the contact is offline, put him/her in the offline queue
//                    if (!offlineContacts.contains(contact.getUserName())) {
//                        offlineContacts.add(contact.getUserName());
//                    }
//                }
//            }
//        }
//    }
    @Override
    public void sendMessage(String sender, Group group, String message, Font font, Color color) throws RemoteException {
//        Group group = groups.get(groupID);
        if (group != null) {
            // Send the message to all the group members including the sender
            for (Contact contact : group.getContacts()) {
                new Thread() {
                    @Override
                    public void run() {
                        ClientInterface clientInterface = clients.get(contact.getUserName());
                        // Check whether the contact is online or offline
                        if (clientInterface != null) {
                            try {
                                clientInterface.receiveMessage(sender, group, message, font, color);
                            } catch (RemoteException ex) {
                                // If the contact is offline, put him/her in the offline queue
                                if (!offlineContacts.contains(contact.getUserName())) {
                                    offlineContacts.add(contact.getUserName());
                                }
                                group.removeContact(contact);
                            }
                        }
                    }
                }.start();
            }
        }
    }

    @Override
    public void sendMessage(String sender, String receiver, String message, Font font, Color color) throws RemoteException {
        // Send the message to both the sender & the receiver

        ClientInterface clientInterface = clients.get(sender);
        // Check whether the contact is online or offline
        if (clientInterface != null) {
            try {
                clientInterface.receiveMessage(sender, message, font, color);
            } catch (RemoteException ex) {
                // If the contact is offline, put him/her in the offline queue
                if (!offlineContacts.contains(sender)) {
                    offlineContacts.add(sender);
                }
            }
        }

        clientInterface = clients.get(receiver);
        // Check whether the contact is online or offline
        if (clientInterface != null) {
            try {
                clientInterface.receiveMessage(sender, message, font, color);
            } catch (RemoteException ex) {
                // If the contact is offline, put him/her in the offline queue
                if (!offlineContacts.contains(receiver)) {
                    offlineContacts.add(receiver);
                }
            }
        }
    }

    @Override
    public void sendFile(String sender, String receiver, File file, byte[] fileContent) throws RemoteException {
        ClientInterface clientInterface = clients.get(receiver);
        // Check whether the contact is online or offline
        if (clientInterface != null) {
            try {
                clientInterface.receiveFile(sender, file, fileContent);
            } catch (RemoteException ex) {
                // If the contact is offline, put him/her in the offline queue
                if (!offlineContacts.contains(receiver)) {
                    offlineContacts.add(receiver);
                }
            }
        }
    }

    @Override
    public void sendFile(String sender, int groupID, File file, byte[] fileContent) throws RemoteException {
        for (Contact contact : databaseHandler.getContacts(groupID)) {
            new Thread() {
                @Override
                public void run() {
                    // Check if the contact is online (exists in the Hash Map)
                    ClientInterface clientInterface = clients.get(contact.getUserName());
                    // Check whether the contact is online or offline
                    if (clientInterface != null) {
                        try {
                            clientInterface.receiveFile(sender, groupID, file, fileContent);
                        } catch (RemoteException ex) {
                            // If the contact is offline, put him/her in the offline queue
                            if (!offlineContacts.contains(contact.getUserName())) {
                                offlineContacts.add(contact.getUserName());
                            }
                        }
                    }
                }
            }.start();
        }
    }

    // Send System Message
    public void sendSystemMessage(String message) {
        new Thread() {
            @Override
            public void run() {
                for (String contact : clients.keySet()) {
                    new Thread() {
                        @Override
                        public void run() {
                            ClientInterface clientInterface = clients.get(contact);
                            // Check whether the contact is online or offline
                            if (clientInterface != null) {
                                try {
                                    clientInterface.receiveSystemMessage(message);
                                } catch (RemoteException ex) {
                                    // If the contact is offline, put him/her in the offline queue
                                    if (!offlineContacts.contains(contact)) {
                                        offlineContacts.add(contact);
                                    }
                                }
                            }
                        }
                    }.start();
                }
            }
        }.start();
    }

    // Send Advertisement
    public void sendAdvertisement(String text) {
        new Thread() {
            @Override
            public void run() {
                // Convert the advertisement image to array of integers
                BufferedImage image = ADVERTISEMENTS[advertisementsCounter];
                int width = image.getWidth();
                int height = image.getHeight();
                int[] imageBytes = image.getRGB(0, 0, width, height, null, 0, width);

                for (String contact : clients.keySet()) {
                    new Thread() {
                        @Override
                        public void run() {
                            ClientInterface clientInterface = clients.get(contact);
                            // Check whether the contact is online or offline
                            if (clientInterface != null) {
                                try {
                                    clientInterface.receiveAdvertisement(imageBytes, width, height);
                                } catch (RemoteException ex) {
                                    // If the contact is offline, put him/her in the offline queue
                                    if (!offlineContacts.contains(contact)) {
                                        offlineContacts.add(contact);
                                    }
                                }
                            }
                        }
                    }.start();
                }

                advertisementsCounter = (advertisementsCounter + 1) % ADVERTISEMENTS.length;
            }
        }.start();
    }

    // Stop Service
    public void stopService() {
        new Thread() {
            @Override
            public void run() {
                try {
                    for (String contact : clients.keySet()) {
                        new Thread() {
                            @Override
                            public void run() {
                                ClientInterface clientInterface = clients.get(contact);
                                // Check whether the contact is online or offline
                                if (clientInterface != null) {
                                    try {
                                        clientInterface.stopService();
                                    } catch (RemoteException ex) {
                                        // If the contact is offline, put him/her in the offline queue
                                        if (!offlineContacts.contains(contact)) {
                                            offlineContacts.add(contact);
                                        }
                                    }
                                }
                            }
                        }.start();
                    }
                } catch (ConcurrentModificationException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    // Start Service
    public void startService() {
        clients = new HashMap<>();
    }

    // Check Clients
    private void checkClients() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    // Sleep for 5 seconds
                    try {
                        Thread.sleep(CLIENTS_CHECK_TIME_SECONDS * 1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    online_temp = 0;
                    for (String contact : clients.keySet()) {
                        ClientInterface clientInterface = clients.get(contact);
                        // Check whether the contact is online or offline
                        if (clientInterface != null) {
                            try {
                                clientInterface.ping();
                                online_temp++;
                            } catch (RemoteException ex) {
                                // If the contact is offline, put him/her in the offline queue
                                if (!offlineContacts.contains(contact)) {
                                    offlineContacts.add(contact);
                                }
                            }
                        }
                    }
                    online = online_temp;
                    controller.updateView(total, online);

                    while (!offlineContacts.isEmpty()) {
                        try {
                            signOut(offlineContacts.firstElement());
                            offlineContacts.remove(0);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

}
