package com.example.messagingappspring.mongoController;

import com.example.messagingappspring.DTO.ChatDTO;
import com.example.messagingappspring.DTO.MessageDTO;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
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

    @CrossOrigin
    @RequestMapping("/addMemberToChat")
    public void addMemberToChat(@RequestParam String chatId, @RequestParam String memberId) {
        Document userById = MongoUtil.findUserById(memberId);
        Document foundChat = MongoUtil.findChat("chat_id", chatId);
        boolean isAlreadyMember = userCollection.find(Filters.and(Filters.eq("user_id", Integer.parseInt(memberId)), Filters.in("chats", Integer.parseInt(chatId)))).first() != null;
        if (isAlreadyMember) {
            return;
        }
        userCollection.updateOne(userById, Updates.push("chats", Integer.parseInt(chatId)));
        if (foundChat != null) {
            chatCollection.updateOne(foundChat, Updates.push("users", Integer.parseInt(memberId)));
        }
    }

    @CrossOrigin
    @RequestMapping("/addNewChat")
    public ChatDTO addNewChat(@RequestBody ChatDTO chat) {
        Document foundUser = MongoUtil.findUserById(String.valueOf(chat.getCreatorId()));
        Document chatFound = chatCollection.find(Filters.and(Filters.eq("chat_name", chat.getChatName()), Filters.eq("creator_id", chat.getCreatorId()))).first();
        if (chatFound != null || foundUser.get("is_admin") == null) {
            return null;
        }

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
            userCollection.updateOne(userById, Updates.push("chats", (int) chatId));
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
            Document chat = MongoUtil.findChat("chat_id", String.valueOf(obj));
            if (chat != null) {
                chats.add(new ChatDTO(chat.getLong("chat_id").toString(), chat.getString("chat_description"), chat.getString("chat_name"), chat.getInteger("creator_id").toString()));
            }
        }
        return chats;
    }

    @CrossOrigin
    @RequestMapping("/checkIfMember")
    public boolean checkIfMember(@RequestParam String chatId, @RequestParam String memberId) {
        return chatCollection.find(Filters.and(Filters.eq("chat_id", Integer.parseInt(chatId)), Filters.in("users", memberId))).first() == null;
    }

    @CrossOrigin
    @RequestMapping("/addMessage")
    public void addMessage(@RequestBody MessageDTO message) {
        Document doc =
                new Document("chat_id", Integer.parseInt(message.getChatId()))
                        .append("content", message.getContent())
                        .append("sender_id", Integer.parseInt(message.getSenderId()))
                        .append("sent_time", new Date());
        Document chatById = MongoUtil.findChat("chat_id", message.getChatId());
        if (chatById != null) {
            chatCollection.updateOne(chatById, Updates.push("messages", doc));
        }
    }

    @CrossOrigin
    @RequestMapping("/getMessages")
    public List<MessageDTO> getMessages(@RequestParam String chatId) {
        List<MessageDTO> messages = new ArrayList<>();
        List<Document> foundMessages = (List<Document>) MongoUtil.findChat("chat_id", chatId).get("messages");
        if (foundMessages == null) {
            return messages;
        }
        for (Document obj : foundMessages) {
            messages.add(new MessageDTO(obj.getInteger("chat_id").toString(), obj.getString("content"), obj.getInteger("sender_id").toString()));
        }
        return messages;
    }

    @CrossOrigin
    @RequestMapping("/getMemberIdsOfGivenChat")
    public List<String> getMemberIdsOfGivenChat(@RequestParam String chatId) {
        List<String> members = new ArrayList<>();
        List<Object> users = (List<Object>) MongoUtil.findChat("chat_id", chatId).get("users");
        for (Object obj : users) {
            members.add(obj.toString());
        }
        return members;
    }

    @CrossOrigin
    @RequestMapping("/getChatUsingNameAndCreatorId")
    ChatDTO getChatUsingNameAndCreatorId(@RequestParam String chatName, @RequestParam String creatorId) throws InterruptedException {
        // wait for 2 seconds so that new created chat can be saved async to the database
        Thread.sleep(2000);
        Document foundChat = chatCollection.find(Filters.and(Filters.eq("chat_name", chatName), Filters.eq("creator_id", Integer.parseInt(creatorId)))).first();
        return new ChatDTO(foundChat.getLong("chat_id").toString(), foundChat.getString("chat_description"), foundChat.getString("chat_name"), foundChat.getInteger("creator_id").toString());
    }

}
