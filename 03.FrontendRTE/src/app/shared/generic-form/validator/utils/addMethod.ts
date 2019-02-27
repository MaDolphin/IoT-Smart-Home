/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Assertion } from '../assertion';
import { addLengthGuard } from './addLengthGuard';
import { flag } from './flag';
import { proxify } from './proxify';
import { transferFlags } from './transferFlags';

/**
 * ### .addMethod(ctx, name, method)
 *
 * Adds a method to the prototype of an object.
 *
 *     utils.addMethod(chai.Assertion.prototype, 'foo', function (str) {
 *       let obj = utils.flag(this, 'object');
 *       new chai.Assertion(obj).to.be.equal(str);
 *     });
 *
 * Can also be accessed directly from `chai.Assertion`.
 *
 *     chai.Assertion.addMethod('foo', fn);
 *
 * Then can be used as any other assertion.
 *
 *     expect(fooStr).to.be.foo('bar');
 *
 * @param ctx object to which the method is added
 * @param name of method to add
 * @param method function to be used for name
 *
 * @name addMethod
 * @api public
 */
export function addMethod(descriptor: PropertyDescriptor, name: string, method: Function) {
  const methodWrapper = function (this: any) {
    // Setting the `ssfi` flag to `methodWrapper` causes this function to be the
    // starting point for removing implementation frames from the stack trace of
    // a failed assertion.
    //
    // However, we only want to use this function as the starting point if the
    // `lockSsfi` flag isn't set.
    //
    // If the `lockSsfi` flag is set, then either this assertion has been
    // overwritten by another assertion, or this assertion is being invoked from
    // inside of another assertion. In the first case, the `ssfi` flag has
    // already been set by the overwriting assertion. In the second case, the
    // `ssfi` flag has already been set by the outer assertion.
    if (!flag(this, 'lockSsfi')) {
      flag(this, 'ssfi', methodWrapper);
    }

    const result = method.apply(this, arguments);
    if (result !== undefined) {
      return result;
    }

    const newAssertion = Assertion.create();
    transferFlags(this, newAssertion);
    return newAssertion;
  };

  addLengthGuard(methodWrapper, name, false);
  descriptor.value = proxify(methodWrapper, name);
}
