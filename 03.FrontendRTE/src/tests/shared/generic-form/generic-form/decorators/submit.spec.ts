/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Injectable } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { expect } from 'chai';
import { spy } from 'sinon';
import { TextFormControl } from '@shared/generic-form/controls';
import { GenericFormGroup } from '@shared/generic-form/generic-form';
import { AfterSubmit, BeforeSubmit, Control, Group, Submit, } from '@shared/generic-form/generic-form/decorators';

@Injectable()
@Group()
class Form extends GenericFormGroup<any> {

  @Control()
  public text: TextFormControl = undefined as any;


}

describe('Generic Form', () => {

  describe('Decorator', () => {

    beforeEach(() => {
      TestBed.configureTestingModule({ providers: [Form] })
    });

    it('@Submit', () => {
      const submit = spy();
      Submit(Form.prototype, '', { value: submit });
      Submit(Form.prototype, '', { value: submit });
      Submit(Form.prototype, '', { value: submit });
      TestBed.get(Form).init().submit();
      expect(submit.calledThrice).to.be.true;
    });

    it('@BeforeSubmit', () => {
      const submit = spy();
      BeforeSubmit(Form.prototype, '', { value: submit });
      BeforeSubmit(Form.prototype, '', { value: submit });
      BeforeSubmit(Form.prototype, '', { value: submit });
      TestBed.get(Form).init().submit();
      expect(submit.calledThrice).to.be.true;
    });

    it('@AfterSubmit', () => {
      const submit = spy();
      AfterSubmit(Form.prototype, '', { value: submit });
      AfterSubmit(Form.prototype, '', { value: submit });
      AfterSubmit(Form.prototype, '', { value: submit });
      TestBed.get(Form).init().submit();
      expect(submit.calledThrice).to.be.true;
    });


  });

});
