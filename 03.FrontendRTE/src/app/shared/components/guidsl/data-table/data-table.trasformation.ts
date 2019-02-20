import * as moment from 'moment';
import * as Big from 'big.js';
import { DecimalPipe } from '@angular/common';
import { ZahlenWertDTO } from '@navigationstab-dto/zahlenwert.dto';

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

export function asZahlenwertType(): (value: any) => string {
  return function (value: ZahlenWertDTO | null): string {
    if (value == null) {
      return ''
    } else if (value.zahlenTyp === 'STUNDE') {
      return asHour()(value.wert);
    } else if (value.zahlenTyp === 'PROZENT') {
      return asPercent()(value.wert);
    } else if (value.zahlenTyp === 'EURO') {
      return asMoney()(value.wert);
    } else {
      return value.wert + '';
    }
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
