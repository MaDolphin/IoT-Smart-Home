/* (c) https://github.com/MontiCore/monticore */

import * as moment from 'moment';
import * as Big from 'big.js';
import {DecimalPipe} from '@angular/common';

export const RIGHT_ALIGNED = 'RIGHT_ALIGNED';

export function asColorBox(): (value: any) => string {
  return function (value: any): string {
    switch (value) {
      case 'SAP': {
        return '<div style="background: #1667ae; width: 20px; height: 20px; border-radius: 5px; display: inline-block"></div>';
      }
      case 'Fehlerhaft': {
        return '<div style="background: #ae163e; width: 20px; height: 20px; border-radius: 5px; display: inline-block"></div>';
      }
      case 'Planung': {
        return '<div style="background: #ae4e16; width: 20px; height: 20px; border-radius: 5px; display: inline-block"></div>';
      }
      case 'Eingereicht': {
        return '<div style="background: #3eae16; width: 20px; height: 20px; border-radius: 5px; display: inline-block"></div>';
      }
      // This part should be placed in another function
      case 'NONE': {
        return '<i style="font-size: 18px; color: #3eae16" class="fa fa-check-circle"></i>';
      }
      case 'SOME': {
        return '<i style="font-size: 18px; color: #f6a800" class="fa fa-adjust"></i>';
      }
      case 'ALL': {
        return '<i style="font-size: 18px; color: #cc071e" class="fa fa-exclamation-circle"></i>';
      }
      case 'KOM_STAT_BEGR_FEHLT': {
        return '<i style="border: 2px solid; border-color: #006165; color: white; border-radius: 20px; background-color: #006165; "><b>&nbsp;OE&nbsp;</b></i>- BF';
      }
      case 'KOM_STAT_ANTW_FEHLT': {
        return '<i style="border: 2px solid; border-color: #612158; color: white; border-radius: 20px; background-color: #612158; "><b>&nbsp;F1&nbsp;</b></i>- AF';
      }
      case 'KOM_STAT_BEGR_CHANGED': {
        return '<i style="border: 2px solid; border-color: #612158; color: white; border-radius: 20px; background-color: #612158; "><b>&nbsp;F1&nbsp;</b></i>- BC';
      }
      case 'KOM_STAT_ANTW_FEHLT_CHANGED': {
        return '<i style="border: 2px solid; border-color: #612158; color: white; border-radius: 20px; background-color: #612158; "><b>&nbsp;F1&nbsp;</b></i>' +
          '<i style="border: 2px solid; border-color: #006165; color: white; border-radius: 20px; background-color: #006165; "><b>&nbsp;OE&nbsp;</b></i>- AF+BC';
      }
      case 'KOM_STAT_ANTW_BEGR_CHANGED': {
        return '<i style="border: 2px solid; border-color: #612158; color: white; border-radius: 20px; background-color: #612158; "><b>&nbsp;F1&nbsp;</b></i>' +
          '<i style="border: 2px solid; border-color: #006165; color: white; border-radius: 20px; background-color: #006165; "><b>&nbsp;OE&nbsp;</b></i>- AC+BC';
      }
      case 'KOM_STAT_NO_ACTION_NEEDED': {
        return '';

      }
      default: {
        return value;
      }
    }
  }
}

export function asDate(): (value: any) => string {
  return function (date: Date | null): string {
    if ((date + '') !== 'Invalid Date') {
      return date ? moment(date).format('DD.MM.YYYY') : '';
    }
    return '';
  }
}

export function asTime(): (value: Date) => string {
  return function (date: Date | null): string {
    if ((date + '') !== 'Invalid Date') {
      return date ? moment(date).format('HH:mm') : '';
    }
    return '';
  }
}

function getRightAlignedFunction(func: (value: any) => string, obj?: any): (value: any) => string {
  let returnFunction = func.bind(obj);
  Reflect.defineMetadata(RIGHT_ALIGNED, true, returnFunction);
  return returnFunction;
}

export function asMoney(): (value: any) => string {
  return getRightAlignedFunction(getAsMoney);
}

function getAsMoney(value: number | null): string {
  let dp = new DecimalPipe('de-DE');
  value = value ? value : 0;
  value = Number(Big(value).div(100).toString());
  return value !== null && !isNaN(value)
    ? `${dp.transform(value, '1.2-2')}` + ' €'
    : '';
}

export function asNegMoney(): (value: any) => string {
  return getRightAlignedFunction(getAsNegMoney);
}

function getAsNegMoney(value: number | null): string {
  return getAsMoney(value * -1);
}

export function asOverhead(rate: number): (value: any) => string {
  return getRightAlignedFunction(getAsOverhead, {rate: rate});
}

function getAsOverhead(value): string {
  return getAsMoney(value * 10000 / (10000 - this.rate));
}

export function asNoOverhead(rate: number): (value: any) => string {
  return getRightAlignedFunction(getAsNoOverhead, {rate: rate});
}

function getAsNoOverhead(value): string {
  return getAsMoney(value * (10000 - this.rate) / 10000);
}

export function asRate(rate: number): (value: any) => string {
  return getRightAlignedFunction(getAsRate, {rate: rate});
}

function getAsRate(value: number): string {
  return getAsMoney(value / 10000 * (10000 + this.rate));
}

export function asNoRate(rate: number): (value: any) => string {
  return getRightAlignedFunction(getAsNoRate, {rate: rate});
}

function getAsNoRate(value: number): string {
  return getAsMoney(value * 10000 / (10000 + this.rate));
}

const EMPTY_VALUE: number = -999999999;

export function asPercent(): (v: any) => string {
  return getRightAlignedFunction(getAsPercent);
}

function getAsPercent(value: number): string {
  if (value === -1) {
    return '0 %';
  }
  return value === EMPTY_VALUE ? '' : value / 100 + ' %';
}

export function asHour(): (v: any) => string {
  return getRightAlignedFunction(getAsHour);
}

function getAsHour(value: number): string {
  if (value === -1) {
    return '0 %';
  }
  return value === EMPTY_VALUE ? '' : value / 100 + ' h';
}

export function asString(): (v: any) => string {
  return function (value: number | null): string {
    return value !== null ? value + '' : "0";
  }
}

export function asCheckbox(): (c: boolean) => string {
  return function (checked: boolean): string {
    if (checked === true) {
      return `<input type="checkbox" disabled checked>`;
    }
    if (checked === false) {
      return `<input type="checkbox" disabled>`;
    }
    return '';
  }
}

export function asColoredBar(): (value: string) => string {
  return function (value: string | null): string {
    let color = '#00000000';

    if (value === 'GEDECKT') {
      color = '#e7f1dc';
    } else if (value === 'FEHLT') {
      color = '#d85c41';
    }
    return '<div class="coloredBar" style="background-color:' + color + '">Test<br>1<br>2</div>'
  }
}

export function filterEmpty(): (v: any) => string {
  return function (value: any): string {
    if (value === -1) {
      return '';
    }
    return value;
  }
}