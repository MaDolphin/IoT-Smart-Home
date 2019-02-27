/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Assertion } from '../assertion';
import { flag } from './flag';
import { getActual } from './getActual';
import { objDisplay } from './objDisplay';

/**
 * ### .getMessage(object, message, negateMessage)
 *
 * Construct the error message based on flags
 * and template tags. Template tags will return
 * a stringified inspection of the object referenced.
 *
 * Message template tags:
 * - `#{this}` current asserted object
 * - `#{act}` actual value
 * - `#{exp}` expected value
 *
 * @param object (constructed Assertion)
 * @param chai.Assertion.prototype.assert arguments
 *
 * @name getMessage
 * @api public
 */

export function getMessage(obj: Assertion, ...args: any[]) {
  const negate   = flag(obj, 'negate');
  const val      = flag(obj, 'object');
  const expected = args[3];
  const actual   = getActual(obj, args);
  let msg        = negate ? args[2] : args[1];
  const flagMsg  = flag(obj, 'message');

  if (typeof msg === 'function') {
    msg = msg();
  }
  msg = msg || '';
  msg = msg
    .replace(/#\{this\}/g, function () {
      return objDisplay(val);
    })
    .replace(/#\{act\}/g, function () {
      return objDisplay(actual);
    })
    .replace(/#\{exp\}/g, function () {
      return objDisplay(expected);
    });

  return flagMsg ? flagMsg + ': ' + msg : msg;
}
