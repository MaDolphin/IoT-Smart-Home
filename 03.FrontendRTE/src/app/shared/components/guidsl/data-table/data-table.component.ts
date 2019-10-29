/* (c) https://github.com/MontiCore/monticore */

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
import {Router} from '@angular/router';
import {DatatableComponent} from '@swimlane/ngx-datatable';

import {Logger} from '@upe/logger';
import 'rxjs/add/observable/fromEvent';
import 'rxjs/add/observable/merge';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/startWith';

import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {NUMBER_MASK} from '@shared/architecture/forms/controls/money.control';
import {ISerializable} from '@shared/architecture/serializable';
import {RouterLocalService} from '@shared/architecture/services/router.local.service';
import {StorageService} from '@shared/architecture/services/storage.service';
import {NotificationService} from '@shared/notification/notification.service';
import {DialogCallbackTwo} from '@shared/utils/dialog/dialog.callback';

import {JsonSchema} from './data-table-schema';
import {XLSXService} from './XLSX.service';
import {CommandRestService} from "@shared/architecture/command/rte/command.rest.service";
import {CommandManager} from "@shared/architecture/command/rte/command.manager";
import {OkDTO} from "@shared/architecture/command/aggregate/ok.dto";
import {IDTO} from "@shared/architecture";
import {RIGHT_ALIGNED} from './data-table.trasformation';
import {IViewModel} from '@shared/architecture/data/viewmodel';

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
    this._columns.forEach((col: TableColumn, i: number) => {
      col.id = i;
    });

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
  @Input() public headerHeight = 'auto';

  public set displayColumns(value: TableColumn[]) {


    let newColumns = this.appendTableControls(this.removeTableControls(value));

    /* TODO: purpose?
    if (this.equalColumns(newColumns, this.displayColumns)) {
      return;
    }
    /** */

    this._displayColumns = newColumns;
  }

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
      totalMessage: this.rows.length !== 1 || this.emptyTable ? 'Einträge' : 'Eintrag',
      selectedMessage: 'ausgewählt',
    }, this._messages);
  }

  /**
   * Unique identifier for the datatable.
   * @type {String}
   */
  @Input('id')
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
  @ViewChild('checkboxHeaderColumn') checkboxHeaderColumn: TemplateRef<any>;
  @ViewChild('creationRow') creationRow: TemplateRef<any>;
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
  private _limit: number = 10;
  @Input()
  public set limit(value: number) {
    this._limit = value;
  }

  public get limit(): number {
    if (!this._limit) {
      return 10;
    } else {
      return this._limit;
    }
  }

  private rowsLimitDefault: number = 10;

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
  public scrollPage: number = 1;

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
  @Input() public _selected: T[] = [];

  @Input() public set selected(value: T[]) {
    this._selected = value;
  }

  public get selected(): T[] {
    return this._selected;
  }

  /**
   * Type of row selection. Options are single, multi, multiClick and chkbox. For no selection pass a falsey.
   * Default value: 'falsey'
   * @type {string}
   */
  @Input() public selectionType: TableSelectionType = undefined;
  private selectionTypeOriginal: TableSelectionType = undefined;

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
  public rowClassFunction: {function: (row: T) => {[key: string]: boolean}};

  private _rowClass: (row: T) => {[key: string]: boolean} = _ => {return {};};

  @Input() public set rowClass(func: (row: T) => {[key: string]: boolean}) {
    this._rowClass = func;
  }

  public get rowClass(): (row: T) => {[key: string]: boolean} {
    if (this.emptyTable) {
      return _ => {return {};};
    }
    return this._rowClass;
  }

  @Input() public actionColumnTitle = '';

  @Input() public exportFileName: string = 'export';

  @Input() public actionTemplate: TemplateRef<any>;

  @Input() public placeholderFilterInput = 'Suche';
  @Input() public editTemplates: {[key: string]: TemplateRef<any>} = {};

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
  public groupExcludeKeys = ["Summe (inkl. Pauschale)", "Pauschale", "Summe (ohne. Pauschale)", "Summe "];

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

    if (this.rows.length && this.rows[0] !== undefined && this.inlineNew) {
      this.emptyTable = false;
    } else if ((!this.rows.length || this.rows[0] === undefined) && this.inlineNew) {
      this.emptyTable = true;
    }

    if (this.scrollbarV) {
      this.recreateTable();
    }
    this.regroupRows();
  }

  private showInactiveDefault: boolean = false;

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

  /**
   * Stapelverarbeitung
   */

  /**
   * Display the BatchMode Switcher (Slider)
   */

  @Input()
  public allowBatchMode = false;

  /**
   * State of the Table regarding BatchMode
   * Can be activated, if table should be in BatchMode, but shouldnt be turned off
   * i.e. go back to single edit (Example Abgleich)
   */

  @Input()
  public batchMode: boolean = false;

  public get isInBatchMode(): boolean {
    return this.batchMode;
  }

  @Input()
  public editableBatchMode: boolean = false;

  @Output('create') createEvent: EventEmitter<T> = new EventEmitter();

  @Output('edit') editEvent: EventEmitter<T> = new EventEmitter();
  @Output('editNavigate') editNavigateEvent: EventEmitter<T> = new EventEmitter();

  @Output('save') saveEvent: EventEmitter<ResponseSaving<T>> = new EventEmitter();
  @Output('delete') deleteEvent: EventEmitter<T> = new EventEmitter();

  @Output('view') viewEvent: EventEmitter<T> = new EventEmitter();

  @Output('cancelDelete') cancelDeleteEvent: EventEmitter<T> = new EventEmitter();
  @Output('cancelEdit') cancelSaveEvent: EventEmitter<T> = new EventEmitter();
  @Output('cancelSave') cancel: EventEmitter<T> = new EventEmitter();

  @Output('clone') cloneEvent: EventEmitter<D> = new EventEmitter();

  @Output('deActivate') deActivateEvent: EventEmitter<T> = new EventEmitter();

  @Output('sort') sortEvent: EventEmitter<T> = new EventEmitter();

  @Output('select') selectEvent: EventEmitter<T> = new EventEmitter();
  @Output('showSelected') showSelectedEvent: EventEmitter<T> = new EventEmitter();
  @Output('deselect') deselectEvent: EventEmitter<T> = new EventEmitter();

  @Output('changeBatchMode') changeBatchModeEvent: EventEmitter<boolean> = new EventEmitter();

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
    this._unfilteredRows = this._rows.slice(0);
    this._unfilteredAllRows = this._rows.slice(0);
    this.setRowsMetadata();

    if (this.isGroupable) {
      this.setGroupedRows(this.currentSort);
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

    this.updateFilter();
    this.triggerSort();

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

  public pageNum: number = 0;
  public emptyTable: boolean = false;
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

  private groupRowsByDefault: string = '';

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

  private _internalRows: T[] = [];
  private _internalAllRows: T[] = [];

  private get _rows(): T[] {
    return this._internalRows;
  }
  private set _rows(rows: T[]) {
    if (!rows.length && this.inlineNew) {
      this.emptyTable = true;
      rows.length = 1;
    } else if (rows.length && rows[0] !== undefined && this.inlineNew) {
      this.emptyTable = false;
    }
    this._internalRows = rows;
  }

  private get _allRows(): T[] {
    return this._internalAllRows;
  }
  private set _allRows(rows: T[]) {
    if (!rows.length && this.inlineNew) {
      this.emptyTable = true;
      rows.length = 1;
    } else if (rows.length && rows[0] !== undefined && this.inlineNew && this.showInactive) {
      this.emptyTable = false;
    }
    this._internalAllRows = rows;
  }

  // variable to help with recreateTable function
  public recreateHelper: any[] = [0];

  @Input()
  public rightClickMenu: TemplateRef<any>;
  private _isGroupable: boolean = false;
  private _showInactive: boolean = false;
  private _unfilteredRows: T[] = [];
  private _unfilteredAllRows: T[] = [];
  protected _messages: TableMassages = {};
  private _cssClasses: TableCssClasses = {};
  private _columns: TableColumn[] = [];
  private _displayColumns: TableColumn[] = [];
  private currentSort: {prop: string, dir: string};
  private mainSort: {prop: string, dir: string};

  protected commandManager: CommandManager;

  constructor(
    private notificationService: NotificationService,
    private _xlsx: XLSXService,
    private storageService: StorageService,
    private router: Router,
    private _routerLocalService: RouterLocalService,
    protected _commandRestService: CommandRestService) {
    this.commandManager = new CommandManager(this._commandRestService);
  }


  // Override table resize host listener
  @HostListener('window:resize')
  onWindowResize(): void {
    this.rerenderBody();
  }

  @HostListener('keydown.enter', ['$event'])
  public handleKeyboardEvent(event: KeyboardEvent) {
    if (!this.formControlHasFocus())
      return;

    event.stopPropagation();

    (<HTMLElement>document.activeElement).blur();

    let row;

    if (this.currentEditId === null) {
      if (this.inlineNew) {
        this.onCreate({});
      } else {
        // Hit enter on something which isnt part of the formcontrol for the datatable
        console.error('Enter', document.activeElement);
      }
    } else {
      row = this.rows.find((e) => {
        return e.id === this.currentEditId;
      });
      if (this.selected.length > 0) {
        this.onEditSave(row);
      }
    }


  }

  @HostListener('keydown.escape', ['$event'])
  public handleKeyboardEventEscape(event: KeyboardEvent) {
    if (!this.formControlHasFocus())
      return;

    let row;

    if (this.currentEditId === null) {
      if ((<any>this.rows[0]).emptyRow) {
        row = this.rows[0];
      } else {
        // Hit enter on something which isnt part of the formcontrol for the datatable
        console.error('ESC', document.activeElement);
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
    let controlName;
    if (el.nodeName === 'MAT-SELECT')
      controlName = el.getAttribute('id');
    else
      controlName = el.getAttribute('name');

    if (controlName === 'wert') {
      if (el.classList.contains('datatable-control'))
        return true;
    } else if (this.formEditControl) {
      let controls: any = this.formEditControl.controls;
      if (controls.hasOwnProperty(controlName))
        return true;
    }

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

    this.groupRowsByDefault = this.groupRowsBy;
    this.showInactiveDefault = this.showInactive;
    this.rowsLimitDefault = this.limit;
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

  public triggerSort() {
    if (this.sorts.length === 0)
      return;

    let sortBy = this.sorts[0].prop;
    let sortDir = this.sorts[0].dir;

    let col = this.displayColumns.find(value => {
      return value.prop === sortBy;
    });

    if (col !== undefined) {
      let event = {
        column: col,
        newValue: sortDir,
        sorts: this.sorts
      };

      this.onSort(event);
    }
  }

  public onSort(event) {
    if (this.isGroupable) {
      this.externalSorting = true;
      const sort = event.sorts[0];
      if (this.groupedCol !== undefined && sort.prop === this.groupedCol.prop) {
        this.mainSort = sort;
      }
      this.currentSort = sort;
      this.setGroupedRows(sort)
      this.rerenderRows();
      return;
    }
    if (event.column.sortFn === undefined) {
      this.externalSorting = true;

      const sort = event.sorts[0];
      this._rows.sort((a, b) => {
        if (!!(<any>a).emptyRow) {
          return -1;
        }
        if (!!(<any>b).emptyRow) {
          return 1;
        }
        return this.compareRows(a, b, sort.prop, sort.dir === 'desc');
      });
      this._allRows.sort((a, b) => {
        if (!!(<any>a).emptyRow) {
          return -1;
        }
        if (!!(<any>b).emptyRow) {
          return 1;
        }
        return this.compareRows(a, b, sort.prop, sort.dir === 'desc');
      });

      this.currentSort = undefined;
    } else {
      this.externalSorting = true;
      const sort = event.sorts[0];
      this.currentSort = sort;
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
    }
    if (col === this.displayColumns[0]) {
      style['margin-left'] = -this.scrollX + 'px'
    }
    return style;
  }

  public onScroll(event) {
    if (event.offsetX !== undefined && event.offsetY !== undefined) {
      this.scrollX = event.offsetX;
      // assuming rowHeight is a number (must be if table is scrollable)
      this.scrollPage = Math.ceil((event.offsetY - +this.rowHeight) / (this.limit * +this.rowHeight)) + 1;
      requestAnimationFrame(this.table.bodyComponent.scroller.updateOffset.bind(this.table.bodyComponent.scroller))
    }
  }

  public onKeyPressPageSize(event) {
    if (event.key === 'Enter') {
      this.onPageSizeChange(event.target.value);
      (<HTMLElement>document.activeElement).blur(); // loose focus
    }
  }

  public onPageSizeChange(pageSize) {
    if (+pageSize > 0) {
      this.limit = +pageSize;
    } else {
      this.limit = 10;
    }

    this.table.offset = 0;
    this.table.limit = this.limit;
    this.table.pageSize = this.limit;
    this.table.recalculatePages();
    if (this.scrollbarV) {
      this.recreateTable();
    }
    if (this.isGroupable) {
      this.setGroupedRows();
    }
  }

  public onPaginated(event) {
    this.pageNum = event.offset;
    this.table.limit = this.limit;
    this.table.pageSize = this.limit;
    this.table.recalculatePages();
  }

  public footerPageChange(event) {
    if (!event || !event.page) {
      return;
    }
    let offsetY;
    if (this.inlineNew) {
      // TODO: imprecise offset calculation
      offsetY = (event.page - 1) * +this.rowHeight * (this.limit + 1) - 3;
    } else {
      offsetY = (event.page - 1) * +this.rowHeight * this.limit;
    }
    const scroller = this.table.bodyComponent.scroller;
    // assuming rowHeight is a number (must be if table is scrollable)
    scroller.setOffset(offsetY);
    scroller.scrollYPos = offsetY;
    // setTimeout to delay rerender, since we directly changed child component
    // of table and not an input property of table (as in normal case)
    setTimeout(() => {
      // rerender body after scroll
      requestAnimationFrame(scroller.updateOffset.bind(scroller))
    })
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

  public getColumnByProp(prop): TableColumn {
    return this._columns.find(c => c.prop === prop);
  }

  public getColumnById(id: number): TableColumn | undefined {
    return this._columns.find(c => c.id === id);
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
    console.error("datatable", "onCreate");

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
    console.error("successfull oncreate");
  }

  public onCreateFailed() {
    console.error("failed oncreate");

  }

  public onCreateCancel(): void {
    this.currentEditId = null;
    this.formEditControl.resetAndEmpty();
    this.cancel.emit();
  }


  /**
   * Edit Events
   */
  public onEdit(event, entry: T): void {

    this.onCreateCancel();
    Reflect.defineMetadata(EDIT_CANCEL, this.onEditCancel.bind(this), entry);
    this.editEvent.emit(entry);

    if (this.inlineEdit) {
      this.currentEditId = entry.id;
      this.toggleEditMode(true);
    } else {
      if (this.editEventLink !== undefined) {
        this.router.navigateByUrl(this.editEventLink + '/' + entry.id);
      } else {
        this.editNavigateEvent.emit(entry);
      }
    }
    if (!!event) {
      event.preventDefault();
      event.stopPropagation();
    }
  }

  public onEditSave(entry: T): void {
    const success = new Subject<boolean>();

    this.formEditControl.markAsCDirty();
    this.formEditControl.markAsCTouched();

    if (this.formEditControl.valid) {
      let res = this.formEditControl.getModelValue();

      res.id = entry.id;

      //this.loadingIndicator = true;
      this.saveEvent.emit({entry: res, success: success});
      this.toggleEditMode(false);

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

  public toggleEditMode(enterEditMode: boolean) {
    if (!enterEditMode) {
      this.currentEditId = null;
      for (const column of this._columns.filter((c: TableColumn) => c.cellClass !== 'action-controls' && c.cellClass !== 'details-control')) {
        if (!column.summaryTemplate && !column.uneditable) {
          column.summaryTemplate = this.editTemplates[column.prop];
        }
      }
      this.displayColumns = [...this.displayColumns];
    } else {
      for (const column of this._columns.filter((c: TableColumn) => c.cellClass !== 'action-controls' && c.cellClass !== 'details-control')) {
        column.summaryTemplate = undefined;
      }
      this.displayColumns = [...this.displayColumns];
    }
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
    // this.selectionType = this.selectionTypeOriginal;
    this.cancelSaveEvent.emit(entry);

    if (this.formEditControl !== undefined) {
      this.formEditControl.resetAndEmpty();
    }

    this.cancel.emit(entry)
    this.toggleEditMode(false);
  }

  public onEditSuccess() {
    console.log("onEdit Success");
  }

  public onEditError() {
    console.log("onEdit Error");
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
          if (col.displayValue !== undefined && !col.filterByModelValue
            && !!col.displayValue(value) && (typeof col.displayValue(value) === 'string')) {
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

      this.setGroupedRows(this.currentSort);


      let groupKeys = [];
      rowExpansions.forEach((v: any, k: any) => {
        if (v === 1) {
          groupKeys.push(k.key)
        }
      })

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

    if (this.isInBatchMode) {
      this.selectRow(event, row);
      // event.stopPropagation();
    } else if (column.onClick !== undefined) {
      column.onClick(row);
    } else {
      event.stopPropagation();
    }
  }

  public selectRow(event, row) {
    let index = this.selected.findIndex((r) => r.id === row.id);
    if (index >= 0)
      this.selected.splice(index, 1);
    else
      this.selected.push(row);

    // TODO: Emit event on selection
    let selected = this.selected;
    this.onSelect({selected: selected});
    event.stopPropagation();
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
  previousSelection: number[] = [];

  public onSelect({selected}) {
    /*
        let editedRow = this.rows.find(row => row.id === this.currentEditId);
        if (!!editedRow) {
          let editedRowIndex = selected.indexOf(editedRow);
          if (this.previousSelection.includes(this.currentEditId) && editedRowIndex === -1) {
            selected.push(editedRow);
          }
          if (!this.previousSelection.includes(this.currentEditId) && editedRowIndex > -1) {
            selected.splice(editedRowIndex, 1);
          }
        }
    
        let emptyRow = selected.find(row => !!row.emptyRow);
        this.skipRowSelection(emptyRow, selected)
    
        // PS: Why dont we allow to select deactivated rows?
        // I needed to comment it out, cause of batch processing.
        /*
            selected.forEach(row => {
              if (!this.activeRow(row)) {
                this.skipRowSelection(row, selected)
              }
            })
    
         */
    /*
        this.previousSelection = selected.filter(row => !!row).map(row => row.id);
    
        this.selected.splice(0, this.selected.length);
        this.selected.push(...selected);
    
    
     */
    this.selectEvent.emit(selected);
  }

  private skipRowSelection(row, selection): void {
    if (row !== undefined) {
      let index = selection.indexOf(row);
      selection.splice(index, 1);
      let skip: boolean = true;
      selection.forEach(selectedRow => {
        if (!this.selected.includes(selectedRow)) {
          skip = false;
        }
      })
      if (skip || !selection.length) {
        if (!this.firedSelect) {
          this.table.onBodySelect({selected: selection});
        }
        return;
      }
    }
  }



  public displayCheck(row, column?, value?) {
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
    }
    this._rows = sort(this._rows);
    this._allRows = sort(this._allRows);
    this.table.sorts = [];
    if (this.isGroupable) {
      this.setGroupedRows(this.currentSort);
    }
    this.resetArrowHTML();
    if (this.scrollbarV) {
      this.recreateTable();
    }
    this.showSelectedEvent.emit();
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

  public summarizeGroup(group, column) {
    let groupFragments = this.groupedRows.filter(gr => gr.key === group.key);
    let groupValue = [].concat.apply([], groupFragments.map(gr => gr.value));
    let summarization;
    if (!column.summarize.extended) {
      let arr = groupValue.map(row => {
        return DataTableComponent.getRowProp(row, column.prop)
      });
      summarization = column.summarize.function(arr);
    } else {
      summarization = column.summarize.function(groupValue);
    }
    if (column.displayValue) {
      summarization = column.displayValue(summarization);
    }
    if (!!column.summarize.title) {
      summarization = column.summarize.title + ': ' + summarization;
    }
    return summarization;
  }

  public setGroupedRows(sort?: {dir: string, prop: string}) {
    if (sort === undefined) {
      this.mainSort = undefined;
    }
    this.groupedRows = this.groupRows(sort);
  }

  public getGroupedRows() {
    if (!this.groupedRows || !this.groupedRows.length) {
      return [];
    }
    return this.groupedRows.filter(group => group.page === this.pageNum);
  }

  public groupRows(sort?: {dir: string, prop: string}) {
    // create a map to hold groups with their corresponding results
    if (this.groupedCol === undefined) {
      if (sort !== undefined) {
        const desc = sort.dir === 'desc';
        this.rows.sort((a, b) => {
          return this.compareRows(a, b, sort.prop, desc);
        });
      }
      let ungrouped = [];
      for (let i = 0, j = 0; i < this.rows.length; i += this.limit, ++j) {
        ungrouped.push({key: 0, value: this.rows.slice(i, i + this.limit), page: j});
      }
      return ungrouped;
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
      const desc = sort.dir === 'desc';
      if (sort.prop === this.groupedCol.prop) {
        sortedMap = new Map(
          Array.from(map).sort((a, b) => {
            return this.compareGroups(a, b, desc);
          })
        );
      } else {
        if (this.groupedRows !== undefined && this.mainSort !== undefined) {
          const prevDesc = this.mainSort.dir === 'desc';
          const groups = this.groupedRows.map(group => group.key);
          sortedMap = new Map(
            Array.from(map).sort((a: [string, any[]], b: [string, any[]]): number => {
              if (a[1].length && a[1][0].emptyRow) {
                return -1;
              }
              if (b[1].length && b[1][0].emptyRow) {
                return 1;
              }
              const indexA = groups.indexOf(a[0]);
              const indexB = groups.indexOf(b[0]);
              if (indexA === -1 || indexB === -1) {
                return this.compareGroups(a, b, prevDesc);
              }
              return this.compare(a[0], b[0], prevDesc);
            })
          );
        } else {
          sortedMap = map;
        }
        sortedMap = new Map(
          Array.from(sortedMap).map((entry): [string, any] => {
            return [entry[0], entry[1].sort((a, b) => {
              return this.compareRows(a, b, sort.prop, desc);
            })];
          })
        );
      }
    } else {
      if (this.groupedRows) {
        const groups = this.groupedRows.map(group => group.key);
        sortedMap = new Map(
          Array.from(map).sort((a: [string, any[]], b: [string, any[]]): number => {
            if (a[1].length && a[1][0].emptyRow) {
              return -1;
            }
            if (b[1].length && b[1][0].emptyRow) {
              return 1;
            }
            const indexA = groups.indexOf(a[0]);
            const indexB = groups.indexOf(b[0]);
            if (indexA < indexB) {
              return -1;
            } else {
              return 1;
            }
          })
        );
      } else {
        sortedMap = map;
      }
    }

    const addGroup = (key: any, value: any) => {
      return {
        key,
        value,
        page: 0,
        firstOnPage: 1,
        lastOnPage: value.length,
        total: value.length
      };
    };

    // convert map back to a simple array of objects
    const sortedArray: {
      key: any,
      value: any,
      page: number,
      firstOnPage?: number,
      lastOnPage?: number,
      total?: number
    }[] = Array.from(sortedMap, x => addGroup(x[0], x[1]));

    // handle row limit per page
    let rowsOnPage = 0;
    let pageNum = 0;
    for (let i = 0; i < sortedArray.length; ++i) {
      const group = sortedArray[i];
      group.page = pageNum;
      rowsOnPage += group.value.length;
      let overflow = rowsOnPage - this.limit;
      if (overflow > 0) {
        const splitGroup1 = {
          key: group.key,
          value: group.value.slice(0, -overflow),
          page: pageNum,
          firstOnPage: group.firstOnPage,
          lastOnPage: group.lastOnPage - overflow,
          total: group.total
        };
        ++pageNum;
        const splitGroup2 = {
          key: group.key,
          value: group.value.slice(-overflow),
          page: pageNum,
          firstOnPage: splitGroup1.lastOnPage + 1,
          lastOnPage: group.lastOnPage,
          total: group.total
        };
        sortedArray[i] = splitGroup1;
        sortedArray.splice(i + 1, 0, splitGroup2);
        rowsOnPage = 0;
      }
    }

    return sortedArray;
  }

  private isUndefined(obj) {
    return obj === undefined || obj === 'undefined';
  }

  public isExcludedFromGroup(key) {
    return this.groupExcludeKeys.includes(key);
  }

  public isFragmentedGroup(curPage): boolean {
    let curPageGroups = this.groupedRows.filter(gr => gr.page === curPage);
    let nextPageGroups = this.groupedRows.filter(gr => gr.page === curPage + 1);
    return curPageGroups.some(group => nextPageGroups.some(gr => gr.key === group.key));
  }

  public transformGroupValue(group: any) {
    let row = group.value[0];
    let prefix = this.getColumnName(this.groupedCol);
    if (row === undefined) {
      return prefix;
    }
    let res = DataTableComponent.getRowProp(row, this.groupedCol.prop);
    // TODO: special case pipes
    if (this.groupedCol.displayValue !== undefined) {
      res = this.groupedCol.displayValue(res);
      // TODO: the same side effect is used to hide advanced table features
      if (this.isFilterable && this.isExportable) {
        res += `<br>(Einträge ${group.firstOnPage}&#8209;${group.lastOnPage}/${group.total})`;
      }
      return res;
    } else {
      if (res === null || res === undefined) {
        return prefix;
      }
      let limit = this.groupedCol.groupHeaderLimit;
      if (limit && res.slice(0, limit) !== res) {
        return res.slice(0, limit) + '...';
      }
      if (this.isFilterable && this.isExportable) {
        res += `<br>(Einträge ${group.firstOnPage}&#8209;${group.lastOnPage}/${group.total})`;
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

  public isRightAligned(column: TableColumn): boolean {
    return column.displayValue
      && Reflect.hasMetadata(RIGHT_ALIGNED, column.displayValue)
      && Reflect.getMetadata(RIGHT_ALIGNED, column.displayValue);
  }

  private isEmptyRowGroup(group): boolean {
    return group.value !== undefined
      && group.value.length === 1
      && group.value[0].emptyRow === true;
  }

  private rerenderRows() {

    let detailExpansions = [];
    // setup for preserving expanded rows on reload
    if (this.groupedRows !== undefined && this.groupedRows.length) {
      if (this.table && this.table.bodyComponent) {
        this.table.bodyComponent.rowExpansions.forEach((value, group) => {
          if (value === 1) {
            detailExpansions.push(group.key);
          }
        })
      }
    }

    // TODO: this is used to rerender the table rows. The workaround is needed due to
    // unsupported by default feature/bug in ngx-datatable library
    setTimeout(() => {
      if (this.scrollbarV) {
        this.resetBodyHeight();
      }
      this._rows = [...this._rows];
      this._allRows = [...this._allRows];
    }, 0);

    // setup for preserving expanded rows on reload
    if (!this.groupsExpanded) {
      setTimeout(() => {
        if (!!this.groupedRows) {
          this.groupedRows.forEach(group => {
            if (group.key
              && !detailExpansions.includes(group.key)
              && this.table.bodyComponent.rowExpansions.get(group) === 1
              && !this.isEmptyRowGroup(group)) {
              this.toggleExpandGroup(group);
            }
          })
        }
      })
    }
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
      const rowCount = this.inlineNew
        ? Math.min(this.limit + 1, this.rows.length + 1)
        : Math.min(this.limit, this.rows.length);
      this.table.bodyHeight = rowCount * this.rowHeight;
    } else {
      console.error('Vertical scroll is not supported without fixed row height');
    }
  }

  private resetArrowHTML() {
    this.rows.forEach(row => {
      this.arrowHTML.set(row, {right: true, value: '<div class=\'fa fa-angle-right\'></div>'});
    })
  }

  private compareRows(a: any, b: any, prop: any, desc?: boolean): number {
    if (a.emptyRow) {
      return -1;
    }
    if (b.emptyRow) {
      return 1;
    }
    const aprop = DataTableComponent.getRowProp(a, prop);
    const bprop = DataTableComponent.getRowProp(b, prop);
    if (this.isUndefined(aprop) || aprop === null) {
      return -1 * (desc ? -1 : 1);
    }
    if (this.isUndefined(bprop) || bprop === null) {
      return 1 * (desc ? -1 : 1);
    }
    let res: number = 0;
    if (aprop instanceof Date && bprop instanceof Date) {
      res = aprop.getTime() - bprop.getTime();
    } else {
      if (typeof aprop === 'number' && typeof aprop === 'number') {
        res = aprop - bprop;
      } else {
        res = aprop.toString().localeCompare(bprop.toString());
      }
    }
    return res * (desc ? -1 : 1);
  }

  private compareGroups(a: any, b: any, desc: boolean): number {
    if (a[1][0].emptyRow) {
      return -1;
    }
    if (b[1][0].emptyRow) {
      return 1;
    }
    return this.compare(a[0], b[0], desc);
  }

  private compare(a: any, b: any, desc: boolean): number {
    if (a === undefined) {
      return -1;
    }
    if (b === undefined) {
      return 1;
    }
    let res: number = 0;
    if (a instanceof Date && b instanceof Date) {
      res = a.getTime() - b.getTime();
    } else {
      if (typeof a === 'number' && typeof b === 'number') {
        res = a - b;
      } else {
        res = a.toString().localeCompare(b.toString());
      }
    }
    return res * (desc ? -1 : 1);
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


  public isCheckedColumn(col) {
    return this.displayColumns.find(c => {
      return c.id === col.id;
    });
  }


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
      if (!column.summaryTemplate) {
        if (!column.uneditable) {
          column.summaryTemplate = this.editTemplates[column.prop];
        }
        column.summaryFunc = () => {};
      }
      column.headerTemplate = this.headerTemplate;
    }
  }

  private removeTableControls(columns: TableColumn[]): TableColumn[] {
    return columns.filter((value) => {
      if (value.cellClass === 'action-controls')
        return false;

      if (value.cellClass === 'selection-checkbox')
        return false;

      return true;
    });
  }

  private appendTableControls(columns: TableColumn[]): TableColumn[] {
    const actionColumnIndex = columns.findIndex((c: TableColumn) => c.cellClass === 'action-controls') !== -1;
    const checkboxColumnIndex = columns.findIndex((c: TableColumn) => c.cellClass === 'selection-checkbox') !== -1;
    // const hasDetailsColumn = this._displayColumns.findIndex((c: TableColumn) => c.cellClass === 'details-control') !== -1;

    if (this.detailsTemplate) {
      this.addDetailsColumn();
    }


    if (!actionColumnIndex && this.hasActionColumn) {
      if (!this.isInBatchMode)
        this.addActionColumn(columns);
      else if (this.showActionColumnInBatchMode)
        this.addActionColumn(columns);
    }

    if (!checkboxColumnIndex && this.isInBatchMode) {
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
      summaryTemplate: this.creationRow,
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
      headerTemplate: this.checkboxHeaderColumn,
      width: 44,
      sortable: false,
      canAutoResize: false,
      draggable: false,
      resizeable: false,
      // TODO: when headerCheckboxable set to true behaves incorrectly (library issue)
      headerCheckboxable: false,
      checkboxable: false,
      cellClass: 'selection-checkbox',
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


  /**
   * Stapelverarbeitung
   */

  @Input() showActionColumnInBatchMode = false;

  public onChangeBatchMode(mode) {
    if (mode.checked === this.batchMode)
      return;

    if (mode.checked === true) {
      this.startBatchMode();
    } else {
      this.stopBatchMode();
    }
  }


  public startBatchMode() {

    this.batchMode = true;

    this.selectionTypeOriginal = this.selectionType;
    this.selectionType = 'checkbox';

    this.changeBatchModeEvent.emit(true);

    this.displayColumns = [...this.displayColumns];
  }

  private stopBatchMode() {

    this.batchMode = false;

    this.selectionType = this.selectionTypeOriginal;

    this.changeBatchModeEvent.emit(false);

    let batchColumnIndex = this.displayColumns.findIndex(value => {
      return value.cellClass === 'selection-checkbox';
    });

    if (batchColumnIndex >= 0) {
      this.displayColumns.splice(batchColumnIndex, 1);
    }

    this.onDeselectAll();

    this.displayColumns = [...this.displayColumns];


  }

  public selectionCBChecked(row) {
    return this.selected.indexOf(row) > -1;
  }

  public selectionHCBChecked() {
    return this.selected.length > 0 && this.selected.length === this.rows.reduce((acc, cur) => {
      if (this.displayCheck(cur))
        acc += 1;
      return acc;
    }, 0);
  }

  public selectionHCBIndeterminate() {
    return this.selected.length > 0 && this.selected.length < this.rows.reduce((acc, cur) => {
      if (this.displayCheck(cur))
        acc += 1;
      return acc;
    }, 0);
  }

  public selectionCBChanged(event, row) {
    if (event.checked === true) {
      this.selected.push(row);
    } else {
      let index = this.selected.findIndex(value => {
        return value.id === row.id;
      });

      if (index >= 0) {
        this.selected.splice(index, 1);
      }
    }
    let selected = this.selected;
    this.onSelect({selected: selected})
  }

  public selectionHCBChanged(event) {
    if (event.checked === true) {
      this.onSelectAll();
    } else {
      this.onDeselectAll();
    }
    let selected = this.selected;
    this.onSelect({selected: selected})
  }

  public onSelectAll() {

    let rows = this._rows;

    if (this.showInactive) {
      rows = this._allRows;
    }

    // PS: Used for performance reason
    // If we need to keep the order of selection, one would need to iterate over selected array
    // and only add, if its not already in the list
    this.onDeselectAll();

    rows.forEach(value => {
      if (this.displayCheck(value))
        this.selected.push(value);
    });


    let selected = this.selected;
    this.onSelect({selected: selected})


  }

  public onDeselectAll() {
    this.selected.length = 0;
    this.deselectEvent.emit();
  }



}

export type TableSelectionType = 'single' | 'multi' | 'multiClick' | 'checkbox' | 'cell' | 'falsey';

export type BatchModeTypes = 'single' | 'batch';

export type TableRowHeight<T> = ((row: T) => number) | number | 'auto';

export type TableMassages = {emptyMessage?: string, totalMessage?: string, selectedMessage?: string};

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

  id?: number;

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

  summarize?: {function?: (arr: any[]) => any, title?: string, extended?: boolean}

  hidden?: boolean;

  excludeFromGrouping?: boolean;

  uneditable?: boolean;

  groupHeaderLimit?: number;

  editInView?: boolean;

  onClick?: any;

  summaryTemplate?: TemplateRef<any>;

  summaryFunc?: (arg: any) => any;
}
