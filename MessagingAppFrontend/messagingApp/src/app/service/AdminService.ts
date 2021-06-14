import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {User} from "../model/User";
import {Admin} from "../model/Admin";
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
export class AdminService {

  // MySQL Endpoints
  getAdminUrl: string = 'http://localhost:8080/getAdmin';
  addAdminUrl: string = 'http://localhost:8080/addAdmin';
  getAdminForLoginUrl: string = 'http://localhost:8080/getAdminForLogin';

  // MongoDB Endpoints
  addAdminMongoUrl: string = 'http://localhost:8080/mongo/addAdmin';
  getAdminForLoginMongoUrl: string = 'http://localhost:8080/mongo/getAdminForLogin';


  constructor(private http: HttpClient) {
  }

  getAdmin(admin: Admin): Observable<User> {
    return this.http.post<Admin>(this.getAdminUrl, admin, httpOptions);
  }

  addAdmin(admin: Admin): Observable<User> {
    if(DBSwitchService.isMongoDB){
      return this.http.post<Admin>(this.addAdminMongoUrl, admin, httpOptions);
    }
    return this.http.post<Admin>(this.addAdminUrl, admin, httpOptions);
  }

  async getAdminForLogin(user: User) {
    if(DBSwitchService.isMongoDB){
      return await this.http.post<User>(this.getAdminForLoginMongoUrl, user, httpOptions).toPromise();
    } else {
      return await this.http.post<User>(this.getAdminForLoginUrl, user, httpOptions).toPromise();
    }
  }


}
