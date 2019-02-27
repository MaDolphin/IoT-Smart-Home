/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { CommandDTO } from '@shared/architecture/command/rte/command.dto';
import { ErrorDTO } from '@shared/architecture/command/aggregate/error.dto';
import { Logger } from '@upe/logger';
import { MontiGemError } from '@shared/utils/montigem.error';
import { CommandResultDTO } from '@shared/architecture/command/response/command.result.dto';
import { CommandResponseDTO } from '@shared/architecture/command/response/command.response.dto';
import { CommandPromise, ResponseModelCallback } from '@shared/architecture/command/rte/command.promise';
import { IDTO } from '@shared/architecture';

export interface CommandCallback {
  send: (commandListId: number, commandList: CommandDTO[], commandManager: CommandManager) => Promise<IDTO>;
}

export class CommandManager {
  private commands: CommandDTO[] = [];
  private promises: CommandPromise[] = [];
  private runningCommandId: number = 0;
  private readonly _callback: CommandCallback;
  private resolve: () => void;
  private reject: (model: ErrorDTO) => void;
  private readonly logger: Logger = new Logger({name: 'CommandManager', flags: ['manager']});

  constructor(callback: CommandCallback) {
    this._callback = callback;
    this.clear();
  }

  protected call(result: CommandResultDTO): Promise<IDTO> {
    let p = this.promises.find(promise => promise.id === result.id);

    return new Promise((resolve, reject) => {
      if (p !== undefined && p.method) {
        result.dto.transform();
        p.method(result.dto).then(() => {
          resolve();
        }).catch(() => {
          reject();
        });
      }
      resolve(result.dto);
    })
  }

  public async received(cmdResponse: CommandResponseDTO) {
    try {
      for (let response of cmdResponse.responses) {
        if (response === undefined) {
          return Promise.reject(new ErrorDTO('0xF0001', MontiGemError.createInternal('Daten können nicht ausgewertet werden.')));
        }

        console.log('Command ' + response.id + ':', response.dto);

        if (response.dto instanceof ErrorDTO) {
          this.reject(<ErrorDTO> response.dto);
        }

        await this.call(response);
      }

      this.resolve();
    } catch (e) {
      this.logger.warn('0xF0002: ' + e.toString(), cmdResponse);
      this.reject(new ErrorDTO(e));
    }
    this.clear();
  }

  public addCommand(command: CommandDTO, callback?: ResponseModelCallback): CommandManager {
    if (this.commands === undefined) {
      this.clear();
    }

    command.commandId = this.commands.length;
    this.commands.push(command);
    let promise: CommandPromise = callback && new CommandPromise(callback) || CommandPromise.emptyPromise();
    promise.id = command.commandId;

    this.logger.debug('0xF0003: addCommand with id ' + command.commandId, command);

    this.promises.push(promise);

    return this;
  }

  public sendCommands(): Promise<null> {
    if (this.commands === undefined || this.commands.length === 0) {
      this.logger.info('0xF0004: No commands given');
      console.log('0xF0004: No commands given');

      // if no commands given, just return ok
      return new Promise<null>((resolve, reject) => {
        resolve(null);
      });
    }

    this.logger.debug('0xF0005: sendCommands: ', this.commands);
    console.log('0xF0005: sendCommands: ', this.commands);
    this.runningCommandId += 1;

    // reset callbacks
    this.resolve = () => {
    };
    this.reject = () => {
    };

    return new Promise<null>((resolve, reject) => {
      this.resolve = () => {
        this.logger.debug('0xF0006: resolve');
        resolve(null);
      };
      this.reject = (err) => {
        this.logger.debug('0xF0007: reject', err);
        reject(err);
      };
      this._callback.send(this.runningCommandId, this.commands, this)
        .catch((err) => {
        this.logger.info('0xF0008: error', err);
        this.clear();
        if (err instanceof SyntaxError) {
          reject(new ErrorDTO('0xF0008', new MontiGemError('Systemfehler', 'Systemfehler', '0xF0008')));
        } else {
          reject(err)
        }
      });
    }).then(() => {
      this.logger.debug('0xF0009: return promises');
      this.clear();

      return new Promise<null>((resolve, reject) => {
        resolve(null);
      });
    });
  }

  public clear() {
    this.commands = [];
    this.promises = [];

    this.resolve = () => {
    };

    this.reject = (err) => {
      throw MontiGemError.createInternal('0xF000A not yet set.');
    }
  }

  public error(e: Error | Event) {
    if (e instanceof Error) {
      this.reject(new ErrorDTO('0xF000B', (<Error> e).message));
    } else if (e instanceof Event) {
      this.reject(new ErrorDTO('0xF000C', (<Event> e).type));
    } else {
      this.reject(new ErrorDTO('0xF000D', 'unknown error'));
    }
  }

  get callback(): CommandCallback {
    return this._callback;
  }
}
