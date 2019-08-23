/* (c) https://github.com/MontiCore/monticore */

import { CurrencyPipe } from '@shared/pipes/currency.pipe';
import { NumberToMoneyPipe } from '@shared/pipes/number-to-money.pipe';
import { DecimalPipe } from '@angular/common';
import { MoneyToNumberPipe } from '@shared/pipes/money-to-number.pipe';

describe('TGermanNumberPipe', () => {
  // This pipe is a pure, stateless function so no need for BeforeEach

  let pipeToMoney = new NumberToMoneyPipe(new DecimalPipe('de-DE'));
  let pipeCurrency = new CurrencyPipe(pipeToMoney);
  let pipeToNumber = new MoneyToNumberPipe();

  it('transforms "1000" to "1.000,00 €"', () => {
    expect(pipeCurrency.transform(100000)).toBe('1.000,00 €');
  });

  it('transforms "123450" to "1.234,50 €', () => {
    let input: number = 123450;
    let res = mtc(input);

    expect(res).toBe('1.234,50 €');
  });

  it('transforms "123," to "123,00 €', () => {
    let input = '123,';
    let res = mtc(input);

    expect(res).toBe('123,00 €');
  });

  it('transforms "1.234,5" to "1.234,50 €', () => {
    let input = '1.234,5';
    let res = mtc(input);

    expect(res).toBe('1.234,50 €');
  });

  xit('transforms "1000.23" to "1.000,23 €"', () => {
    // expect(pipe.transform(1000.23)).toBe('1.000,23 €');
  });

  xit('transforms "100" to "100,00 €"', () => {
    // expect(pipe.transform(100)).toBe('100,00 €');
  });

  xit('transforms "100.23" to "100,23 €"', () => {
    // expect(pipe.transform(100.23)).toBe('100,23 €');
  });

  // money to number to money to currency
  function mtc(input) {
    let resToNumber = pipeToNumber.transform(input);
    let resToCurrency = pipeCurrency.transform(resToNumber);

    return resToCurrency;
  }

});
