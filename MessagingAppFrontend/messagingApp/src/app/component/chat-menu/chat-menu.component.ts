import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {ChatService} from "../../service/ChatService";
import {ChatDTO} from "../../model/ChatDTO";
import {SessionService} from "../../service/SessionService";

@Component({
  selector: 'chat-menu',
  templateUrl: './chat-menu.component.html',
  styleUrls: ['./chat-menu.component.css']
})
export class ChatMenuComponent implements OnInit {

  chats:ChatDTO[] = []
  constructor(private router:Router, private chatService:ChatService) { }

  ngOnInit(): void {
    if(SessionService.getCurrentUser() === undefined) {
      this.router.navigate(['/login']);
    }
    this.chatService.getChatsForUserId().then(chats => {
      this.chats = chats;
    });
  }

  createNewChat() {
    this.router.navigate(['/createChatRoom']);
  }

  chooseNewHobby() {
    this.router.navigate(['/chooseHobby']);
  }

  validateAdmin() {
    this.router.navigate(['/adminValidation']);
  }

  addNewFriend() {
    this.router.navigate(['/addFriend']);
  }

  getUserName() : string {
    return SessionService.getCurrentUser().userName;
  }

  refreshChats() {
    this.chatService.getChatsForUserId().then(chats => {
      this.chats = chats;
    });
  }
}
