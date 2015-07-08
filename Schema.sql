DROP TABLE friends;
DROP TABLE requests;
DROP TABLE blocks;
DROP TABLE groups_contacts;
DROP TABLE groups;
DROP TABLE contacts;
 
DROP SEQUENCE sequence_groups;
 
CREATE TABLE contacts (
   username VARCHAR2(25) PRIMARY KEY CHECK (LENGTH(username) > 3),
   password VARCHAR2(50) NOT NULL CHECK (LENGTH(password) > 5),
   fullname VARCHAR2(50) NOT NULL CHECK (LENGTH(fullname) > 3),
   gender VARCHAR2(1) NOT NULL CHECK(gender = 'M' or gender = 'F'),
   birthdate DATE NOT NULL,
   joindate DATE DEFAULT SYSDATE,
   status NUMBER(1) NOT NULL
);
CREATE INDEX index_contacts_fullname ON contacts(fullname);
 
CREATE TABLE requests (
   sender VARCHAR2(25),
   receiver VARCHAR2(25),
   requestDate DATE DEFAULT SYSDATE,
   CONSTRAINT pk_requests PRIMARY KEY(sender, receiver),
   CONSTRAINT fk_requests_username_1 FOREIGN KEY (sender) REFERENCES contacts(username),
   CONSTRAINT fk_requests_username_2 FOREIGN KEY (receiver) REFERENCES contacts(username)
);
CREATE INDEX index_requests_username_1 ON requests(sender);
CREATE INDEX index_requests_username_2 ON requests(receiver);
 
CREATE TABLE friends (
   sender VARCHAR2(25),
   receiver VARCHAR2(25),
   confirmDate DATE DEFAULT SYSDATE,
   CONSTRAINT pk_friends PRIMARY KEY (sender, receiver),
   CONSTRAINT fk_friends_username_1 FOREIGN KEY (sender) REFERENCES contacts(username),
   CONSTRAINT fk_friends_username_2 FOREIGN KEY (receiver) REFERENCES contacts(username)
);
CREATE INDEX index_friends_username_1 ON friends(sender);
CREATE INDEX index_friends_username_2 ON friends(receiver);
 
CREATE TABLE blocks (
   sender VARCHAR2(25),
   receiver VARCHAR2(25),
   CONSTRAINT pk_blocks PRIMARY KEY(sender, receiver),
   CONSTRAINT fk_blocks_username_1 FOREIGN KEY (sender) REFERENCES contacts(username),
   CONSTRAINT fk_blocks_username_2 FOREIGN KEY (receiver) REFERENCES contacts(username)
);
CREATE INDEX index_blocks_username_1 ON blocks(sender);
CREATE INDEX index_blocks_username_2 ON blocks(receiver);
 
CREATE SEQUENCE sequence_groups INCREMENT BY 1 START WITH 1;
 
CREATE TABLE groups (
   id NUMBER(10) PRIMARY KEY,
   creator VARCHAR2(25),
   creationdate DATE DEFAULT SYSDATE,
   CONSTRAINT fk_groups_username FOREIGN KEY (creator) REFERENCES contacts(username)
);
 
CREATE TABLE groups_contacts (
   groupid NUMBER(10),
   contact VARCHAR2(25),
   CONSTRAINT pk_groups_contacts PRIMARY KEY(groupid, contact),
   CONSTRAINT fk_groups_contacts_group_id FOREIGN KEY (groupid) REFERENCES groups(id),
   CONSTRAINT fk_groups_contacts_username FOREIGN KEY (contact) REFERENCES contacts(username)
);
CREATE INDEX index_groups_users_groupid_1 ON groups_contacts(groupid);
CREATE INDEX index_groups_users_username_2 ON groups_contacts(contact);