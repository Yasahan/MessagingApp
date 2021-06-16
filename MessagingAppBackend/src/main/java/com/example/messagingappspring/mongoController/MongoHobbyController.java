package com.example.messagingappspring.mongoController;

import com.example.messagingappspring.DTO.HobbyDTO;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController()
@RequestMapping("mongo")
public class MongoHobbyController {

    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("messagingappdb");
    MongoCollection<Document> hobbiesCollection = database.getCollection("hobbies");
    MongoCollection<Document> userCollection = database.getCollection("user_info");

    @CrossOrigin
    @RequestMapping("/getHobbies")
    public List<HobbyDTO> getHobbies() {
        List<HobbyDTO> hobbies = new ArrayList<>();
        for (Document document : hobbiesCollection.find()) {
            hobbies.add(new HobbyDTO(document.getLong("hobby_id").toString(), document.getString("hobby_name"), document.getString("hobby_description")));
        }
        return hobbies;
    }

    @CrossOrigin
    @RequestMapping("/addChoice")
    public void addChoice(@RequestParam String hobbyId, @RequestParam String userId) {
        Document user = MongoUtil.findUserById(userId);
        if (!isHobbyAlreadyPresent(hobbyId, userId) && user != null) {
            userCollection.updateOne(user, Updates.push("hobbies", Integer.parseInt(hobbyId)));
        }
    }

    private boolean isHobbyAlreadyPresent(String hobbyId, String userId) {
        return userCollection.find(Filters.and(Filters.eq("user_id", Integer.parseInt(userId)), Filters.in("hobbies", Integer.parseInt(hobbyId)))).first() != null;
    }
}
