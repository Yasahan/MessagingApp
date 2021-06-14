import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Hobby} from "../model/Hobby";
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
export class HobbyService {

  //MySQL Endpoints
  getHobbiesUrl: string = 'http://localhost:8080/getHobbies';

  //MongoDB Endpoints
  getHobbiesMongoUrl: string = 'http://localhost:8080/mongo/getHobbies';

  constructor(private http: HttpClient) {
  }

  getHobbies(): Observable<Hobby[]> {
    if (DBSwitchService.isMongoDB) {
      return this.http.get<Hobby[]>(this.getHobbiesMongoUrl);
    }
    return this.http.get<Hobby[]>(this.getHobbiesUrl);
  }
}
