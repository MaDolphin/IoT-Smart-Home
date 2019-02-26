import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { ApiService } from '@shared/architecture/services/api.service';
import { JsonApiService, JsonResponse } from '@jsonapiservice/json-api.service';
import { CommandCallback, CommandManager } from '@shared/architecture/command/rte/command.manager';
import { CommandDTO } from '@shared/architecture/command/rte/command.dto';
import { CommandResponseDTO } from '@shared/architecture/command/response/command.response.dto';
import { TypedJSON } from '@upe/typedjson';
import { IDTO } from '@shared/architecture';

@Injectable()
export class CommandRestService extends ApiService<IDTO> implements CommandCallback {
  constructor(api: JsonApiService) {
    super(api, '/commands');
    this.logger.addFlag('command');
  }

  public send(commandListId: number, commandList: CommandDTO[], commandManager: CommandManager): Promise<null> {
    return new Promise<null>((resolve, reject) => {

      try {
        // serialize the commands
        let cmds = JSON.stringify({id: commandListId, commands: commandList});

        console.log('send: ', cmds);

        let observer: Observable<any> = this.api.post(
          this.baseUrl,
          cmds,
          JsonApiService.HeaderJson)
          .map((response: JsonResponse) => {
            // deserialize all models
            return response.json;
          });

        observer.toPromise().then((jsonResponseList: string) => {
          try {
            console.log('received: ', jsonResponseList);
            let cmdResponse: CommandResponseDTO = TypedJSON.parse(jsonResponseList, CommandResponseDTO);
            commandManager.received(cmdResponse);
            resolve(null);
          } catch (e) {
            reject(e);
          }
        }).catch((err) => {
          reject(err);
        });
      } catch (e) {
        reject(e);
      }
    });
  }
}
