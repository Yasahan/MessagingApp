import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {ChatDTO} from "../model/ChatDTO";
import {Hobby} from "../model/Hobby";
import {Admin} from "../model/Admin";
import {User} from "../model/User";
import {SessionService} from "./SessionService";
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
export class ChoosesService {

  //MySQL Endpoints
  addChoiceUrl: string = 'http://localhost:8080/addChoice';

  //MongoDB Endpoints
  addChoiceMongoUrl: string = 'http://localhost:8080/mongo/addChoice';

  constructor(private http: HttpClient) {
  }


  addChoice(hobby: Hobby): Observable<Hobby> {
    if (DBSwitchService.isMongoDB) {
      return this.http.post<Hobby>(this.addChoiceMongoUrl + "?hobbyId=" + hobby.hobbyId + "&userId=" + SessionService.getCurrentUser().userId, httpOptions);
    } else {
      return this.http.post<Hobby>(this.addChoiceUrl + "?hobbyId=" + hobby.hobbyId + "&userId=" + SessionService.getCurrentUser().userId, httpOptions);
    }

  }
}
