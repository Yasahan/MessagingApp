import {Component, Input, OnInit} from '@angular/core';
import {Chat} from "../../model/Chat";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'chat',
  templateUrl: './chat-room.component.html',
  styleUrls: ['./chat-room.component.css']
})
export class ChatRoomComponent implements OnInit {

  @Input()
  chat: Chat = new Chat;
  constructor(private route: ActivatedRoute) {

  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.chat.name = params.get('name');
    })
  }


}
