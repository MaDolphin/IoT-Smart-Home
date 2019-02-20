/*!
 * Chai - test utility
 * Copyright(c) 2012-2014 Jake Luer <jake@alogicalparadox.com>
 * MIT Licensed
 */

/*!
 * Module dependancies
 */

import { flag } from './flag';

/**
 * ### .test(object, expression)
 *
 * Test and object for expression.
 *
 * @param obj
 * @param args
 */
export function test(obj, args) {
  let negate = flag(obj, 'negate')
    , expr   = args[0];
  return negate ? !expr : expr;
}
