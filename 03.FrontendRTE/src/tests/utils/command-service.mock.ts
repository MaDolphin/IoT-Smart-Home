/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { CommandResultDTO } from '@shared/architecture/command/response/command.result.dto';
import { CommandDTO } from '@shared/architecture/command/rte/command.dto';
import { CommandManager } from '@shared/architecture/command/rte/command.manager';
import { CommandResponseDTO } from '@shared/architecture/command/response/command.response.dto';

export class CommandServiceMock {

  constructor(public commandDtoMapping: {[value: string]: any}) {}

  send(commandListId: number, commandList: CommandDTO[], commandManager: CommandManager): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      let commandResponseDto = new CommandResponseDTO();
      commandResponseDto.id = commandListId;
      for (let command of commandList) {
        let res = new CommandResultDTO();
        if (this.commandDtoMapping[command.typeName]) {
          res.dto = this.commandDtoMapping[command.typeName];
        } else {
          fail( command.typeName + ' in mapping is missing!');
        }
        res.id = 0;
        commandResponseDto.responses.push(res);
      }
      commandManager.received(commandResponseDto);
      resolve();
    });
  }
}