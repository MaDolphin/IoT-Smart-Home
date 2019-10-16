/* (c) https://github.com/MontiCore/monticore */

import { expect } from 'chai';
import { Assertion } from '@shared/generic-form/validator';
import { mockControl } from './until.spec';

describe('Generic Form', () => {

  describe('Validators', () => {

    describe('Min', () => {

      it('with string less then min', () => {

        const min = 10;
        let str   = '';

        for (let i = 0; i < min; i++) {
          str += '-';
          try {
            Assertion.create(mockControl(str)).min(min);
            expect.fail('String is greater then min', 'String is less then min');
          } catch (e) {
          }

        }

      });

      it('with string greater then min', () => {

        const min = 10;
        let str   = '';

        for (let i = 0; i < min; i++) {
          str += '-';
        }

        for (let i = 0; i < min; i++) {
          str += '-';
          try {
            Assertion.create(mockControl(str)).min(min);
          } catch (e) {
            expect.fail('String is less then min', 'String is greater then min', e.msg);
          }
        }

      });

      it('with number less then min', () => {

        const min = 10;

        for (let i = 0; i < min; i++) {
          try {
            Assertion.create(mockControl(i)).min(min);
            expect.fail('Number is greater then min', 'Number is less then min');
          } catch (e) {
          }

        }

      });

      it('with number greater then min', () => {

        const min = 10;

        for (let i = min; i < min * 2; i++) {
          try {
            Assertion.create(mockControl(i)).min(min);
          } catch (e) {
            console.log({ i, msg: e });
            expect.fail('Number is less then min', 'Number is greater then min', e.msg);
          }
        }

      });

    });

  });

});
