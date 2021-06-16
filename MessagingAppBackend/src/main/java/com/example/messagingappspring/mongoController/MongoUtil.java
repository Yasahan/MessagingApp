package com.example.messagingappspring.mongoController;

import com.example.messagingappspring.DTO.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RestController()
@RequestMapping("mongo")
public class MongoUtil {
    public static MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    public static MongoDatabase database = mongoClient.getDatabase("messagingappdb");
    public static MongoCollection<Document> userCollection = database.getCollection("user_info");
    public static MongoCollection<Document> chatCollection = database.getCollection("chat");



    public static Document findUser(String key, String value) {
        return userCollection.find(Filters.eq(key,value)).first();
    }

    public static Document findUserById(String value) {
        return userCollection.find(Filters.eq("user_id",Integer.parseInt(value))).first();
    }

    public static Document loginValidation(String userName, String userPassword) {
        return userCollection.find(Filters.and(Filters.eq("user_name",userName), Filters.eq("user_password",userPassword))).first();
    }

    public static List<ChatDTO> getAllChatsAsChatDTO() {
        List<ChatDTO> allChatDTOs = new ArrayList<>();
        FindIterable<Document> allChats = chatCollection.find();
        for(Document chat : allChats){
            allChatDTOs.add( new ChatDTO(chat.getLong("chat_id").toString(), chat.getString("chat_description"), chat.getString("chat_name"), chat.getInteger("creator_id").toString()));
        }
        return allChatDTOs;
    }

    public static ChatDTO getChatAsChatDTO(Document chat){
        return new ChatDTO(chat.getLong("chat_id").toString(), chat.getString("chat_description"), chat.getString("chat_name"), chat.getInteger("creator_id").toString());
    }

    public static Integer getActiveUsers(Document chat){
        Set<Integer> activeUsers = new HashSet<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        List<Document> messages = (List<Document>) chat.get("messages");
        if(messages == null){
            return 0;
        }
        for(Document message : messages){
            Date date = message.getDate("sent_time");
            if(date.after(cal.getTime())){
                activeUsers.add(message.getInteger("sender_id"));
            }
        }
        return activeUsers.size();
    }

    public static Document findChat(String key, String value) {
        return chatCollection.find(Filters.eq(key,Integer.parseInt(value))).first();
    }

}
