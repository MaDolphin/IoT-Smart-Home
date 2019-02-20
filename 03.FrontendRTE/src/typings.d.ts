// Typings reference file, see links for more information
// https:// github.com/typings/typings
// https:// www.typescriptlang.org/docs/handbook/writing-declaration-files.html

declare var System: any;
// declare var module: { id: string };
// declare var require: NodeRequire;

// declare var jQuery: any;

declare interface ObjectConstructor {
  assign(...objects: Object[]): Object;
}

interface JQuery {
  nestable(obj: any): void;
  modal(obj: any): void;
}

interface JQueryStatic {
  speechApp: any;
  smallBox(obj: any): void;
}

declare module '*.json' {
  const value: any;
  export default value;
}
