import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    Authorization: 'my-auth-token'
  })
};

@Injectable({
  providedIn: 'root'
})
export class PopulateService {

  populateMySQLUrl: string = 'http://localhost:8080/populateDB';
  migrateToMongoUrl: string = 'http://localhost:8080/mongo/migrate';

  constructor(private http: HttpClient) {
  }

  populateMySQL() {
    return this.http.get<Observable<void>>(this.populateMySQLUrl);
  }

  migrateToMongo() {
    return this.http.get<Observable<void>>(this.migrateToMongoUrl);
  }
}
