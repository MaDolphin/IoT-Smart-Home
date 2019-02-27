/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Assertion } from '../assertion';
import { transferFlags } from './transferFlags';

/**
 * ### .overwriteChainableMethod(ctx, name, method, chainingBehavior)
 *
 * Overwites an already existing chainable method
 * and provides access to the previous function or
 * property.  Must return functions to be used for
 * name.
 *
 *     utils.overwriteChainableMethod(chai.Assertion.prototype, 'lengthOf',
 *       function (_super) {
 *       }
 *     , function (_super) {
 *       }
 *     );
 *
 * Can also be accessed directly from `chai.Assertion`.
 *
 *     chai.Assertion.overwriteChainableMethod('foo', fn, fn);
 *
 * Then can be used as any other assertion.
 *
 *     expect(myFoo).to.have.lengthOf(3);
 *     expect(myFoo).to.have.lengthOf.above(3);
 *
 * @param ctx object whose method / property is to be overwritten
 * @param name of method / property to overwrite
 * @param method function that returns a function to be used for name
 * @param chainingBehavior function that returns a function to be used for property
 *
 * @name overwriteChainableMethod
 * @api public
 */
export function overwriteChainableMethod(ctx, name, method, chainingBehavior) {
  let chainableBehavior = ctx.__methods[name];

  let _chainingBehavior              = chainableBehavior.chainingBehavior;
  chainableBehavior.chainingBehavior = function overwritingChainableMethodGetter() {
    let result = chainingBehavior(_chainingBehavior).call(this);
    if (result !== undefined) {
      return result;
    }

    let newAssertion = Assertion.create();
    transferFlags(this, newAssertion);
    return newAssertion;
  };

  let _method              = chainableBehavior.method;
  chainableBehavior.method = function overwritingChainableMethodWrapper() {
    let result = method(_method).apply(this, arguments);
    if (result !== undefined) {
      return result;
    }

    let newAssertion = Assertion.create();
    transferFlags(this, newAssertion);
    return newAssertion;
  };
}
