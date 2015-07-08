package database;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Vector;

import javax.imageio.ImageIO;

import common.Contact;
import common.Contact.Gender;
import common.Contact.Status;
import common.Group;

class DatabaseTester {

    private static DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandler();

    public static void main(String[] args) {
        Contact contact1 = new Contact("Medhat");
        contact1.setPassword("password");
        contact1.setFullName("Ahmed Medhat");
        contact1.setGender(Gender.MALE);
        contact1.setBirthDate(LocalDate.of(1988, 3, 10));
        contact1.setStatus(Status.AVAILABLE);
        try {
            contact1.setPhoto(ImageIO.read(new File("C:\\users\\marco\\desktop\\0.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Contact contact2 = new Contact("Mazen");
        contact2.setPassword("password");
        contact2.setFullName("Ahmed Mazen");
        contact2.setGender(Gender.MALE);
        contact2.setBirthDate(LocalDate.of(1985, 7, 15));
        contact2.setStatus(Status.AWAY);
        try {
            contact2.setPhoto(ImageIO.read(new File("C:\\users\\marco\\desktop\\1.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Contact contact3 = new Contact("Gabr");
        contact3.setPassword("password");
        contact3.setFullName("Ahmed Gabr");
        contact3.setGender(Gender.MALE);
        contact3.setBirthDate(LocalDate.of(1987, 5, 20));
        contact3.setStatus(Status.BUSY);
        try {
            contact3.setPhoto(ImageIO.read(new File("C:\\users\\marco\\desktop\\2.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Contact contact4 = new Contact("Said");
        contact4.setPassword("password");
        contact4.setFullName("Mohammed Said");
        contact4.setGender(Gender.MALE);
        contact4.setBirthDate(LocalDate.of(1986, 10, 5));
        contact4.setStatus(Status.OFFLINE);
        try {
            contact4.setPhoto(ImageIO.read(new File("C:\\users\\marco\\desktop\\3.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Contact contact5 = new Contact("Mohsen");
        contact5.setPassword("password");
        contact5.setFullName("Mohsen Diab");
        contact5.setGender(Gender.MALE);
        contact5.setBirthDate(LocalDate.of(1988, 1, 25));
        contact5.setStatus(Status.AVAILABLE);
        try {
            contact5.setPhoto(ImageIO.read(new File("C:\\users\\marco\\desktop\\4.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Contact contact6 = new Contact("Mostafa");
        contact6.setPassword("password");
        contact6.setFullName("Mostafa Magdy");
        contact6.setGender(Gender.MALE);
        contact6.setBirthDate(LocalDate.of(1989, 9, 15));
        contact6.setStatus(Status.AWAY);
        try {
            contact6.setPhoto(ImageIO.read(new File("C:\\users\\marco\\desktop\\5.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        databaseHandler.addContact(contact1);
        databaseHandler.addContact(contact2);
        databaseHandler.addContact(contact3);
        databaseHandler.addContact(contact4);
        databaseHandler.addContact(contact5);
        databaseHandler.addContact(contact6);

        System.out.println("Number of contacts: " + databaseHandler.getContactsCount());

        System.out.println("--------------------------------------------------------------------"
                + "-----------------------------------------------------------------------------");

        addRequest("Medhat", "Gabr");
        addRequest("Medhat", "Mohsen");

        addRequest("Mazen", "Mostafa");
        addRequest("Mazen", "Said");

        addRequest("Gabr", "Mazen");
        addRequest("Gabr", "Mostafa");

        addRequest("Said", "Medhat");
        addRequest("Said", "Gabr");

        addRequest("Mohsen", "Said");
        addRequest("Mohsen", "Mazen");

        addRequest("Mostafa", "Said");
        addRequest("Mostafa", "Medhat");

        addRequest("Medhat", "Ali");

        System.out.println();

        addRequest("Gabr", "Mazen");
        addRequest("Mohsen", "Said");
        addRequest("Mohsen", "Medhat");
        addRequest("Mostafa", "Gabr");

        System.out.println();

        confirmRequest("Mazen", "Medhat");
        confirmRequest("Mohsen", "Medhat");

        confirmRequest("Medhat", "Mazen");
        confirmRequest("Said", "Mazen");

        confirmRequest("Mohsen", "Gabr");
        confirmRequest("Mazen", "Gabr");

        confirmRequest("Medhat", "Said");
        confirmRequest("Mohsen", "Said");

        confirmRequest("Said", "Mohsen");
        confirmRequest("Medhat", "Mohsen");
        confirmRequest("Mazen", "Mohsen");

        confirmRequest("Mohsen", "Mostafa");
        confirmRequest("Said", "Mostafa");

        System.out.println("--------------------------------------------------------------------"
                + "-----------------------------------------------------------------------------");

        validate("Medhat", "password");
        validate("Medhat", "any");
        validate("Medhat", "Password");
        validate("Any", "password");

        System.out.println("--------------------------------------------------------------------"
                + "-----------------------------------------------------------------------------");

        Vector<String> all = new Vector<String>();
        all.add("Mazen");
        all.add("Gabr");
        all.add("Said");
        all.add("Mohsen");
        all.add("Mostafa");
        databaseHandler.addGroup("Medhat", all);

        Vector<String> web = new Vector<String>();
        web.add("Gabr");
        web.add("Mohsen");
        databaseHandler.addGroup("Medhat", web);

        Vector<String> mobile = new Vector<String>();
        mobile.add("Said");
        mobile.add("Mostafa");
        databaseHandler.addGroup("Mazen", mobile);

        databaseHandler.getContact("Medhat").printData(true);
        databaseHandler.getContact("Mazen").printData(true);
        databaseHandler.getContact("Gabr").printData(true);
        databaseHandler.getContact("Said").printData(true);
        databaseHandler.getContact("Mohsen").printData(true);
        databaseHandler.getContact("Mostafa").printData(true);

        System.out.println("--------------------------------------------------------------------"
                + "-----------------------------------------------------------------------------");

        Contact contact = databaseHandler.getContact("Medhat");

        contact.setFullName("Ahmed Medhat Youssif");
        contact.setBirthDate(LocalDate.of(1990, 1, 1));
        contact.setStatus(Status.AVAILABLE);
        databaseHandler.updateContact(contact);

        databaseHandler.getContact("Medhat").printData(false);

        System.out.println("--------------------------------------------------------------------"
                + "-----------------------------------------------------------------------------");

        System.out.println("Mostafa's groups:");
        System.out.println();
        Vector<Group> groups = databaseHandler.getGroups("Mostafa");
        for (Group group : groups) {
            group.printData();
        }
    }

    // Validate
    private static void validate(String userName, String password) {
        int response = databaseHandler.validateContact(userName, password);
        System.out.print("Validate(" + userName + ", " + password + "): ");
        if (response == DatabaseHandler.VALID_CONTACT) {
            System.out.println("VALID CONTACT");
        } else if (response == DatabaseHandler.INCORRECT_PASSWORD) {
            System.out.println("INCORRECT PASSWORD");
        } else if (response == DatabaseHandler.CONTACT_DOES_NOT_EXIST) {
            System.out.println("CONTACT DOES NOT EXIST");
        }
    }

    // Add friend
    private static void confirmRequest(String sender, String receiver) {
        int response = databaseHandler.confirmRequest(sender, receiver);
        System.out.print(sender + "-->" + receiver + ": ");
        if (response == DatabaseHandler.CONTACT_ADDED_SUCCESSFULLY) {
            System.out.println("CONTACT ADDED SUCCESSFULLY");
        } else if (response == DatabaseHandler.NO_REQUEST_SENT) {
            System.out.println("NO REQUEST SENT");
        }
    }

    // Add Request
    private static void addRequest(String sender, String receiver) {
        int response = databaseHandler.addRequest(sender, receiver);
        System.out.print(sender + "->" + receiver + ": ");
        if (response == DatabaseHandler.REQUEST_SENT_SUCCESSFULLY) {
            System.out.println("REQUEST SENT SUCCESSFULLY");
        } else if (response == DatabaseHandler.REQUEST_ALREADY_SENT) {
            System.out.println("REQUEST ALREADY SENT");
        } else if (response == DatabaseHandler.CONTACT_ALREADY_ADDED) {
            System.out.println("CONTACT ALREADY ADDED");
        } else if (response == DatabaseHandler.CONTACT_DOES_NOT_EXIST) {
            System.out.println("CONTACT DOES NOT EXIST");
        }
    }

}
