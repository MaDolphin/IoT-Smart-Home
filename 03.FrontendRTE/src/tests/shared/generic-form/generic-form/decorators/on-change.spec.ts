/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Injectable } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { expect } from 'chai';
import { spy } from 'sinon';
import { TextFormControl } from '@shared/generic-form/controls';
import { GenericFormGroup } from '@shared/generic-form/generic-form';
import { Control, Group, OnChange, } from '@shared/generic-form/generic-form/decoretors';

@Injectable()
@Group()
class Form extends GenericFormGroup<any> {

  @Control()
  public username: TextFormControl = undefined as any;

  @Control()
  public password: TextFormControl = undefined as any;


}

describe('Generic Form', () => {

  describe('Decorators', () => {

    beforeEach(() => {
      TestBed.configureTestingModule({ providers: [Form] })
    });

    it('@OnChange username 0', () => {
      const onChangeDecorator = OnChange('username');
      const onChange          = spy();
      onChangeDecorator(Form.prototype, '', { value: onChange });

      let form = TestBed.get(Form);

      form.init();

      expect(onChange.called).to.be.false;

      form.password.setValue('');

      expect(onChange.called, 'on change function should not be called on password control change').to.be.false;

      form.username.setValue('username');

      expect(onChange.calledOnce).to.be.true;


    });


  });

});