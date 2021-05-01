import { Component, OnInit } from '@angular/core';
import {User} from "../../model/User";

@Component({
  selector: 'new-chat-room',
  templateUrl: './new-chat-room.component.html',
  styleUrls: ['./new-chat-room.component.css']
})
export class NewChatRoomComponent implements OnInit {

  users:User[] = [];
  constructor() { }

  ngOnInit(): void {
    let user1 = new User();
    user1.username = "User 21";
    let user2 = new User();
    user2.username = "User 32";
    let user3 = new User();
    user3.username = "User 51";
    this.users.push(user1,user2,user3);
  }

}