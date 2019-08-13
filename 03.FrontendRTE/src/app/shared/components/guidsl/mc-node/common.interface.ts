/* (c) https://github.com/MontiCore/monticore */

/**
 * Signature of a component, which uses data from backend. This includes all
 * text inputs, pie chart, data table, etc.
 */
export interface IDataComponent {
  setData(data: any);
}

/**
 * Command configuration object structure
 *
 * @example
 * data: {
 *   cmd: [{ className: 'PersonalDetailsKontenTable', method: 'getById', idRef: 'personalId' }]
 * }
 */
export interface ICommandConfig {
  className: string;
  command: string;
  idRef?: string;
  param?: string;
}

/**
 * Signature of components, which accept css properties and classes in their
 * configuration. This includes all text inputs, button, etc.
 */
export interface IStylable {
  style: any
  classes: string[]
}
