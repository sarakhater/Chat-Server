package common;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Vector;

public class Group implements Serializable {

    private int id;
    private Contact creator;
    private LocalDate creationDate;

    private Vector<Contact> members = new Vector<Contact>();

    public Group() {
    }

    public Group(int id) {
        setId(id);
    }

    public Group(int id, Contact creator) {
        setId(id);
        setCreator(creator);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Group) {
            Group anotherGroup = (Group) object;
            if (members.size() == anotherGroup.members.size()) {
                Collections.sort(members);
                Collections.sort(anotherGroup.members);
                for (int i = 0; i < members.size(); i++) {
                    if (!members.elementAt(i).equals(anotherGroup.members.elementAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    // Set ID
    public void setId(int id) {
        this.id = id;
    }

    // Get ID
    public int getId() {
        return id;
    }

    // Set Creator
    public void setCreator(Contact creator) {
        this.creator = creator;
        members.add(creator);
    }

    // Get Creator
    public Contact getCreator() {
        return creator;
    }

    // Set Creation Date
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    // Get Creation Date
    public LocalDate getCreationDate() {
        return creationDate;
    }

    // Set Contacts
    public void setContacts(Vector<Contact> contacts) {
        this.members = contacts;
    }

    // Get Contacts
    public Vector<Contact> getContacts() {
        return members;
    }

    // Add Contact
    public void addContact(Contact contact) {
        members.addElement(contact);
    }

    // Remove Contact
    public void removeContact(Contact contact) {
        members.remove(contact);
    }

    // Print Data
    public void printData() {
        System.out.println("ID: " + id);
        System.out.println("Creator: " + creator.getUserName());
        System.out.println("Creation Date: " + creationDate);
        System.out.print("Memebers: ");
        printMembers();
        System.out.println();
        System.out.println();
    }

    // Print Members
    private void printMembers() {
        for (int i = 0; i < members.size(); i++) {
            System.out.print(members.elementAt(i).getUserName());
            if (i != members.size() - 1) {
                System.out.print(", ");
            }
        }
    }

}
