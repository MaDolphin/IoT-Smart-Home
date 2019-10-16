/* (c) https://github.com/MontiCore/monticore */

/**
 * ### .transferFlags(assertion, object, includeAll = true)
 *
 * Transfer all the flags for `assertion` to `object`. If
 * `includeAll` is set to `false`, then the base Chai
 * assertion flags (namely `object`, `ssfi`, `lockSsfi`,
 * and `message`) will not be transferred.
 *
 *
 *     let newAssertion = Assertion.create()
 *     utils.transferFlags(assertion, newAssertion);
 *
 *     let anotherAsseriton = new Assertion(myObj);
 *     utils.transferFlags(assertion, anotherAssertion, false);
 *
 * @param assertion the assertion to transfer the flags from
 * @param object the object to transfer the flags to; usually a new assertion
 * @param includeAll
 *
 * @name transferFlags
 * @api private
 */
export function transferFlags(assertion, object, includeAll?) {
  let flags = assertion.__flags || (assertion.__flags = Object.create(null));

  if (!object.__flags) {
    object.__flags = Object.create(null);
  }

  includeAll = arguments.length === 3 ? includeAll : true;

  for (let flag in flags) {
    if (includeAll ||
      (flag !== 'object' && flag !== 'ssfi' && flag !== 'lockSsfi' && flag !== 'message')) {
      object.__flags[flag] = flags[flag];
    }
  }
}
