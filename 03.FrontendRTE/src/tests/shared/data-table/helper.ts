/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Observable } from 'rxjs/Observable';
import { IViewModel } from "@shared/architecture/data/viewmodel";
import { IModel, Model } from "@shared/architecture";

export class ITestModel implements IModel {}

export class TestModel extends Model<ITestModel> {
  constructor(public value: any) {
    super();
  }
}

export class ITestViewModel implements IViewModel<TestModel> {
  concert: TestModel;
  internId: string;

  deepSave(): Observable<IViewModel<TestModel> | any> {
    return Observable.create();
  }

  flatSave(): Observable<IViewModel<TestModel> | any> {
    return Observable.create();
  }

  delete(): Observable<boolean> {
    return Observable.create();
  }

  update(): Observable<IViewModel<TestModel> | any> {
    return Observable.create();
  }

  clear(): void {
  }
}

export class TestViewModel extends ITestViewModel {
  constructor(internId: string) {
    super();

    this.internId = internId;
  }
}