/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import * as AssertionError from 'assertion-error';
import { Assertion } from '../assertion';
import { flag } from './flag';
import { typeDetect } from './type-detect';

/**
 * ### .expectTypes(obj, types)
 *
 * Ensures that the object being tested against is of a valid type.
 *
 *     utils.expectTypes(this, ['array', 'object', 'string']);
 *
 * @param obj
 * @param types
 */
export function expectTypes(obj: Assertion, types: any[]) {
  let flagMsg = flag(obj, 'message');
  const ssfi  = flag(obj, 'ssfi');

  flagMsg = flagMsg ? flagMsg + ': ' : '';

  obj   = flag(obj, 'object');
  types = types.map(function (t) {
    return t.toLowerCase();
  });
  types.sort();

  // Transforms ['lorem', 'ipsum'] into 'a lorem, or an ipsum'
  const str = types.map(function (t, index) {
    const art = ~['a', 'e', 'i', 'o', 'u'].indexOf(t.charAt(0)) ? 'an' : 'a';
    const or  = types.length > 1 && index === types.length - 1 ? 'or ' : '';
    return or + art + ' ' + t;
  }).join(', ');

  const objType = typeDetect(obj).toLowerCase();

  if (!types.some(function (expected) {
    return objType === expected;
  })) {
    throw new AssertionError(
      flagMsg + 'object tested must be ' + str + ', but ' + objType + ' given',
      undefined,
      ssfi,
    );
  }
}
