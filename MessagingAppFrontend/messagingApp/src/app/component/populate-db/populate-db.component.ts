import {Component, OnInit} from '@angular/core';
import {PopulateService} from "../../service/PopulateService";

@Component({
  selector: 'app-populate-db',
  templateUrl: './populate-db.component.html',
  styleUrls: ['./populate-db.component.css']
})
export class PopulateDbComponent implements OnInit {

  progressText:string = '';

  constructor(private populateService: PopulateService) {
  }

  ngOnInit(): void {

  }

  populateMySQL() {
    this.progressText = 'MySQL populate started... This could take a minute';
    this.populateService.populateMySQL().subscribe(result => {
        this.progressText = "Data inserted successfully!";
      }
    );

  }

  migrateToMongo() {
    this.progressText = 'Date Migration to MongoDB started... This could take a minute';
    this.populateService.migrateToMongo().subscribe(result => {
      this.progressText = "Migration Successful!";
      }
    );
  }
}
