package common;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    
    /**
     * Checks client availability.
     * @throws RemoteException 
     */
    public void ping() throws RemoteException;

    /**
     * Prints the message with the contact name on the chat screen of the chat.
     *
     * @param sender the user name of the sender
     * @param message the message to send to the group
     * @param font the font of the message
     * @param color the color of the message
     * @throws RemoteException
     */
    public void receiveMessage(String sender, String message, Font font, Color color) throws RemoteException;

    /**
     * Prints the message with the contact name on the chat screen of the group.
     *
     * @param sender the user name of the sender
     * @param groupID the ID of the group the message is sent to
     * @param message the message to send to the group
     * @param font the font of the message
     * @param color the color of the message
     * @throws RemoteException
     */
    public void receiveMessage(String sender, Group group, String message, Font font, Color color) throws RemoteException;

    /**
     * Shows a file download message on the chat screen of the chat.
     *
     * @param sender the user name of the sender
     * @param file the name of the file
     * @param fileContent the data of the file
     * @throws RemoteException
     */
    public void receiveFile(String sender, File file, byte[] fileContent) throws RemoteException;

    /**
     * Shows a file download message on the chat screen of the group chat.
     *
     * @param sender the user name of the sender
     * @param groupID the ID of the group the file is sent to
     * @param file the file info
     * @param fileContent the data of the file
     * @throws RemoteException
     */
    public void receiveFile(String sender, int groupID, File file, byte[] fileContent) throws RemoteException;

    /**
     * Changes the status of the user in the list.
     *
     * @param userName the name of the contact who changed his/her status
     * @param status the current status of the contact
     * @throws RemoteException
     */
    public void statusChanged(String userName, int status) throws RemoteException;

    /**
     * Adds the contact to the user's request list.
     *
     * @param sender the name of the contact who sent the request
     * @throws RemoteException
     */
    public void receiveRequest(Contact sender) throws RemoteException;

    /**
     * Removes the contact from the request list and adds him/her to the friends
     * list.
     *
     * @param sender the contact who confirmed the request
     * @throws RemoteException
     */
    public void requestConfirmed(Contact sender) throws RemoteException;

    /**
     * Adds the contact to a group.
     * @param group the group the contact is added to
     * @throws RemoteException 
     */
    public void addToGroup(Group group) throws RemoteException;

    /**
     * Shows a message to the contact.
     * @param systemMessage the message to be displayed
     * @throws RemoteException 
     */
    public void receiveSystemMessage(String systemMessage) throws RemoteException;
    
    /**
     * Shows an advertisement in the advertisement section.
     *
     * @param adContent the content of the advertisement
     * @throws RemoteException
     */
    public void receiveAdvertisement(int[] image, int width, int height) throws RemoteException;

    /**
     * Stops the service and logs out.
     *
     * @throws RemoteException
     */
    public void stopService() throws RemoteException;

}
