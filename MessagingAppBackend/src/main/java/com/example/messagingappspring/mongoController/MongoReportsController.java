package com.example.messagingappspring.mongoController;


import com.example.messagingappspring.DTO.ChatDTO;
import com.example.messagingappspring.DTO.FirstReportDTO;
import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController()
@RequestMapping("mongo")
public class MongoReportsController {

    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("messagingappdb");
    MongoCollection<Document> chatCollection = database.getCollection("chat");
    MongoCollection<Document> userCollection = database.getCollection("user_info");

    @CrossOrigin
    @GetMapping("/firstReport")
    public List<FirstReportDTO> firstReport() {
        List<FirstReportDTO> report = new ArrayList<>();
        HashMap<ChatDTO, Integer> chats = new HashMap<>();

        FindIterable<Document> allChats = chatCollection.find();
        for(Document chat : allChats){
            chats.put(MongoUtil.getChatAsChatDTO(chat),MongoUtil.getActiveUsers(chat));
        }
        return report;
    }
}
