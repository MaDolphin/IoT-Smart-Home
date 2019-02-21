import { DecimalPipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { DateToStringPipe } from '../pipes/date-to-string.pipe';
import { NumberToMoneyPipe } from '../pipes/number-to-money.pipe';
import { DataTableComponent as DT } from '@shared/components/guidsl/data-table/data-table.component';
import { ViewModel } from "@shared/architecture/data/viewmodel";

let datePipe = new DateToStringPipe();
let moneyPipe = new NumberToMoneyPipe(new DecimalPipe('de-DE'));

@Injectable()
export class CopyToClipboardService {

  public copyModelItemToClipboard(item) {
    if (item && item.model) {

      let selectedItem = this.transformValues(item.prop, item.model);
      this.copyToClipboard(selectedItem);
    }
  }

  public copyModelToClipboard(model: ViewModel<any, any, any> | null,
    orderedSelection: string[] = []) {
    if (model) {
      const json = model.serialize(false);
      const data: any[] = [];
      let keys = Object.keys(json);

      // order keys by selection and choose only present ones
      if (orderedSelection.length) {
        keys = orderedSelection.filter(a => keys.some(b => a === b));
      }
      for (const key of keys) {
        data.push(this.transformValues(key, json));
      }

      const val = data.join(';');

      this.copyToClipboard(val);
    }
  }

  public copyToClipboard(data: string) {
    // Create an auxiliary hidden input
    let aux = document.createElement('input');
    // Get the text from the element passed into the input
    aux.setAttribute('value', data);
    // Append the aux input to the body
    document.body.appendChild(aux);
    // Highlight the content
    aux.select();
    // Execute the copy command
    document.execCommand('copy');
    // Remove the input from the body
    document.body.removeChild(aux);
  }

  private transformValues(prop, model) {
    switch (prop) {
      case 'date':
      case 'voucherDate':
      case 'bookingDate':
        return datePipe.transform(model[prop]);

      case 'amountCent':
        return moneyPipe.transform(model[prop]);

      default:
        return model[prop];
    }

  }
}

export function getDisplayedCell(event) {
  let model = event.item.model;
  let prop = event.item.prop;
  let column = event.item.columns.find(col => col.prop === prop);
  return getDisplayedValue(model, prop, column);
}

export function getDisplayedValue(model, prop, column): string {
  let propName: string[] = prop.split('.');
  let res = propName.reduce((o, i) => o[i], model);
  if (column.displayValue !== undefined) {
    res = column.displayValue(res);
  }
  return res;
}

export function getDisplayedRow(event) {
  // filter undefined
  if (!event.item) {
    console.warn('No model is received!');
    return;
  }
  let props = event.item.columns.filter(x => x.prop !== undefined);
  // convert to string numerical properties
  const selection = props.map(x => {
    if (x.prop) return x.prop.toString();
    return '';
  });

  let model = event.item.model;
  let toCopy: string[] = [];

  for (let properties of selection) {
    let prop = properties;
    let column = event.item.columns.find(col => col.prop === prop);
    if (DT.getRowProp(model, prop) !== undefined && DT.getRowProp(model, prop) !== null) {
      toCopy.push(getDisplayedValue(model, prop, column));
    } else {
      toCopy.push('');
    }
  }
  return toCopy.join(';');
}