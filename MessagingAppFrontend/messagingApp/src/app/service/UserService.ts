import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
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
export class UserService {

  // MySQL Endpoints
  getUsersUrl: string = 'http://localhost:8080/getUsers';
  getUserUrl: string = 'http://localhost:8080/getUser';
  addUserUrl: string = 'http://localhost:8080/addUser';
  removeAllUserUrl: string = 'http://localhost:8080/removeAllUser';
  removeUserUrl: string = 'http://localhost:8080/removeUser';
  getViaNameUrl: string = 'http://localhost:8080/getViaName';


  // MongoDB Endpoints
  addUserMongoUrl: string = 'http://localhost:8080/mongo/addUser';


  constructor(private http: HttpClient) {
  }

  getUsers(): Observable<User[]> {

    return this.http.get<User[]>(this.getUsersUrl);
  }

  async getUser(user: User): Promise<User> {
    return await this.http.post<User>(this.getUserUrl, user, httpOptions).toPromise();
  }

  addUser(user: User): Observable<User> {
<<<<<<< HEAD
    // return this.http.post<User>(this.addUserMongoUrl, user, httpOptions);
    return this.http.post<User>(this.addUserUrl, user, httpOptions);
=======
    if(DBSwitchService.isMongoDB){
      return this.http.post<User>(this.addUserMongoUrl, user, httpOptions);
    } else {
      return this.http.post<User>(this.addUserUrl, user, httpOptions);
    }
>>>>>>> 0adffd0d197888ba4ad0a54f3e8b2060116712db
  }

  removeAllUser() {
    return this.http.get(this.removeAllUserUrl);
  }

  removeUser(user: User): Observable<User> {
    return this.http.post<User>(this.removeUserUrl, user, httpOptions);
  }

  async getViaName(userName:string): Promise<User> {
    return await this.http.post<User>(this.getViaNameUrl,userName, httpOptions).toPromise();
  }

}
