import { Component } from '@angular/core';
import {DBSwitchService} from "../../service/DBSwitchService";

@Component({
  selector:'app-header',
  templateUrl: './header.component.html'
})

export class HeaderComponent {
  collapsed = true;
  dbName:string = 'MySQL';

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
