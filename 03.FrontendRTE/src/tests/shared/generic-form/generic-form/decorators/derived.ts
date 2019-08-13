/* (c) https://github.com/MontiCore/monticore */

import { Control, GenericFormGroup, Group } from '@shared/generic-form/ngx-forms';
import { TextFormControl } from '@shared/generic-form/controls';
import { Derived } from '@shared/generic-form/generic-form/decoretors/derived';
import { Injectable } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { expect } from 'chai';

describe('Generic Form', () => {

  describe('Decorators', () => {

    describe('Derived', () => {

      @Injectable()
      @Group()
      class TestForm extends GenericFormGroup<any> {

        @Control()
        public username: TextFormControl = undefined as any;

        @Control()
        public password: TextFormControl = undefined as any;

        @Control()
        @Derived
        public hash: TextFormControl = undefined as any;

      }

      let form: TestForm;

      beforeEach(() => {
        TestBed.configureTestingModule({providers: [TestForm]});

        form = TestBed.get(TestForm);
      });

      it('init with model', () => {

        const model = {
          username: 'username',
          password: 'password',
          hash: 'hash',
        };

        form.init(Object.assign({}, model));

        expect(form.model).to.be.not.deep.eq(model);

        delete model.hash;

        expect(form.model).to.be.deep.eq(model);

      });

      it('setValues', () => {

        const model = {
          username: 'username',
          password: 'password',
          hash: 'hash',
        };

        form.init();

        form.setValues(Object.assign({}, model));

        expect(form.model).to.be.not.deep.eq(model);

        delete model.hash;

        expect(form.model).to.be.deep.eq(model);

      });

      it('set model values manuel', () => {

        const model = {
          username: 'username',
          password: 'password',
          hash: 'hash',
        };

        form.init();

        form.setValues(Object.assign({}, model));

        form.username.setModelValue(model.username);
        form.password.setModelValue(model.password);
        form.hash.setModelValue(model.hash);

        expect(form.model).to.be.not.deep.eq(model);

        delete model.hash;

        expect(form.model).to.be.deep.eq(model);


      });

      it('set values manuel', () => {

        const model = {
          username: 'username',
          password: 'password',
          hash: 'hash',
        };

        form.init();

        form.setValues(Object.assign({}, model));

        form.username.setValue(model.username);
        form.password.setValue(model.password);
        form.hash.setValue(model.hash);

        expect(form.model).to.be.not.deep.eq(model);

        delete model.hash;

        expect(form.model).to.be.deep.eq(model);

      });


    });

  });

});
