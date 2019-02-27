/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Injectable } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { expect } from 'chai';
import { GenericFormControl, GenericFormGroup } from '../../../../../../app/shared/generic-form/generic-form';
import { Control, Group } from '../../../../../../app/shared/generic-form/generic-form/decorators';

describe('Generic Form', () => {

  describe('Decorators', () => {

    describe('Group', () => {

      describe('Init', () => {

        @Injectable()
        @Group()
        class TestFormGroup extends GenericFormGroup<any> {

          @Control()
          public prop: GenericFormControl<any> = undefined as any;

          @Control()
          public prop1: GenericFormControl<any> = undefined as any;

        }


        const model = {
          prop:  'prop',
          prop1: 'prop1',
        };

        const model2 = {
          prop:  'prop_2',
          prop1: 'prop1_2',
        };

        class Model {

          public get prop() {
            return 'prop';
          }

          public prop1 = 'prop1';

        }

        class Model2 {

          public get prop() {
            return 'prop_2';
          }

          public prop1 = 'prop1_2';

        }

        let form: TestFormGroup;

        beforeEach(() => {
          TestBed.configureTestingModule({ providers: [TestFormGroup] });
          form = TestBed.get(TestFormGroup);
        });

        describe('readonly model', () => {

          it('model object', () => {

            form.init(model, true);

            expect(form.model).to.be.deep.eq(model);
            expect(form.model).to.be.not.eq(model);


          });

          it('model instance', () => {

            const modelInstance = new Model();

            form.init(modelInstance, true);

            expect(form.model).to.be.deep.eq(modelInstance);
            expect(form.model).to.be.not.eq(modelInstance);

          });


        });

        describe('setValues', () => {

          it('model object', () => {

            form.init(model);

            form.setValues(model2);
            expect(form.model).to.be.deep.eq(model2);

          });

          it('model object', () => {

            const modelInstance = new Model();

            form.init(modelInstance, true);

            const model2Instance = new Model2();

            form.setValues(model2Instance);
            expect(form.model).to.be.deep.eq(model2Instance);

          });


        });


      });

    });

  });

});