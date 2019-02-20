import { nonNullObject } from '@shared/utils/util';

/**
 * Replace minus one values in aggregates with empty string, since -1 represents
 * missing value.
 *
 * @param data Aggregate array
 */
// TODO: replace with something
export function replaceMinusOnes(data: any, recursive: boolean = false): void {
  if (recursive) {
    let traverse = (el) => {
      for (let key in el) {
        if (nonNullObject(el[key]) && el.hasOwnProperty(key)) {
          traverse(el[key]);
        }
        if (+el[key] === -1 && el.hasOwnProperty(key)) {
          el[key] = '';
        }
      }
    };
    traverse(data);
  } else {
    for (let key in data) {
      if (+data[key] === -1 && data.hasOwnProperty(key)) {
        data[key] = '';
      }
    }
  }
}
