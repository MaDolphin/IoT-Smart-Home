import { JsonMember, JsonObject } from '@upe/typedjson';
import { DTO } from '@shared/architecture/data';
import { AppError } from '@shared/utils/general.error';

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
    type: AppError,
  }) private _error?: AppError;

  constructor(errorCode?: string, msg?: AppError | Error | string) {
    super();
    this.typeName = 'ErrorDTO';
    this._errorCode = errorCode ? errorCode : '0xFFFFE';
    if (msg) {
      if (msg instanceof AppError) {
        this._error = msg;
      } else if (msg instanceof Error) {
        this._error = AppError.createFromError(msg);
      } else {
        this._error = AppError.createInternal(msg);
      }
    }
  }

  get errorCode(): string {
    return this._errorCode;
  }

  set errorCode(value: string) {
    this._errorCode = value;
  }

  get error(): AppError {
    return this._error;
  }

  set error(value: AppError) {
    this._error = value;
  }

  get msg(): string {
    return this._error.message();
  }

  public message(msg: AppError | Error | string) {
    if (msg instanceof AppError) {
      this._error = msg;
    } else if (msg instanceof Error) {
      this._error = AppError.createFromError(msg);
    } else {
      this._error = AppError.createInternal(msg);
    }
  }
}