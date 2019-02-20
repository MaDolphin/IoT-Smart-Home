import { Observable } from 'rxjs';
import { IModel } from './models/model';

export interface IViewModel<D extends IModel> extends IModel {
  concert: D;
  internId: string;
  deepSave(): Observable<IViewModel<D> | null>;
  flatSave(): Observable<IViewModel<D> | null>;
  delete(): Observable<boolean>;
  update(): Observable<IViewModel<D> | null>;
  clear(): void;
}
