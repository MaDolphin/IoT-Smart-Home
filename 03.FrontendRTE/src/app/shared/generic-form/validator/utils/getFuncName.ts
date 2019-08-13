/* (c) https://github.com/MontiCore/monticore */

/**
 * ### .getFuncName(constructorFn)
 *
 * Returns the name of a function.
 * When a non-function instance is passed, returns `null`.
 * This also includes a polyfill function if `aFunc.name` is not defined.
 *
 * @name getFuncName
 * @param funct
 */
let functionNameMatch = /\s*function(?:\s|\s*\/\*[^(?:*\/)]+\*\/\s*)*([^\s\(\/]+)/;

export function getFuncName(aFunc) {
  if (typeof aFunc !== 'function') {
    return null;
  }

  let name = '';
  if (typeof Function.prototype.name === 'undefined' && typeof aFunc.name === 'undefined') {
    // Here we run a polyfill if Function does not support the `name` property and if aFunc.name is not defined
    let match = Function.prototype.toString.call(aFunc).match(functionNameMatch);
    if (match) {
      name = match[1];
    }
  } else {
    // If we've got a `name` property we just use it
    name = aFunc.name;
  }

  return name;
}
