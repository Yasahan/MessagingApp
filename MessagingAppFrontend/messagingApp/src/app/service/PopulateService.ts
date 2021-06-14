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

  constructor(private http: HttpClient) {
  }

  async populateMySQL() {
    return await this.http.get<Observable<void>>(this.populateMySQLUrl).toPromise();
  }
}
