CREATE DATABASE IF NOT EXISTS MessagingAppDB;
GRANT ALL PRIVILEGES ON *.* TO 'admin'@'%' WITH GRANT OPTION;

USE MessagingAppDB; 

CREATE TABLE user_info (
  user_id INT AUTO_INCREMENT NOT NULL,
  user_name VARCHAR(30) NOT NULL UNIQUE,
  user_password VARCHAR(40) NOT NULL,
    
  PRIMARY KEY (user_id)
);

CREATE TABLE is_friend_of (
  user_id INT NOT NULL,
  friend_id INT NOT NULL,

  CHECK(friend_id <> user_id),

  CONSTRAINT pk_friends PRIMARY KEY (user_id, friend_id),

  FOREIGN KEY (user_id)
    REFERENCES user_info(user_id)  
    ON DELETE CASCADE,
  FOREIGN KEY (friend_id)
    REFERENCES user_info(user_id) 
    ON DELETE CASCADE
);

CREATE TABLE admin_info (
  user_id INT NOT NULL,
  birthdate DATE NOT NULL,
  email VARCHAR(320) NOT NULL,
  
  PRIMARY KEY (user_id),
  FOREIGN KEY (user_id)
    REFERENCES user_info(user_id) 
    ON DELETE CASCADE
);

CREATE TABLE chat (
  chat_id INT AUTO_INCREMENT NOT NULL,
  creation_date DATE DEFAULT(CURRENT_DATE) NOT NULL,
  chat_description VARCHAR(2000) NOT NULL,
  chat_name VARCHAR(30) NOT NULL,
  creator_id INT NOT NULL,
  
  UNIQUE(creator_id, chat_name),
  PRIMARY KEY (chat_id),
  FOREIGN KEY (creator_id)
    REFERENCES admin_info(user_id)
);

CREATE TABLE is_member(
  chat_id INT NOT NULL,
  member_id INT NOT NULL,

  CONSTRAINT pk_is_member PRIMARY KEY (chat_id, member_id),
  FOREIGN KEY (chat_id)
    REFERENCES chat(chat_id)
    ON DELETE CASCADE,
  FOREIGN KEY (member_id)
    REFERENCES user_info(user_id)
    ON DELETE CASCADE
);

CREATE TABLE invites (
  chat_id INT NOT NULL,
  invited_by_id INT NOT NULL,
  user_id INT NOT NULL,

  CONSTRAINT pk_invites PRIMARY KEY (chat_id, user_id),
  FOREIGN KEY (chat_id)
    REFERENCES chat(chat_id)
    ON DELETE CASCADE,
  FOREIGN KEY (user_id)
    REFERENCES user_info(user_id)
    ON DELETE CASCADE,
  FOREIGN KEY (invited_by_id)
    REFERENCES user_info(user_id)
    ON DELETE CASCADE
);

CREATE TABLE message (
  message_id INT AUTO_INCREMENT NOT NULL,
  chat_id INT NOT NULL,
  sent_time DATE DEFAULT(CURRENT_DATE) NOT NULL,
  content VARCHAR(2000) NOT NULL,
    
  sender_id INT NOT NULL,
    
  CONSTRAINT pk_message PRIMARY KEY (message_id, chat_id),
  FOREIGN KEY (chat_id)
    REFERENCES chat(chat_id)
    ON DELETE CASCADE,
  FOREIGN KEY (sender_id)
    REFERENCES user_info(user_id)
    ON DELETE CASCADE,
  FOREIGN KEY (sender_id, chat_id)
    REFERENCES is_member(member_id, chat_id)
    ON DELETE CASCADE
);

CREATE TABLE hobby (
  hobby_id INT AUTO_INCREMENT NOT NULL,
  hobby_name VARCHAR(30) NOT NULL UNIQUE,
  hobby_description VARCHAR(2000),
  
  PRIMARY KEY (hobby_id)
);

CREATE TABLE chooses (
  hobby_id INT NOT NULL,
  user_id INT NOT NULL,
  
  CONSTRAINT pk_chooses PRIMARY KEY (hobby_id, user_id),
  
  FOREIGN KEY (hobby_id)
      REFERENCES hobby(hobby_id),
  FOREIGN KEY (user_id)
      REFERENCES user_info(user_id)
);