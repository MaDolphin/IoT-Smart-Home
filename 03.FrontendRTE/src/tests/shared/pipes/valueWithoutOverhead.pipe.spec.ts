/* (c) https://github.com/MontiCore/monticore */

import { ValueWithoutOverheadPipe } from '@shared/pipes/valueWithoutOverhead.pipe';

describe('ValueWithoutOverheadPipe', () => {

  let pipe: ValueWithoutOverheadPipe = new ValueWithoutOverheadPipe();

  it('transforms "1100, 10" to "900"', () => {
    expect(pipe.transform(1000, 10)).toBe(900);
  });

  it('transforms "2000, 50" to "1.000"', () => {
    expect(pipe.transform(2000, 50)).toBe(1000);
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
