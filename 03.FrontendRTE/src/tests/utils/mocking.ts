import { Component, Directive } from '@angular/core';
import { TypedJSON } from '@upe/typedjson';
import { Token } from '@shared/auth/token';

/**
 *
 * Creates a mocked Component for any Component
 *
 * Examples:
 * MockComponent({ selector: 'node-list-table' });
 * MockComponent({ selector: 'node-rollup', inputs: ['someprop', 'otherprop'] });
 *
 * See https:// angular.io/docs/ts/latest/api/core/index/Component-decorator.html for a list
 * of supported properties.
 *
 */
export function MockComponent(options: Component): any {

  let metadata: Component = {
    selector: options.selector,
    template: options.template || '<ng-content></ng-content>',
    inputs:   options.inputs,
    outputs:  options.outputs,
  };

  return Component(metadata)(class Clazz {
  });
}

/**
 *
 * Creates a mocked Directive for any Directive
 *
 * Examples:
 * MockDirective({ selector: 'node-list-table' });
 * MockDirective({ selector: 'node-rollup', inputs: ['someprop', 'otherprop'] });
 *
 * See https:// angular.io/docs/ts/latest/api/core/index/Component-decorator.html for a list
 * of supported properties.
 *
 */
export function MockDirective(options: Directive): any {
  let metadata: Directive = {
    selector: options.selector,
    inputs:   options.inputs,
    outputs:  options.outputs,
  };

  return Directive(metadata)(class Clazz {
  });
}

export const A = f => done => f().then(done).catch(done.fail);

export function MockToken(): void {
  const token = TypedJSON.parse(JSON.stringify({
                                                 jwt:            'token',
                                                 refreshToken:   'token',
                                                 expirationDate: '2050-01-01',
                                               }), Token);
  localStorage.setItem('jwt', 'token');
  localStorage.setItem('refreshToken', 'token');
  localStorage.setItem('expirationDate', '2050-01-01');

}

export function RemoveMockTocken(): void {
  localStorage.removeItem('jwt');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('expirationDate');
}
