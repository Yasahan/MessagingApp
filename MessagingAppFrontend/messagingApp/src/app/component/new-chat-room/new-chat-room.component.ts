import { Component, OnInit } from '@angular/core';
import {User} from "../../model/User";
import {UserService} from "../../service/UserService";
import {ChatService} from "../../service/ChatService";
import {ChatDTO} from "../../model/ChatDTO";
import {SessionService} from "../../service/SessionService";
import {Admin} from "../../model/Admin";
import {Router} from "@angular/router";
import {FriendService} from "../../service/FriendService";

@Component({
  selector: 'new-chat-room',
  templateUrl: './new-chat-room.component.html',
  styleUrls: ['./new-chat-room.component.css']
})
export class NewChatRoomComponent implements OnInit {

  users:User[] = [];
  usersForNewChatRoom:User[] = []
  constructor(private router: Router, private userService: UserService, private chatService:ChatService, private friendService:FriendService) { }

  ngOnInit(): void {
    if(SessionService.getCurrentUser() === undefined) {
      this.router.navigate(['/login']);
    }
    this.friendService.getFriends().then(friends => {
      for(let user in friends) {
        if(SessionService.getCurrentUser().userId != friends[user].userId) {
          this.users.push(friends[user]);
        }
      }
    });
  }

  addUserToNewChat(user: User) {
    console.log("Clicked on a user")
    if(this.usersForNewChatRoom.includes(user)){
      const index = this.usersForNewChatRoom.indexOf(user, 0);
      if (index > -1) {
        this.usersForNewChatRoom.splice(index, 1);
      }
    } else {
      this.usersForNewChatRoom.push(user);
    }
  }

  getMembers() {

  }

  createNewChatRoom(form:any) {
    let chat:ChatDTO = new ChatDTO();
    chat.chatName = form.chatName;
    chat.chatDescription = form.chatDescription;
    chat.creatorId = SessionService.getCurrentUser().userId;
    if(SessionService.isAdmin) {
      this.chatService.addNewChat(chat).then(foundChat => {

      });
      this.chatService.getChatUsingNameAndCreatorId(chat).then(foundChat=>{
        if(foundChat!=null) {
          this.chatService.addMemberToChat(foundChat.chatId, foundChat.creatorId).then();
          for (let member in this.usersForNewChatRoom) {
            console.log("checkpoint " + this.usersForNewChatRoom[member].userId)
            this.chatService.addMemberToChat(foundChat.chatId, this.usersForNewChatRoom[member].userId).then();
          }
        }
      })
    }
    else {
      alert("Only admins can create chatrooms");
    }

    this.router.navigate(['chatMenu/']);
  }
}
