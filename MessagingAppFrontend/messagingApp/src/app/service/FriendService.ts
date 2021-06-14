import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {SessionService} from "./SessionService";
import {User} from "../model/User";
import {DBSwitchService} from "./DBSwitchService";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    Authorization: 'my-auth-token'
  })
};

@Injectable({
  providedIn: 'root'
})
export class FriendService {

  //MySQL Endpoints
  addFriendServiceUrl: string = 'http://localhost:8080/addFriend';
  getFriendsUrl: string = 'http://localhost:8080/getFriends';

  //MongoDB Endpoints
  addFriendServiceMongoUrl: string = 'http://localhost:8080/mongo/addFriend';
  getFriendsMongoUrl: string = 'http://localhost:8080/mongo/getFriends';


  constructor(private http: HttpClient) {
  }

  addFriend(friendId: string): Observable<any> {
    if (DBSwitchService.isMongoDB) {
      console.log(this.addFriendServiceMongoUrl + "?userId=" + SessionService.getCurrentUser().userId + "&friendId=" + friendId)
      return this.http.post<any>(this.addFriendServiceMongoUrl + "?userId=" + SessionService.getCurrentUser().userId + "&friendId=" + friendId, httpOptions);
    }
    return this.http.post<any>(this.addFriendServiceUrl + "?userId=" + SessionService.getCurrentUser().userId + "&friendId=" + friendId, httpOptions);
  }

  async getFriends(): Promise<User[]> {
    if (DBSwitchService.isMongoDB) {
      return await this.http.post<User[]>(this.getFriendsMongoUrl + "?userId=" + SessionService.getCurrentUser().userId, httpOptions).toPromise();
    }
    return await this.http.post<User[]>(this.getFriendsUrl + "?userId=" + SessionService.getCurrentUser().userId, httpOptions).toPromise();
  }
}
