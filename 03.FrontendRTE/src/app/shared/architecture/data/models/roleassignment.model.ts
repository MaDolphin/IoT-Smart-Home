import { JsonMember, JsonObject } from '@upe/typedjson';
import { IModel, Model } from './model';

export interface IRoleAssignment extends IModel {
    role: string;
    username: string;
    objClass: string;
    objId: number | null;
    attribute: string | null;
}

@JsonObject()
export class RoleAssignment extends Model<RoleAssignment> implements IRoleAssignment {

    @JsonMember({isRequired: true, name: 'role', type: String}) public role: string = '';

    @JsonMember({isRequired: true, name: 'username', type: String}) public username: string = '';

    @JsonMember({isRequired: true, name: 'objClass', type: String}) public objClass: string = '';

    @JsonMember({name: 'objId', type: Number}) public objId: number | null = null;

    @JsonMember({name: 'attribute', type: String}) public attribute: string | null = null;
}
