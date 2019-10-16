/* (c) https://github.com/MontiCore/monticore */

import { JsonMember, JsonObject } from '@upe/typedjson';
import { DTO } from '@shared/architecture/data';
import { MontiGemError } from '@shared/utils/montigem.error';

@JsonObject()
export class ErrorDTO extends DTO<ErrorDTO> {

  @JsonMember({
    isRequired: true,
    name: 'errorCode',
    type: String,
  }) _errorCode: string;

  @JsonMember({
    isRequired: true,
    name: 'error',
    type: MontiGemError,
  }) private _error?: MontiGemError;

  constructor(errorCode?: string, msg?: MontiGemError | Error | string) {
    super();
    this.typeName = 'ErrorDTO';
    this._errorCode = errorCode ? errorCode : '0xFFFFE';
    if (msg) {
      if (msg instanceof MontiGemError) {
        this._error = msg;
      } else if (msg instanceof Error) {
        this._error = MontiGemError.createFromError(msg);
      } else {
        this._error = MontiGemError.createInternal(msg);
      }
    }
  }

  get errorCode(): string {
    return this._errorCode;
  }

  set errorCode(value: string) {
    this._errorCode = value;
  }

  get error(): MontiGemError {
    return this._error;
  }

  set error(value: MontiGemError) {
    this._error = value;
  }

  get msg(): string {
    return this._error.message();
  }

  public message(msg: MontiGemError | Error | string) {
    if (msg instanceof MontiGemError) {
      this._error = msg;
    } else if (msg instanceof Error) {
      this._error = MontiGemError.createFromError(msg);
    } else {
      this._error = MontiGemError.createInternal(msg);
    }
  }
}
