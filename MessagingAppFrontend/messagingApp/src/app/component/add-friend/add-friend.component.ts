import { Component, OnInit } from '@angular/core';
import {FriendService} from "../../service/FriendService";
import {UserService} from "../../service/UserService";
import {Router} from "@angular/router";

@Component({
  selector: 'app-add-friend',
  templateUrl: './add-friend.component.html',
  styleUrls: ['./add-friend.component.css']
})
export class AddFriendComponent implements OnInit {
  constructor(private friendService:FriendService, private userService:UserService,private router: Router) { }

  ngOnInit(): void {
  }

  onClickSubmit(result: { username: string }) {
    this.userService.getViaName(result.username).then(userFound=>{
      if(userFound != null) {
        this.friendService.addFriend(userFound.userId).subscribe();
        this.router.navigate(['chatMenu/']);
      }
      else {
        alert("User with name " + result.username + " was not found!");
      }
    });
  }

}
