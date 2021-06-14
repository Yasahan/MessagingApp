import {Component, Input, OnInit} from '@angular/core';
import {Chat} from "../../model/Chat";
import {ActivatedRoute, Router} from "@angular/router";
import {ChatDTO} from "../../model/ChatDTO";
import {ChatService} from "../../service/ChatService";
import {MessageDTO} from "../../model/MessageDTO";
import {SessionService} from "../../service/SessionService";
import {waitForAsync} from "@angular/core/testing";

@Component({
  selector: 'chat',
  templateUrl: './chat-room.component.html',
  styleUrls: ['./chat-room.component.css']
})
export class ChatRoomComponent implements OnInit {

  @Input()
  chat: ChatDTO = new ChatDTO;
  messages:MessageDTO[] = [];

  constructor(private router: Router, private route: ActivatedRoute, private chatService: ChatService) {
  }

  async ngOnInit(): Promise<void> {

    if (SessionService.getCurrentUser() === undefined) {
      this.router.navigate(['/login']);
      return;
    }

    this.route.paramMap.subscribe(params => {
      this.chat.chatName = params.get('chatName');
      this.chat.chatId = params.get('chatId');
      this.chat.chatDescription = params.get('chatDescription');
      this.chat.creatorId = params.get('creatorId');
    })
    this.chatService.getMessages(this.chat.chatId).then(messages => {
        this.messages = messages;
    });
  }

  sendMessage(messageBox: any) {
    let message = new MessageDTO();
    message.content = messageBox.value;
    message.senderId = SessionService.getCurrentUser().userId;
    message.chatId = this.chat.chatId;

    this.chatService.checkIfMember(this.chat, message.senderId).then(isMember =>{
      if(isMember) {
        this.chatService.addMessage(message).then(message => {
          this.refreshMessages();
          messageBox.reset();
        });
      }
      else {
        alert("You are not a member of this chat");
      }
    });
  }

  getCurrentUserId() {
    return SessionService.getCurrentUser().userId;
  }

  refreshMessages() {
    this.chatService.getMessages(this.chat.chatId).then(messages => {
      this.messages = messages;
    });
  }

  routeInviteFriends(chat: ChatDTO) {
    this.router.navigate(['/inviteFriends/'+chat.chatId]);
  }
}
