import { JsonMember, JsonObject, TypedJSON } from '@upe/typedjson';
import * as moment from 'moment';
import { DateToStringPipe } from '@shared/pipes/date-to-string.pipe';
import * as JWT from 'jwt-decode';

export enum PermissionFlags {
  NONE = 0,
  USER = 1 << 0
}

@JsonObject()
export class Token {

  public static GetToken(): Token | null {
    try {
      return TypedJSON.parse(JSON.stringify({
        jwt: localStorage.getItem('jwt'),
        refreshToken: localStorage.getItem('refreshToken'),
        expirationDate: localStorage.getItem('expirationDate')
      }), Token);
    } catch (e) {
      return null;
    }
  }

  public static SetToken(token: Token): void {
    if (token == null) {
      Token.RemoveToken();
    } else {
      localStorage.setItem('jwt', token.jwt);
      localStorage.setItem('refreshToken', token.refreshToken);
      localStorage.setItem('expirationDate', token.expirationDate.toISOString());
    }
  }

  public static RemoveToken(): void {
    localStorage.removeItem('jwt');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('expirationDate');
  }

  public static getUserId(): string | null {
    let token: Token = this.GetToken();
    if (token != null) {
      let jwt = JWT(token.jwt);
      let subject: string = jwt.sub;
      let list: string[] = subject.split('$');
      return list[0];
    }
    else null;
  }

  public static getExpirationDate(): string | null {
    let token: Token = this.GetToken();
    if (token != null) {
      let jwt = JWT(token.jwt);
      return moment(jwt.exp * 1000).format('YYYY-MM-DDTHH:mm');
    }
    else null;
  }

  public static getInstanceName(): string | null {
    let token: Token = this.GetToken();
    if (token != null) {
      let jwt = JWT(token.jwt);
      let subject: string = jwt.sub;
      let list: string[] = subject.split('$');
      return list[2];
    }
    else null;
  }

  /**
   * get the permissions as flags (from token)
   */
  public static getPermissionFlags(): number {
    let token: Token = this.GetToken();
    if (token != null) {
      let jwt = JWT(token.jwt);
      let subject: string = jwt.sub;
      let list: string[] = subject.split('$');
      // use reverse string, because the bits in the string are in specific order
      return parseInt(list[3].split('').reverse().join(''), 2);
      // use this to test permissions
      // return PermissionFlags.NONE;
    }
    else {
      return 0;
    }
  }

  /**
   * check if the user has specific permission
   * @param permissions (multiple Flags are alternatives)
   * if all of the flags have to be satisfied connect them via bit-or "|" => hasPermissionFor(PermissionFlags.FAKULTAET | PermissionFlags.FAKULTAET_INSTANZ, ...)
   * either of the flags has to be satisfied => hasPermissionFor(PermissionFlags.FAKULTAET, PermissionFlags.FAKULTAET_INSTANZ)
   */
  public static hasPermissionFor(...permissions: PermissionFlags[]): boolean {
    let flags = this.getPermissionFlags();
    return permissions.map((p) => (p & flags) === p).reduce((a, b) => a || b, false);
  }

  @JsonMember({
    isRequired: true,
    name: 'jwt',
    type: String
  }) public jwt: string;

  @JsonMember({
    isRequired: true,
    name: 'refreshToken',
    type: String
  }) public refreshToken: string;

  @JsonMember({
    isRequired: true,
    name: 'expirationDate',
    type: String
  }) private _expirationDate: string;

  public get expirationDate(): Date {
    return moment(this._expirationDate).toDate();
  }

  public set expirationDate(value: Date) {
    this._expirationDate = new DateToStringPipe().transform(value);
  }

  public static bitOr(...permissions: PermissionFlags[]): PermissionFlags {
    return permissions.reduce((a, b) => a | b, PermissionFlags.NONE);
  }

}
