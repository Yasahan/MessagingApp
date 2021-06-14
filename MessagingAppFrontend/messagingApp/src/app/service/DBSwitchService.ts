import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DBSwitchService {

  private static _isMongoDB: boolean | undefined;

  constructor() {
  }

  static get isMongoDB(): boolean | undefined {
    return this._isMongoDB;
  }

  static set isMongoDB(value: boolean | undefined) {
    this._isMongoDB = value;
  }

}
