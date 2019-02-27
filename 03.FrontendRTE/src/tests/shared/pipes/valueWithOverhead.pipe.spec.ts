/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { ValueWithOverheadPipe } from '@shared/pipes/valueWithOverhead.pipe';

describe('ValueWithOverheadPipe', () => {

  let pipe: ValueWithOverheadPipe = new ValueWithOverheadPipe();

  it('transforms "1000, 50" to "2000"', () => {
    expect(pipe.transform(1000, 50)).toBe(2000);
  });

  it('transforms "900, 10" to "1000"', () => {
    expect(pipe.transform(900, 10)).toBe(1000);
  });

  it('transforms "1000, 0" to "1.000"', () => {
    expect(pipe.transform(1000, 0)).toBe(1000);
  });

  it('transforms "0, 0" to "0"', () => {
    expect(pipe.transform(0, 0)).toBe(0);
  });

});
