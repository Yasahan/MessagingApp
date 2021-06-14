package com.example.messagingappspring.mongoController;

import com.example.messagingappspring.DTO.HobbyDTO;
import com.mongodb.client.*;
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
    @RequestMapping("/populateHobbies")
    public void populateHobbies() {
        HashMap<String, String> hobbies = new HashMap<>();
        hobbies.put("Tennis", "Tennis is a racket sport that can be played individually against a single opponent (singles) or between two teams of two players each (doubles).");
        hobbies.put("Football", "Just a football");
        hobbies.put("Basketball", "Just a basketball");
        hobbies.put("Golf", "Just a golf");

        Iterator<Map.Entry<String, String>> it = hobbies.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pair = it.next();
            String hobbyName = pair.getKey();
            String hobbyDescription = pair.getValue();
            it.remove();
            Document doc =
                    new Document("hobby_id", hobbiesCollection.countDocuments() + 1)
                            .append("hobby_name", hobbyName)
                            .append("hobby_description", hobbyDescription);
            hobbiesCollection.insertOne(doc);
        }
    }


    @CrossOrigin
    @RequestMapping("/getHobbies")
    public List<HobbyDTO> getHobbies() {
        List<HobbyDTO> hobbies = new ArrayList<>();
        FindIterable<Document> iterDoc = hobbiesCollection.find();
        for (Document document : iterDoc) {
            hobbies.add(new HobbyDTO(document.getLong("hobby_id").toString(), document.getString("hobby_name"), document.getString("hobby_description")));
        }
        return hobbies;
    }

    @CrossOrigin
    @RequestMapping("/addChoice")
    public void addChoice(@RequestParam String hobbyId, @RequestParam String userId) {
        Document user = MongoUtil.findUserById(userId);
        if (!isHobbyAlreadyPresent(hobbyId, userId) && user != null) {
            userCollection.updateOne(user, Updates.push("hobbies", hobbyId));
        }
    }
    private boolean isHobbyAlreadyPresent(String hobbyId, String userId) {
        Document user = MongoUtil.findUserById(userId);
        List<Object> hobbies = (List<Object>) user.get("hobbies");
        if (hobbies == null) {
            return false;
        }
        for (Object obj : hobbies) {
            if (obj.toString().equals(hobbyId)) {
                return true;
            }
        }
        return false;
    }
}
