package javaprojectserver;

import common.ClientInterface;
import common.Contact;
import common.Group;
import common.ServerInterface;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.Vector;
import javax.imageio.ImageIO;

class ServerTester extends UnicastRemoteObject implements ClientInterface {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 1025;
    private static final String SERVICE_NAME = "Chat Service";

    private static ServerInterface serverInterface;

    private final Contact contact;

    public ServerTester(Contact contact) throws RemoteException {
        this.contact = contact;
//        serverInterface.signUp(this, contact);
        serverInterface.signIn(this, contact.getUserName(), "password");
    }

    @Override
    public void ping() throws RemoteException {}

    @Override
    public void receiveMessage(String sender, String message, Font font, Color color) throws RemoteException {
        System.out.println("Receiver: " + contact.getUserName());
        System.out.println(sender + ": " + message);
        System.out.println();
    }

    @Override
    public void receiveMessage(String sender, Group group, String message, Font font, Color color) throws RemoteException {
        System.out.println("Receiver: " + contact.getUserName());
        System.out.println(sender + ": " + message);
        System.out.println();
    }

    @Override
    public void receiveFile(String sender, File file, byte[] fileContent) throws RemoteException {
    }

    @Override
    public void receiveFile(String sender, int groupID, File file, byte[] fileContent) throws RemoteException {
    }

    @Override
    public void statusChanged(String userName, int status) throws RemoteException {
        System.out.println("Receiver: " + contact.getUserName());
        System.out.println(userName + " changed status: " + Contact.Status.values()[status]);
        System.out.println();
    }

    @Override
    public void receiveRequest(Contact sender) throws RemoteException {
        contact.addReceivedRequest(new Contact(sender));
        System.out.println("Receiver: " + contact.getUserName());
        System.out.println(sender + " sent a friend request");
        System.out.println();
    }

    @Override
    public void requestConfirmed(Contact sender) throws RemoteException {
        contact.addFriend(sender);
        System.out.println("Receiver: " + contact.getUserName());
        System.out.println(sender + " confirmed the friend request");
        System.out.println();
    }

    @Override
    public void addToGroup(Group group) throws RemoteException {
        contact.addGroup(group);
        System.out.println("Receiver: " + contact.getUserName());
        System.out.println("You were added to group " + group.getId());
        System.out.println();
    }

    @Override
    public void receiveSystemMessage(String systemMessage) throws RemoteException {
        System.out.println("Receiver: " + contact.getUserName());
        System.out.println("Server Message: " + systemMessage);
        System.out.println();
    }

    @Override
    public void receiveAdvertisement(int[] image, int width, int height) throws RemoteException {
        BufferedImage advertisement = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        advertisement.setRGB(0, 0, width, height, image, 0, width);
        try {
            ImageIO.write(advertisement, "png", new File("C:\\users\\marco\\desktop\\ad.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stopService() throws RemoteException {
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER_IP, SERVER_PORT);
            serverInterface = (ServerInterface) registry.lookup(SERVICE_NAME);
        } catch (RemoteException | NotBoundException ex) {
            ex.printStackTrace();
        }

        Contact contact1 = new Contact("Medhat");
        contact1.setPassword("password");
        contact1.setFullName("Ahmed Medhat");
        contact1.setGender(Contact.Gender.MALE);
        contact1.setBirthDate(LocalDate.of(1988, 3, 10));
        contact1.setStatus(Contact.Status.AVAILABLE);
        try {
            contact1.setPhoto(ImageIO.read(new File("C:\\users\\marco\\desktop\\0.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Contact contact2 = new Contact("Mazen");
        contact2.setPassword("password");
        contact2.setFullName("Ahmed Mazen");
        contact2.setGender(Contact.Gender.MALE);
        contact2.setBirthDate(LocalDate.of(1985, 7, 15));
        contact2.setStatus(Contact.Status.AWAY);
        try {
            contact2.setPhoto(ImageIO.read(new File("C:\\users\\marco\\desktop\\1.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Contact contact3 = new Contact("Gabr");
        contact3.setPassword("password");
        contact3.setFullName("Ahmed Gabr");
        contact3.setGender(Contact.Gender.MALE);
        contact3.setBirthDate(LocalDate.of(1987, 5, 20));
        contact3.setStatus(Contact.Status.BUSY);
        try {
            contact3.setPhoto(ImageIO.read(new File("C:\\users\\marco\\desktop\\2.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Contact contact4 = new Contact("Said");
        contact4.setPassword("password");
        contact4.setFullName("Mohammed Said");
        contact4.setGender(Contact.Gender.MALE);
        contact4.setBirthDate(LocalDate.of(1986, 10, 5));
        contact4.setStatus(Contact.Status.OFFLINE);
        try {
            contact4.setPhoto(ImageIO.read(new File("C:\\users\\marco\\desktop\\3.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Contact contact5 = new Contact("Mohsen");
        contact5.setPassword("password");
        contact5.setFullName("Mohsen Diab");
        contact5.setGender(Contact.Gender.MALE);
        contact5.setBirthDate(LocalDate.of(1988, 1, 25));
        contact5.setStatus(Contact.Status.AVAILABLE);
        try {
            contact5.setPhoto(ImageIO.read(new File("C:\\users\\marco\\desktop\\4.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Contact contact6 = new Contact("Mostafa");
        contact6.setPassword("password");
        contact6.setFullName("Mostafa Magdy");
        contact6.setGender(Contact.Gender.MALE);
        contact6.setBirthDate(LocalDate.of(1989, 9, 15));
        contact6.setStatus(Contact.Status.AWAY);
        try {
            contact6.setPhoto(ImageIO.read(new File("C:\\users\\marco\\desktop\\5.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Contact contact7 = new Contact("Marco");
        contact7.setPassword("password");
        contact7.setFullName("Marco Ghaly");
        contact7.setGender(Contact.Gender.MALE);
        contact7.setBirthDate(LocalDate.of(1991, 5, 12));
        contact7.setStatus(Contact.Status.AWAY);
        try {
            contact7.setPhoto(ImageIO.read(new File("C:\\users\\marco\\desktop\\6.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            new ServerTester(contact1);
            new ServerTester(contact2);
            new ServerTester(contact3);
            new ServerTester(contact4);
            new ServerTester(contact5);
            new ServerTester(contact6);
            new ServerTester(contact7);

            serverInterface.sendRequest("medhat", "gabr");
            serverInterface.sendRequest("medhat", "mohsen");

            serverInterface.sendRequest("mazen", "mostafa");
            serverInterface.sendRequest("mazen", "said");

            serverInterface.sendRequest("gabr", "mazen");
            serverInterface.sendRequest("gabr", "mostafa");

            serverInterface.sendRequest("said", "medhat");
            serverInterface.sendRequest("said", "gabr");

            serverInterface.sendRequest("mohsen", "said");
            serverInterface.sendRequest("mohsen", "mazen");

            serverInterface.sendRequest("mostafa", "said");
            serverInterface.sendRequest("mostafa", "medhat");

            serverInterface.sendRequest("medhat", "ali");

            System.out.println("------------------------------------------------------------------------------------------");

            serverInterface.confirmRequest("mazen", "medhat");
            serverInterface.confirmRequest("mohsen", "medhat");

            serverInterface.confirmRequest("gabr", "mazen");
            serverInterface.confirmRequest("said", "mazen");

            serverInterface.confirmRequest("mohsen", "gabr");
            serverInterface.confirmRequest("mazen", "gabr");

            serverInterface.confirmRequest("medhat", "said");
            serverInterface.confirmRequest("gabr", "said");

            serverInterface.confirmRequest("said", "mohsen");
            serverInterface.confirmRequest("medhat", "mohsen");
            serverInterface.confirmRequest("mazen", "mohsen");

            serverInterface.confirmRequest("mohsen", "mostafa");
            serverInterface.confirmRequest("said", "mostafa");

            System.out.println("------------------------------------------------------------------------------------------");

            Vector<String> all = new Vector<String>();
            all.add("mazen");
            all.add("gabr");
            all.add("said");
            all.add("mohsen");
            all.add("mostafa");
            contact1.addGroup(serverInterface.createGroup("medhat", all));

            Vector<String> web = new Vector<String>();
            web.add("gabr");
            web.add("mohsen");
            contact1.addGroup(serverInterface.createGroup("medhat", web));

            Vector<String> mobile = new Vector<String>();
            mobile.add("said");
            mobile.add("mostafa");
            contact2.addGroup(serverInterface.createGroup("mazen", mobile));

            System.out.println("------------------------------------------------------------------------------------------");

            serverInterface.setStatus("medhat", Contact.Status.AWAY.ordinal());

            serverInterface.setStatus("mohsen", Contact.Status.BUSY.ordinal());

            serverInterface.setStatus("mostafa", Contact.Status.OFFLINE.ordinal());

            System.out.println("------------------------------------------------------------------------------------------");

            Font font = new Font(Font.DIALOG, Font.PLAIN, 12);
            Color color = Color.BLACK;

            serverInterface.sendMessage("said", "mazen", "Message 1", font, color);
            serverInterface.sendMessage("gabr", "medhat", "Message 2", font, color);
            serverInterface.sendMessage("mohsen", "mostafa", "Message 3", font, color);

            System.out.println("------------------------------------------------------------------------------------------");

            serverInterface.sendMessage("medhat", contact1.getGroups().elementAt(0), "Hello Jets", font, color);

            System.out.println();

            serverInterface.sendMessage("mostafa", contact6.getGroups().elementAt(1), "Hello Mobile", font, color);

            System.out.println();

            serverInterface.sendMessage("gabr", contact3.getGroups().elementAt(1), "Hello Web", font, color);

        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

}
