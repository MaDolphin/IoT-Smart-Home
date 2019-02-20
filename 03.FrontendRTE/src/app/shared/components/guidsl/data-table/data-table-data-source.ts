import { DataSource } from '@angular/cdk/collections';
import { MatPaginator, MatSort } from '@angular/material';
import { Logger } from '@upe/logger';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { IViewModel } from '@shared/architecture/data/viewmodels/viewmodel';
import { DataTable } from './data-table';

export class DataTableDataSource<T extends IViewModel<any>> extends DataSource<any> {

  private _filterChange = new BehaviorSubject('');
  private logger: Logger = new Logger({name: 'DataTableDataSource'});

  constructor(private _dataTable: DataTable<T>,
    private _paginator: MatPaginator,
    private _sort: MatSort,
  ) {
    super();
  }

  public get filter(): string {
    return this._filterChange.value;
  }

  public set filter(filter: string) {
    this._filterChange.next(filter.toLowerCase());
  }

  /** Connect function called by the table to retrieve one stream containing the data to render. */
  connect(): Observable<T[]> {
    const displayDataChanges = [
      this._dataTable.dataChange,
      this._filterChange,
      this._paginator.page,
      this._sort.sortChange,
    ];

    return Observable.merge(...displayDataChanges).map(() => {

      // Grab the page's slice of data.
      const startIndex = this._paginator.pageIndex * this._paginator.pageSize;

      let data = this._dataTable.data.slice();

      data = data.filter((item: T) => this._dataTable.filter(this.filter, item));

      if (this._sort.active && this._sort.direction !== '') {
        data = data.sort((a, b) => {
          const propertyA: number | string | null = a[this._sort.active];
          const propertyB: number | string | null = b[this._sort.active];

          if (propertyA === null) {
            this.logger.error('MAF0x00C2: property A is null');
            throw new Error('MAF0x00C2: property A is null');
          }
          if (propertyB === null) {
            this.logger.error('MAF0x00C3: property B is null');
            throw new Error('MAF0x00C3: property B is null');
          }

          const valueA = isNaN(+propertyA) ? propertyA : +propertyA;
          const valueB = isNaN(+propertyB) ? propertyB : +propertyB;

          return (valueA < valueB ? -1 : 1) * (this._sort.direction === 'asc' ? 1 : -1);
        });
      }

      data = data.splice(startIndex, this._paginator.pageSize);

      return data;

    });

  }

  disconnect() {
  }

}
