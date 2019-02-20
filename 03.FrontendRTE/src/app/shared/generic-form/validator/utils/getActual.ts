/**
 * ### .getActual(object, [actual])
 *
 * Returns the `actual` value for an Assertion.
 *
 * @param object (constructed Assertion)
 * @param chai.Assertion.prototype.assert arguments
 *
 * @name getActual
 */
import { Assertion } from '../assertion';

export function getActual(obj: Assertion, ...args: any[]) {
  return args.length > 4 ? args[4] : obj._obj;
}
