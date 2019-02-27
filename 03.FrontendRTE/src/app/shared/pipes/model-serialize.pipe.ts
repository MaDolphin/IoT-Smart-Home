/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';
import { ISerializable } from '../architecture/serializable';

@Pipe({
  name: 'serialize',
  pure: false,
})
export class ModelSerializePipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'ModelSerializePipe', flags: ['serialze']});

  transform<T>(value: ISerializable<T>, format?: any): string {
    if (value) {
      if (Array.isArray(value)) {
        let jsons = value.map((model: ISerializable<T>) => model.serialize());
        jsons = jsons.map((json, index) => {
          json['id'] = value[index].id;
          return json;
        });
        return JSON.stringify(jsons);
      }
      try {
        let json = value.serialize();
        json['id'] = value['id'];
        return JSON.stringify(json);
      } catch (e) {
        this.logger.error('MAF0x0095: method serialize missing', value);
      }
      return 'fail';
    }
    return 'null';
  }

}
