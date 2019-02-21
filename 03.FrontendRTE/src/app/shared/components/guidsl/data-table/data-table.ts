import { Logger } from '@upe/logger';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { IViewModel } from '@shared/architecture/data/viewmodel';

export class DataTable<T extends IViewModel<any>> {
  /** Stream that emits whenever the data has been modified. */
  public dataChange: BehaviorSubject<T[]> = new BehaviorSubject<T[]>([]);
  private logger: Logger = new Logger({name: 'DataTable'});

  constructor(data: T[] = [], public filter: (search: string, data: T) => boolean) {
    this.addEntries(data);
  }

  public get size(): number {
    return this._data.size;
  }

  private _data: Map<string, T> = new Map();

  public get data(): T[] {
    return this.dataChange.value;
  }

  public addEntries(entries: T[]): void {
    for (const entry of  entries) {
      this.addEntry(entry);
    }
  }

  public addEntry(entry: T): void {
    if (this._data.has(entry.internId)) {
      this.logger.error('MAF0x00C7: entry is already added to the data table');
      throw new Error('MAF0x00C7: entry is already added to the data table');
    }
    this._data.set(entry.internId, entry);
    this.triggerDataChange();
  }

  public getEntry(key: string): T | null {
    return this._data.get(key) || null;
  }

  public removeEntry(entry: T): void {
    this.removeEntryByKey(entry.internId);
  }

  public removeEntryByKey(key: string): void {
    if (!this._data.has(key)) {
      this.logger.error('MAF0x00C8: entry is not added to the data table');
      throw new Error('MAF0x00C8: entry is not added to the data table');
    }
    this._data.delete(key);
    this.triggerDataChange();
  }

  public hasEntry(entry: T): boolean {
    return this.hasEntryByKey(entry.internId);
  }

  public hasEntryByKey(key: string): boolean {
    return this._data.has(key);
  }

  private triggerDataChange(): void {
    this.dataChange.next(Array.from(this._data.values()));
  }

}
