package com.example.messagingappspring.database;

import com.example.messagingappspring.DTO.AdminInfoDTO;
import com.example.messagingappspring.DTO.ChatDTO;
import com.example.messagingappspring.DTO.HobbyDTO;
import com.example.messagingappspring.DTO.UserInfoDTO;
import com.example.messagingappspring.mongoController.MongoUtil;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.print.Doc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController()
@RequestMapping("mongo")
public class MigrateDataToMongoDB {

    DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("messagingappdb");
    MongoCollection<Document> userCollection = database.getCollection("user_info");
    MongoCollection<Document> hobbiesCollection = database.getCollection("hobbies");
    MongoCollection<Document> chatCollection = database.getCollection("chat");

    @CrossOrigin
    @RequestMapping("/migrate")
    void populate() {
        addUsers();
        addHobbies();
        addAdmins();
        addFriends();
        addchats();
        addChatMembers();
    }


    void addUsers() {
        List<UserInfoDTO> users = new ArrayList<>();
        try {
            ResultSet resultSet = databaseConnection.createStatement().executeQuery("select * from user_info");
            while (resultSet.next()) {
                users.add(new UserInfoDTO(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (UserInfoDTO user : users) {
            Document doc =
                    new Document("user_id", userCollection.countDocuments() + 1)
                            .append("user_name", user.getUserName())
                            .append("user_password", user.getUserPassword());
            userCollection.insertOne(doc);
        }
    }


    void addHobbies() {
        List<HobbyDTO> hobbies = new ArrayList<>();
        try {
            ResultSet resultSet = databaseConnection.createStatement().executeQuery("select * from hobby");
            while (resultSet.next()) {
                hobbies.add(new HobbyDTO(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (HobbyDTO hobby : hobbies) {
            Document doc =
                    new Document("hobby_id", hobbiesCollection.countDocuments() + 1)
                            .append("hobby_name", hobby.getHobbyName())
                            .append("hobby_description", hobby.getHobbyDescription());
            hobbiesCollection.insertOne(doc);
        }
    }


    void addAdmins() {
        List<AdminInfoDTO> admins = new ArrayList<>();
        FindIterable<Document> users = userCollection.find();

        try {
            ResultSet resultSet = databaseConnection.createStatement().executeQuery("" +
                    "select user_info.user_id, user_name, user_password, admin_info.email, admin_info.birthdate  " +
                    "from admin_info " +
                    "INNER JOIN user_info " +
                    "ON admin_info.user_id = user_info.user_id");
            while (resultSet.next()) {
                admins.add(new AdminInfoDTO(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (AdminInfoDTO admin : admins) {
            Document doc =
                    new Document()
                            .append("birthdate", admin.getUserBirthdate())
                            .append("email", admin.getUserEmail());
            Document foundUser = MongoUtil.findUserById(String.valueOf(admin.getUserId()));
            userCollection.updateOne(foundUser, Updates.set("is_admin", doc));
        }
    }

    void addFriends() {
        List<UserInfoDTO> users = new ArrayList<>();
        try {
            ResultSet resultSet = databaseConnection.createStatement().executeQuery("select * from user_info");
            while (resultSet.next()) {
                users.add(new UserInfoDTO(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        for (UserInfoDTO user : users) {
            try {
                ResultSet resultSet = resultSet = databaseConnection.createStatement().executeQuery("" +
                        "select * from is_friend_of where user_id = " + user.getUserId());
                while (resultSet.next()) {
                    userCollection.updateOne(MongoUtil.findUserById(String.valueOf(user.getUserId())),
                            Updates.push("friends", Integer.parseInt(resultSet.getString(2))));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    void addchats(){
        List<ChatDTO> chats = new ArrayList<>();
        try {
            ResultSet resultSet = databaseConnection.createStatement().executeQuery("select * from chat");
            while (resultSet.next()) {
                chats.add(new ChatDTO(String.valueOf(resultSet.getInt(1)), resultSet.getString(3), resultSet.getString(4), String.valueOf(resultSet.getInt(5))));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        for(ChatDTO chat : chats){
            Document foundUser = MongoUtil.findUserById(String.valueOf(chat.getCreatorId()));
            for (Document doc : chatCollection.find()) {
                if(doc.getString("chat_name").equals(chat.getChatName()) && doc.getInteger("creator_id").toString().equals(chat.getCreatorId())){
                    return;
                }
            }
            if (foundUser.get("is_admin") == null) {
                return;
            }


            long chatId = chatCollection.countDocuments() + 1;
            Document doc =
                    new Document("chat_id", chatId)
                            .append("chat_name", chat.getChatName())
                            .append("creator_id", Integer.parseInt(chat.getCreatorId()))
                            .append("creation_date", new Date())
                            .append("chat_description", chat.getChatDescription());
            chatCollection.insertOne(doc);
            chatCollection.updateOne(doc, Updates.push("users", Integer.parseInt(chat.getCreatorId())));
            Document userById = MongoUtil.findUserById(chat.getCreatorId());
            if (userById != null) {
                userCollection.updateOne(userById, Updates.push("chats",(int) chatId));
            }
        }
    }

    void addChatMembers(){
        List<ChatDTO> chats = new ArrayList<>();
        try {
            ResultSet resultSet = databaseConnection.createStatement().executeQuery("select * from chat");
            while (resultSet.next()) {
                chats.add(new ChatDTO(String.valueOf(resultSet.getInt(1)), resultSet.getString(3), resultSet.getString(4), String.valueOf(resultSet.getInt(5))));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        for(ChatDTO chat : chats){
            try {
                ResultSet resultSet = databaseConnection.createStatement().executeQuery("select * from is_member where chat_id =" +  chat.getChatId());
                while (resultSet.next()) {
                    chatCollection.updateOne(MongoUtil.findChat("chat_id", chat.getChatId()), Updates.push("users", resultSet.getInt(2)));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }


}
