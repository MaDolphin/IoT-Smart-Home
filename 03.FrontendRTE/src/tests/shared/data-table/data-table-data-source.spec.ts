/* (c) https://github.com/MontiCore/monticore */

import { MatPaginator, MatSort } from '@angular/material';
import { TypedJSON } from '@upe/typedjson';
import { DataTableDataSource } from '@components/data-table/data-table-data-source';
import { DataTable } from '@components/data-table/data-table';
import { TestViewModel } from './helper';

describe('Data Table', () => {

  describe('Data Source', () => {

    let ds;

    beforeEach(() => {
      let dt = new DataTable([], (_: string, __: TestViewModel) => { return true; });
      let mdPaginator = TypedJSON.parse(
        JSON.stringify({ length: 0, pageIndex: 0, pageSize: 0, pageSizeOptions: 0 }),
        MatPaginator,
      );
      ds = new DataTableDataSource(dt, mdPaginator, new MatSort());

      spyOn(ds, 'connect');
    });

    xit('connect', () => {
      ds.filter = 'filter';

      ds.connect();
      expect(ds.connect).toHaveBeenCalled();
    });

  });

});
