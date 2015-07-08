package common;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface ServerInterface extends Remote {

    public static final int REQUEST_SENT_SUCCESSFULLY = 0;
    public static final int REQUEST_ALREADY_SENT = 1;
    public static final int CONTACT_ALREADY_ADDED = 2;
    public static final int CONTACT_DOES_NOT_EXIST = 3;

    /**
     * Checks Server availability.
     *
     * @throws RemoteException
     */
    public void ping() throws RemoteException;

    /**
     * Returns the data of the contact if the user name and password exist in
     * the database, and null otherwise. And adds the contact to the online list
     * and informs his/her friends.
     *
     * @param client the client implementation
     * @param userName the user name of the contact
     * @param password the password of the contact
     * @return the contact matching the user name and password
     * @throws RemoteException
     */
    public Contact signIn(ClientInterface client, String userName, String password) throws RemoteException;

    /**
     * Removes the user from the online list, saves his/her last status in the
     * database and informs his/her friends.
     *
     * @param userName the user name of the contact
     * @throws RemoteException
     */
    public void signOut(String userName) throws RemoteException;

    // Add client interface here
    /**
     * Adds a new contact in the database.
     *
     * @param client the client implementation
     * @param contact the contact to be added
     * @return true if the contact is added successfully and false if the user
     * name of the contact already exists
     * @throws RemoteException
     */
    public boolean signUp(ClientInterface client, Contact contact) throws RemoteException;

    /**
     * Sets the current status of the contact and informs his/her online
     * friends.
     *
     * @param userName the user name of the contact
     * @param status the current status of the contact
     * <ul>
     * <li>AVAILABLE</li>
     * <li>AWAY</li>
     * <li>BUSY</li>
     * <li>INVISIBLE</li>
     * </ul>
     * @throws RemoteException
     */
    public void setStatus(String userName, int status) throws RemoteException;

    /**
     * Sends an add request to the contact and returns an int representing the
     * operation. If the contact exists in the database and not added in the
     * user's list of friends, an add request is added to both the contacts'
     * request list. If the contact is online, the request is sent to him/her.
     * If the contact exists in the user's request list, the contact is added
     * directly to the user's friends and a group is created.
     *
     * @param sender the user name of the sender
     * @param receiver the user name of the receiver
     * @return an integer representing the add operation result
     * <ul>
     * <li>REQUEST_SENT_SUCCESSFULLY</li>
     * <li>REQUEST_ALREADY_SENT</li>
     * <li>CONTACT_ALREADY_ADDED</li>
     * <li>CONTACT_DOES_NOT_EXIST</li>
     * </ul>
     * @throws RemoteException
     */
    public int sendRequest(String sender, String receiver) throws RemoteException;

    public Contact confirmRequest(String sender, String receiver) throws RemoteException;

    public void cancelRequest(String sender, String receiver) throws RemoteException;

    /**
     * check if the group containing theses contacts already exists
     *
     * @param creator the user name of the group creator
     * @param contacts the contacts of the group
     * @return the ID of the created group
     * @throws RemoteException
     */
    public Group createGroup(String creator, Vector<String> contacts) throws RemoteException;

    /**
     * Sends a message to a contact.
     *
     * @param sender the user name of the sender
     * @param receiver the user name of the receiver
     * @param message the message to send to the receiver
     * @param font the font of the message
     * @param color the color of the message
     * @throws RemoteException
     */
    public void sendMessage(String sender, String receiver, String message, Font font, Color color) throws RemoteException;

    /**
     * Sends a message to all the online contacts in a group.
     *
     * @param sender the user name of the sender
     * @param groupID the ID of the group the message is sent to
     * @param message the message to send to the group
     * @param font the font of the message
     * @param color the color of the message
     * @throws RemoteException
     */
    public void sendMessage(String sender, Group group, String message, Font font, Color color) throws RemoteException;

    /**
     * Sends a message to a contact.
     *
     * @param sender the user name of the sender
     * @param receiver the user name of the receiver
     * @param file the name of the file
     * @param fileContent the data of the file
     * @throws RemoteException
     */
    public void sendFile(String sender, String receiver, File file, byte[] fileContent) throws RemoteException;

    /**
     * Sends a file to all the online contacts in a group.
     *
     * @param sender the user name of the sender
     * @param groupID the ID of the group the file is sent to
     * @param file the name of the file
     * @param fileContent the data of the file
     * @throws RemoteException
     */
    public void sendFile(String sender, int groupID, File file, byte[] fileContent) throws RemoteException;

}
