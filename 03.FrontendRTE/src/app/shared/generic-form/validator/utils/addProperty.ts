/* (c) https://github.com/MontiCore/monticore */

import { Assertion } from '../assertion';
import { flag } from './flag';
import { isProxyEnabled } from './isProxyEnabled';
import { transferFlags } from './transferFlags';

/**
 * ### .addProperty(ctx, name, getter)
 *
 * Adds a property to the prototype of an object.
 *
 *     utils.addProperty(chai.Assertion.prototype, 'foo', function () {
 *       let obj = utils.flag(this, 'object');
 *       new chai.Assertion(obj).to.be.instanceof(Foo);
 *     });
 *
 * Can also be accessed directly from `chai.Assertion`.
 *
 *     chai.Assertion.addProperty('foo', fn);
 *
 * Then can be used as any other assertion.
 *
 *     expect(myFoo).to.be.foo;
 *
 * @param ctx object to which the property is added
 * @param name of property to add
 * @param getter function to be used for name
 *
 * @name addProperty
 * @api public
 */

export function addProperty(ctx: any, name?: string, getter?: Function) {
  getter = getter === undefined ? function () {
  } : getter;

  Object.defineProperty(ctx, name,
    {
      get: function propertyGetter(this: any) {
        // Setting the `ssfi` flag to `propertyGetter` causes this function to
        // be the starting point for removing implementation frames from the
        // stack trace of a failed assertion.
        //
        // However, we only want to use this function as the starting point if
        // the `lockSsfi` flag isn't set and proxy protection is disabled.
        //
        // If the `lockSsfi` flag is set, then either this assertion has been
        // overwritten by another assertion, or this assertion is being invoked
        // from inside of another assertion. In the first case, the `ssfi` flag
        // has already been set by the overwriting assertion. In the second
        // case, the `ssfi` flag has already been set by the outer assertion.
        //
        // If proxy protection is enabled, then the `ssfi` flag has already been
        // set by the proxy getter.
        if (!isProxyEnabled() && !flag(this, 'lockSsfi')) {
          flag(this, 'ssfi', propertyGetter);
        }

        const result = getter.call(this);
        if (result !== undefined) {
          return result;
        }

        const newAssertion = Assertion.create();
        transferFlags(this, newAssertion);
        return newAssertion;
      }
      , configurable: true,
    },
  );
}
