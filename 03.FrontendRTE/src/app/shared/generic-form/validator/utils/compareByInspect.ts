/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { inspect } from './inspect';

/**
 * ### .compareByInspect(mixed, mixed)
 *
 * To be used as a compareFunction with Array.prototype.sort. Compares elements
 * using inspect instead of default behavior of using toString so that Symbols
 * and objects with irregular/missing toString can still be sorted without a
 * TypeError.
 *
 * @param first element to compare
 * @param second element to compare
 * @returns -1 if 'a' should come before 'b'; otherwise 1
 * @name compareByInspect
 *
 * @api public
 */

export function compareByInspect(a: any, b: any): -1 | 1 {
  return inspect(a) < inspect(b) ? -1 : 1;
}
