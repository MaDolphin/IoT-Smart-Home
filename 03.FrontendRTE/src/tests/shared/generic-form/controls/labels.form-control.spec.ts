/* (c) https://github.com/MontiCore/monticore */

import { expect } from 'chai';
import { LabelFormControl } from '@shared/generic-form/controls/label.form-control';
import { TestBed } from '@angular/core/testing';

describe('Generic Form', () => {

  describe('Controls', () => {

    describe('Labels', () => {

      beforeEach(() => {
        TestBed.configureTestingModule({providers: [LabelFormControl]})
      });

      it('setModelValue', () => {

        const control = TestBed.get(LabelFormControl);

        const labels = ['label1', 'label2', 'label3'];

        control.setModelValue(labels);

        expect(control.value).eq(labels);

      });

      it('getModelValue', () => {

        const control = TestBed.get(LabelFormControl);

        const labels = ['label1', 'label2', 'label3'];

        control.setValue(labels);

        expect(control.getModelValue()).deep.eq('label1,label2,label3'.split(','));

      });

    });

  });

});
