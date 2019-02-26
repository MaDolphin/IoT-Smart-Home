import { Injectable, NgZone } from '@angular/core';
import { Http } from '@angular/http';
import { IHttpLogger, Log, Logger, LogType } from '@upe/logger';
import { JsonApiService } from '@jsonapiservice/json-api.service';

@Injectable()
export class LoggerService implements IHttpLogger {

  constructor(private http: Http, private ngZone: NgZone) {
    this.fakeInterval();
  }

  private fakeInterval() {
  }

  private sendLogs() {
  }

  public log(log: Log): void {
  }

}
