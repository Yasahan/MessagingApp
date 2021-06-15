package com.example.messagingappspring.database;

import com.example.messagingappspring.DTO.AdminInfoDTO;
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
public class PopulateMongoDB {

    DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("messagingappdb");
    MongoCollection<Document> userCollection = database.getCollection("user_info");
    MongoCollection<Document> hobbiesCollection = database.getCollection("hobbies");


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

        for(Document user : users){
            try {
                ResultSet resultSet = databaseConnection.createStatement().executeQuery("select * from admin_info where user_id = '"
                        + user.getInteger("user_id") + "'");
                while (resultSet.next()) {
                    admins.add(new AdminInfoDTO(resultSet.getInt(1), resultSet.getString(2),
                            resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

/*        int counter = 0;
        for (AdminInfoDTO admin : admins) {
            Document doc =
                    new Document()
                            .append("birthdate", admin.getUserBirthdate())
                            .append("email", admin.getUserEmail());
            userCollection.updateOne(user, Updates.set("is_admin", doc));
            counter++;
            if(counter == 250){
                return;
            }
        }*/
    }

    void addFriends(){

/*        Document user = MongoUtil.findUserById(userId);
        Document friend = MongoUtil.findUserById(friendId);
        if(isAlreadyFriend(userId,friendId)){
            return;
        }
        if (user != null) {
            userCollection.updateOne(user, Updates.push("friends", Integer.parseInt(friendId)));
        }
        if (friend != null) {
            userCollection.updateOne(friend, Updates.push("friends", Integer.parseInt(userId)));
        }*/
    }


}
