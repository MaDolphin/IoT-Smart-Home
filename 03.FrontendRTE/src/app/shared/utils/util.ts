// function to merge second object into the first one
export function mergeIn(obj1, obj2): void {
  let keys1 = Object.keys(obj1);
  let keys2 = Object.keys(obj2);
  if (!keys2.length) {
    return;
  }
  for (let i = 0; i < keys2.length; ++i) {
    if (keys1.indexOf(keys2[i]) === -1) {
      obj1[keys2[i]] = obj2[keys2[i]];
    } else {
      if (nonNullObject(obj1[keys2[i]]) && nonNullObject(obj2[keys2[i]])) {
        mergeIn(obj1[keys2[i]], obj2[keys2[i]]);
      }
    }
  }
}
export function nonNullObject(obj: any): boolean {
  return typeof obj === 'object' && obj !== null;
}

export function nonEmptyObject(obj: any): boolean {
  return typeof obj === 'object' && !!Object.keys(obj).length;
}

export function copyByValue(obj: any): any {
  return JSON.parse(JSON.stringify(obj));
}