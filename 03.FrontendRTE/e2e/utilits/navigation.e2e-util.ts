import {browser} from 'protractor';
import 'rxjs/add/operator/concatMap';

export async function navigateToAccount(id: number, tab: string = '') {
  return browser.get('/finanzen/konto/' + id.toString() + tab)
}

export async function navigateToFinances() {
  return browser.get('/finanzen/konten');
}

export async function navigateToPersonal() {
  return browser.get('/personal');
}

export async function navigateToPersonalExpert() {
  return browser.get('/personal/mitarbeiter/anlegen');
}

export async function navigateToBookings() {
  return browser.getCurrentUrl().then((url) => {
    browser.get(url + '/buchungen')
  })
}

export async function navigateToAllocations() {
  return browser.getCurrentUrl().then((url) => {
    browser.get(url + '/mittelzuweisungen')
  })
}

export async function navigateToDashboard() {
  return browser.get('/dashboard');
}

export async function navigateToLogin() {
  return browser.get('/auth/login');
}

export async function navigateToAccountExpert(id: number = -1) {
  if (id < 0) {
    return browser.get('/finanzen/konto/anlegen');
  }
  return browser.get('/finanzen/konto/' + id.toString() + '/bearbeiten');
}