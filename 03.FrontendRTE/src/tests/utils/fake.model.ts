import { Injectable } from '@angular/core';
import { JsonObject } from '@upe/typedjson';
import { JsonMember } from '@upe/typedjson/dist';
import { IModel, Model } from '@shared/architecture/data/models/model';
import { JsonApiService } from '@jsonapiservice/json-api.service';
import { IModelService, ModelService } from '@shared/architecture/services/models/model.service';
import { NotificationService } from '@shared/notification/notification.service';

export interface IFake extends IModel {
  name: string;
  data: string;
}

export interface IFakeService extends IModelService<IFake> {
}

@JsonObject()
export class FakeModel extends Model<FakeModel> implements IFake {
  @JsonMember({isRequired: true, name: 'name', type: String}) public name: string = '';
  @JsonMember({isRequired: true, name: 'data', type: String}) public data: string = '';
}

@Injectable()
export class FakeModelService extends ModelService<FakeModel> implements IFakeService {
  constructor(api: JsonApiService, notificationService: NotificationService) {
    super(api, FakeModel, '/domain/fakes', notificationService);
  }
}
