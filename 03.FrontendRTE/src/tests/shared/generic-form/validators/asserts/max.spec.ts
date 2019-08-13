/* (c) https://github.com/MontiCore/monticore */

import { expect } from 'chai';
import { Assertion } from '@shared/generic-form/validator';
import { mockControl } from './until.spec';

describe('Generic Form', () => {

  describe('Validators', () => {

    describe('Max', () => {

      it('with string less then max', () => {

        const max = 10;
        let str   = '';

        for (let i = 0; i < max; i++) {
          str += '-';
          try {
            Assertion.create(mockControl(str)).max(max);
          } catch (e) {
            expect.fail('String is greater then max', 'String is less then max');
          }

        }

      });

      it('with string greater then max', () => {

        const max = 10;
        let str   = '';

        for (let i = 0; i < max; i++) {
          str += '-';
        }

        for (let i = 0; i < max; i++) {
          str += '-';
          try {
            Assertion.create(mockControl(str)).max(max);
            expect.fail('String is less then max', 'String is greater then max');
          } catch (e) {
          }
        }

      });

      it('with number less then max', () => {

        const max = 10;

        for (let i = 0; i < max; i++) {
          try {
            Assertion.create(mockControl(i)).max(max);
          } catch (e) {
            expect.fail('Number is greater then max', 'Number is less then max');
          }

        }

      });

      it('with number greater then max', () => {

        const max = 10;

        for (let i = max; i < max * 2; i++) {
          try {
            Assertion.create(mockControl(i)).max(max);
            expect.fail('Number is less then max', 'Number is greater then max');
          } catch (e) {
          }
        }

      });

    });

  });

});
