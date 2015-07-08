package database;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Vector;

import javax.imageio.ImageIO;

import oracle.jdbc.driver.OracleDriver;
import common.Contact;
import common.Contact.Gender;
import common.Contact.Status;
import common.Group;

public class DatabaseHandler {

    // Tables Names
    private static final String DUAL = "DUAL";
    private static final String CONTACTS = "CONTACTS";
    private static final String REQUESTS = "REQUESTS";
    private static final String FRIENDS = "FRIENDS";
    private static final String BLOCKS = "BLOCKS";
    private static final String GROUPS = "GROUPS";
    private static final String GROUPS_CONTACTS = "GROUPS_CONTACTS";

    // Sequences Names
    private static final String GROUPS_SEQUENCE = "SEQUENCE_GROUPS";

    // Columns Names
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String FULLNAME = "FULLNAME";
    private static final String GENDER = "GENDER";
    private static final String BIRTH_DATE = "BIRTHDATE";
    private static final String JOIN_DATE = "JOINDATE";
    private static final String STATUS = "STATUS";

    private static final String SENDER = "SENDER";
    private static final String RECEIVER = "RECEIVER";

    private static final String CONTACT = "CONTACT";
    private static final String CREATOR = "CREATOR";

    private static final String REQUEST_DATE = "REQUESTDATE";
    private static final String CONFIRM_DATE = "CONFIRMDATE";
    private static final String CREATION_DATE = "CREATIONDATE";

    private static final String ID = "ID";
    private static final String GROUP_ID = "GROUPID";

    // Constants
    private static final String IMAGES_DIRECTORY = System.getProperty("user.home") + "\\Java Chat\\Contacts\\Images\\";

    private static final String DATE_FORMAT = "YYYY-MM-DD";

    public static final int VALID_CONTACT = 0;
    public static final int CONTACT_ADDED_SUCCESSFULLY = 0;
    public static final int REQUEST_SENT_SUCCESSFULLY = 0;
    public static final int REQUEST_ALREADY_SENT = 1;
    public static final int NO_REQUEST_SENT = 1;
    public static final int CONTACT_ALREADY_ADDED = 2;
    public static final int CONTACT_DOES_NOT_EXIST = 3;
    public static final int CAN_NOT_ADD_YOURSELF = 4;
    public static final int INCORRECT_PASSWORD = 4;

    private static final int RECEIVERS = 0;
    private static final int SENDERS = 1;

    // Database Handler
    private static final DatabaseHandler databaseHandler = new DatabaseHandler();

    // Database Utilities
    private final DatabaseUtilities databaseUtilities = DatabaseUtilities.getDatabaseUtilities();

    // Static Initializer
    static {
        File imagesDirectory = new File(IMAGES_DIRECTORY);
        if (!imagesDirectory.exists()) {
            imagesDirectory.mkdirs();
        }
    }

    // Get the Database Handler instance
    public static DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }

    /**
     * Constructs the Database connection
     */
    private DatabaseHandler() {
//            Statement statement = connection.createStatement();
//            statement.executeQuery("DELETE FROM " + GROUPS_CONTACTS);
//            statement.executeQuery("DELETE FROM " + GROUPS);
//            statement.executeQuery("DELETE FROM " + REQUESTS);
//            statement.executeQuery("DELETE FROM " + FRIENDS);
//            statement.executeQuery("DELETE FROM " + CONTACTS);
//            statement.executeQuery("COMMIT");
    }

    /**
     * Returns the number of all the registered accounts in the system whether
     * they are online or offline.
     *
     * @return the number of all the registered accounts in the system
     */
    public int getContactsCount() {
        String[] columns = {"COUNT(*) AS COUNT"};
        int count = 0;
        try {
            ResultSet resultSet = databaseUtilities.select(CONTACTS, columns);
            resultSet.next();
            count = resultSet.getInt("COUNT");
            resultSet.close();
            resultSet.getStatement().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Returns whether the user name exists in the DB.
     *
     * @param userName the user name of the contact
     * @return true if the user name exists in the DB and false otherwise
     */
    public boolean contactExists(String userName) {
        userName = userName.toLowerCase();
        String columns[] = {"0"};

        try {
            String condition = USERNAME + "='" + userName + "'";
            ResultSet resultSet = databaseUtilities.select(CONTACTS, columns, condition);
            boolean exists = resultSet.next();
            resultSet.close();
            resultSet.getStatement().close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if the userName & password match any record in the DB.
     *
     * @param userName the user name of the contact
     * @param password the password of the contact
     * @return an integer representing the request operation result
     * <ul>
     * <li>VALID_CONTACT</li>
     * <li>INCORRECT_PASSWORD</li>
     * <li>CONTACT_DOES_NOT_EXIST</li>
     * </ul>
     */
    public int validateContact(String userName, String password) {
        if (!contactExists(userName)) {
            return CONTACT_DOES_NOT_EXIST;
        }
        userName = userName.toLowerCase();
        String columns[] = {"0"};

        try {
            String condition = USERNAME + "='" + userName + "' AND " + PASSWORD + "='" + password + "'";
            ResultSet resultSet = databaseUtilities.select(CONTACTS, columns, condition);
            boolean valid = resultSet.next();
            resultSet.close();
            resultSet.getStatement().close();
            if (valid) {
                return VALID_CONTACT;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return INCORRECT_PASSWORD;
    }

    /**
     * Adds a new contact record to the DB if the user name doesn't already
     * exist.
     *
     * @param contact the contact to be added to the database
     * @return true if the contact is added, and false if the user name of the
     * contact already exists in the DB
     */
    public boolean addContact(Contact contact) {
        String[] columns = {USERNAME, PASSWORD, FULLNAME, GENDER, BIRTH_DATE, STATUS};

        String userName = "'" + contact.getUserName().toLowerCase() + "'";
        String password = "'" + contact.getPassword() + "'";
        String fullName = "'" + contact.getFullName() + "'";
        String gender = "'" + contact.getGender().toString().substring(0, 1).toUpperCase() + "'";
        String birthDate = toDateFunction("'" + contact.getBirthDate().toString() + "'");
        String status = contact.getStatus().ordinal() + "";

        String[] values = {userName, password, fullName, gender, birthDate, status};

        saveContactImage(contact.getUserName(), contact.getPhoto());
        try {
            databaseUtilities.insert(CONTACTS, columns, values);
            databaseUtilities.commit();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the contact fields in the DB (except the user name and the
     * password) according to the user name if it already exists in the DB.
     *
     * @param contact the contact to be updated
     * @return true if the user name exists and false otherwise
     */
    public boolean updateContact(Contact contact) {
        String[] columns = {FULLNAME, GENDER, BIRTH_DATE, STATUS};

        String userName = "'" + contact.getUserName().toLowerCase() + "'";
        String fullName = "'" + contact.getFullName() + "'";
        String gender = "'" + contact.getGender().toString().substring(0, 1).toUpperCase() + "'";
        String birthDate = toDateFunction("'" + contact.getBirthDate().toString() + "'");
        String status = contact.getStatus().ordinal() + "";

        String[] values = {fullName, gender, birthDate, status};

        saveContactImage(contact.getUserName(), contact.getPhoto());
        try {
            String condition = USERNAME + "=" + userName;
            databaseUtilities.update(CONTACTS, columns, values, condition);
            databaseUtilities.commit();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the status of the contact in the DB.
     *
     * @param userName the user name of the contact
     * @param status the status of the contact
     */
    public void updateContactStatus(String userName, int status) {
        String[] columns = {STATUS};
        String[] values = {status + ""};
        String condition = USERNAME + "='" + userName + "'";
        try {
            databaseUtilities.update(CONTACTS, columns, values, condition);
            databaseUtilities.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Saves the image of the contact (if it's not null) in the directory
     * "home\java chat\contacts\images".
     *
     * @param userName the user name of the contact
     * @param image the image of the contact
     */
    private void saveContactImage(String userName, BufferedImage image) {
        if (image != null) {
            try {
                ImageIO.write(image, "png", new File(IMAGES_DIRECTORY + userName + ".png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Returns the image of the contact (if exists) from the directory
     * "home\java chat\contacts\images".
     *
     * @param userName the user name of the contact
     * @return the image of the contact if exists and null otherwise
     */
    private BufferedImage getContactImage(String userName) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(IMAGES_DIRECTORY + userName + ".png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return image;
    }

    /**
     * Returns the contact matching the user name if he/she exists in the DB.
     *
     * @param userName the user name of the contact
     * @return the contact if he/she exists in the DB and null otherwise
     */
    public Contact getContact(String userName) {
        userName = userName.toLowerCase();
        Contact contact = getContactData(userName);
        contact.setFriends(getFriends(userName));
        contact.setReceivedRequests(getReceivedRequests(userName));
        contact.setSentRequests(getSentRequests(userName));
        contact.setGroups(getGroups(userName));
        return contact;
    }

    /**
     * Returns a contact containing only the basic info (without sent or
     * received request, friends or groups) if the contact exists in the DB.
     *
     * @param userName the user name of the contact
     * @return the contact if he/she exists in the DB and null otherwise
     */
    private Contact getContactData(String userName) {
        userName = userName.toLowerCase();
        String columns[] = {USERNAME, FULLNAME, GENDER, toCharFunction(BIRTH_DATE) + " AS " + BIRTH_DATE,
            toCharFunction(JOIN_DATE) + " AS " + JOIN_DATE, STATUS};
        Contact contact = null;
        try {
            String condition = USERNAME + "='" + userName + "'";
            ResultSet resultSet = databaseUtilities.select(CONTACTS, columns, condition);
            resultSet.next();
            contact = new Contact(userName);
            contact.setPassword("??????");
            contact.setFullName(resultSet.getString(FULLNAME));
            contact.setGender(resultSet.getString(GENDER).equals("M") ? Gender.MALE : Gender.FEMALE);
            contact.setBirthDate(LocalDate.parse(resultSet.getString(BIRTH_DATE)));
            contact.setJoinDate(LocalDate.parse(resultSet.getString(JOIN_DATE)));
            contact.setStatus(Status.values()[resultSet.getInt(STATUS)]);
            contact.setPhoto(getContactImage(userName));
            resultSet.close();
            resultSet.getStatement().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contact;
    }

    /**
     * Adds a contact in the requests table if there's no previous request and
     * the two contacts are not already friends.
     *
     * @param sender the user name of the request sender
     * @param receiver the user name of the request receiver
     * @return an integer representing the request operation result
     * <ul>
     * <li>REQUEST_SENT_SUCCESSFULLY</li>
     * <li>REQUEST_ALREADY_SENT</li>
     * <li>CONTACT_ALREADY_ADDED</li>
     * <li>CONTACT_DOES_NOT_EXIST</li>
     * </ul>
     */
    public int addRequest(String sender, String receiver) {
        sender = sender.toLowerCase();
        receiver = receiver.toLowerCase();

        if (sender.equals(receiver)) {
            return CAN_NOT_ADD_YOURSELF;
        } else if (!contactExists(receiver)) {
            return CONTACT_DOES_NOT_EXIST;
        } else if (contactsJoined(REQUESTS, sender, receiver, false)) {
            return REQUEST_ALREADY_SENT;
        } else if (contactsJoined(FRIENDS, sender, receiver, false)) {
            return CONTACT_ALREADY_ADDED;
        }

        joinContacts(REQUESTS, sender, receiver);;
        return REQUEST_SENT_SUCCESSFULLY;
    }

    /**
     * Adds a contact in the friends table if the two contacts are not already
     * friends.
     *
     * @param sender the user name of the request sender
     * @param receiver the user name of the request receiver
     * @return an integer representing the add operation result
     * <ul>
     * <li>CONTACT_ADDED_SUCCESSFULLY</li>
     * <li>NO_REQUEST_SENT</li>
     * </ul>
     */
    public int confirmRequest(String sender, String receiver) {
        sender = sender.toLowerCase();
        receiver = receiver.toLowerCase();

        if (!contactsJoined(REQUESTS, receiver, sender, true)) {
            return NO_REQUEST_SENT;
        }

        removeRequest(sender, receiver);

        joinContacts(FRIENDS, sender, receiver);
        return CONTACT_ADDED_SUCCESSFULLY;
    }

    /**
     * Removes the request from the DB.
     *
     * @param sender the user name of the request sender
     * @param receiver the user name of the request receiver
     */
    public void removeRequest(String sender, String receiver) {
        sender = sender.toLowerCase();
        receiver = receiver.toLowerCase();

        String condition = SENDER + "='" + receiver + "' AND " + RECEIVER + "='" + sender + "'";
        try {
            databaseUtilities.delete(REQUESTS, condition);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Join two contacts by a request or friendship.
     *
     * @param tableName the name of the table (REUQUESTS or FRIENDS)
     * @param sender the user name of the request sender
     * @param receiver the user name of the request receiver
     */
    private void joinContacts(String tableName, String sender, String receiver) {
        sender = sender.toLowerCase();
        receiver = receiver.toLowerCase();

        String[] columns = {SENDER, RECEIVER};
        String[] values = {"'" + sender + "'", "'" + receiver + "'"};

        try {
            databaseUtilities.insert(tableName, columns, values);
            databaseUtilities.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns whether two contacts are joined by a request or friendship.
     *
     * @param tableName the name of the table (REUQUESTS or FRIENDS)
     * @param contact1 the user name of the first contact
     * @param contact2 the user name of the second contact
     * @param ordered if true, search only with the contacts order where
     * contact1 is the request sender and contact2 is the receiver
     * @return true if the two contacts are joined by a request or friendship
     * and false otherwise
     */
    private boolean contactsJoined(String tableName, String contact1, String contact2, boolean ordered) {
        contact1 = contact1.toLowerCase();
        contact2 = contact2.toLowerCase();
        String columns[] = {"0"};

        String condition1 = SENDER + "='" + contact1 + "' AND " + RECEIVER + "='" + contact2 + "'";
        String condition2 = RECEIVER + "='" + contact1 + "' AND " + SENDER + "='" + contact2 + "'";
        String condition = condition1;
        if (!ordered) {
            condition += " OR " + condition2;
        }

        try {
            ResultSet resultSet = databaseUtilities.select(tableName, columns, condition);
            boolean joined = resultSet.next();
            resultSet.close();
            resultSet.getStatement().close();
            return joined;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the contacts who received an add request from a contact.
     *
     * @param userName the user name of the contact
     * @return a Vector containing the contacts who received an add request from
     * this contact
     */
    public Vector<Contact> getSentRequests(String userName) {
        userName = userName.toLowerCase();
        return getContactsList(REQUESTS, userName, RECEIVERS);
    }

    /**
     * Returns the contacts who sent an add request to a contact.
     *
     * @param userName the user name of the contact
     * @return a Vector containing the contacts who sent an add request to this
     * contact
     */
    public Vector<Contact> getReceivedRequests(String userName) {
        userName = userName.toLowerCase();
        return getContactsList(REQUESTS, userName, SENDERS);
    }

    /**
     * Returns the friends of a contact.
     *
     * @param userName the user name of the contact
     * @return a Vector containing the friends of the contact
     */
    public Vector<Contact> getFriends(String userName) {
        userName = userName.toLowerCase();
        Vector<Contact> contacts = getContactsList(FRIENDS, userName, RECEIVERS);
        for (Contact contact : getContactsList(FRIENDS, userName, SENDERS)) {
            contacts.add(contact);
        }
        return contacts;
    }

    /**
     * Returns a Vector of contacts that represent friends or pending requests
     * (senders or receivers only).
     *
     * @param tableName the name of the table (REUQUESTS or FRIENDS)
     * @param userName the user name of the contact
     * @param type represents whether to get senders or receivers
     * <ul>
     * <li>SENDERS</li>
     * <li>RECEIVERS</li>
     * </ul>
     * @return a Vector of contacts that represent friends or pending requests
     */
    private Vector<Contact> getContactsList(String tableName, String userName, int type) {
        if (!tableName.equals(REQUESTS) && !tableName.equals(FRIENDS)) {
            return null;
        }
        userName = userName.toLowerCase();
        Vector<Contact> contacts = new Vector<Contact>();
        String selectColumn = SENDER;
        String conditionColumn = RECEIVER;
        if (type == RECEIVERS) {
            selectColumn = RECEIVER;
            conditionColumn = SENDER;
        }
        try {
            String[] columns = {selectColumn};
            ResultSet resultSet = databaseUtilities.select(tableName, columns, conditionColumn + "='" + userName + "'");
            while (resultSet.next()) {
                contacts.add(getContactData(resultSet.getString(selectColumn)));
            }
            resultSet.close();
            resultSet.getStatement().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    /**
     * Adds a new group to the DB containing a list of contacts.
     *
     * @param creator the user name of the contact who created the group
     * @param contacts a Vector of contacts belonging to that group
     * @return the id of the created group that's generated by a sequence
     */
    public int addGroup(String creator, Vector<String> contacts) {
        creator = creator.toLowerCase();
        String[] columns = {ID, CREATOR};
        String[] values = {GROUPS_SEQUENCE + ".NEXTVAL", "'" + creator + "'"};
        try {
            databaseUtilities.insert(GROUPS, columns, values);
            databaseUtilities.commit();
            ResultSet resultSet = databaseUtilities.select(DUAL, new String[]{GROUPS_SEQUENCE + ".CURRVAL AS ID"});
            resultSet.next();
            int groupID = resultSet.getInt("ID");
            addToGroup(groupID, creator);
            addToGroup(groupID, contacts);
            resultSet.close();
            resultSet.getStatement().close();
            return groupID;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Adds a Vector of contacts to a group
     *
     * @param groupID the id of the group
     * @param userNames the user name of the contact
     */
    public void addToGroup(int groupID, Vector<String> userNames) {
        for (String userName : userNames) {
            userName = userName.toLowerCase();
            addToGroup(groupID, userName);
        }
    }

    /**
     * Adds a contact to a group
     *
     * @param groupID the id of the group
     * @param userName the user name of the contact
     */
    public void addToGroup(int groupID, String userName) {
        userName = userName.toLowerCase();
        String[] columns = {GROUP_ID, CONTACT};
        String[] values = {groupID + "", "'" + userName + "'"};
        try {
            databaseUtilities.insert(GROUPS_CONTACTS, columns, values);
            databaseUtilities.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a group matching the group id if it exists in the DB.
     *
     * @param groupID the ID of the group
     * @return the group matching this ID if it exists in the DB and null
     * otherwise
     */
    public Group getGroup(int groupID) {
        String[] columns = {ID, CREATOR, toCharFunction(CREATION_DATE) + " AS " + CREATION_DATE};
        Group group = null;
        String condition = ID + "=" + groupID;
        try {
            ResultSet resultSet = databaseUtilities.select(GROUPS, columns, condition);
            resultSet.next();
            group = new Group(resultSet.getInt(ID));
            group.setCreator(getContactData(resultSet.getString(CREATOR)));
            group.setCreationDate(LocalDate.parse(resultSet.getString(CREATION_DATE)));
            group.setContacts(getContacts(groupID));
            resultSet.close();
            resultSet.getStatement().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return group;
    }

    /**
     * Returns the contacts belonging to a group.
     *
     * @param groupID the ID of the group
     * @return a Vector containing the contacts of the group
     */
    public Vector<Contact> getContacts(int groupID) {
        Vector<Contact> contacts = new Vector<Contact>();
        String[] columns = {CONTACT};
        String condition = GROUP_ID + "=" + groupID;
        try {
            ResultSet resultSet = databaseUtilities.select(GROUPS_CONTACTS, columns, condition);
            while (resultSet.next()) {
                contacts.add(getContactData(resultSet.getString(CONTACT)));
            }
            resultSet.close();
            resultSet.getStatement().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    /**
     * Returns the groups that a contact belongs to.
     *
     * @param userName the user name of the contact
     * @return a Vector containing the groups the contact belong to
     */
    public Vector<Group> getGroups(String userName) {
        userName = userName.toLowerCase();
        Vector<Group> groups = new Vector<Group>();
        try {
            String[] columns = {GROUP_ID};
            String condition = CONTACT + "='" + userName + "'";
            ResultSet resultSet = databaseUtilities.select(GROUPS_CONTACTS, columns, condition);
            while (resultSet.next()) {
                groups.add(getGroup(resultSet.getInt(GROUP_ID)));
            }
            resultSet.close();
            resultSet.getStatement().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    /**
     * Converts the date to a String that calls a function in the DB to convert
     * the string to a DB date
     *
     * @param date the date to be converted
     * @return a String representing a DB date conversion function
     */
    private String toDateFunction(String date) {
        return "TO_DATE(" + date + ", '" + DATE_FORMAT + "')";
    }

    /**
     * Converts the date to a String that calls a function in the DB to convert
     * the string to a DB char
     *
     * @param date the date to be converted
     * @return a String representing a DB char conversion function
     */
    private String toCharFunction(String date) {
        return "TO_CHAR(" + date + ", '" + DATE_FORMAT + "')";
    }

}
