package com.example.messagingappspring.controller;
import com.example.messagingappspring.DTO.ChatDTO;
import com.example.messagingappspring.DTO.MessageDTO;
import com.example.messagingappspring.DTO.UserInfoDTO;
import com.example.messagingappspring.database.DatabaseConnection;
import org.aspectj.bridge.Message;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RestController
public class ChatController {

    DatabaseConnection databaseConnection = DatabaseConnection.getInstance();

    @CrossOrigin
    @RequestMapping("/addMemberToChat")
    public void addMemberToChat(@RequestParam String chatId, @RequestParam String memberId) {
        try {
            System.out.println("INSERT INTO is_member(chat_id, member_id) VALUES(" + chatId + ", " + memberId + ")");
            databaseConnection.createStatement().executeUpdate("INSERT INTO is_member(chat_id, member_id) VALUES(" + chatId + ", " + memberId + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @CrossOrigin
    @RequestMapping("/getChatsForUserId")
    public List<ChatDTO> getChatsForUserId(@RequestParam String userId) {
        List<ChatDTO> chats = new ArrayList<>();
        try {
            List<String> chatIds = new ArrayList<>();
            ResultSet rs = databaseConnection.createStatement().executeQuery("select * from is_member where member_id="+userId);
            while(rs.next()) {
                chatIds.add(String.valueOf(rs.getInt(1)));
            }
            for(String chatId : chatIds) {
                ResultSet resultSet = databaseConnection.createStatement().executeQuery("select * from chat where chat_id=" + chatId);
                while (resultSet.next()) {
                    chats.add(new ChatDTO(String.valueOf(resultSet.getInt(1)), resultSet.getString(3), resultSet.getString(4), String.valueOf(resultSet.getInt(5))));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return chats;
    }

    @CrossOrigin
    @RequestMapping("/checkIfMember")
    public boolean checkIfMember(@RequestParam String chatId, @RequestParam String memberId) {
        try {
            ResultSet resultSet = databaseConnection.createStatement().executeQuery("select * from is_member where member_id=" + memberId + " and chat_id="+chatId);
            while (resultSet.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @CrossOrigin
    @GetMapping("/getChats")
    public List<ChatDTO> getAllChats() {
        List<ChatDTO> chats = new ArrayList<>();
        try {
            ResultSet resultSet = databaseConnection.createStatement().executeQuery("select * from chat");
            while (resultSet.next()) {
                chats.add(new ChatDTO(String.valueOf(resultSet.getInt(1)), resultSet.getString(3), resultSet.getString(4), String.valueOf(resultSet.getInt(5))));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return chats;
    }

    @CrossOrigin
    @RequestMapping("/getMemberIdsOfGivenChat")
    public List<String> getMemberIdsOfGivenChat(@RequestParam String chatId) {
        List<String> members = new ArrayList<>();
        try {
            ResultSet resultSet = databaseConnection.createStatement().executeQuery("select * from is_member where chat_id=" + chatId);
            while (!resultSet.isClosed() && resultSet.next()) {
                members.add(String.valueOf(resultSet.getInt(2)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return members;
    }

    @CrossOrigin
    @RequestMapping("/getMessages")
    public List<MessageDTO> getMessages(@RequestParam String chatId) {
        List<MessageDTO> messages = new ArrayList<>();
        try {
            String sql = "select * from message where chat_id=" + chatId;
            ResultSet rs = databaseConnection.createStatement().executeQuery(sql);
            while (rs.next()) {
                MessageDTO msg = new MessageDTO(String.valueOf(rs.getInt(2)), rs.getString(4), String.valueOf(rs.getInt(5)));
                messages.add(msg);
            }
            return messages;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @CrossOrigin
    @RequestMapping("/addNewChat")
    void addNewChat(@RequestBody ChatDTO chat) {
        try {
            databaseConnection.createStatement().executeUpdate("INSERT INTO chat (chat_description, chat_name, creator_id) " +
                    "VALUES (" + "'" + chat.getChatDescription() + "' , '" + chat.getChatName() + "', " + chat.getCreatorId() + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @CrossOrigin
    @RequestMapping("/getChatUsingNameAndCreatorId")
    ChatDTO getChatUsingNameAndCreatorId(@RequestParam String chatName, @RequestParam String creatorId) {
        try {
            ResultSet resultSet = databaseConnection.createStatement().executeQuery("SELECT * FROM chat WHERE chat_name=" + "'" + chatName + "' AND creator_id=" + creatorId);

            while (resultSet.next()) {
                return new ChatDTO(String.valueOf(resultSet.getInt(1)), resultSet.getString(3), resultSet.getString(4), String.valueOf(resultSet.getInt(5)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @CrossOrigin
    @RequestMapping("/addMessage")
    public void addMessage(@RequestBody MessageDTO message){
        try {
            databaseConnection.createStatement().executeUpdate(
                    "INSERT INTO message(chat_id, content, sender_id) VALUES(" +
                            message.getChatId() + ", " +
                      "\"" + message.getContent() + "\", "+ message.getSenderId()+")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
