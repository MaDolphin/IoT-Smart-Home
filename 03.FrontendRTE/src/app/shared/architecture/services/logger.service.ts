import { Injectable, NgZone } from '@angular/core';
import { Http } from '@angular/http';
import { IHttpLogger, Log, Logger, LogType } from '@upe/logger';
import { JsonApiService } from './json-api.service';

@Injectable()
export class LoggerService implements IHttpLogger {

  public logs: Log[] = [];

  constructor(private http: Http, private ngZone: NgZone) {
    this.fakeInterval();
  }

  private fakeInterval() {
    if (!Logger.DISABLED) {
      this.sendLogs();
      this.ngZone.runOutsideAngular(() => {
        setTimeout(() => {
          this.fakeInterval();
        }, 10000);
      });
    }
  }

  private sendLogs() {
    const logs: Log[] = this.logs;
    this.logs = [];

    const data = logs.map((log: Log) => {
      let logData = {};
      try {
        logData = JSON.stringify(log.data);
      } catch (e) {
        logData = {};
      }

      return {
        'logname': LogType[log.type],
        'timeStamp': `${log.timeStamp}`,
        'className': 'Frontend',
        'msg': `${log.message}: ${logData}`
      };
    });

    if (data.length > 0) {
      this.http.post(JsonApiService.buildUrl(`/logger/log`), data).subscribe();
    }
  }

  public log(log: Log): void {
    this.logs.push(log);
  }

}
