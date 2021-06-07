import { Component, OnInit } from '@angular/core';
import {Hobby} from "../../model/Hobby";
import {SessionService} from "../../service/SessionService";
import {Router} from "@angular/router";

@Component({
  selector: 'app-new-hobby',
  templateUrl: './new-hobby.component.html',
  styleUrls: ['./new-hobby.component.css']
})
export class NewHobbyComponent implements OnInit {

  hobbies:Hobby[] = [];
  constructor(private router: Router) { }

  ngOnInit(): void {
    if(SessionService.getCurrentUser() === undefined) {
      this.router.navigate(['/login']);
    }

    let hobby1: Hobby = new Hobby();
    hobby1.name = "Football";

    let hobby2: Hobby = new Hobby();
    hobby2.name = "Basketball";

    let hobby3: Hobby = new Hobby();
    hobby3.name = "Golf";

    let hobby4: Hobby = new Hobby();
    hobby4.name = "Tennis";

    let hobby5: Hobby = new Hobby();
    hobby5.name = "Archery";

    this.hobbies.push(hobby1, hobby2, hobby3, hobby4, hobby5);
  }

}
