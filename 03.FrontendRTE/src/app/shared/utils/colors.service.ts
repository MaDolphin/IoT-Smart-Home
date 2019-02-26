import { Injectable } from '@angular/core';


@Injectable()
export class ColorsService {

  private _colors = [
      // MontiGem Colors
      '#00549f',
      '#555555',
      '#57ab27',
      '#f6a800',
      '#006165',

      '#A11035',
      '#0098A1',
      '#612158',
      '#FFED00',
      '#7A6FAC',

      '#d85c41',
      '#8dc060',
      '#B65256',
      // Self Picked Colors
      '#66e0ff',
      '#ff6600',

      '#1a6600',
      '#3366cc',
      '#ff66ff',
      '#b3b300',
      '#339966'
  ];


  public getColors(): string[] {
    return this._colors;
  }

  public getErrorColor() {

  }

}