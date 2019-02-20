import { config } from '../config';
import { inspect } from './inspect';

/**
 * ### .objDisplay(object)
 *
 * Determines if an object or an array matches
 * criteria to be inspected in-line for error
 * messages or should be truncated.
 *
 * @param javascript object to inspect
 * @name objDisplay
 *
 * @api public
 */
export function objDisplay(obj) {
  let str  = inspect(obj)
    , type = Object.prototype.toString.call(obj);

  if (config.truncateThreshold && str.length >= config.truncateThreshold) {
    if (type === '[object Function]') {
      return !obj.name || obj.name === ''
             ? '[Function]'
             : '[Function: ' + obj.name + ']';
    } else if (type === '[object Array]') {
      return '[ Array(' + obj.length + ') ]';
    } else if (type === '[object Object]') {
      let keys = Object.keys(obj)
        , kstr = keys.length > 2
                 ? keys.splice(0, 2).join(', ') + ', ...'
                 : keys.join(', ');
      return '{ Object (' + kstr + ') }';
    } else {
      return str;
    }
  } else {
    return str;
  }
}
