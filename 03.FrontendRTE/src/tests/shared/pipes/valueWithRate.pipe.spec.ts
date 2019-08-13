/* (c) https://github.com/MontiCore/monticore */

import { ValueWithRatePipe } from '@shared/pipes/valueWithRate.pipe';

describe('ValueWithRatePipe', () => {

  let pipe: ValueWithRatePipe = new ValueWithRatePipe();

  it('transforms "1000, 10" to "1.100"', () => {
    expect(pipe.transform(1000, 10)).toBe(1100);
  });

  it('transforms "1000, 10.5" to "1105"', () => {
    expect(pipe.transform(1000, 10.5)).toBe(1105);
  });

  it('transforms "1000.50, 10" to "1100.55"', () => {
    expect(Math.round(pipe.transform(1000.50, 10) * 100) / 100).toBe(1100.55);
  });

  it('transforms "1000.50, 10.5" to "1100.55"', () => {
    expect(Math.round(pipe.transform(1000.50, 10) * 100) / 100).toBe(1100.55);
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
