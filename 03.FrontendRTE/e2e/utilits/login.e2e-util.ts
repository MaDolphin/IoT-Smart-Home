/* (c) https://github.com/MontiCore/monticore */
import {browser} from 'protractor';
import {LoginPo} from '../pageobjects/login/login.po';
import {Logger} from '@upe/logger';
import {promise} from 'selenium-webdriver';
import {navigateToLogin} from './navigation.e2e-util';
import * as moment from 'moment';

let fetch = require('node-fetch');
let request: any = require('request');
export let token: String = '';
export let refreshToken: String = '';
Logger.DISABLED = true;

export let headerMaCoCo: any = {
  'Accept': 'application/json, text/plain, */*',
  'Accept-Encoding': 'gzip, deflate, br',
  'Accept-Language': 'de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4',
  'Connection': 'keep-alive',
  'Content-Length': '2',
  'Content-Type': 'application/json',
  'Host': 'localhost:4200',
  'Origin': 'http://localhost:4200',
  'Referer': 'http://localhost:4200/personal',
  'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36'
};

let headerLogin: any = {
  'Accept': 'application/json',
  'Accept-Encoding': 'gzip, deflate, br',
  'Accept-Language': 'de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4',
  'Connection': 'keep-alive',
  'Content-Length': '42',
  'Content-Type': 'application/json',
  'Host': 'localhost:4200',
  'Origin': 'http://localhost:4200',
  'Referer': 'http://localhost:4200/auth/login',
  'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36',
};

export let optionsResetDB: any = {
  method: 'GET',
  url: 'http://localhost:8080/macoco-be/api/develop/resetDB',
  headers: headerMaCoCo
};

export let optionsCreateDummy: any = {
  method: 'POST',
  url: 'http://localhost:8080/macoco-be/api/develop/createDummy',
  headers: headerMaCoCo,
  body: '{}'
};

async function postLogin(username: string = 'admin', pass: string = 'passwort', resource: string = 'TestDB'): promise.Promise<any> {
  let optionsLoginf: any = {
    method: 'POST',
    headers: headerLogin,
    body: JSON.stringify({ username: username, password: pass, resource: resource})
  };
  let ret: promise.Promise<any> = fetch('http://localhost:8080/macoco-be/api/auth/login', optionsLoginf).then(res => res.json()).catch((e) => fail('unable to login: ' + e));
  return await ret;
}

export async function logind(): promise.Promise<any> {
  return postLogin().then((jtoken) => {

    let date: Date = moment(jtoken.expirationDate).toDate();
    token = jtoken.jwt;
    headerMaCoCo['X-AUTH'] = jtoken.jwt;
    browser.executeScript('return window.localStorage.setItem("jwt", "' + jtoken.jwt + '")').catch((e) => console.log('a' + e));
    browser.executeScript('return window.localStorage.setItem("refreshToken", "' + jtoken.refreshToken + '")').catch((e) => console.log('b' + e));
    browser.executeScript('return window.localStorage.setItem("expirationDate", "' + date + '")').catch((e) => console.log('c' + e));
    return
  })
}


export function login(user: string = 'admin', password: string = 'passwort', withDummy: boolean = true): promise.Promise<any> {
  let xAuth;
  return postLogin().then((result) => {

    // get the current login token from server
    xAuth = result.jwt;
    headerMaCoCo['X-AUTH'] = xAuth;
    token = xAuth;
    refreshToken = result.refreshToken;
    // if this test is not the first executed test, resetDB() is just called in another test's afterAll()
    // then it can happen the first resetDB is still processing, this resetDB() will cause hibernate exception in backend
    // to solve this problem, define a params "hasRunResetDB" in protractor.conf.js and set it to false/true when
    // createDummy/resetDB has run  run ResetDB only if hasRunResetDB is false
    // resetDB
    if (!browser.params.hasRunResetDB) {
      request(optionsResetDB);
    }
    // create dummy data
    if (withDummy) {
      request(optionsCreateDummy);
    }
    browser.params.hasRunResetDB = false;

    return navigateToLogin()
  }).then(() => {
    // have to refresh page to get rid of UserReport modal dialog!
    return browser.refresh();
  }).then(() => {
    return LoginPo.setUserName(user);
  }).then(() => {
    return LoginPo.setPassword(password);
  }).then(() => {
    return LoginPo.loginButtonClick();

  });
}

export async function setup(): promise.Promise<any> {
  navigateToLogin().then(() => {
    return logind()
  })
}

export async function loginAsPlan(): promise.Promise<any> {
  return postLogin('plan', 'plan').then((jtoken) => {
    let date: Date = moment(jtoken.expirationDate).toDate();
    browser.executeScript('return window.localStorage.setItem("jwt", "' + jtoken.jwt + '")').catch((e) => console.log('a' + e));
    browser.executeScript('return window.localStorage.setItem("refreshToken", "' + jtoken.refreshToken + '")').catch((e) => console.log('b' + e));
    browser.executeScript('return window.localStorage.setItem("expirationDate", "' + date + '")').catch((e) => console.log('c' + e));
    return
  })
}

export async function loginAsReader(): promise.Promise<any> {
  return postLogin('reader', 'reader').then((jtoken) => {
    let date: Date = moment(jtoken.expirationDate).toDate();
    browser.executeScript('return window.localStorage.setItem("jwt", "' + jtoken.jwt + '")').catch((e) => console.log('a' + e));
    browser.executeScript('return window.localStorage.setItem("refreshToken", "' + jtoken.refreshToken + '")').catch((e) => console.log('b' + e));
    browser.executeScript('return window.localStorage.setItem("expirationDate", "' + date + '")').catch((e) => console.log('c' + e));
    return
  })
}

export async function createDummys(): promise.Promise<any> {
  let optionsCreateDummy: any = {
    method: 'POST',
    headers: headerMaCoCo
  };
  let ret: promise.Promise<any> = fetch('http://localhost:8080/macoco-be/api/develop/createDummy', optionsCreateDummy);
  return await ret;
}
