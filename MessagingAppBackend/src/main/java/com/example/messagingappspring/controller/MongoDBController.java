package com.example.messagingappspring.controller;

import com.example.messagingappspring.DTO.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController()
@RequestMapping("mongo")
public class MongoDBController {
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("messagingappdb");
    MongoCollection<Document> userCollection = database.getCollection("user_info");
    MongoCollection<Document> chatCollection = database.getCollection("chat");
    MongoCollection<Document> hobbiesCollection = database.getCollection("hobbies");


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
    public List<UserInfoDTO> getUsers() {
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
    @RequestMapping("/getUser")
    public UserInfoDTO getUser(@RequestBody UserInfoDTO user) {
        Document userById = loginValidation(user.getUserName(), user.getUserPassword());
        if (userById != null) {
            return new UserInfoDTO(Math.toIntExact(userById.getLong("user_id")), userById.getString("user_name"), userById.getString("user_password"));
        }
        return null;
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
        Document userById = findUser("user_id",memberId);
        userCollection.updateOne(userById, Updates.set("chat_id", chatId));
    }


    @CrossOrigin
    @RequestMapping("/addNewChat")
    public ChatDTO addNewChat(@RequestBody ChatDTO chat) {
        long chatId = chatCollection.countDocuments() + 1;
        Document doc =
                new Document("chat_id", chatId)
                        .append("chat_name", chat.getChatName())
                        .append("creator_id", chat.getCreatorId())
                        .append("creation_date", new Date())
                        .append("chat_description", chat.getChatDescription());
        chatCollection.insertOne(doc);
        chatCollection.updateOne(doc, Updates.push("users", chat.getCreatorId()));

        Document userById = findUser("user_id",chat.getCreatorId());
        userCollection.updateOne(userById, Updates.push("chat_id", chatId));

        return new ChatDTO(Integer.toString((int) (chatCollection.countDocuments() + 1)), chat.getChatDescription(), chat.getChatName(), chat.getCreatorId());
    }

    @CrossOrigin
    @RequestMapping("/addMessage")
    public void addMessage(@RequestBody MessageDTO message) {
        Document doc =
                new Document("chat_id", message.getChatId())
                        .append("content", message.getContent())
                        .append("sender_id", message.getSenderId());
        Document chatById = findChat("chat_id", message.getChatId());
        chatCollection.updateOne(chatById, Updates.push("messages", doc));
    }

    @CrossOrigin
    @RequestMapping("/getMessages")
    public List<MessageDTO> getMessages(@RequestParam String chatId) {
        List<MessageDTO> messages = new ArrayList<>();
        Document document = findChat("chat_id", chatId);
        List<Object> foundMessages = (List<Object>) document.get("users");
        for (Object obj : foundMessages) {
            messages.add(new MessageDTO(document.getString("chat_id"), document.getString("content"), document.getString("sender_id")));
        }
        return messages;
    }

    @CrossOrigin
    @RequestMapping("/getChatUsingNameAndCreatorId")
    ChatDTO getChatUsingNameAndCreatorId(@RequestParam String chatName, @RequestParam String creatorId) {
        FindIterable<Document> iterDoc = userCollection.find();
        for (Document document : iterDoc) {
            if (document.getString("chat_name").equals(chatName) && document.getString("creator_id").equals(creatorId)) {
                return new ChatDTO(document.getString("chat_id"), document.getString("chat_description"), document.getString("chat_name"), document.getString("creator_id"));
            }
        }
        return null;
    }

    /**
     * Different approach then mysql database, we dont save user_id under name "user_id" but "is_admin"
     * so to check if a user is admin or not just search this users id with key "is_admin"
     *
     * @param admin
     */
    @CrossOrigin
    @RequestMapping("/addAdmin")
    public void addAdmin(@RequestBody AdminInfoDTO admin) {
        Document doc =
                new Document("is_admin", admin.getUserId())
                        .append("birthdate", admin.getUserBirthdate())
                        .append("email", admin.getUserEmail());
        userCollection.insertOne(doc);
    }


    @CrossOrigin
    @RequestMapping("/getHobbies")
    public List<HobbyDTO> getHobbies() {
        List<HobbyDTO> hobbies = new ArrayList<>();
        FindIterable<Document> iterDoc = hobbiesCollection.find();
        for (Document document : iterDoc) {
            hobbies.add(new HobbyDTO(document.getString("hobby_id"), document.getString("hobby_name"), document.getString("hobby_description")));
        }
        return hobbies;
    }

    @CrossOrigin
    @RequestMapping("/getMemberIdsOfGivenChat")
    public List<String> getMemberIdsOfGivenChat(@RequestParam String chatId) {
        List<String> members = new ArrayList<>();
        Document document = findChat("chat_id", chatId);
        List<Object> users = (List<Object>) document.get("users");

        for (Object obj : users) {
            members.add(obj.toString());
        }
        return members;
    }

    public Document findChat(String key, String value) {
        FindIterable<Document> iterDoc = chatCollection.find();
        for (Document document : iterDoc) {
            if (document.getLong(key).toString().equals(value)) {
                return document;
            }
        }
        return null;
    }

    public Document findUser(String key, String value) {
        FindIterable<Document> iterDoc = userCollection.find();
        for (Document document : iterDoc) {
            if (document.getLong(key).toString().equals(value)) {
                return document;
            }
        }
        return null;
    }

    public Document loginValidation(String userName, String userPassword) {
        FindIterable<Document> iterDoc = userCollection.find();
        for (Document document : iterDoc) {
            if (document.getString("user_name").equals(userName) && document.getString("user_password").equals(userPassword)) {
                return document;
            }
        }
        return null;
    }

}
