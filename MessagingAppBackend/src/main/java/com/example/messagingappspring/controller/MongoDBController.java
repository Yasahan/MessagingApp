package com.example.messagingappspring.controller;

import com.example.messagingappspring.DTO.ChatDTO;
import com.example.messagingappspring.DTO.MessageDTO;
import com.example.messagingappspring.DTO.UserInfoDTO;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("mongo")
public class MongoDBController {
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("messagingappdb");
    MongoCollection<Document> userCollection = database.getCollection("user_info");
    MongoCollection<Document> chatCollection = database.getCollection("chat");


    @CrossOrigin
    @RequestMapping("/addUser")
    public UserInfoDTO addUser(@RequestBody UserInfoDTO user) {
        Document doc =
                new Document("user_id", userCollection.countDocuments() + 1)
                        .append("user_name", user.getUserName())
                        .append("user_password", user.getUserPassword());
        userCollection.insertOne(doc);
        return new UserInfoDTO((int) userCollection.countDocuments(), user.getUserName(),
                user.getUserPassword());
    }

    @CrossOrigin
    @GetMapping("/getUsers")
    public List<UserInfoDTO> getDataFromDB() {
        List<UserInfoDTO> users = new ArrayList<>();
        
        FindIterable<Document> iterDoc = userCollection.find();
        for (Document document : iterDoc) {
            int user_id = Math.toIntExact(document.getLong("user_id"));
            String user_name = document.getString("user_name");
            String user_password = document.getString("user_password");
            users.add(new UserInfoDTO(user_id, user_name, user_password));
        }
        return users;
    }

    @CrossOrigin
    @RequestMapping("/checkUserName")
    public boolean isUserAlreadyExist(@RequestBody String userName) {
        FindIterable<Document> iterDoc = userCollection.find();
        for (Document document : iterDoc) {
            if (document.getString("user_name").equals(userName)) {
                return true;
            }
        }
        return false;
    }

    @CrossOrigin
    @RequestMapping("/addMemberToChat")
    public void addMemberToChat(@RequestParam String chatId, @RequestParam String memberId) {
        Document userById = findUserById(memberId);
        userCollection.updateOne(userById, Updates.set("chat_id",chatId));
    }

    public Document findUserById(String userId) {
        FindIterable<Document> iterDoc = userCollection.find();
        for (Document document : iterDoc) {
            if (document.getLong("user_id").toString().equals(userId)) {
                return document;
            }
        }
        return null;
    }

    @CrossOrigin
    @RequestMapping("/addNewChat")
    public ChatDTO addNewChat(@RequestBody ChatDTO chat) {
        Document doc =
                new Document("chat_id", chatCollection.countDocuments() + 1)
                        .append("chat_name", chat.getChatName())
                        .append("creator_id", chat.getCreatorId())
                        .append("creation_date", new Date())
                        .append("chat_description", chat.getChatDescription());
        userCollection.insertOne(doc);
        return new ChatDTO(Integer.toString((int)(chatCollection.countDocuments() + 1)), chat.getChatDescription(), chat.getChatName(), chat.getCreatorId());
    }


    public Document findChatById(String chatId) {
        FindIterable<Document> iterDoc = userCollection.find();
        for (Document document : iterDoc) {
            if (document.getLong("chat_id").toString().equals(chatId)) {
                return document;
            }
        }
        return null;
    }

}
