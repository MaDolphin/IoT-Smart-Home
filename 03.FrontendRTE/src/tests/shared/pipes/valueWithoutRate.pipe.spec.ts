/* (c) https://github.com/MontiCore/monticore */

import { ValueWithoutRatePipe } from '@shared/pipes/valueWithoutRate.pipe';

describe('ValueWithoutRatePipe', () => {

  let pipe: ValueWithoutRatePipe = new ValueWithoutRatePipe();

  it('transforms "1100, 10" to "1000"', () => {
    expect(pipe.transform(1100, 10)).toBe(1000);
  });

  it('transforms "1105, 10.5" to "1.000"', () => {
    expect(pipe.transform(1105, 10.5)).toBe(1000);
  });

  it('transforms "1100.55, 10" to "1000.50"', () => {
    expect(pipe.transform(1100.55, 10)).toBe(1000.50);
  });

  it('transforms "1000, 0" to "1.000"', () => {
    expect(pipe.transform(1000, 0)).toBe(1000);
  });

  it('transforms "0, 100" to "0"', () => {
    expect(pipe.transform(0, 100)).toBe(0);
  });

  it('transforms "0, 0" to "0"', () => {
    expect(pipe.transform(0, 0)).toBe(0);
  });

});
