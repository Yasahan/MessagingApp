import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {FirstReportDTO} from "../model/FirstReportDTO";
import {SecondReportDTO} from "../model/SecondReportDTO";
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
export class ReportsService {

  //MySQL Endpoints
  firstReportUrl: string = 'http://localhost:8080/firstReport';
  secondReportUrl: string = 'http://localhost:8080/secondReport';

  // MongoDB Endpoints
  firstReportMongoUrl: string = 'http://localhost:8080/mongo/firstReport';


  constructor(private http: HttpClient) {
  }

  firstReport(): Observable<FirstReportDTO[]> {
    console.log("report wurde aufgerufen")
    console.log(DBSwitchService.isMongoDB)
    if (DBSwitchService.isMongoDB) {
      return this.http.get<FirstReportDTO[]>(this.firstReportMongoUrl, httpOptions);
    }
    return this.http.get<FirstReportDTO[]>(this.firstReportUrl, httpOptions);
  }

  secondReport(): Observable<SecondReportDTO[]> {
    return this.http.get<SecondReportDTO[]>(this.secondReportUrl, httpOptions);
  }
}
