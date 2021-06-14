import { Component, OnInit } from '@angular/core';
import {SessionService} from "../../service/SessionService";
import {Admin} from "../../model/Admin";
import {User} from "../../model/User";
import {UserService} from "../../service/UserService";
import {AdminService} from "../../service/AdminService";
import {Router} from "@angular/router";

@Component({
  selector: 'app-admin-validation',
  templateUrl: './admin-validation.component.html',
  styleUrls: ['./admin-validation.component.css']
})
export class AdminValidationComponent implements OnInit {

  constructor(private router: Router, private adminService: AdminService) { }

  ngOnInit(): void {
    if(SessionService.getCurrentUser() === undefined) {
      this.router.navigate(['/login']);
    }
  }

  onSubmit(form: any) {
    let admin = new Admin();
    admin.userBirthdate = form.birthdate;
    console.log(admin.userBirthdate);
    admin.userEmail = form.email;
    let user = SessionService.getCurrentUser();
    admin.userName = user.userName;
    admin.userPassword = user.userPassword;
    admin.userId = user.userId;

    SessionService.isAdmin = true;
    this.adminService.addAdmin(admin).subscribe(subAdmin => {
      if(subAdmin == null) {
        // alert("You are already an admin");
      }
      this.router.navigate(['/chatMenu']);
    });
  }
}
