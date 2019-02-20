import { JsonMember, JsonObject, TypedJSON } from '@upe/typedjson';

// TODO : move to model folder
@JsonObject()
export class User {
  public get picture(): string {
    return this._picture;
  }

  public get lastName() {
    return this._lastName;
  }

  public get firstName() {
    return this._firstName;
  }

  public get userId() {
    return this._userId;
  }

  @JsonMember({
    name: 'userId',
    isRequired: true, type: String
  })
  private _userId: string;

  @JsonMember({
    name: 'firstName',
    isRequired: true, type: String
  })
  private _firstName: string;

  @JsonMember({
    name: 'lastName',
    isRequired: true, type: String
  })
  private _lastName: string;

  @JsonMember({
    name: 'picture',
    isRequired: true, type: String
  })
  private _picture: string;

  @JsonMember({name: 'lastSeen', type: String}) lastSeen: string;

  // TODO : load userSettings via lazyload?
  // @JsonMember userSettings: UserSettings;

  public static parseUser(json: any): User {
    return TypedJSON.parse(JSON.stringify(json), User);
  }

  public static parseUserArray(json: any): User[] {
    let users: User[] = [];
    json.map((user: any) => users.push(User.parseUser(user)));
    return users;
  }
}
