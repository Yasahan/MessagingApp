package com.example.messagingappspring.database;
import com.opencsv.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBPopulate {

    private static String path = "src/data/";
    private static CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

    public static void populate() {
        try {
            List<String[]> users = DBPopulate.readUsers();
            List<String[]> emails = DBPopulate.readEmails();
            List<String[]> descriptions = DBPopulate.readDescriptions();
            List<String[]> hobbies = DBPopulate.readHobbies();

            /* =================== INSERTING HOBBIES =================== */
            for(int i = 0; i < hobbies.size(); ++i) {
                String insertHobby = String.format("INSERT INTO hobby(hobby_name, hobby_description) VALUES('%s', '%s')", hobbies.get(i)[0], descriptions.get(i)[0]);
                try {
                    DatabaseConnection.getInstance().createStatement().executeUpdate(insertHobby);
                } catch (Exception e) {
                    System.err.println("Error while executing INSERT INTO statement: " + e.getMessage());
                }
            }
            /* ========================================================== */

            int emailsRead = 0;
            int descriptionsRead = 0;
            int hobbyNr = 1;
            for (int i = 0; i < (users.size()/5); ++i) {
                String nickname = users.get(i)[0]; // current user nickname
                String pwd = users.get(i)[1]; // user password
                String birthdate = users.get(i)[2];

                String insertUser = String.format("INSERT INTO user_info(user_name, user_password) VALUES('%s', '%s')", nickname, pwd);
                String insertFriends = null;

                if(i > 0) {
                    insertFriends = String.format("INSERT INTO is_friend_of VALUES(%s, %s)", i-1, i);
                }

                String insertAdmin = null;

                if(emailsRead < 250) { // create exactly 250 admins
                    String dateType = "'%d-%M-%Y'";
                    insertAdmin = String.format("INSERT INTO admin_info VALUES(%s, STR_TO_DATE('%s',%s), '%s')", i, birthdate, dateType, emails.get(emailsRead++)[0]);
                }

                String insertChat = null;
                String insertChoose = null;
                String insertChatMember = null;

                if(descriptionsRead < 100) { // create exactly 100 rooms
                    insertChat = String.format("INSERT INTO chat(chat_description, chat_name, creator_id) VALUES('%s', 'Chat id: %s', %s)", descriptions.get(descriptionsRead++)[0], i - 1, i);
                    insertChatMember = String.format("INSERT INTO is_member(chat_id, member_id) VALUES(%s, %s)", i - 1, i);
                }
                else if (descriptionsRead < descriptions.size()) {
                    if(hobbyNr != hobbies.size()) {
                        insertChoose = String.format("INSERT INTO chooses(hobby_id, user_id) VALUES(%s, %s)", hobbyNr++, i);
                    }
                }
                // Execution of statements
                try {
                    //executeUpdate Method: Executes the SQL statement, which can be an INSERT, UPDATE, or DELETE statement
                    if(insertUser != null) {
                        DatabaseConnection.getInstance().createStatement().executeUpdate(insertUser);
                    }

                    if(insertFriends != null) {
                        DatabaseConnection.getInstance().createStatement().executeUpdate(insertFriends);
                    }

                    if(insertAdmin != null) {
                        DatabaseConnection.getInstance().createStatement().executeUpdate(insertAdmin);
                    }

                    if(insertChat != null && insertChatMember != null) {
                        DatabaseConnection.getInstance().createStatement().executeUpdate(insertChat);
                        DatabaseConnection.getInstance().createStatement().executeUpdate(insertChatMember);
                    }

                    if(insertChoose != null) {
                        DatabaseConnection.getInstance().createStatement().executeUpdate(insertChoose);
                    }

                } catch (Exception e) {
                    System.err.println("Error while executing INSERT INTO statement: " + e.getMessage());
                }
            }

            boolean first = true;
            for(int i = (users.size()/5) - 1, j = 1; i >= (users.size()/5) - 90; --i, ++j) { // additional 200 Sessions
                String insertSession = null;
                if(first) {
                    insertSession = String.format("INSERT INTO is_member(chat_id, member_id) VALUES(1, %s)", i);
                    first = false;
                }
                else {
                    insertSession = String.format("INSERT INTO is_member(chat_id, member_id) VALUES(2, %s)", i);
                    first = true;
                }
                // Execution of statements
                try {
                    if(insertSession != null) {
                        DatabaseConnection.getInstance().createStatement().executeUpdate(insertSession);
                    }
                } catch (Exception e) {
                    System.err.println("Error while executing INSERT INTO is_member statement: " + e.getMessage());
                }
            }

            first = true;
            int counter = 0;
            for(int i = (users.size()/5) - 1; i >= (users.size()/5) - 90; --i) {
                String insertMessage = null;
                if(first) {
                    insertMessage = String.format("INSERT INTO message(chat_id, content, sender_id) VALUES(1, '%s', %s)", descriptions.get(counter++)[0], i);
                    first = false;
                }
                else {
                    insertMessage = String.format("INSERT INTO message(chat_id, content, sender_id) VALUES(2, '%s', %s)", descriptions.get(counter++)[0], i);
                    first = true;
                }
                // Execution of statements
                try {
                    if(insertMessage != null) {
                        DatabaseConnection.getInstance().createStatement().executeUpdate(insertMessage);
                    }
                } catch (Exception e) {
                    System.err.println("Error while executing INSERT INTO Message statement: " + e.getMessage());
                }
            }
            for(int j = 0; j < 3; j++) {
                int hobbyId = (int) (Math.random() * (hobbies.size()));
                try {
                    DatabaseConnection.getInstance().createStatement().executeUpdate(String.format("INSERT INTO chooses(hobby_id, user_id) VALUES(%s, %s)", hobbyId, 3));
                    if (j == 2) {
                        hobbyId = (int) (Math.random() * (hobbies.size()));
                    }
                    DatabaseConnection.getInstance().createStatement().executeUpdate(String.format("INSERT INTO chooses(hobby_id, user_id) VALUES(%s, %s)", hobbyId, 2));
                } catch (Exception e) {
                    j--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String[]> readHobbies() throws IOException {
        FileReader filereader = new FileReader(path+"hobbies.csv");
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withCSVParser(parser)
                .build();

        List<String[]> hobbies = csvReader.readAll();
        return hobbies;
    }

    private static List<String[]> readDescriptions() throws IOException {
        FileReader filereader = new FileReader(path+"descriptions.csv");
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withCSVParser(parser)
                .build();

        csvReader.readNext(); // read header

        List<String[]> descriptions = new ArrayList<>();
        for(int i = 0; i < 400; ++i) { // descriptions.csv -> 1000 rows. Read 400
            descriptions.add(csvReader.readNext());
        }

        return descriptions;
    }


    private static List<String[]> readEmails() throws IOException {
        FileReader filereader = new FileReader(path+"emails.csv");
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withCSVParser(parser)
                .build();

        csvReader.readNext();

        List<String[]> emails = new ArrayList<>();
        for(int i = 0; i < 250; ++i) { // emails.csv -> 1000 rows. Read 250
            emails.add(csvReader.readNext());
        }

        return emails;
    }

    private static List<String[]> readUsers() throws IOException {
        FileReader filereader = new FileReader(path+"userinfo.csv");
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withCSVParser(parser)
                .build();

        // Read table info and header
        csvReader.readNext();
        csvReader.readNext();

        // Read all data at once
        List<String[]> userInfoData = csvReader.readAll();
        return  userInfoData;
    }
}
