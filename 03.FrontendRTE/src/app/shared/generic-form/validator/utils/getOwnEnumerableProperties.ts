import { getOwnEnumerablePropertySymbols } from './getOwnEnumerablePropertySymbols';

/**
 * ### .getOwnEnumerableProperties(object)
 *
 * This allows the retrieval of directly-owned enumerable property names and
 * symbols of an object. This function is necessary because Object.keys only
 * returns enumerable property names, not enumerable property symbols.
 *
 * @param object
 * @returns
 *
 * @name getOwnEnumerableProperties
 * @api public
 */

export function getOwnEnumerableProperties(obj: object) {
  return Object.keys(obj).concat(getOwnEnumerablePropertySymbols(obj) as any);
}
