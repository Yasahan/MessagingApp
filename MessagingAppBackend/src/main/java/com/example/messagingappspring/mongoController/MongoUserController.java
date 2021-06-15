package com.example.messagingappspring.mongoController;

import com.example.messagingappspring.DTO.AdminInfoDTO;
import com.example.messagingappspring.DTO.UserInfoDTO;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController()
@RequestMapping("mongo")
public class MongoUserController {
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("messagingappdb");
    MongoCollection<Document> userCollection = database.getCollection("user_info");

    @CrossOrigin
    @RequestMapping("/addUser")
    public UserInfoDTO addUser(@RequestBody UserInfoDTO user) {
        Document foundUser = MongoUtil.findUser("user_name", user.getUserName());
        if (foundUser != null) {
            return null;
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
        Document userById = MongoUtil.loginValidation(user.getUserName(), user.getUserPassword());
        if (userById != null) {
            return new UserInfoDTO(Math.toIntExact(userById.getLong("user_id")), userById.getString("user_name"), userById.getString("user_password"));
        }
        return null;
    }

    @CrossOrigin
    @RequestMapping("/getAdminForLogin")
    public UserInfoDTO getAdminForLogin(@RequestBody UserInfoDTO user) {
        Document foundUser = MongoUtil.findUserById(String.valueOf(user.getUserId()));
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

    /**
     * Different approach then mysql database, we dont save user_id under name "user_id" but "is_admin"
     * so to check if a user is admin or not just search this users id with key "is_admin"
     *
     * @param admin
     */
    @CrossOrigin
    @RequestMapping("/addAdmin")
    public void addAdmin(@RequestBody AdminInfoDTO admin) {
        Document foundUser = MongoUtil.findUserById(String.valueOf(admin.getUserId()));
        Document doc =
                new Document("is_admin", admin.getUserId())
                        .append("birthdate", admin.getUserBirthdate())
                        .append("email", admin.getUserEmail());
        userCollection.updateOne(foundUser, Updates.set("is_admin", doc));
    }

    @CrossOrigin
    @RequestMapping("/getFriends")
    public List<UserInfoDTO> getFriends(@RequestParam String userId) {
        List<UserInfoDTO> friends = new ArrayList<>();
        Document user = MongoUtil.findUserById(userId);
        List<Object> foundFriends = (List<Object>) user.get("friends");
        if (foundFriends == null) {
            return friends;
        }
        for (Object obj : foundFriends) {
            Document friend = MongoUtil.findUserById(String.valueOf(obj));
            if (friend != null) {
                friends.add(new UserInfoDTO(Math.toIntExact(friend.getLong("user_id")), friend.getString("user_name"), friend.getString("user_password")));
            }
        }
        return friends;
    }

    @CrossOrigin
    @RequestMapping("/getViaName")
    public UserInfoDTO getViaName(@RequestBody String userName) {
        Document foundUser = MongoUtil.findUser("user_name", userName);
        if (foundUser != null) {
            return new UserInfoDTO(Math.toIntExact(foundUser.getLong("user_id")), foundUser.getString("user_name"), foundUser.getString("user_password"));
        }
        return null;
    }

    @CrossOrigin
    @RequestMapping("/addFriend")
    public void addFriend(@RequestParam String userId, @RequestParam String friendId) {
        Document user = MongoUtil.findUserById(userId);
        Document friend = MongoUtil.findUserById(friendId);
        if(isAlreadyFriend(userId,friendId)){
            return;
        }
        if (user != null) {
            userCollection.updateOne(user, Updates.push("friends", Integer.parseInt(friendId)));
        }
        if (friend != null) {
            userCollection.updateOne(friend, Updates.push("friends", Integer.parseInt(userId)));
        }
    }

    boolean isAlreadyFriend(String userId, String friendId){
        Document user = MongoUtil.findUserById(userId);
        List<Object> friends = (List<Object>) user.get("friends");
        if(friends == null){
            return false;
        }
        for(Object friend : friends){
            if(friend.toString().equals(friendId)){
                return true;
            }
        }
        return false;
    }




}
