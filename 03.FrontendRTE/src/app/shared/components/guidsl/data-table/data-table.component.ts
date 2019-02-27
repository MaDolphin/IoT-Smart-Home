/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  HostBinding,
  HostListener,
  Input,
  OnInit,
  Output,
  PipeTransform,
  QueryList,
  TemplateRef,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { Router } from '@angular/router';
import { DatatableComponent } from '@swimlane/ngx-datatable';

import { Logger } from '@upe/logger';
import 'rxjs/add/observable/fromEvent';
import 'rxjs/add/observable/merge';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/startWith';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { NUMBER_MASK } from '@shared/architecture/forms/controls/money.control';
import { ISerializable } from '@shared/architecture/serializable';
import { RouterLocalService } from '@shared/architecture/services/router.local.service';
import { StorageService } from '@shared/architecture/services/storage.service';
import { NotificationService } from '@shared/notification/notification.service';
import { DialogCallbackTwo } from '@shared/utils/dialog/dialog.callback';

import { JsonSchema } from './data-table-schema';
import { XLSXService } from './XLSX.service';
import { IViewModel } from '@shared/architecture/data/viewmodel';


export interface ResponseSaving<T> {
  entry: T;
  success: Subject<boolean>
}

export const EDIT_CANCEL = 'EDIT_CANCEL';

@Component({
  selector: 'macoco-data-table',
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.scss'],
})
export class DataTableComponent<T extends IViewModel<D> & ISerializable<D>, D> implements OnInit, AfterViewInit {

  public arrowHTML = new Map();

  public mask: Array<string | RegExp> = NUMBER_MASK;

  @HostBinding('attr.name')
  name: string;

  public get columns(): TableColumn[] {
    return this._columns;
  }

  public setData(data: any) {
    this.rows = this.formatValues(data);
  }

  private formatValues(data: any) {
    let tmp = JSON.parse(JSON.stringify(data));
    this._columns.forEach(col => {
      if (col.format) {
        tmp.forEach(row => {
          if (typeof col.prop === 'string') {
            let mapping = col.prop.split('.');
            let ref = row;
            while (mapping.length > 1) {
              ref = ref[mapping.shift()];
            }
            if (!!ref[mapping[0]]) {
              ref[mapping[0]] = col.format.replace('{value}', ref[mapping[0]]);
            }
          }
        })
      }
    });
    return tmp;
  }

  /**
   * Array of columns (model).
   * @type {TableColumn[]}
   */
  @Input()
  public set columns(value: TableColumn[]) {
    if (this.equalColumns(value, this._columns)) {
      return;
    }

    this._columns = value;

    this.copyColumnDetails();

    this.loadSettings();
  }

  private equalColumns(col1: TableColumn[], col2: TableColumn[]): boolean {
    for (let i = 0; i < col1.length; i++) {
      if (i >= col2.length)
        return false;

      if (col1[i].prop !== col2[i].prop)
        return false;

      if (col1[i].uneditable !== col2[i].uneditable)
        return false;

      if (col1[i].headerClass !== col2[i].headerClass)
        return false;

      // TODO PS: need cleaner check if columns are equal
    }

    return true;
  }

  /**
   * The height of the header in pixels. Pass a falsey for no header.
   * Pass 'auto' for automatic height of the cell
   * Default value: 30
   * @type {number}
   */
  public set displayColumns(value: TableColumn[]) {

    let newColumns = this.appendTableControls(value);

    /* TODO: purpose?
    if (this.equalColumns(newColumns, this.displayColumns)) {
      return;
    }
    /** */

    this._displayColumns = newColumns;
  }

  @Input() public headerHeight = 'auto';

  public get displayColumns() {
    return this._displayColumns;
  }

  public get cssClasses(): TableCssClasses {
    return Object.assign({
      sortAscending: 'datatable-icon-up',
      sortDescending: 'datatable-icon-down',
      pagerLeftArrow: 'datatable-icon-left',
      pagerRightArrow: 'datatable-icon-right',
      pagerPrevious: 'datatable-icon-prev',
      pagerNext: 'datatable-icon-skip',
    }, this._cssClasses);
  }

  // region Inputs

  @ViewChild(DatatableComponent) table: DatatableComponent;
  @ViewChild('filter') filter: ElementRef;
  @ViewChildren('editInstances') editInstances: QueryList<ElementRef>;

  /**
   * Custom CSS classes that can be defined to override the icons classes
   * for up/down in sorts and previous/next in the pager.
   * @type {TableCssClasses}
   */
  @Input()
  public set cssClasses(value: TableCssClasses) {
    this._cssClasses = value;
  }

  public get messages(): TableMassages {
    return Object.assign({
      emptyMessage: 'Keine Daten zum Anzeigen',
      totalMessage: this.rows.length !== 1 ? 'Einträge' : 'Eintrag',
      selectedMessage: 'ausgewählt',
    }, this._messages);
  }


  /**
   * Unique identifier for the datatable.
   * @type {String}
   */
  @Input()
  public uId: string;

  /**
   * Static messages in the table you can override for localization.
   * @param {TableMassages} value
   */
  @Input()
  public set messages(value: TableMassages) {
    this._messages = value;
  }

  @ViewChild('detailColumn') detailColumn: TemplateRef<any>;
  @ViewChild('actionColumn') actionColumn: TemplateRef<any>;
  @ViewChild('checkboxColumn') checkboxColumn: TemplateRef<any>;
  @ViewChild('cellTemplate') cellTemplate: TemplateRef<any>;
  @ViewChild('headerTemplate') headerTemplate: TemplateRef<any>;
  /**
   * The mode which the columns are distributed across the table.
   * For example: flex will use flex-grow api, force will distribute
   * proportionally and standard will just distrbute based on widths.
   * Default value: force
   * @type {'force' | 'flex' | 'standard'}
   */
  @Input() public columnMode: 'force' | 'flex' | 'standard' = 'force';


  /**
   * Defines a style function which should be applied to every cell
   *
   * @type {any} style object
   */
  @Input() public cellStyle: (column: any, row: any, value: any) => any;

  /**
   * The total count of all rows.
   * Default value: 0
   * @type {number}
   */
  @Input() public count = 0;

  /**
   * Should the table use external paging vs client-side.
   * Default value: false
   * @type {boolean}
   */
  @Input() public externalPaging = false;

  /**
   * Should the table use external sorting vs client-side.
   * Default value: false
   * @type {boolean}
   */
  @Input() public externalSorting = false;

  /**
   * The height of the footer in pixels. Pass a falsey for no footer.
   * Default value: 50
   * @type {number}
   */
  @Input()
  public footerHeight = 'auto';
  @Input() public detailsRowHeight = 'auto';

  /**
   * The page size to be shown.
   * Default value: 10.
   * @type {number}
   */
  @Input() public limit: number = 10;

  /**
   * Show the linear loading bar.
   * Default value: true
   * @type {boolean}
   */
  @Input() public loadingIndicator: boolean = true;

  /**
   * The current offset ( page - 1 ) shown.
   * Default value: 0
   * @type {number}
   */
  @Input() public offset: number = 0;

  /**
   * Column re-ordering enabled/disabled.
   * Default value: true
   * @type {boolean}
   */
  @Input() public reorderable: boolean = true;

  /**
   * The height of the row.
   * @type {TableRowHeight<T>}
   */
  @Input() public rowHeight: TableRowHeight<T> = 'auto';

  /**
   * Array of rows to display.
   */
  @Input() public asyncRows: Observable<T[]> | Subject<T[]> | BehaviorSubject<T[]>;

  /**
   * Enabled horizontal scrollbars.
   * Default value: false
   * @type {boolean}
   */
  @Input() public scrollbarH: boolean = true;

  /**
   * Enable vertical scrollbar for fixed height vs fluid. This is necessary for virtual scrolling.
   * Default value: false
   * @type {boolean}
   */
  @Input() public scrollbarV: boolean = false;
  private _scrollbarV: boolean = false;

  /**
   * A boolean/function you can use to check whether you want to select a particular
   * row based on a criteria.
   */
  @Input() public selectCheck: (row: T, column: TableColumn, value) => boolean = () => false;

  /**
   * List of row objects that should be represented as selected in the grid.
   * It does object equality, for prop checking use the selectCheck function.
   * Default value: []
   * @type {Array}
   */
  @Input() public selected = [];

  /**
   * Type of row selection. Options are single, multi, multiClick and chkbox. For no selection pass a falsey.
   * Default value: 'falsey'
   * @type {string}
   */
  @Input() public selectionType: TableSelectionType = 'falsey';
  private selectionTypeOriginal: TableSelectionType = 'falsey';

  @Input() public selectionSum: (rows: T) => string;
  /**
   * Array of sorted columns by property and type.
   * Default value: []
   * TODO : add typing
   * @type {Array}
   */
  @Input() public sorts = [];

  /**
   * Single vs Multi sorting. When in single mode, any click after the initial click will
   * replace the current selection with the next selection. In multi selection mode, any
   * incremental click will add to the current selection array.
   * Default value: 'single'
   * @type {string}
   */
  @Input() public sortType: 'single' | 'multi' = 'single';

  /**
   * A property on the row object that uniquely identifies the row.
   * Default value: 'internId'
   * @type {string}
   */
  @Input() public trackByProp = 'internId';

  /**
   * A function that will invoked with each row's properties. The result of the function
   * can be a string or object with key/boolean
   */
  public rowClassFunction: { function: (row: T) => { [key: string]: boolean } };

  @Input() public rowClass: (row: T) => { [key: string]: boolean };

  @Input() public actionColumnTitle = '';

  @Input() public exportFileName: string = 'export';

  @Input() public actionTemplate: TemplateRef<any>;

  @Input() public placeholderFilterInput = 'Suche';
  @Input() public editTemplates: { [key: string]: TemplateRef<any> } = {};

  @Input() public detailsTemplate: TemplateRef<any>;

  @Input() public inlineEdit = false;
  @Input() public inlineNew: boolean = false;

  @Input()
  public isEditable = false;
  @Input()
  public isDeletable = false;
  @Input()
  public skipNotification = false;
  @Input()
  public isViewable = false;
  @Input() public hasActionColumn = false;
  @Input()
  public isFilterable = true;
  @Input()
  public isExportable = true;

  @Input()
  public isCreateSub = false;

  /**
   * Special Keys need to be excluded from grouping.
   * These keys are defined here
   */

  @Input()
  public groupExcludeKeys = ['Summe (inkl. Pauschale)', 'Pauschale', 'Summe (ohne. Pauschale)', 'Summe '];

  @Input() public hasCreateNew = false;
  @Input() public canHideColumns = true;
  @Input()
  public isDeActivateable = false;
  @Input() public deActivatedProperties: boolean[] = [];

  @Input()
  public viewEventLink: string;

  public editEventLink: string;

  @Input() public bordered = false;

  @Input() public sortable: boolean = true;
  @Input() public draggable: boolean = true;
  @Input() public resizeable: boolean = true;

  @Input() public hasShowInactiveBtn: boolean = true;

  @Input()
  public set showInactive(value: boolean) {
    this._showInactive = value;
    if (this.scrollbarV) {
      this.recreateTable();
    }
    this.regroupRows();
  }

  @Input() public dataType;

  public get showInactive(): boolean {
    return this._showInactive;
  }

  @Input()
  public activeRow: (row: any) => boolean = _ => true;

  public presentInactiveRow(): boolean {
    if (this._allRows === undefined || this._allRows === null) {
      return false;
    }
    return this._allRows.some((r: any) => !this.activeRow(r) && !r.emptyRow);
  }

  @Input()
  public formEditControl;



  @Output('create') createEvent: EventEmitter<T> = new EventEmitter();

  @Output('edit') editEvent: EventEmitter<T> = new EventEmitter();
  @Output('editNavigate') editNavigateEvent: EventEmitter<T> = new EventEmitter();

  @Output('save') saveEvent: EventEmitter<ResponseSaving<T>> = new EventEmitter();
  @Output('delete') deleteEvent: EventEmitter<T> = new EventEmitter();

  @Output('view') viewEvent: EventEmitter<T> = new EventEmitter();

  @Output('cancelDelete') cancelDeleteEvent: EventEmitter<T> = new EventEmitter();
  @Output('cancelEdit') cancelSaveEvent: EventEmitter<T> = new EventEmitter();

  @Output('clone') cloneEvent: EventEmitter<D> = new EventEmitter();

  @Output('deActivate') deActivateEvent: EventEmitter<T> = new EventEmitter();

  @Output('sort') sortEvent: EventEmitter<T> = new EventEmitter();

  @Output('select') selectEvent: EventEmitter<T> = new EventEmitter();
  @Output('showSelected') showSelectedEvent: EventEmitter<T> = new EventEmitter();
  @Output('deselect') deselectEvent: EventEmitter<T> = new EventEmitter();

  public test() {
    console.log(this.displayColumns);
  }

  @Input() public currentEditId: number = null;

  @Input()
  public set rows(value: T[]) {
    let detailExpansions = [];
    if (value === undefined) value = [];
    // setup for preserving expanded rows on reload
    if (this.detailsTemplate !== undefined) {
      if (this.table && this.table.bodyComponent) {
        this.table.bodyComponent.rowExpansions.forEach((_, key) => {
          if (key.id) {
            detailExpansions.push(key.id);
          }
        })
      }
    }
    this._rows = value.filter((row: any) => this.activeRow(row));
    this._allRows = value || [];
    this._unfilteredRows = this.rows.slice(0);
    this._unfilteredAllRows = this.rows.slice(0);
    this.setRowsMetadata();

    this.prependCreationRow();

    if (this.isGroupable) {
      this.setGroupedRows();
    }

    if (this.table && this.rows.length <= this.table.offset * this.limit) {
      this.table.offset--;
    }
    if (this.scrollbarV) {
      this.recreateTable();
    } else {
      this.rerenderRows();
    }

    // setup for preserving expanded rows on reload
    if (this.detailsTemplate !== undefined) {
      setTimeout(() => {
        detailExpansions.forEach(id => {
          if (this.table && this.table.bodyComponent
              && this.table.rowDetail
          ) {
            let r = this.rows[this.rows.findIndex(row => row.id === id)];
            this.table.rowDetail.toggleExpandRow(r);
          }
        })
      })
    }

  }

  private logger: Logger = new Logger({name: 'DataTableComponent'});

  // endregion

  public get rows(): T[] {
    if (this.showInactive) {
      return this._allRows;
    } else {
      return this._rows;
    }
  }

  /*
  @Input()
  public set rightClickMenu(value: any[]) {
    value.forEach(v => {
      if (v.divider === undefined) v.divider = false;
      if (v.passive === undefined) v.passive = false;
      if (v.enabled === undefined) v.enabled = true;
      if (v.visible === undefined) v.visible = true;
      if (v.execute === undefined) v.execute = () => { };
    });
    this._contextMenu = value;
  }

  public get rightClickMenu(): any[] {
    return this._contextMenu;
  }
  /**/

  @Input()
  public groupsExpanded: boolean;

  @Input()
  public groupRowsBy: string = '';

  public groupedRows: any[];
  public groupedCol: TableColumn;

  @Input()
  public set isGroupable(value: boolean) {
    if (value) {
      if (!!this.groupRowsBy) {
        this.groupedCol = this.columns.filter(col => col.prop.toString() === this.groupRowsBy)[0];
      }
      if (this.groupsExpanded === undefined) {
        this.groupsExpanded = false;
      }
      this.setGroupedRows();
    }
    this._isGroupable = value;
  }

  public get isGroupable(): boolean {
    return this._isGroupable;
  }

  // variable to help with recreateTable function
  public recreateHelper: any[] = [0];

  @Input()
  public rightClickMenu: TemplateRef<any>;
  private _isGroupable: boolean = false;
  private _showInactive: boolean = false;
  private _rows: T[] = [];
  private _allRows: T[] = [];
  private _unfilteredRows: T[] = [];
  private _unfilteredAllRows: T[] = [];
  protected _messages: TableMassages = {};
  private _cssClasses: TableCssClasses = {};
  private _columns: TableColumn[] = [];
  private _displayColumns: TableColumn[] = [];

  constructor(
      private notificationService: NotificationService,
      private _xlsx: XLSXService,
      private storageService: StorageService,
      private router: Router,
      private _routerLocalService: RouterLocalService) {
  }

  // Override table resize host listener
  @HostListener('window:resize')
  onWindowResize(): void {
    this.rerenderBody();
  }

  @HostListener('keydown.enter', ['$event'])
  public handleKeyboardEvent(event: KeyboardEvent) {
    if ( !this.formControlHasFocus() )
      return;

    event.stopPropagation();

    let row;

    if (this.currentEditId === null) {
      if ( (<any>this.rows[0]).emptyRow) {
        row = this.rows[0];
        this.onCreate(row);
      } else {
        // Hit enter on something which isnt part of the formcontrol for the datatable
        console.error('Enter', document.activeElement);
      }
    } else {
      row = this.rows.find((e) => {
        return e.id === this.currentEditId;
      });
      this.onEditSave(row);
    }




  }

  @HostListener('keydown.escape', ['$event'])
  public handleKeyboardEventEscape(event: KeyboardEvent) {
    if ( !this.formControlHasFocus() )
      return;

    let row;

    if (this.currentEditId === null) {
      if ( (<any>this.rows[0]).emptyRow) {
        row = this.rows[0];
      } else {
        // Hit enter on something which isnt part of the formcontrol for the datatable
        console.error('Enter', document.activeElement);
      }
    } else {
      row = this.rows.find((e) => {
        return e.id === this.currentEditId;
      });
    }

    event.stopPropagation();

    this.onEditCancel(row);
  }

  private formControlHasFocus(): boolean {
    let el: Element = document.activeElement;
    let controlName = el.getAttribute('name');

    let controls: any = this.formEditControl.controls;
    if ( controls.hasOwnProperty(controlName))
      return true;

    return false;
  }

  public ngOnInit() {

    if (this.rowClassFunction !== undefined && this.rowClassFunction !== null) {
      this.rowClass = this.rowClassFunction.function;
    }

    if (this.asyncRows && this.rows) {
      throw 'can not use async and sync data!';
    }
    if (!this.rows && !this.asyncRows) {
      throw 'rows not set!';
    }
    if (this.asyncRows) {
      this.asyncRows.subscribe((rows: T[]) => {
        this.rows = rows;
        this.loadingIndicator = false;
      });
    } else {
      this.loadingIndicator = false;
    }

    /*
    this.viewEvent.subscribe((res) => {
      const url = this.viewEventLink + '/' + res.id;
      console.log('navigating to ' + url);
      this.router.navigateByUrl(url);

    });
    /**/

    if (this.isViewable || this.isEditable || this.isDeletable || this.isDeActivateable)
      this.hasActionColumn = true;

    this.copyColumnDetails();

  }

  // TODO: uncomment when sorting is fixed
  /*
  public onSort(event) {
    if (this.isGroupable) {
      this.externalSorting = true;
      const sort = event.sorts[0];
      this.groupedRows = this.groupRows(sort);
      this.rerenderRows();
    } else {
      this.externalSorting = false;
    }
  }
  /** */

  // TODO: original sorting rule, needs to be merged with grouped sorting
  public onSort(event) {
    if (this.isGroupable) {
      return;
    }
    if (event.column.sortFn === undefined) {
      this.externalSorting = false;
    } else {
      this.externalSorting = true;
      const sort = event.sorts[0];
      this._rows.sort((a, b) => {
        if (!!(<any>a).emptyRow) {
          return -1;
        }
        if (!!(<any>b).emptyRow) {
          return 1;
        }
        return event.column.sortFn.function(a[sort.prop], b[sort.prop]) * (sort.dir === 'desc' ? -1 : 1);
      });
      this._allRows.sort((a, b) => {
        if (!!(<any>a).emptyRow) {
          return -1;
        }
        if (!!(<any>b).emptyRow) {
          return 1;
        }
        return event.column.sortFn.function(a[sort.prop], b[sort.prop]) * (sort.dir === 'desc' ? -1 : 1);
      });
      // TODO: investigate
      this._rows = [...this._rows];
      this._allRows = [...this._allRows];
      if (this.scrollbarV) {
        this.recreateTable();
      }
    }
    this.sortEvent.emit(event);
  }

  private scrollX = 0;

  public getGroupHeaderStyle(col) {
    let style = {
      'width': col.width + 'px',
      'min-width': col.width + 'px',
    };
    if (col === this.displayColumns[0]) {
      style['margin-left'] = -this.scrollX + 'px'
    }
    return style;
  }

  public onScroll(event) {
    if (event.offsetX !== undefined && event.offsetY !== undefined) {
      this.scrollX = event.offsetX;
    }
  }

  public onKeyPressPageSize(event) {
    if (event.key === 'Enter') {
      this.onPageSizeChange(event.target.value);
    }
  }

  public onPageSizeChange(pageSize) {

    if (pageSize > 0) {
      this.limit = +pageSize;
    } else
      this.limit = 10;

    this.table.offset = 0;
    this.table.limit = this.limit;
    this.table.pageSize = this.limit;
    this.table.recalculatePages();
    if (this.scrollbarV) {
      this.recreateTable();
    }
  }

  public onPaginated(event) {
    this.table.limit = this.limit;
    this.table.pageSize = this.limit;
    this.table.recalculatePages();
  }

  public pageCount(): number {
    if (this.limit > 0) {
      return Math.floor((this.rows.length - 1) / this.limit) + 1;
    }
    console.error('datatable', 'page limit cant be < 1');
    return -1;
  }


  public jumpToLastPage() {
    this.table.offset = this.pageCount() - 1;
  }

  public toggleColumn(col) {
    const isChecked = this.isCheckedColumn(col);

    if (isChecked) {
      this.displayColumns = this.displayColumns.filter(c => {
        return c.name !== col.name;
      });
    } else {
      let modelIndex = this.columns.findIndex((c: TableColumn) => c.name === col.name);
      // var displayIndex = this.displayColumns.findIndex((c: TableColumn) => c.name === col.name);

      if (this.hasActionColumn) {
        modelIndex++;
      }

      if (this.detailsTemplate) {
        modelIndex++;
      }

      this.displayColumns.splice(modelIndex, 0, col);

      // Fix damit sich die Tabelle aktualisert
      this.displayColumns = this.displayColumns.slice();
    }

    this.storeSettings();

  }

  public onDelete(event, entry: T): void {
    if (!this.skipNotification) {
      this.notificationService.notificationYesNo('Eintrag löschen', 'Soll dieser Eintrag wirklich gelöscht werden?', new DialogCallbackTwo(() => {
        this.deleteEvent.emit(entry);
      }, () => {
        this.cancelDeleteEvent.emit(entry);
      }), undefined, 500);
    } else {
      this.deleteEvent.emit(entry);
    }
    event.stopPropagation();
  }

  /**
   * Create Events
   */

  public onCreate(row): void {
    console.error('datatable', 'onCreate');

    console.log(this.formEditControl.validate());

    this.formEditControl.markAsCDirty();

    if (this.formEditControl.valid)
      this.createEvent.emit(row);
    else
      console.error('Form not valid');
  }

  /*
    public onCreateSave(): void {
      let res;

      if (this.inlineNew) {
        res = this.mergeObject({}, this.formEditControl.getModelValue());
        this.createEvent.emit(res);
      }


    }
  */
  public onCreateSuccess() {
    console.error('successfull oncreate');
  }

  public onCreateFailed() {
    console.error('failed oncreate');

  }

  public onCreateCancel(): void {
    this.currentEditId = null;
    this.formEditControl.reset();
  }


  /**
   * Edit Events
   */
  public onEdit(event, entry: T): void {

    Reflect.defineMetadata(EDIT_CANCEL, this.onEditCancel.bind(this), entry);
    this.editEvent.emit(entry);

    if (this.inlineEdit) {
      this.currentEditId = entry.id;
      this.selectionTypeOriginal = this.selectionType;
      this.selectionType = undefined;
    } else {
      if (this.editEventLink !== undefined) {
        this.router.navigateByUrl(this.editEventLink + '/' + entry.id);
      } else {
        this.editNavigateEvent.emit(entry);
      }
    }
    event.stopPropagation();
  }

  public onEditSave(entry: T): void {
    const success = new Subject<boolean>();

    this.formEditControl.markAsCDirty();
    this.formEditControl.markAsCTouched();
    this.selectionType = this.selectionTypeOriginal;

    if (this.formEditControl.valid) {
      let res = this.formEditControl.getModelValue();

      res.id = entry.id;

      this.saveEvent.emit({entry: res, success: success});
      this.currentEditId = null;

    } else {
      console.log('Form not valid', this.formEditControl.value);
    }


    // TODO: this actually works, but if we send value too early, subscription will
    // not work, we should wait on server sending positive response, which is currently
    // not used anywhere
    /*
    const sub = success.subscribe((status) => {
      if (status) {
        this.currentEditId = 0;
      }
      this.loadingIndicator = false;
      sub.unsubscribe();
    });
    /**/
  }

  /*
  private markFormGroupTouched(formGroup: FormGroup): void {
    console.log(Object.values(formGroup.controls));
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  };
  /** */

  public onEditCancel(entry: T): void {
    this.selectionType = this.selectionTypeOriginal;
    if (this.formEditControl !== undefined) {
      this.formEditControl.reset();
    }

    this.cancelSaveEvent.emit(entry);
    this.currentEditId = null;
  }

  public onEditSuccess() {
    console.log('onEdit Success');
  }

  public onEditError() {
    console.log('onEdit Error');
  }

  public onDeActivate(entry: T): void {
    this.deActivateEvent.emit(entry);
  }

  public isDeActivatedProperties(entry: number): boolean {
    if (this.deActivatedProperties.length > entry) {
      return this.deActivatedProperties[entry];
    }
    return false;
  }

  public excelExport() {
    this._xlsx.saveToXLSX(this.applySchemaFilterOnData());
  }

  public csvExport() {
    this._xlsx.saveToCSV(this.applySchemaFilterOnData(), this.exportFileName);
  }

  public updateFilter() {
    if (this.filter) {
      const val = this.filter.nativeElement.value.toLowerCase();
      let searchSet = [val];

      /*
      if (val) {
        searchSet = val.filter(e => {
          if (e.length > 0)
            return e;
        });
      }
      /** */

      let filterFunction = row => {
        let valueSet = [];
        let satisfySearch = false;
        this.columns.forEach(col => {
          let value = DataTableComponent.getRowProp(row, col.prop);
          if (col.displayValue !== undefined && !col.filterByModelValue) {
            valueSet.push(col.displayValue(value).toLowerCase());
          } else {
            value = value === null || value === undefined ? '' : value;
            if (Array.isArray(value)) {
              valueSet.push(...value.filter(v => v !== null && v !== undefined).map(v => v.toString().toLowerCase()));
            } else {
              valueSet.push(value.toString().toLowerCase());
            }
          }
        });
        searchSet.forEach(searchValue => {
          valueSet.forEach(value => {
            if (typeof value === 'string' && value.includes(searchValue)) {
              satisfySearch = true;
            }
          })
        });
        return satisfySearch;
      };

      if (searchSet.length > 0) {
        if (!this.showInactive) {
          this._rows = this._unfilteredRows.filter(filterFunction);
        } else {
          this._allRows = this._unfilteredAllRows.filter(filterFunction);
        }
      } else {
        if (!this.showInactive) {
          this._rows = this._unfilteredRows.slice(0);
        } else {
          this._allRows = this._unfilteredAllRows.slice(0);
        }
      }

      this.prependCreationRow();

      if (this.scrollbarV) {
        this.recreateTable();
      }

      this.regroupRows();

      /*
      if (searchSet.length > 0) {
        // Search each word of the search query indiviual
        this._rows = searchSet.map(el => {
          return this._unfilteredRows.filter((model: T) => {
            const json = model;
            const keys = Object.keys(json);
            let searchString = '';
            for (const key of keys) {
              // find pipe for column
              let pipe = (this._columns.filter(col => {
                return col.prop === key;
              }).map(a => a.pipe))[0];

              if (json[key]) {
                if (pipe)
                  searchString += ',' + pipe.transform(json[key]);
                else if (json[key].hasOwnProperty('zahlenTyp')) // check type for ZahlenWert
                  searchString += ',' + json[key].wert;
                else
                  searchString += ',' + json[key];
              }
            }
            return searchString.toLowerCase().indexOf(el) !== -1;
          });
        }).reduce((acc, cur) => { // find intersection of the result sets from the search
          return acc.filter(e => {
            if (cur.indexOf(e) >= 0)
              return e;
          });
        });

        this.table.offset = 0;
      } else {
        this._rows = this._unfilteredRows.slice(0);
      }
      /** */
    }
  }

  public rowHasClass(row, klass: string) {
    if (this.rowClass === null || this.rowClass === undefined)
      return false;

    let classes = this.rowClass(row);

    if (classes === undefined)
      return false;

    if (classes.hasOwnProperty(klass))
      return classes[klass];

    return false;
  }

  private regroupRows() {
    if (this.isGroupable) {
      // TODO: shorten up
      let rowExpansions = new Map(this.table.bodyComponent.rowExpansions);

      this.setGroupedRows();


      let groupKeys = [];
      rowExpansions.forEach((v: any, k: any) => {
        if (v === 1) {
          groupKeys.push(k.key)
        }
      });

      setTimeout(() => {
        if (this.groupedRows !== undefined && this.groupedRows.length) {
          this.groupedRows.forEach(group => {
            if (group.key
                && this.table.bodyComponent.rowExpansions.get(group) === 1
                && !groupKeys.includes(group.key)
                && !this.isEmptyRowGroup(group)) {
              this.toggleExpandGroup(group);
            }
          })
        }
      }, 0)
    }
  }

  public cellOverlayAction(event, row, column: TableColumn) {
    this.toggleExpandRow(event, row, column);
    if (column.onClick !== undefined) {
      column.onClick(row);
    }
  }

  public toggleExpandRow(event, row, column) {
    if (column.detailsColumn) {
      this.table.rowDetail.toggleExpandRow(row);
      if (this.arrowHTML.get(row).right) {
        this.arrowHTML.set(row, {right: false, value: '<div class=\'fa fa-angle-down\'></div>'})
      } else {
        this.arrowHTML.set(row, {right: true, value: '<div class=\'fa fa-angle-right\'></div>'})
      }
      event.stopPropagation();
    }
  }

  public onDetailToggle(event) {
    // console.log('Detail Toggled', event);
  }

  public onColumnResize(event) {
    this.displayColumns = this.table.recalculateColumns();
  }

  firedSelect = false;

  public onSelect({selected}) {

    let emptyRow = selected.find(row => !!row.emptyRow);
    if (emptyRow !== undefined) {
      let index = selected.indexOf(emptyRow);
      if (index > -1) {
        selected.splice(index, 1);
        let skip: boolean = true;
        selected.forEach(row => {
          if (!this.selected.includes(row)) {
            skip = false;
          }
        });
        if (skip || !selected.length) {
          if (!this.firedSelect) {
            this.table.onBodySelect({selected});
            this.firedSelect = false;
          } else {
            this.firedSelect = true;
          }
          return;
        }
      }
    }
    this.selected.splice(0, this.selected.length);
    this.selected.push(...selected);
    this.selectEvent.emit(selected);
  }

  public displayCheck(row) {
    return !row.emptyRow;
  }

  public onShowSelected() {
    if (this.selected === undefined || !this.selected.length) return;
    let sort = (arr) => {
      let to = [];
      let emptyRow = arr.find(row => !!row.emptyRow);
      if (emptyRow !== undefined) {
        to.push(emptyRow);
      }
      arr.forEach(row => {
        if (this.selected.includes(row)) {
          to.push(row);
        }
      });
      arr.forEach(row => {
        if (!this.selected.includes(row) && !row.emptyRow) {
          to.push(row);
        }
      });
      return to;
    };
    this._rows = sort(this._rows);
    this._allRows = sort(this._allRows);
    this.table.sorts = [];
    if (this.isGroupable) {
      this.setGroupedRows();
    }
    this.resetArrowHTML();
    if (this.scrollbarV) {
      this.recreateTable();
    }
    this.showSelectedEvent.emit();
  }

  public onDeselectAll() {
    this.selected = [];
    this.deselectEvent.emit();
  }

  public trackByFn(index: any, item: JsonSchema) {
    return item.key;
  }

  public toggleExpandGroup(group) {
    if (this.groupedCol !== undefined) {
      let bodyComponent = this.table.bodyComponent;

      // this line is to trick the bodyComponent that listener is subscribed to
      // something so that the error is not thrown on destroy
      bodyComponent.listener = new Observable().subscribe();
      bodyComponent.toggleRowExpansion(group);
      // bypass private modifier to force change detection of bodyComponent
      bodyComponent['cd'].markForCheck();
    }
  }

  public toggleGrouping(prop: string) {
    if (prop === 'Keine Gruppierung') {
      this.groupRowsBy = '';
      this.groupedCol = undefined;
    } else {
      this.groupRowsBy = prop;
      this.groupedCol = this.columns.filter(col => !!col.prop && col.prop.toString() === this.groupRowsBy)[0];
    }
    this.setGroupedRows();

    this.rerenderRows();
  }

  public summarizeGroup(rowGroup, column) {
    let summarization;
    if (!column.summarize.extended) {
      let arr = rowGroup.map(row => {
        return DataTableComponent.getRowProp(row, column.prop)
      });
      summarization = column.summarize.function(arr);
    } else {
      summarization = column.summarize.function(rowGroup);
    }
    if (column.displayValue) {
      summarization = column.displayValue(summarization);
    }
    if (!!column.summarize.title) {
      summarization = column.summarize.title + ': ' + summarization;
    }
    return summarization;
  }

  public setGroupedRows(sort?: { dir: string, prop: string }) {
    this.groupedRows = this.groupRows(sort);
  }

  public groupRows(sort?: { dir: string, prop: string }) {
    // create a map to hold groups with their corresponding results
    if (this.groupedCol === undefined) {
      if (sort !== undefined) {
        this.rows.sort((a, b) => {
          return this.compareRows(a, b, sort.prop) * (sort.dir === 'desc' ? -1 : 1);
        });
      }
      return [{key: 0, value: this.rows}];
    }

    const map = new Map();

    this.rows.forEach((row: any) => {
      let key = DataTableComponent.getRowProp(row, this.groupedCol.prop);
      if (this.groupedCol.displayValue !== undefined && !this.groupedCol.filterByModelValue) {
        key = this.groupedCol.displayValue(key);
      }

      // exclusion function
      if (this.isExcludedFromGroup(key)) {
        key = '';
      }

      if (!map.has(key)) {
        map.set(key, [row]);
      } else {
        map.get(key).push(row);
      }
    });

    let sortedMap;

    if (sort !== undefined) {
      if (sort.prop === this.groupedCol.prop) {
        sortedMap = new Map(
            Array.from(map).sort((a, b) => {
              return this.compare(a[0], b[0]) * (sort.dir === 'desc' ? -1 : 1);
            })
        );
      } else {
        sortedMap = new Map(
            Array.from(map).map((entry): [string, any] => {
              return [entry[0], entry[1].sort((a, b) => {
                return this.compareRows(a, b, sort.prop) * (sort.dir === 'desc' ? -1 : 1);
              })];
            })
        );
      }
    } else {
      sortedMap = map;
    }

    const addGroup = (key: any, value: any) => {
      return {key, value};
    };

    // convert map back to a simple array of objects
    return Array.from(sortedMap, x => addGroup(x[0], x[1]));
  }

  public isExcludedFromGroup(key) {
    return this.groupExcludeKeys.includes(key);
  }

  public transformGroupValue(row: any) {
    let prefix = this.getColumnName(this.groupedCol);
    if (row === undefined) {
      return prefix;
    }
    let res = DataTableComponent.getRowProp(row, this.groupedCol.prop);
    // TODO: special case pipes
    if (this.groupedCol.displayValue !== undefined) {
      return this.groupedCol.displayValue(res);
    } else {
      if (res === null || res === undefined) {
        return prefix;
      }
      let limit = this.groupedCol.groupHeaderLimit;
      if (limit && res.slice(0, limit) !== res) {
        return res.slice(0, limit) + '...';
      }
      return res;
    }
  }

  public getColumnName(column) {
    return column.name.replace(/\&shy;/gi, '');
  }

  public getCellValue(column, row, value) {
    if (column.displayValue !== undefined) {
      return !column.detailsColumn
          ? column.displayValue(value)
          : this.arrowHTML.get(row).value + ' ' + column.displayValue(value);
    } else {
      return !column.detailsColumn
          ? value
          : this.arrowHTML.get(row).value + ' ' + value;
    }
  }

  public getCellStyle(column, row, value, extendStyle = false) {
    let style: any = {
      'width': column.width + 'px',
      'margin-left': '-1.2rem'
    };

    if (extendStyle && this.cellStyle !== undefined) {
      style = Object.assign(style, this.cellStyle(column, row, value));
    }

    return style;

  }

  private setGroupExpansionDefault() {
    if (!this.groupsExpanded) {
      setTimeout(() => {
        if (this.groupedRows !== undefined && this.groupedRows.length) {
          this.groupedRows.forEach(group => {
            if (group.key
                && this.table.bodyComponent.rowExpansions.get(group) === 1
                && !this.isEmptyRowGroup(group)) {
              this.toggleExpandGroup(group);
            }
          })
        }
      })
    }
  }

  private isEmptyRowGroup(group): boolean {
    return group.value !== undefined
        && group.value.length === 1
        && group.value[0].emptyRow === true;
  }

  private rerenderRows() {
    // TODO: this is used to rerender the table rows. The workaround is needed due to
    // unsupported by default feature/bug in ngx-datatable library
    setTimeout(() => {
      if (this.scrollbarV) {
        this.resetBodyHeight();
      }
      this._rows = [...this._rows];
      this._allRows = [...this._allRows];
    }, 0);
    this.setGroupExpansionDefault();
  }

  ngAfterViewInit() {
    this.rerenderBody();
    this.loadSettings();
    this.editInstances.changes.subscribe(this.focusFirstElement);
  }

  private focusFirstElement(queryList) {
    if (queryList.length === 0)
      return;

    let input: ElementRef = queryList.first.nativeElement.querySelector('input');
    (<any>input).focus();
  }

  private rerenderBody() {
    // TODO: there is a lot of scramble with calculation of table dimensions
    if (this.scrollbarV) {
      setTimeout(() => {
        this.resetBodyHeight();
      }, 0);
    }
  }

  /**
   * This function makes sure to create table anew, which is useful in case of
   * vertically scrollable table, since the calculation of table body dimensions
   * is only possible on table initiation.
   */
  // TODO: rework or rename
  private recreateTable() {
    this._scrollbarV = true;
    /*
    if (this.rows.length <= this.limit) {
      this._scrollbarV = false;
    } else {
      this._scrollbarV = true;
    }
    /** */
    this.recreateHelper = [0];
    this.rerenderBody();
  }

  private resetBodyHeight() {
    if (this.table && typeof this.rowHeight === 'number') {
      this.table.bodyHeight = Math.min(this.limit, this.rows.length) * this.rowHeight;
    } else {
      console.error('Vertical scroll is not supported without fixed row height');
    }
  }

  private resetArrowHTML() {
    this.rows.forEach(row => {
      this.arrowHTML.set(row, {right: true, value: '<div class=\'fa fa-angle-right\'></div>'});
    })
  }

  private prependCreationRow() {
    if (this.inlineNew && !this._rows.some(row => (<any>row).emptyRow === true)) {
      this._rows.unshift(<any>{isActive: true, emptyRow: true});
    }
    if (this.inlineNew && !this._allRows.some(row => (<any>row).emptyRow === true)) {
      this._allRows.unshift(<any>{isActive: true, emptyRow: true});
    }
  }

  private compareRows(a: any, b: any, prop: any): number {
    const aprop = DataTableComponent.getRowProp(a, prop);
    const bprop = DataTableComponent.getRowProp(b, prop);
    if (aprop instanceof Date && bprop instanceof Date) {
      return aprop.getTime() - bprop.getTime();
    } else {
      if (typeof aprop === 'number' && typeof aprop === 'number') {
        return aprop - bprop;
      } else {
        return aprop.toString().localeCompare(bprop.toString());
      }
    }
  }

  private compare(a: any, b: any): number {
    if (a instanceof Date && b instanceof Date) {
      return a.getTime() - b.getTime();
    } else {
      if (typeof a === 'number' && typeof b === 'number') {
        return a - b;
      } else {
        return a.toString().localeCompare(b.toString());
      }
    }
  }

  public static getRowProp(row, prop) {
    let ref = row;
    if (typeof prop === 'string') {
      let mapping = prop.split('.');
      while (mapping.length > 0 && ref !== undefined && ref !== null) {
        ref = ref[mapping.shift()];
      }
    }
    return ref;
  }

  private loadSettings() {

    const data = this.storageService.retrieve('columns_' + this.uId);

    if (this.canHideColumns && data) {
      let toBeDisplayed: TableColumn[] = [];

      JSON.parse(data).forEach((c) => {
        let col = this.columns.find((cl) => {
          return cl.prop === c.prop;
        });

        if (col)
          toBeDisplayed.push(col);
      });

      this.displayColumns = toBeDisplayed;

    } else {
      this.displayColumns = this._columns.filter(c => !c.hidden);
    }
  }

  private storeSettings() {

    let store: any = [];

    if (this.uId) {
      this.displayColumns.forEach((col: TableColumn) => {
        if (col.name !== '' &&
            col.cellClass !== 'action-controls' &&
            col.cellClass !== 'details-control') {

          store.push({
            prop: col.prop
          });
        }

      });

      this.storageService.store('columns_' + this.uId, JSON.stringify(store));
    } else {
      this.logger.warn('MAF0x00A9: no unique identifier for datatable set')
    }

  }

  public isCheckedColumn(col) {
    return this.displayColumns.find(c => {
      return c.name === col.name;
    });
  }

  /* TODO: remove this?
  private createFormGroup(columns: TableColumn[], entry: any): FormGroup {

    let formGroup: FormGroup = new FormGroup({});

    let getValue = (object, pathArray) => {
      if (pathArray.length === 0 || object === undefined)
        return null;

      let prop = pathArray.shift();

      if (pathArray.length === 0) {
        return object[prop];
      }

      return getValue(object[prop], pathArray);
    };

    columns.filter(col => {
      if (col.cellClass && col.cellClass === 'action-controls')
        return false;
      return true;
    }).forEach(col => {

      let p: string = col.prop.toString();
      let nValue;

      let properties = p.split('.');
      let value = getValue(entry, properties);

      switch (col.type) {
        case 'date':
          let stringToDate: StringToDatePipe = new StringToDatePipe();
          let dateToString: DateToStringPipe = new DateToStringPipe();

          nValue = dateToString.transform(value);

          formGroup.addControl(col.prop.toString(), new DateControl(nValue, stringToDate, dateToString));
          break;
        case 'money':
          let _numberToMoney: NumberToMoneyPipe = new NumberToMoneyPipe(new DecimalPipe('de-DE'));
          let _moneyToNumber: MoneyToNumberPipe = new MoneyToNumberPipe();

          nValue = _numberToMoney.transform(value);
          nValue = value;

          formGroup.addControl(col.prop.toString(), new MoneyControl(nValue, _numberToMoney, _moneyToNumber));
          break;
        default:
          formGroup.addControl(col.prop.toString(), new FormControl(value));
          break;
      }


    });

    return formGroup;

  }
  /**/

  private mergeObject(obj1, obj2) {
    let keys = Object.keys(obj2);

    keys.forEach(key => {
      let index = key.indexOf('.');
      if (index === -1) {
        obj1[key] = obj2[key];
      } else {
        let prefix = key.substr(0, index);
        let postfix = key.substr(index + 1);

        if (!obj1.hasOwnProperty(prefix)) {
          obj1[prefix] = {};
        }

        let subObj = {};
        subObj[postfix] = obj2[key];

        if (obj1[prefix] === null)
          obj1[prefix] = {};

        this.mergeObject(obj1[prefix], subObj);

      }
    });

    return obj1;
  }

  private copyColumnDetails() {
    for (const column of this._columns.filter((c: TableColumn) => c.cellClass !== 'action-controls' && c.cellClass !== 'details-control')) {
      column.sortable = this.sortable;
      column.resizeable = this.resizeable;
      column.draggable = this.draggable;
      if (!column.cellTemplate) {
        column.cellTemplate = this.cellTemplate;
      }
      column.headerTemplate = this.headerTemplate;
    }
  }

  private appendTableControls(columns: TableColumn[]): TableColumn[] {
    const actionColumnIndex = columns.findIndex((c: TableColumn) => c.cellClass === 'action-controls') !== -1;
    const checkboxColumnIndex = columns.findIndex((c: TableColumn) => !!c.headerCheckboxable) !== -1;
    // const hasDetailsColumn = this._displayColumns.findIndex((c: TableColumn) => c.cellClass === 'details-control') !== -1;

    if (this.detailsTemplate) {
      this.addDetailsColumn();
    }


    if (!actionColumnIndex && this.hasActionColumn) {
      this.addActionColumn(columns);
    }

    if (!checkboxColumnIndex && this.selectionType === 'checkbox') {
      this.addCheckboxColumn(columns);
    }

    return columns;

  }

  private applySchemaFilterOnData(): any {
    const data = this.rows;
    const table: any[] = [];
    for (const entry of data) {
      const row = {};
      for (const column of this._columns) {
        if (column.name && column.prop) {
          if (column.displayValue !== undefined) {
            row[this.getColumnName(column)] = column.displayValue(DataTableComponent.getRowProp(entry, column.prop));
          } else {
            let vals = DataTableComponent.getRowProp(entry, column.prop);
              if (vals instanceof Array) {
                let res: string = '';
                  for (const val of vals) {
                    res += val + ',';
                  }
                row[this.getColumnName(column)] = res
             } else {
                row[this.getColumnName(column)] = vals
            }
          }
        }
      }
      table.push(row);
    }

    return table;

  }


  private addActionColumn(cols: TableColumn[]) {

    let amountItems: number = 0;

    if (this.isViewable)
      amountItems++;

    if (this.isEditable)
      amountItems++;

    if (this.isDeletable || this.isEditable)
      amountItems++;

    if (this.isDeActivateable)
      amountItems++;

    let width = amountItems * 36;

    cols.unshift({
      name: this.actionColumnTitle,
      cellTemplate: this.actionColumn,
      resizeable: false,
      sortable: false,
      draggable: false,
      width: width,
      canAutoResize: false,
      cellClass: 'action-controls',
    });
  }

  private addCheckboxColumn(cols: TableColumn[]) {
    cols.unshift({
      name: '',
      cellTemplate: this.checkboxColumn,
      width: 30,
      sortable: false,
      canAutoResize: false,
      draggable: false,
      resizeable: false,
      // TODO: when headerCheckboxable set to true behaves incorrectly (library issue)
      headerCheckboxable: false,
      checkboxable: true,
    });
  }

  private setRowsMetadata() {
    this.rows.forEach(row => {
      this.arrowHTML.set(row, {right: true, value: '<div class=\'fa fa-angle-right\'></div>'});
    })
  }

  private addDetailsColumn() {
    let detailsColumn = this.columns.filter(column => !!column.detailsColumn)[0];
    /*
    this._displayColumns.unshift({
      width: 50,
      maxWidth: 50,
      minWidth: 50,
      resizeable: false,
      sortable: false,
      draggable: false,
      canAutoResize: false,
      cellTemplate: this.detailColumn,
      cellClass: 'details-control'
    });
    /**/
  }

}

export type TableSelectionType = 'single' | 'multi' | 'multiClick' | 'checkbox' | 'cell' | 'falsey';

export type TableRowHeight<T> = ((row: T) => number) | number | 'auto';

export type TableMassages = { emptyMessage?: string, totalMessage?: string, selectedMessage?: string };

export type TableCssClasses = {
  sortAscending?: string,
  sortDescending?: string,
  pagerLeftArrow?: string,
  pagerRightArrow?: string,
  pagerPrevious?: string,
  pagerNext?: string
}

/**
 * Column property that indicates how to retrieve this column's
 * value from a row.
 * 'a.deep.value', 'normalprop', 0 (numeric)
 */
export type TableColumnProp = string | number;

/**
 * Column Type
 * @type {object}
 */
export interface TableColumn {

  /**
   * Determines if column is checkbox
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  checkboxable?: boolean;

  /**
   * Determines if the column is frozen to the left
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  frozenLeft?: boolean;

  /**
   * Determines if the column is frozen to the right
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  frozenRight?: boolean;

  /**
   * The grow factor relative to other columns. Same as the flex-grow
   * API from http =//www.w3.org/TR/css3-flexbox/. Basically;
   * take any available extra width and distribute it proportionally
   * according to all columns' flexGrow values.
   *
   * @type {number}
   * @memberOf TableColumn
   */
  flexGrow?: number;

  /**
   * Min width of the column
   *
   * @type {number}
   * @memberOf TableColumn
   */
  minWidth?: number;

  /**
   * Max width of the column
   *
   * @type {number}
   * @memberOf TableColumn
   */
  maxWidth?: number;

  /**
   * The default width of the column, in pixels
   *
   * @type {number}
   * @memberOf TableColumn
   */
  width?: number;

  /**
   * Can the column be resized
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  resizeable?: boolean;

  /**
   * Custom sort comparator
   *
   * @type {*}
   * @memberOf TableColumn
   */
  comparator?: any;

  /**
   * Custom pipe transforms
   *
   * @type {PipeTransform}
   * @memberOf TableColumn
   */
  pipe?: PipeTransform;

  displayValue?: (arg: any) => any;

  filterByModelValue?: boolean;

  /**
   * Can the column be sorted
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  sortable?: boolean;

  /**
   * Can the column be re-arranged by dragging
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  draggable?: boolean;

  /**
   * Whether the column can automatically resize to fill space in the table.
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  canAutoResize?: boolean;

  /**
   * Column name or label
   *
   * @type {string}
   * @memberOf TableColumn
   */
  name?: string;

  /**
   * Property to bind to the row. Example:
   *
   * `someField` or `some.field.nested`, 0 (numeric)
   *
   * If left blank, will use the name as camel case conversion
   *
   * @type {TableColumnProp}
   * @memberOf TableColumn
   */
  prop?: TableColumnProp;

  /**
   * Cell template ref
   *
   * @type {*}
   * @memberOf TableColumn
   */
  cellTemplate?: any;

  /**
   * Header template ref
   *
   * @type {*}
   * @memberOf TableColumn
   */
  headerTemplate?: any;

  /**
   * CSS Classes for the cell
   *
   *
   * @memberOf TableColumn
   */
  cellClass?: string | ((data: any) => string | any);

  /**
   * CSS classes for the header
   *
   *
   * @memberOf TableColumn
   */
  headerClass?: string | ((data: any) => string | any);

  /**
   * Header checkbox enabled
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  headerCheckboxable?: boolean;

  format?: string

  detailsColumn?: boolean;

  tooltip?: (arg: any) => any;

  summarize?: { function?: (arr: any[]) => any, title?: string, extended?: boolean }

  hidden?: boolean;

  excludeFromGrouping?: boolean;

  uneditable?: boolean;

  groupHeaderLimit?: number;

  editInView?: boolean;

  onClick?: any;

}
