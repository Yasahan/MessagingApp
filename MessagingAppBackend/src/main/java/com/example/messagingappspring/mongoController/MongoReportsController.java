package com.example.messagingappspring.mongoController;


import com.example.messagingappspring.DTO.ChatDTO;
import com.example.messagingappspring.DTO.FirstReportDTO;
import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
        HashMap<ChatDTO, Integer> chats = new LinkedHashMap<>();
        for (Document chat : chatCollection.find()) {
            chats.put(MongoUtil.getChatAsChatDTO(chat), MongoUtil.getActiveUsers(chat));
        }
        Iterator<Map.Entry<ChatDTO, Integer>> it = chats.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ChatDTO, Integer> chat = it.next();
            report.add(new FirstReportDTO(chat.getKey().getChatId(), chat.getKey().getChatName(), chat.getKey().getCreatorId(), chat.getValue().toString()));
            it.remove();
        }
        return report;
    }


    @CrossOrigin
    @GetMapping("/secondReport")
    public List<FirstReportDTO> secondReport() {
        for (Document chat : chatCollection.find()) {
            List<Object> members = (List<Object>) chat.get("members");
        }
        return null;
    }
}
