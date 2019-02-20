export interface JsonSchema {
  title: string;
  description?: string;
  type: string;
  key: string;
}

export class DataTableSchema {


  constructor(private _jsonSchema: JsonSchema[]) {
  }

  public get columnTitles(): string[] {
    return this._jsonSchema.map((entry: JsonSchema) => entry.title);
  }

  public get columnKeys(): string[] {
    return this._jsonSchema.map((entry: JsonSchema) => entry.key);
  }

  public get columnMeta(): JsonSchema[] {
    return this._jsonSchema;
  }

  public get columnTypes(): string[] {
    return this._jsonSchema.map((entry: JsonSchema) => entry.type);
  }

  public getKeyFromTitle(title: string): string {
    const column = this._jsonSchema.find((entry: JsonSchema) => entry.title === title);
    return column ? column.key : '';
  }

  public getColumnByKey(key: string): JsonSchema | null {
    return this._jsonSchema.find((entry: JsonSchema) => entry.key === key) || null;
  }

  public getColumnIndexByKey(key: string): number {
    return this._jsonSchema.findIndex((entry: JsonSchema) => entry.key === key);
  }

  public reOrder(a: string, b: string) {
    const column: JsonSchema | null = this.getColumnByKey(a);
    if (!column) {
      const err = `MAF0x00C4: column with key: ${a} not found`;
      throw new Error(err);
    }
    const sourceIndex = this.getColumnIndexByKey(a);
    if (sourceIndex < 0) {
      const err = `MAF0x00C5: column with key: ${a} not found`;
      throw new Error(err);
    }
    const targetIndex = this.getColumnIndexByKey(b);
    if (targetIndex < 0) {
      const err = `MAF0x00C6: column with key: ${b} not found`;
      throw new Error(err);
    }
    const jsonSchema = this._jsonSchema.slice(0);

    if (sourceIndex < targetIndex) {

      for (let i = sourceIndex; i < targetIndex; i++) {
        jsonSchema[i] = jsonSchema[i + 1];
      }

    } else if (targetIndex < sourceIndex) {

      for (let i = sourceIndex; i > targetIndex; i--) {
        jsonSchema[i] = jsonSchema[i - 1];
      }

    }

    jsonSchema[targetIndex] = column;

    this._jsonSchema = jsonSchema;

  }

}
