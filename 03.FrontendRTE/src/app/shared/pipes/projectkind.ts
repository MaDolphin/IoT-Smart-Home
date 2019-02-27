/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

export enum Selection {
  Yes, No, NotSpecified
}

export class Projectkind {

  private readonly _name: string;

  private readonly _salesControllable: Selection;

  private readonly _gainsTax: Selection;

  private readonly _deductionOption: Selection;

  constructor(name: string, salesControllable: Selection, gainsTax: Selection, deductionOption: Selection) {
    this._name = name;
    this._salesControllable = salesControllable;
    this._gainsTax = gainsTax;
    this._deductionOption = deductionOption;
  }

  public get name(): string {
    return this._name;
  }

  public get salesControllable(): Selection {
    return this._salesControllable;
  }

  public get gainsTax(): Selection {
    return this._gainsTax;
  }

  public get deductionOption(): Selection {
    return this._deductionOption;
  }

}


