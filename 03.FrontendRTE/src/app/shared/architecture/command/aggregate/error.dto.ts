import { JsonMember, JsonObject } from '@upe/typedjson';
import { DTO } from '@shared/architecture/data';
import { GeneralError } from '@shared/utils/general.error';

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
    type: GeneralError,
  }) private _error?: GeneralError;

  constructor(errorCode?: string, msg?: GeneralError | Error | string) {
    super();
    this.typeName = 'ErrorDTO';
    this._errorCode = errorCode ? errorCode : '0xFFFFE';
    if (msg) {
      if (msg instanceof GeneralError) {
        this._error = msg;
      } else if (msg instanceof Error) {
        this._error = GeneralError.createFromError(msg);
      } else {
        this._error = GeneralError.createInternal(msg);
      }
    }
  }

  get errorCode(): string {
    return this._errorCode;
  }

  set errorCode(value: string) {
    this._errorCode = value;
  }

  get error(): GeneralError {
    return this._error;
  }

  set error(value: GeneralError) {
    this._error = value;
  }

  get msg(): string {
    return this._error.message();
  }

  public message(msg: GeneralError | Error | string) {
    if (msg instanceof GeneralError) {
      this._error = msg;
    } else if (msg instanceof Error) {
      this._error = GeneralError.createFromError(msg);
    } else {
      this._error = GeneralError.createInternal(msg);
    }
  }
}