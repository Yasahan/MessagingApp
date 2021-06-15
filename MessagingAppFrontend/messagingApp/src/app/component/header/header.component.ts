import { Component } from '@angular/core';
import {DBSwitchService} from "../../service/DBSwitchService";

@Component({
  selector:'app-header',
  templateUrl: './header.component.html'
})

export class HeaderComponent {
  collapsed = true;


}
