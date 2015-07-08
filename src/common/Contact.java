package common;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Vector;

public class Contact implements Serializable, Comparable<Contact> {

    // Gender
    public enum Gender {

        MALE, FEMALE;

        // To String
        public String toString() {
            String origin = super.toString();
            return (origin.substring(0, 1) + origin.substring(1).toLowerCase());
        }
    };

    // Status
    public enum Status {

        AVAILABLE, AWAY, BUSY, OFFLINE;

        // To String
        public String toString() {
            String origin = super.toString();
            return (origin.substring(0, 1) + origin.substring(1).toLowerCase());
        }
    };

    // Constants
    private static final int USER_NAME_MIN_LENGTH = 4;
    private static final int USER_NAME_MAX_LENGTH = 25;

    private static final int FULL_NAME_MIN_LENGTH = 4;
    private static final int FULL_NAME_MAX_LENGTH = 30;

    private static final int PASSWORD_MIN_LENGTH = 6;
    private static final int PASSWORD_MAX_LENGTH = 50;

    // Fields
    private String userName;
    private String password;
    private String fullName;
    private LocalDate birthDate;
    private LocalDate joinDate;
    private Gender gender;
    private Status status = Status.AVAILABLE;
    private int[] imageArray;
    private int imageWidth, imageHeight;

    private Vector<Contact> friends = new Vector<Contact>();
    private Vector<Contact> receivedRequests = new Vector<Contact>();
    private Vector<Contact> sentRequests = new Vector<Contact>();
    private Vector<Group> groups = new Vector<Group>();

    // Constructors
    public Contact() {
    }

    public Contact(String userName) {
        setUserName(userName);
    }

    public Contact(String userName, String password) {
        setUserName(userName);
        setPassword(password);
    }

    public Contact(String userName, String password, String fullName) {
        setUserName(userName);
        setPassword(password);
        setFullName(fullName);
    }

    public Contact(Contact contact) {
        copy(contact);
    }

    @Override
    public String toString() {
        return userName + " (" + fullName + ")";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof Contact && userName.equals(((Contact) object).userName);
    }

    @Override
    public int compareTo(Contact contact) {
        if (status != Status.OFFLINE && contact.status == Status.OFFLINE) {
            return -1;
        } else if (status == Status.OFFLINE && contact.status != Status.OFFLINE) {
            return 1;
        } else {
            int order = fullName.compareToIgnoreCase(contact.fullName);
            if (order == 0) {
                order = fullName.compareTo(contact.fullName);
            }
            return order;
        }
    }

    // Copy
    public void copy(Contact person) {
        userName = person.userName;
        fullName = person.fullName;
        birthDate = person.birthDate;
        joinDate = person.joinDate;
        gender = person.gender;
        status = person.status;
        imageArray = person.imageArray;
    }

    // Set User Name
    public void setUserName(String userName) {
        if (userName == null) {
            throw new IllegalArgumentException("User name can't be null");
        }

        userName = userName.trim().toLowerCase();
        int length = userName.length();
        if (length < USER_NAME_MIN_LENGTH) {
            throw new IllegalArgumentException("Minimun length of user name is " + USER_NAME_MIN_LENGTH + " characters");
        } else if (length > USER_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Maximum length of user name is " + USER_NAME_MAX_LENGTH + " characters");
        }

        for (int i = 0; i < length; i++) {
            char c = userName.charAt(i);
            boolean letters = c >= 'a' && c <= 'z';
            boolean numbers = c >= '0' && c <= '9';
            boolean others = c == '_' || c == '.';
            if (!(letters || numbers || others)) {
                throw new IllegalArgumentException("Only letters, numbers, underscores and periods are allowed");
            } else if (!letters && i == 0) {
                throw new IllegalArgumentException("User name must start with a letter");
            } else if (!(letters || numbers) && i == length - 1) {
                throw new IllegalArgumentException("User name must end with a letter or number");
            }
        }

        this.userName = userName;
    }

    // Get User Name
    public String getUserName() {
        return userName;
    }

    // Set Full Name
    public void setFullName(String fullName) {
        if (fullName == null) {
            throw new IllegalArgumentException("Name can't be null");
        }

        fullName = fullName.trim();
        int length = fullName.length();
        if (length < FULL_NAME_MIN_LENGTH) {
            throw new IllegalArgumentException("Minimun length of name is " + FULL_NAME_MIN_LENGTH + " characters");
        } else if (length > FULL_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Maximum length of name is " + FULL_NAME_MAX_LENGTH + " characters");
        }

        for (int i = 0; i < length; i++) {
            char c = fullName.charAt(i);
            boolean letters = c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
            boolean numbers = c >= '0' && c <= '9';
            boolean others = c == ' ' || c == '\'' || c == '.' || c == '-' || c == '_';
            if (!(letters || numbers || others)) {
                throw new IllegalArgumentException("Special characters are not allowed");
            }
        }

        this.fullName = fullName;
    }

    // Get Full Name
    public String getFullName() {
        return fullName;
    }

    // Set Password
    public void setPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password can't be null");
        }

        password = password.trim();
        int length = password.length();
        if (length < PASSWORD_MIN_LENGTH) {
            throw new IllegalArgumentException("Minimun length of Password is " + PASSWORD_MIN_LENGTH + " characters");
        } else if (length > PASSWORD_MAX_LENGTH) {
            throw new IllegalArgumentException("Maximum length of Password is " + PASSWORD_MAX_LENGTH + " characters");
        }

        this.password = password;
    }

    // Get Password
    public String getPassword() {
        return password;
    }

    // Set Birth Date
    public void setBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Birthdate can't be null");
        }

        this.birthDate = birthDate;
    }

    // Get Birth Date
    public LocalDate getBirthDate() {
        return birthDate;
    }

    // Get Age
    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // Set Join Date
    public void setJoinDate(LocalDate joinDate) {
        if (joinDate == null) {
            throw new IllegalArgumentException("Joindate can't be null");
        }

        this.joinDate = joinDate;
    }

    // Get Join Date
    public LocalDate getJoinDate() {
        return joinDate;
    }

    // Set Gender
    public void setGender(Gender gender) {
        if (gender == null) {
            throw new IllegalArgumentException("Gender can't be null");
        }

        this.gender = gender;
    }

    // Get Gender
    public Gender getGender() {
        return gender;
    }

    // Set Status
    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status can't be null");
        }

        this.status = status;
    }

    // Get Status
    public Status getStatus() {
        return status;
    }

    // Set Image
    public void setPhoto(BufferedImage image) {
        if (image == null) {
            return;
        }
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
        this.imageArray = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    }

    // Get Image
    public BufferedImage getPhoto() {
        if (imageArray == null) {
            return null;
        }
        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0, 0, imageWidth, imageHeight, imageArray, 0, imageWidth);
        return bufferedImage;
    }

    // Set Friends
    public void setFriends(Vector<Contact> friends) {
        this.friends = friends;
    }

    // Get Friends
    public Vector<Contact> getFriends() {
        return friends;
    }

    // Add Friend
    public void addFriend(Contact contact) {
        friends.add(contact);
    }

    // Remove Friend
    public void removeFriend(Contact contact) {
        friends.remove(contact);
    }

    // Set Sent Requests
    public void setSentRequests(Vector<Contact> sentRequests) {
        this.sentRequests = sentRequests;
    }

    // Get Requests
    public Vector<Contact> getSentRequests() {
        return sentRequests;
    }

    // Add Sent Request
    public void addSentRequest(Contact contact) {
        sentRequests.add(contact);
    }

    // Remove Sent Request
    public void removeSentRequest(Contact contact) {
        sentRequests.remove(contact);
    }

    // Set Received Requests
    public void setReceivedRequests(Vector<Contact> receivedRequests) {
        this.receivedRequests = receivedRequests;
    }

    // Get Received Requests
    public Vector<Contact> getReceivedRequests() {
        return receivedRequests;
    }

    // Add Received Request
    public void addReceivedRequest(Contact contact) {
        receivedRequests.add(contact);
    }

    // Remove Received Request
    public void removeReceivedRequest(Contact contact) {
        receivedRequests.remove(contact);
    }

    // Set Groups
    public void setGroups(Vector<Group> groups) {
        this.groups = groups;
    }

    // Get Groups
    public Vector<Group> getGroups() {
        return groups;
    }

    // Add Group
    public void addGroup(Group group) {
        groups.add(group);
    }

    // Remove Group
    public void removeGroup(Group group) {
        groups.remove(group);
    }

    // Print Data
    public void printData(boolean oneLine) {
        if (oneLine) {
            System.out.print(userName + " : " + password + " : " + fullName + " : " + gender + " : " + birthDate + " : "
                    + getAge() + " : " + joinDate + " : " + status + " : {");
            printGroups();
            System.out.print("} : {");
            printFriends();
            System.out.print("} : {");
            printSentRequests();
            System.out.print("} : {");
            printReceivedRequests();
            System.out.println("}");
        } else {
            System.out.println("User name: " + userName);
            System.out.println("Password: " + password);
            System.out.println("Full name: " + fullName);
            System.out.println("Gender: " + gender);
            System.out.println("Birth Date: " + birthDate);
            System.out.println("Age: " + getAge());
            System.out.println("Join Date: " + joinDate);
            System.out.println("Status: " + status);

            System.out.println();
            System.out.print("Sent Requests: ");
            printSentRequests();
            System.out.println();
            System.out.print("Received Requests: ");
            printReceivedRequests();
            System.out.println();
            System.out.print("Friends: ");
            printFriends();
            System.out.println();
            System.out.print("Groups: ");
            printGroups();
            System.out.println();
        }
    }

    // Print Received Requests
    private void printReceivedRequests() {
        for (int i = 0; i < receivedRequests.size(); i++) {
            System.out.print(receivedRequests.elementAt(i).getUserName());
            if (i != receivedRequests.size() - 1) {
                System.out.print(", ");
            }
        }
    }

    // Print Sent Requests
    private void printSentRequests() {
        for (int i = 0; i < sentRequests.size(); i++) {
            System.out.print(sentRequests.elementAt(i).getUserName());
            if (i != sentRequests.size() - 1) {
                System.out.print(", ");
            }
        }
    }

    // Print Friends
    private void printFriends() {
        for (int i = 0; i < friends.size(); i++) {
            System.out.print(friends.elementAt(i).getUserName());
            if (i != friends.size() - 1) {
                System.out.print(", ");
            }
        }
    }

    // Print Groups
    private void printGroups() {
        for (int i = 0; i < groups.size(); i++) {
            System.out.print(groups.elementAt(i).getId());
            if (i != groups.size() - 1) {
                System.out.print(", ");
            }
        }
    }

}
