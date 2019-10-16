/* (c) https://github.com/MontiCore/monticore */

import { PatternFormControl } from '@shared/generic-form/controls';
import { expect } from 'chai';
import { TestBed } from '@angular/core/testing';

describe('Generic Form', () => {

  describe('Controls', () => {

    describe('Pattern', () => {

      beforeEach(() => {
        TestBed.configureTestingModule({providers: [PatternFormControl]});
      });

      it('pattern valid', () => {

        const control = TestBed.get(PatternFormControl);

        control.pattern = /\d{3}/;

        control.setValue('111');

        control.updateValueAndValidity();

        expect(control.valid).true;

      });

      it('pattern invalid', () => {

        const control = TestBed.get(PatternFormControl);

        control.pattern = /\d{3}/;

        control.setValue('aaa');

        control.updateValueAndValidity();

        expect(control.valid).false;

      });


    });

  });

});
