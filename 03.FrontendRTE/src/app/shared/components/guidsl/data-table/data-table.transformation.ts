/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import * as moment from 'moment';
import * as Big from 'big.js';
import { DecimalPipe } from '@angular/common';

export function asColorBox(): (value: any) => string {
  return function (value: any): string {
    switch (value) {
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

export function asMoney(): (value: any) => string {
  return function (value: number | null): string {
    let dp = new DecimalPipe('de-DE');
    value = value ? value : 0;
    value = Number(Big(value).div(100).toString());
    return value !== null && !isNaN(value)
      ? `${dp.transform(value, '1.2-2')}` + ' €'
      : '';
  }
}

export function asNegMoney(): (value: any) => string {
  return function (value: number | null): string {
    return asMoney()(value * -1);
  }
}

export function asOverhead(rate: number): (value: any) => string {
  return function (value: number): string {
    return asMoney()(value * 10000 / (10000 - rate));
  }
}

export function asNoOverhead(rate: number): (value: any) => string {
  return function (value: number): string {
    return asMoney()(value * (10000 - rate) / 10000);
  }
}

export function asRate(rate: number): (value: any) => string {
  return function (value: number): string {
    return asMoney()(value / 10000 * (10000 + rate));
  }
}

export function asNoRate(rate: number): (value: any) => string {
  return function (value: number): string {
    return asMoney()(value * 10000 / (10000 + rate));
  }
}

const EMPTY_VALUE: number = -999999999;

export function asPercent(): (v: any) => string {
  return function (value: number): string {
    return value === EMPTY_VALUE ? '' : value / 100 + ' %';
  }
}

export function asHour(): (v: any) => string {
  return function (value: number): string {
    return value === EMPTY_VALUE ? '' : value / 100 + ' h';
  }
}

export function asString(): (v: any) => string {
  return function (value: number | null): string {
    return value !== null ? value + '' : '0';
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
