import { Component, OnInit } from '@angular/core';
import {PopulateService} from "../../service/PopulateService";

@Component({
  selector: 'app-populate-db',
  templateUrl: './populate-db.component.html',
  styleUrls: ['./populate-db.component.css']
})
export class PopulateDbComponent implements OnInit {

  constructor(private populateService: PopulateService) { }

  ngOnInit(): void {
    this.populateService.populateMySQL().then();
  }

}
