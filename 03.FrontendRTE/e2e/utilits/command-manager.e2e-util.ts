/* (c) https://github.com/MontiCore/monticore */
import 'rxjs/add/operator/concatMap';
import { Observable } from "rxjs/Rx";
import * as variables from "./login.e2e-util";
import { CommandCallback, CommandManager } from "@shared/architecture/command/rte/command.manager";
import { CommandDTO } from "@shared/architecture/command/rte/command.dto";
import { CommandResponseDTO } from "@shared/architecture/command/response/command.response.dto";
import { TypedJSON } from "@upe/typedjson";

let fetch = require('node-fetch');

class TestCommandRestClient implements CommandCallback {
  public send(commandListId: number, commandList: CommandDTO[], commandManager: CommandManager): Promise<any> {
    let restOptions = {
      method: 'POST',
      headers: {
        'cache-control': 'no-cache',
        'content-type': 'application/json',
        'x-auth': variables.token
      },
      body: commandList
    };

    let observer: Promise<any> = fetch('http://localhost:8080/macoco-be/api/commands', restOptions);

    observer.then(res => res.json()).then((data: string) => {
      try {
        let cmdResponse: CommandResponseDTO = TypedJSON.parse(data, CommandResponseDTO);
        commandManager.received(cmdResponse);
      } catch (e) {
        commandManager.error(e);
      }
    }).catch((err) => {
      commandManager.error(err);
    });

    return Observable.of().toPromise();
  }
}

export class E2ECommandCaller {
  private static _commandRestClient: TestCommandRestClient = new TestCommandRestClient();
  private static _commandManager: CommandManager = new CommandManager(E2ECommandCaller._commandRestClient);

  static get commandManager(): CommandManager {
    return this._commandManager;
  }
}
