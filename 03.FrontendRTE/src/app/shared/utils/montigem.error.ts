/* (c) https://github.com/MontiCore/monticore */

import { JsonMember, JsonObject } from '@upe/typedjson';

@JsonObject()
export class MontiGemError {
  @JsonMember({isRequired: true, name: 'errorCode', type: String}) public errorCode: string;
  @JsonMember({isRequired: false, name: 'title', type: String}) public errorTitle: string | undefined;
  @JsonMember({isRequired: false, name: 'httpStatusCode', type: String}) protected _httpStatusCode: string | undefined;
  @JsonMember({isRequired: true, name: 'description', type: String}) protected _message: string;

  public constructor(message?: string, title?: string, errorCode?: string, httpStatusCode?: string) {
    Object.setPrototypeOf(this, new.target.prototype); // restore prototype chain
    this._message = message || 'Fehler';
    this.errorCode = errorCode || '0xFEFFF';
    this.errorTitle = title || 'Fehler';
    this._httpStatusCode = httpStatusCode;
  }

  public title(): string {
    return this.errorTitle ? this.errorTitle : 'Fehler';
  }

  public message(): string {
    return this.errorCode + ': ' + this._message;
  }

  public messageOnly(): string {
    return this._message;
  }

  public code(): string {
    return this.errorCode;
  }

  get httpStatusCode(): string | undefined {
    return this._httpStatusCode;
  }

  set httpStatusCode(value: string | undefined) {
    this._httpStatusCode = value;
  }

  public static createFromError(error: Error) {
    let err = new MontiGemError();
    err.errorCode = '0xFFFFF';
    err.errorTitle = error.name;
    err._message = error.message;

    return err;
  }

  public static create(errorCode: string, title: string | undefined, message: string): MontiGemError {
    let err = new MontiGemError();
    err.errorCode = errorCode;
    err.errorTitle = title ? title : undefined;
    err._message = message;

    return err;
  }

  public static createInternal(message: string): MontiGemError {
    let err = new MontiGemError();
    err.errorCode = '0xFFFFE';
    err.errorTitle = 'Interner Fehler';
    err._message = message;

    return err;
  }

  public static fromJson(maCoCoError: any): MontiGemError {
    return MontiGemError.create(maCoCoError.errorCode, maCoCoError.title, maCoCoError.message);
  }


}
