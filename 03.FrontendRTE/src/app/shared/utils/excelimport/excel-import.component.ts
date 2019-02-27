/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Component, EventEmitter, Output } from '@angular/core';
import { Logger } from '@upe/logger';
import { Observable } from 'rxjs/Observable';
import { JsonApiService, JsonResponse } from '@jsonapiservice/json-api.service';
import { LoadingService } from '../../layout/loading/loading.service';
import { NotificationService } from '../../notification/notification.service';
import { MontiGemError } from '../montigem.error';

@Component({
  selector: 'excelimport',
  templateUrl: './excel-import.component.html',
})
export class ExcelImportComponent {
  fileName: any;
  newFile: boolean = false;

  private logger: Logger;
  public test: boolean = false;
  public import: boolean = false;
  public importable: boolean = false;
  @Output('loaded') onLoadedEvent: EventEmitter<boolean> = new EventEmitter();

  constructor(private jsonApiService: JsonApiService,
              private _loadingService: LoadingService,
              private notificationService: NotificationService) {
    this.logger = new Logger({
      name: this['constructor']['name'] + 'Service',
      flags: ['service', 'api']
    });
  }


  public onLoaded(): void {
    this.onLoadedEvent.emit();
  }

  public send(body: any) {
    if (this.newFile) {
      this._loadingService.start();

      this.newFile = false;
      if (this.import) {
        this.jsonApiService.post('/domain/excel/import', body, JsonApiService.HeaderOctetStream)
            .map((res: JsonResponse) => {
              this.logger.info('response', res);
              this._loadingService.stop();
              this.onLoaded();
              if (!!res.json) {
                let messages: string[] = res.json.split('\n');
                this.notificationService.notificationOkOnlyMultilines('Excel-Import', messages);
              }
              return true;
            })
            .catch((error: Response | any) => {
              this._loadingService.stop();
              this.onLoaded();
              let appErr: MontiGemError = JsonApiService.deserializeError(error.json(), this.logger);
              this.logger.info('MontiGemError', appErr);
              this.notificationService.errorWithMessage(appErr);
              return Observable.of(false);
            }).share().toPromise();
      }
      if (this.test) {
        this.jsonApiService.post('/domain/excel/trockenimport', body, JsonApiService.HeaderOctetStream)
            .map((res: JsonResponse) => {
              this.logger.info('response', res);
              this._loadingService.stop();
              this.onLoaded();
              if (!!res.json) {
                let messages: string[] = res.json.split('\n');
                this.notificationService.notificationOkOnlyMultilines('Test Excel-Import', messages);
              }
              return true;
            })
            .catch((error: Response | any) => {
              this._loadingService.stop();
              this.onLoaded();
              let appErr: MontiGemError = JsonApiService.deserializeError(error.json(), this.logger);
              this.logger.info('MontiGemError', appErr);
              this.notificationService.errorWithMessage(appErr);
              return Observable.of(false);
            }).share().toPromise();
      }
      this.fileName = ' ';
    }
  }

  public booleanimport() {
    this.import = !this.import;
    this.test = false;
    this.readyToImport()
  }

  public booleantest() {
    this.import = false;
    this.test = !this.test;
    this.readyToImport()
  }

  public onChange(fileInput: any) {
    this.fileName = fileInput;
    this.newFile = true;
    this.readyToImport();
  }

  private readyToImport() {
    if ((this.test || this.import) && this.newFile) {
      this.importable = true;
    }
    else {
      this.importable = false;
    }
  }
}
