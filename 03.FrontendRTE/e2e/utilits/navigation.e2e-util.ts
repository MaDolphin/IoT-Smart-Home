import {browser} from 'protractor';
import 'rxjs/add/operator/concatMap';

export async function navigateToDashboard() {
  return browser.get('/dashboard');
}

export async function navigateToLogin() {
  return browser.get('/auth/login');
}
