package com.example.messagingappspring.controller;

import com.example.messagingappspring.database.DBPopulate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PopulateController {

    @CrossOrigin
    @GetMapping("/populateDB")
    public String populateDB() {
        DBPopulate.populate();
        return "Success";
    }
}
