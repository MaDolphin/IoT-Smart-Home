import { GenericFormGroup } from '@shared/generic-form/generic-form';
import { CONTROL_NAME, DESIGN_TYPE } from '@shared/generic-form/generic-form/config';
import { extractPrototype } from '@shared/generic-form/generic-form/decorators/group/utils';

export function deleteAllControlMetadatas(constrcutor: new (...args: any[]) => GenericFormGroup<any>): void {

  const instance = new constrcutor();

  const backlist = [CONTROL_NAME, DESIGN_TYPE];

  const prototype = extractPrototype(instance);

  const propertyKeys = Object.getOwnPropertyNames(instance);

  for (const propertyKey of propertyKeys.filter((pk) => Reflect.hasMetadata(CONTROL_NAME, prototype, pk))) {

    const propertyMetadataKeys = Reflect.getMetadataKeys(prototype, propertyKey);

    for (const key of propertyMetadataKeys.filter((k) => !backlist.includes(k))) {

      Reflect.deleteMetadata(key, prototype, propertyKey);

    }

  }

}
