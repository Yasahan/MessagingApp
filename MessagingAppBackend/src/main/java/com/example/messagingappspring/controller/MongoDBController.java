package com.example.messagingappspring.controller;

import com.example.messagingappspring.DTO.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RestController()
@RequestMapping("mongo")
public class MongoDBController {
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("messagingappdb");
    MongoCollection<Document> userCollection = database.getCollection("user_info");
    MongoCollection<Document> chatCollection = database.getCollection("chat");
    MongoCollection<Document> hobbiesCollection = database.getCollection("hobbies");


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
            it.remove(); // avoids a ConcurrentModificationException
            Document doc =
                    new Document("hobby_id", hobbiesCollection.countDocuments() + 1)
                            .append("hobby_name", hobbyName)
                            .append("hobby_description", hobbyDescription);
            hobbiesCollection.insertOne(doc);
        }
    }

    @CrossOrigin
    @RequestMapping("/addUser")
    public UserInfoDTO addUser(@RequestBody UserInfoDTO user) {
        Document foundUser = findUser("user_name", user.getUserName());
        if (foundUser != null) {
            new UserInfoDTO(Math.toIntExact(foundUser.getLong("user_id")), user.getUserName(),
                    user.getUserPassword());
        }
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
    @RequestMapping("/getAdminForLogin")
    public UserInfoDTO getAdminForLogin(@RequestBody UserInfoDTO user) {
        Document foundUser = findUserById(String.valueOf(user.getUserId()));
        if (foundUser.get("is_admin") != null) {
            return user;
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
        Document userById = findUserById(memberId);
        if (userById != null) {
            userCollection.updateOne(userById, Updates.push("chats", Integer.parseInt(chatId)));
        }
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
        Document userById = findUserById(chat.getCreatorId());
        if (userById != null) {
            userCollection.updateOne(userById, Updates.push("chats", chatId));
        }

        return new ChatDTO(Integer.toString((int) (chatCollection.countDocuments() + 1)), chat.getChatDescription(), chat.getChatName(), chat.getCreatorId());
    }

    @CrossOrigin
    @RequestMapping("/getChatsForUserId")
    public List<ChatDTO> getChatsForUserId(@RequestParam String userId) {
        List<ChatDTO> chats = new ArrayList<>();
        Document foundUser = findUserById(userId);
        List<Object> foundChats = (List<Object>) foundUser.get("chats");
        if (foundChats == null) {
            return chats;
        }
        for (Object obj : foundChats) {
            Document chat = findChat("chat_id", String.valueOf(obj));
            chats.add(new ChatDTO(chat.getLong("chat_id").toString(), chat.getString("chat_description"), chat.getString("chat_name"), chat.getString("creator_id")));
        }
        return chats;
    }

    private Document findUserById(String value) {
        FindIterable<Document> iterDoc = userCollection.find();
        for (Document document : iterDoc) {
            if (document.getLong("user_id") == null) {
                return null;
            }
            if (document.getLong("user_id").toString().equals(value)) {
                return document;
            }
        }
        return null;
    }

    @CrossOrigin
    @RequestMapping("/addMessage")
    public void addMessage(@RequestBody MessageDTO message) {
        Document doc =
                new Document("chat_id", Integer.parseInt(message.getChatId()))
                        .append("content", message.getContent())
                        .append("sender_id", message.getSenderId());
        Document chatById = findChat("chat_id", message.getChatId());
        chatCollection.updateOne(chatById, Updates.push("messages", doc));
    }

    @CrossOrigin
    @RequestMapping("/getMessages")
    public List<MessageDTO> getMessages(@RequestParam String chatId) {
        List<MessageDTO> messages = new ArrayList<>();
        Document foundChat = findChat("chat_id", chatId);
        List<Document> foundMessages = (List<Document>) foundChat.get("messages");
        if (foundMessages == null) {
            return messages;
        }
        for (Document obj : foundMessages) {
            messages.add(new MessageDTO(obj.getInteger("chat_id").toString(), obj.getString("content"), obj.getString("sender_id")));
        }
        return messages;
    }

    @CrossOrigin
    @RequestMapping("/getChatUsingNameAndCreatorId")
    ChatDTO getChatUsingNameAndCreatorId(@RequestParam String chatName, @RequestParam String creatorId) {
        FindIterable<Document> iterDoc = chatCollection.find();
        for (Document document : iterDoc) {
            if (document.getString("creator_id").equals(creatorId)) {
                return new ChatDTO(document.getLong("chat_id").toString(), document.getString("chat_description"), document.getString("chat_name"), document.getString("creator_id"));
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
        Document foundUser = findUserById(String.valueOf(admin.getUserId()));
        Document doc =
                new Document("is_admin", admin.getUserId())
                        .append("birthdate", admin.getUserBirthdate())
                        .append("email", admin.getUserEmail());
        userCollection.updateOne(foundUser, Updates.set("is_admin", doc));
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
        Document user = findUserById(userId);
        if (!isHobbyAlreadyPresent(hobbyId, userId) && user != null) {
            userCollection.updateOne(user, Updates.push("hobbies", hobbyId));
        }
    }

    private boolean isHobbyAlreadyPresent(String hobbyId, String userId) {
        Document user = findUserById(userId);
        List<Object> hobbies = (List<Object>) user.get("hobbies");
        if(hobbies == null){
            return false;
        }
        for (Object obj : hobbies) {
            if(obj.toString().equals(hobbyId)){
                return true;
            }
        }
        return false;
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

    @CrossOrigin
    @RequestMapping("/getViaName")
    public UserInfoDTO getViaName(@RequestBody String userName) {
        Document foundUser = findUser("user_name", userName);
        if (foundUser != null) {
            return new UserInfoDTO(Math.toIntExact(foundUser.getLong("user_id")), foundUser.getString("user_name"), foundUser.getString("user_password"));
        }
        return null;
    }

    @CrossOrigin
    @RequestMapping("/addFriend")
    public void addFriend(@RequestParam String userId, @RequestParam String friendId) {
        Document user = findUserById(userId);
        Document friend = findUserById(friendId);
        if (user != null) {
            userCollection.updateOne(user, Updates.push("friends", Integer.parseInt(friendId)));
        }
        if (friend != null) {
            userCollection.updateOne(friend, Updates.push("friends", Integer.parseInt(userId)));
        }
    }

    @CrossOrigin
    @RequestMapping("/getFriends")
    public List<UserInfoDTO> getFriends(@RequestParam String userId) {
        List<UserInfoDTO> friends = new ArrayList<>();
        Document user = findUserById(userId);
        List<Object> foundFriends = (List<Object>) user.get("friends");
        if (foundFriends == null) {
            return friends;
        }
        for (Object obj : foundFriends) {
            Document friend = findUserById(String.valueOf(obj));
            friends.add(new UserInfoDTO(Math.toIntExact(friend.getLong("user_id")), friend.getString("user_name"), friend.getString("user_password")));
        }
        return friends;
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
            if (document.getString(key) == null) {
                return null;
            }
            if (document.getString(key).equals(value)) {
                return document;
            }
        }
        return null;
    }

    public Document loginValidation(String userName, String userPassword) {
        FindIterable<Document> iterDoc = userCollection.find();
        for (Document document : iterDoc) {
            if (document.getString("user_name") != null && document.getString("user_name").equals(userName) && document.getString("user_password").equals(userPassword)) {
                return document;
            }
        }
        return null;
    }

}
