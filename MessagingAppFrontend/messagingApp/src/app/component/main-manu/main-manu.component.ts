import { Component, OnInit } from '@angular/core';
import {DBSwitchService} from "../../service/DBSwitchService";

@Component({
  selector: 'app-main-manu',
  templateUrl: './main-manu.component.html',
  styleUrls: ['./main-manu.component.css']
})
export class MainManuComponent implements OnInit {

  dbName:string = DBSwitchService.isMongoDB === true ? 'MongoDB' : 'MySQL';

  constructor() { }

  ngOnInit(): void {
  }
  changeDBType() {
    DBSwitchService.isMongoDB = !DBSwitchService.isMongoDB;
    console.log("isMongo: " + DBSwitchService.isMongoDB)
    if(DBSwitchService.isMongoDB){
      this.dbName = 'MongoDB';
    } else {
      this.dbName = 'MySQL';
    }
  }
}
