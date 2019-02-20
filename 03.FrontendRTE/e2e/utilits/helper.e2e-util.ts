import { browser, by, element, ElementArrayFinder, ElementFinder, protractor } from 'protractor';
import { Locator } from 'protractor/built/locators';
import 'rxjs/add/operator/concatMap';
import { promise } from 'selenium-webdriver';
import { MoneyToNumberPipe } from '../../src/app/shared/pipes/money-to-number.pipe';
import { headerMaCoCo } from './login.e2e-util';

let fetch = require('node-fetch');

export const moneyToNumber = new MoneyToNumberPipe();

export function getElement(locator: Locator): ElementFinder {
  const eml = element(locator);
  browser.executeScript('arguments[0].scrollIntoView();', eml.getWebElement());
  return eml;
}

export async function getElementAsync(locator: Locator) {
  return element(locator).getWebElement().then((ele) => {
    return browser.executeScript('arguments[0].scrollIntoView();', ele);
  }).then(() => {
    return element(locator).getWebElement();
  });
}

export function getInputByName(name: string): ElementFinder {
  return getElement(by.css('input[name=' + name + ']'));
}

export async function getInputTextByName(name: string): promise.Promise<string> {
  return getInputByName(name).getAttribute("value");
}

export async function setInputByName(name: string, value: string | number): promise.Promise<void> {
  return getInputByName(name).sendKeys(value);
}

export async function resetInputByName(name: string, value: string | number) {
  await getInputByName(name).clear();
  return getInputByName(name).sendKeys(value);
}

export function getButtonByName(name: string): ElementFinder {
  return getElement(by.css('button[name=' + name + ']'));
}

export async function setButtonByName(name: string, value: string): promise.Promise<void> {
  await getButtonByName(name).sendKeys(value);
}

export async function openContextMenuOnElement(element) {
  await browser.actions().mouseMove(element).perform();   //takes the mouse to hover the element
  await browser.actions().click(protractor.Button.RIGHT).perform();
}

export function getContextMenuItems(): ElementArrayFinder {
  return getElement(by.css('context-menu-content')).all(by.css('li:not(.divider)'));
}

export async function clickOnContextMenu(rowNumber: number) {
  const contextMenuItems = getContextMenuItems();
  await contextMenuItems.get(rowNumber).click();
}

export function getElementById(id: string): ElementFinder {
  return getElement(by.id(id));
}

export async function getElementByIdAsync(id: string) {
  return getElementAsync(by.id(id));
}

export async function setElementById(id: string, value: string): promise.Promise<void> {
  await getElementById(id).sendKeys(value);
}

export async function isDialogOpen(): promise.Promise<boolean> {
  return element(by.css('mat-dialog-content')).isPresent();
}

export async function isDialogOpenNew(): promise.Promise<boolean> {
  return await element(by.css('mat-dialog-content')).isPresent();
}

export async function clickConfirmOnDialog(): promise.Promise<void> {
  return getElementById("btnConfirm").click();
}

export async function clickDenyOnDialog(): promise.Promise<void> {
  return getElementById("btnDeny").click();
}

export async function clickOnLogout(): promise.Promise<void> {
  return getElementAsync(by.cssContainingText('.btn-text', 'Abmelden')).then((ele) => {
    return ele.click()
  });
}

export async function cleanup(): promise.Promise<any> {
  return browser.executeScript('window.sessionStorage.clear();').then(() => {
    return browser.executeScript('window.localStorage.clear();');
  }).then(() => {
    return softCleanup();
  })
}

export async function softCleanup(): promise.Promise<any> {
  let optionsResetDBf: any = {
    headers: headerMaCoCo
  };
  let ret:promise.Promise<any> = fetch('http://localhost:8080/macoco-be/api/develop/resetDB', optionsResetDBf);
  return await ret;
}

export async function isTokenValid(token:string) {
  return fetch('http://localhost:8080/macoco-be/api/auth/tokens/' + token + '/validity').then((res) => res.json()).then((json) => json.value).catch(fail)
}

export async function getActiveToken(): promise.Promise<any> {
  return browser.executeScript('return window.localStorage.getItem("jwt")')
}

export const A = f => done => f().then(done).catch(done.fail);