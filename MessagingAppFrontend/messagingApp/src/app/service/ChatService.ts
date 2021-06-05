import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Chat} from "../model/Chat";
import {ChatDTO} from "../model/ChatDTO";
import {MessageDTO} from "../model/MessageDTO";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    Authorization: 'my-auth-token'
  })
};

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  getChatsUrl: string = 'http://localhost:8080/getChats';
  getChatUrl: string = 'http://localhost:8080/getChat/';
  addChatUrl: string = 'http://localhost:8080/addNewChat';
  addMessageUrl: string = 'http://localhost:8080/addMessage';

  constructor(private http: HttpClient) {
  }

  /*  getChat(chatRoom:ChatDTO): Observable<ChatDTO> {
      return this.http.get<ChatDTO>(this.getChatUrl,{params: {chatId: chatRoom.chatId}});
    }*/

  getChats(): Observable<ChatDTO[]> {
    return this.http.get<ChatDTO[]>(this.getChatsUrl);
  }

  addNewChat(chat: ChatDTO): Observable<ChatDTO> {
    return this.http.post<ChatDTO>(this.addChatUrl, chat, httpOptions);
  }

  sendMessage(message: MessageDTO, chat: ChatDTO): Observable<MessageDTO[]> {
    console.log(chat)
    let params = new HttpParams();
    if (chat.chatName != null) {
      params = params.append('chatName', chat.chatName);
    }
    params = params.append('messageContent', message.content);
    params = params.append('messageSender', message.senderId);
    return this.http.post<MessageDTO[]>(this.addMessageUrl +"?chatName=" + chat.chatName + "&messageContent=" + message.content + "&messageSender=" + message.senderId , {});
  }

}
