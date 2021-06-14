package com.example.messagingappspring.mongoController;

import com.example.messagingappspring.DTO.ChatDTO;
import com.example.messagingappspring.DTO.MessageDTO;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController()
@RequestMapping("mongo")
public class MongoChatController {
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("messagingappdb");
    MongoCollection<Document> userCollection = database.getCollection("user_info");
    MongoCollection<Document> chatCollection = database.getCollection("chat");
    MongoCollection<Document> hobbiesCollection = database.getCollection("hobbies");

    @CrossOrigin
    @RequestMapping("/addMemberToChat")
    public void addMemberToChat(@RequestParam String chatId, @RequestParam String memberId) {
        Document userById = MongoUtil.findUserById(memberId);
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
                        .append("creator_id", Integer.parseInt(chat.getCreatorId()))
                        .append("creation_date", new Date())
                        .append("chat_description", chat.getChatDescription());
        chatCollection.insertOne(doc);
        chatCollection.updateOne(doc, Updates.push("users", Integer.parseInt(chat.getCreatorId())));
        Document userById = MongoUtil.findUserById(chat.getCreatorId());
        if (userById != null) {
            userCollection.updateOne(userById, Updates.push("chats", chatId));
        }

        return new ChatDTO(Integer.toString((int) (chatCollection.countDocuments() + 1)), chat.getChatDescription(), chat.getChatName(), chat.getCreatorId());
    }

    @CrossOrigin
    @RequestMapping("/getChatsForUserId")
    public List<ChatDTO> getChatsForUserId(@RequestParam String userId) {
        List<ChatDTO> chats = new ArrayList<>();
        Document foundUser = MongoUtil.findUserById(userId);
        List<Object> foundChats = (List<Object>) foundUser.get("chats");
        if (foundChats == null) {
            return chats;
        }
        for (Object obj : foundChats) {
            Document chat = findChat("chat_id", String.valueOf(obj));
            chats.add(new ChatDTO(chat.getLong("chat_id").toString(), chat.getString("chat_description"), chat.getString("chat_name"), chat.getInteger("creator_id").toString()));
        }
        return chats;
    }

    @CrossOrigin
    @RequestMapping("/checkIfMember")
    public boolean checkIfMember(@RequestParam String chatId, @RequestParam String memberId) {
        Document foundUser = MongoUtil.findUserById(memberId);
        List<Object> foundChats = (List<Object>) foundUser.get("chats");
        if (foundChats == null) {
            return false;
        }
        for (Object obj : foundChats) {
            if(obj.toString().equals(chatId)){
                return true;
            }
        }
        return false;
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




}
