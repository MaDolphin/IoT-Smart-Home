/* (c) https://github.com/MontiCore/monticore */

/**
 * ### .getOwnEnumerablePropertySymbols(object)
 *
 * This allows the retrieval of directly-owned enumerable property symbols of an
 * object. This function is necessary because Object.getOwnPropertySymbols
 * returns both enumerable and non-enumerable property symbols.
 *
 * @param object
 * @returns
 *
 * @name getOwnEnumerablePropertySymbols
 * @api public
 */
export function getOwnEnumerablePropertySymbols(obj: object): string[] | symbol[] {
  if (typeof Object.getOwnPropertySymbols !== 'function') {
    return [];
  }

  return Object.getOwnPropertySymbols(obj).filter(function (sym) {
    return Object.getOwnPropertyDescriptor(obj, sym).enumerable;
  });
}
