/* (c) https://github.com/MontiCore/monticore */

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Logger } from '@upe/logger';
import { Observable } from 'rxjs/Observable';
import { JsonApiService, JsonResponse } from '@jsonapiservice/json-api.service';
import { LoadingService } from '../../layout/loading/loading.service';
import { NotificationService } from '../../notification/notification.service';
import { MontiGemError } from '../montigem.error';

@Component({
  selector: 'csvimport',
  templateUrl: './csv-import.component.html',
})
export class CsvImportComponent {
  fileName: any;
  newFile: boolean = false;

  private logger: Logger;
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
      this.jsonApiService.post(this.url, body, JsonApiService.HeaderCSV)
        .map((res: JsonResponse) => {
          this.logger.info('response', res);
          this._loadingService.stop();
          this.onLoaded();
          if (!!res.json) {
            let messages: string[] = res.json.split('\n');
            this.notificationService.notificationOkOnlyMultilines('Import', messages);
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
      this.fileName = ' ';
    }
  }

  public onChange(fileInput: any) {
    this.fileName = fileInput;
    this.newFile = true;
  }

  @Input() public label: string = 'Wählen Sie eine Datei für den Import aus.';

  @Input() public url: string = ''

}
