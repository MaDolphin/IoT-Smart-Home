/* (c) https://github.com/MontiCore/monticore */

/**
 * ### .getEnumerableProperties(object)
 *
 * This allows the retrieval of enumerable property names of an object,
 * inherited or not.
 *
 * @param object
 * @returns
 *
 * @name getEnumerableProperties
 * @api public
 */

export function getEnumerableProperties(object: any) {
  const result = [];
  for (const name in object) {
    result.push(name);
  }
  return result;
}
