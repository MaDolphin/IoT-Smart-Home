/* (c) https://github.com/MontiCore/monticore */

import { FormControl } from '@angular/forms';
import { after } from './asserts/after';
import { all } from './asserts/all';
import { any } from './asserts/any';
import { assertDelta } from './asserts/assertDelta';
import { assertFalse } from './asserts/assertFalse';
import { assertLength } from './asserts/assertLength';
import { assertNull } from './asserts/assertNull';
import { assertTrue } from './asserts/assertTrue';
import { assertUndefined } from './asserts/assertUndefined';
import { before } from './asserts/before';
import { checked } from './asserts/checked';
import { closeTo } from './asserts/closeTo';
import { assertDate } from './asserts/date';
import { email } from './asserts/email';
import { assertEmpty } from './asserts/empty';
import { assertEqual } from './asserts/equal';
import { assertMax } from './asserts/max';
import { assertMin } from './asserts/min';
import { assertMoney } from './asserts/money';
import { NaN } from './asserts/nan';
import { not } from './asserts/not';
import { number } from './asserts/number';
import { own } from './asserts/own';
import { pattern } from './asserts/pattern';
import { satisfy } from './asserts/satisfy';
import { AssertionChain, AssertionMethod, AssertionProperty } from './decoretors';
import { flag } from './utils/flag';
import { proxify } from './utils/proxify';
import { test } from './utils/test';
import { ValidationError } from './validation.error';

export class Assertion {

  public static create(obj?: any, msg?: string, ssfi?: Function, lockSsfi?: boolean): Assertion {
    return proxify(new Assertion(obj, msg, ssfi, lockSsfi));
  }

  /*!
   * ### ._obj
   *
   * Quick reference to stored `actual` value for plugin developers.
   *
   * @api private
   */
  public get _obj() {
    return flag(this, 'object');
  }

  public set _obj(value) {
    flag(this, 'object', value);
  }

  public __flags;

  // region chain properties

  @AssertionChain
  public is: Assertion;

  @AssertionChain
  public a: Assertion;

  @AssertionChain
  public an: Assertion;

  @AssertionChain
  public be: Assertion;

  @AssertionChain
  public been: Assertion;

  @AssertionChain
  public to: Assertion;

  @AssertionChain
  public and: Assertion;

  @AssertionChain
  public has: Assertion;

  @AssertionChain
  public have: Assertion;

  @AssertionChain
  public with: Assertion;

  @AssertionChain
  public that: Assertion;

  @AssertionChain
  public which: Assertion;

  @AssertionChain
  public at: Assertion;

  @AssertionChain
  public of: Assertion;

  @AssertionChain
  public same: Assertion;

  @AssertionChain
  public but: Assertion;

  @AssertionChain
  public does: Assertion;

  @AssertionProperty(email)
  public email: Assertion;

  @AssertionProperty(checked)
  public checked: Assertion;

  @AssertionProperty(not)
  public not: Assertion;

  @AssertionProperty(own)
  public own: Assertion;

  @AssertionProperty(any)
  public any: Assertion;

  @AssertionProperty(all)
  public all: Assertion;

  @AssertionProperty(assertTrue)
  public true: Assertion;

  @AssertionProperty(assertFalse)
  public false: Assertion;

  @AssertionProperty(assertNull)
  public null: Assertion;

  @AssertionProperty(assertUndefined)
  public undefined: Assertion;

  @AssertionProperty(NaN)
  public NaN: Assertion;

  @AssertionProperty(assertEmpty)
  public empty: Assertion;

  @AssertionProperty(assertDate)
  public date: Assertion;

  @AssertionProperty(number)
  public number: Assertion;

  @AssertionProperty(assertMoney)
  public money: Assertion;

  /*!
   * Assertion Constructor
   *
   * Creates object for chaining.
   *
   * `Assertion` objects contain metadata in the form of flags. Three flags can
   * be assigned during instantiation by passing arguments to this constructor:
   *
   * - `object`: This flag contains the target of the assertion. For example, in
   *   the assertion `expect(numKittens).to.equal(7);`, the `object` flag will
   *   contain `numKittens` so that the `equal` assertion can reference it when
   *   needed.
   *
   * - `message`: This flag contains an optional custom error message to be
   *   prepended to the error message that's generated by the assertion when it
   *   fails.
   *this.assertion
   * - `ssfi`: This flag stands for "start stack function indicator". It
   *   contains a function reference that serves as the starting point for
   *   removing frames from the stack trace of the error that's created by the
   *   assertion when it fails. The goal is to provide a cleaner stack trace to
   *   end users by removing Chai's internal functions. Note that it only works
   *   in environments that support `Error.captureStackTrace`, and only when
   *   `Chai.config.includeStack` hasn't been set to `false`.
   *
   * - `lockSsfi`: This flag controls whether or not the given `ssfi` flag
   *   should retain its current value, even as assertions are chained off of
   *   this object. This is usually set to `true` when creating a new assertion
   *   from within another assertion. It's also temporarily set to `true` before
   *   an overwritten assertion gets called by the overwriting assertion.
   *
   * @param obj target of the assertion
   * @param msg (optional) custom error message
   * @param ssfi (optional) starting point for removing stack frames
   * @param lockSsfi (optional) whether or not the ssfi flag is locked
   * @api private
   */
  private constructor(obj?: any, msg?: string, ssfi?: Function, lockSsfi?: boolean) {
    flag(this, 'ssfi', ssfi || Assertion);
    flag(this, 'lockSsfi', lockSsfi);
    flag(this, 'object', obj);
    flag(this, 'message', msg);
  }

  @AssertionMethod(before)
  public before(dateVal: string | Date | FormControl): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(after)
  public after(dateVal: string | Date | FormControl): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(assertMax)
  public max(max: number, forceText: boolean = false): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(assertMin)
  public min(min: number, forceText: boolean = false): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(assertEqual)
  public equal(val, msg?: string): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(assertEqual)
  public equals(val, msg?: string): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(assertEqual)
  public eq(val, msg?: string): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(assertLength)
  public length(n, msg?: string): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(assertLength)
  public lengthOf(n, msg?: string): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(satisfy)
  public satisfy(matcher, msg?: string): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(satisfy)
  public satisfies(matcher, msg?: string): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(closeTo)
  public closeTo(expected, delta, msg?: string): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(closeTo)
  public approximately(expected, delta, msg?: string): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(assertDelta)
  public by(delta, msg?: string): Assertion {
    throw new Error('Method not implemented.');
  }

  @AssertionMethod(pattern)
  public pattern(regexp: string | RegExp) {
    throw new Error('Method not implemented.');
  }

  // endregion

  /**
   * ### .assert(expression, message, negateMessage, expected, actual, showDiff)
   *
   * Executes an expression and check expectations. Throws AssertionError for reporting if test doesn't pass.
   *
   * @name assert
   * @param expression to be tested
   * @param message or function that returns message to display if expression fails
   * @api private
   */
  public assert(expr, msg) {
    const ok = test(this, arguments);

    if (!flag(this, 'negate')) {
      msg.replace(' not ', ' ');
    }

    if (!ok) {
      throw new ValidationError(flag(this, 'message') || msg);
    }
  }

}
