import {Component, Input, OnInit} from '@angular/core';
import {Hobby} from "../../model/Hobby";
import {SessionService} from "../../service/SessionService";
import {Router} from "@angular/router";
import {User} from "../../model/User";
import {ChoosesService} from "../../service/ChoosesService";
import {HobbyService} from "../../service/HobbyService";

@Component({
  selector: 'app-hobby',
  templateUrl: './new-hobby.component.html',
  styleUrls: ['./new-hobby.component.css']
})
export class NewHobbyComponent implements OnInit {

  hobbiesToAdd:Hobby[] = []
  hobbies:Hobby[] = [];
  @Input()
  hobby:Hobby = new Hobby();
  constructor(private router: Router, private choosesService : ChoosesService, private hobbyService : HobbyService) { }

  ngOnInit(): void {
    if(SessionService.getCurrentUser() === undefined) {
      this.router.navigate(['/login']);
    }
    this.hobbyService.getHobbies().subscribe(hobbies=>{
      this.hobbies = hobbies;
    })
  }

  tapOnHobby(hobby: Hobby) {
    console.log("Clicked on a user")
    if(this.hobbiesToAdd.includes(hobby)){
      const index = this.hobbiesToAdd.indexOf(hobby, 0);
      if (index > -1) {
        this.hobbiesToAdd.splice(index, 1);
      }
    } else {
      this.hobbiesToAdd.push(hobby);
    }
    console.log(this.hobbiesToAdd)
  }

  addSelectedHobbies() {
    for(let hobby in this.hobbiesToAdd) {
      this.choosesService.addChoice(this.hobbiesToAdd[hobby]).subscribe(hobby => {

      });
    }
    /*this.router.navigate(['/chatMenu']);*/
  }
}
