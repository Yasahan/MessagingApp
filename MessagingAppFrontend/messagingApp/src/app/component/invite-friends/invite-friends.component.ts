import {Component, Input, OnInit} from '@angular/core';
import {ChatService} from "../../service/ChatService";
import {ActivatedRoute, Router} from "@angular/router";
import {SessionService} from "../../service/SessionService";
import {UserService} from "../../service/UserService";
import {User} from "../../model/User";
import {FriendService} from "../../service/FriendService";

@Component({
  selector: 'app-invite-friends',
  templateUrl: './invite-friends.component.html',
  styleUrls: ['./invite-friends.component.css']
})
export class InviteFriendsComponent implements OnInit {
  friends: User[] = [];
  selectedUsers:User[] = []


  @Input()
  chatId: string | null ='';

  constructor(private actRoute: ActivatedRoute, private chatService:ChatService, private router: Router, private userService : UserService, private friendService : FriendService) { }

  ngOnInit(): void {
    console.log(this.chatId);
    this.chatId = this.actRoute.snapshot.paramMap.get('chatId');
    this.friendService.getFriends().then(friends => {
      for(let user in friends) {
        if(SessionService.getCurrentUser().userId != friends[user].userId) {
          this.friends.push(friends[user]);
        }
      }
    });
  }

  selectInvitedFriend(friend: any) {
    console.log("Selected user!");
    if(this.selectedUsers.includes(friend)){
      const index = this.selectedUsers.indexOf(friend, 0);
      if (index > -1) {
        this.selectedUsers.splice(index, 1);
      }
    } else {
      this.selectedUsers.push(friend);
    }
  }

  inviteSelected() {
    for (let member in this.selectedUsers) {
      this.chatService.addMemberToChat(this.chatId, this.selectedUsers[member].userId).then();
      alert("Friend invited!")
    }
    this.router.navigate(['../chatMenu']);
  }
}
