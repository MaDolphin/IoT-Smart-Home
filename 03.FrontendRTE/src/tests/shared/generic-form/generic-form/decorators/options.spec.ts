/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Injectable } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { expect } from 'chai';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { spy } from 'sinon';
import { SelectFormControl } from '@shared/generic-form/controls/select.form-control';
import { GenericFormGroup } from '@shared/generic-form/generic-form';
import { Control, Group } from '@shared/generic-form/generic-form/decoretors';
import { OptionsBehaviorSubject } from '@shared/generic-form/generic-form/decoretors/options';
import { deleteAllControlMetadatas } from './utils.spec';

describe('Generic Form', () => {

  describe('Decorators', () => {

    xdescribe('OptionsBehaviorSubject', () => {

      @Injectable()
      @Group()
      class Form extends GenericFormGroup<any> {
        @Control()
        public select: SelectFormControl = undefined as any;

        public selectOptions: BehaviorSubject<string[]> = new BehaviorSubject<string[]>([]);
      }

      let form: Form;

      beforeEach(() => {
        TestBed.configureTestingModule({ providers: [Form] });
      });

      afterEach(() => deleteAllControlMetadatas(Form));

      it('external', () => {

        const bs = new BehaviorSubject<string[]>([]);

        const dec = OptionsBehaviorSubject(bs);

        dec(Form, 'select');

        form = TestBed.get(Form);

        form.init();

        const zuweisungSetOptions = spy(form.select, 'setOptions');

        expect(zuweisungSetOptions.called).false;
        expect(form.select.options).to.have.length(0);

        bs.next(['opt1', 'opt2', 'opt3']);

        expect(zuweisungSetOptions.calledOnce).true;
        expect(form.select.options).to.have.length(3);

        bs.next(['opt1', 'opt2', 'opt3', 'opt1', 'opt2', 'opt3']);

        expect(zuweisungSetOptions.calledTwice).true;
        expect(form.select.options).to.have.length(6);

        bs.next(['opt1', 'opt2', 'opt3']);

        expect(zuweisungSetOptions.calledThrice).true;
        expect(form.select.options).to.have.length(3);

      });

      it('internal', () => {

        const dec = OptionsBehaviorSubject('selectOptions');

        dec(Form, 'select');

        form = TestBed.get(Form);

        form.init();

        const zuweisungSetOptions = spy(form.select, 'setOptions');

        expect(zuweisungSetOptions.called).false;
        expect(form.select.options).to.have.length(0);

        form.selectOptions.next(['opt1', 'opt2', 'opt3']);

        expect(zuweisungSetOptions.calledOnce).true;
        expect(form.select.options).to.have.length(3);

        form.selectOptions.next(['opt1', 'opt2', 'opt3', 'opt1', 'opt2', 'opt3']);

        expect(zuweisungSetOptions.calledTwice).true;
        expect(form.select.options).to.have.length(6);

        form.selectOptions.next(['opt1', 'opt2', 'opt3']);

        expect(zuweisungSetOptions.calledThrice).true;
        expect(form.select.options).to.have.length(3);

      });

    });

  });

});