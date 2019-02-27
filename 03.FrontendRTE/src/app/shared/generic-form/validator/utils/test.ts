/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
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
