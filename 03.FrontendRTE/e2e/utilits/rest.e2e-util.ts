import 'rxjs/add/operator/concatMap';
import {promise} from 'selenium-webdriver';
import * as variables from './login.e2e-util';

let fetch = require('node-fetch');

function getAccountType(accountKind: string): string {
  switch (accountKind) {
    case 'Drittmittelprojekt':
      return 'EU';
    case 'Industrieprojekt':
      return 'FuE';
    case 'Haushalt':
      return 'HSPIII';
    case 'Sonstiges':
      return 'Sonderfonds';
  }
  return '';
}

export async function createAccount(accountKind: string, name: string, value: number = 50000, pp: boolean = false): promise.Promise<any> {
  let _demoAccount = {
    accountKind: accountKind,
    accountType: getAccountType(accountKind),
    name: name,
    budgetRaiseCent: 0,
    comments: [],
    endDate: '2018-01-31T00:00:00.000Z',
    externalAccountId: 0,
    financialBurdenCent: 0,
    flatRate: pp ? 1750 : 0,
    generalCostRate: 0,
    hasYearlyBudget: false,
    isActive: true,
    isFlatRateAccount: pp,
    isPlanAccount: false,
    isSingleAccount: true,
    moneyInflowsKind: 0,
    plannedBurdenCent: 0,
    residualBudgetCent: 0,
    startDate: '2018-01-01T00:00:00.000Z',
    termInMonth: 0,
    totalBudgetCent: 0
  };
  let optionsCreateAccount = {
    method: 'POST',
    headers: {
      'cache-control': 'no-cache',
      'content-type': 'application/json',
      'x-auth': variables.token
    },
    body: JSON.stringify(_demoAccount)
  };
  let accountId: promise.Promise<any> = fetch('http://localhost:8080/macoco-be/api/domain/accounts', optionsCreateAccount).then(res => res.json()).then(json => json.id).then((id) => {
    let _demoTotal = {
      'accountId': id,
      'bookingIds': [],
      'budgetRaiseCent': 0,
      'financialBurdenCent': 0,
      'isActive': true,
      'jobAllocations': [],
      'parentBudgetId': 0,
      'plannedBurdenCent': 0,
      'residualBudgetCent': 0,
      'subBudgets': [],
      'type': 'Totalbudget',
      'value': value,
      'year': 0,
      'yearlyBudgets': []
    };
    let optionsCreateTotal = {
      method: 'POST',
      headers: {
        'cache-control': 'no-cache',
        'content-type': 'application/json',
        'x-auth': variables.token
      },
      body: JSON.stringify(_demoTotal)
    };
    return fetch('http://localhost:8080/macoco-be/api/domain/budgets', optionsCreateTotal);
  }).then(res => res.json()).then((total) => {
    let _demoBudget = {
      'accountId': 0,
      'bookingIds': [],
      'budgetRaiseCent': 0,
      'financialBurdenCent': 0,
      'isActive': true,
      'jobAllocations': [],
      'parentBudgetId': total.id,
      'plannedBurdenCent': 0,
      'residualBudgetCent': 0,
      'subBudgets': [],
      'type': 'Personalbudget',
      'value': value,
      'year': 0,
      'yearlyBudgets': []
    };
    let optionsCreateBudget = {
      method: 'POST',
      headers: {
        'cache-control': 'no-cache',
        'content-type': 'application/json',
        'x-auth': variables.token
      },
      body: JSON.stringify(_demoBudget),
    };
    return fetch('http://localhost:8080/macoco-be/api/domain/budgets', optionsCreateBudget);
  }).then(res => res.json()).catch((e) => fail(e));
  return await accountId;
}

export async function createPlanAccount(accountKind: string, name: string, value: number = 50000): promise.Promise<any> {
  let _demoAccount = {
    accountKind: accountKind,
    accountType: getAccountType(accountKind),
    name: name,
    budgetRaiseCent: 0,
    comments: [],
    endDate: '2018-01-31T00:00:00.000Z',
    externalAccountId: 0,
    financialBurdenCent: 0,
    flatRate: 0,
    generalCostRate: 0,
    hasYearlyBudget: false,
    isActive: true,
    isFlatRateAccount: false,
    isPlanAccount: true,
    isSingleAccount: true,
    moneyInflowsKind: 0,
    plannedBurdenCent: 0,
    residualBudgetCent: 0,
    startDate: '2018-01-01T00:00:00.000Z',
    termInMonth: 0,
    totalBudgetCent: 0
  };
  let optionsCreateAccount = {
    method: 'POST',
    headers: {
      'cache-control': 'no-cache',
      'content-type': 'application/json',
      'x-auth': variables.token
    },
    body: JSON.stringify(_demoAccount)
  };
  let accountId: promise.Promise<any> = fetch('http://localhost:8080/macoco-be/api/domain/accounts', optionsCreateAccount).then(res => res.json()).then(json => json.id).then((id) => {
    let _demoTotal = {
      'accountId': id,
      'bookingIds': [],
      'budgetRaiseCent': 0,
      'financialBurdenCent': 0,
      'isActive': true,
      'jobAllocations': [],
      'parentBudgetId': 0,
      'plannedBurdenCent': 0,
      'residualBudgetCent': 0,
      'subBudgets': [],
      'type': 'Totalbudget',
      'value': value,
      'year': 0,
      'yearlyBudgets': []
    };
    let optionsCreateTotal = {
      method: 'POST',
      headers: {
        'cache-control': 'no-cache',
        'content-type': 'application/json',
        'x-auth': variables.token
      },
      body: JSON.stringify(_demoTotal)
    };
    return fetch('http://localhost:8080/macoco-be/api/domain/budgets', optionsCreateTotal);
  })
    .then(res => res.json()).then((total) => total.accountId).catch((e) => fail(e));
  return await accountId;
}

export async function createBooking(id: number, value: number = 50000): promise.Promise<any> {
  let _demoBooking = {
    'amountCent': value,
    'bookingDate': null,
    'bookingText': 'Test',
    'bookingTitle': 'Test',
    'budgetId': id,
    'creditDebit': '',
    'date': null,
    'isActive': true,
    'isNegative': false,
    'mainBudgetType': 'Personalbudget',
    'realAccount': '',
    'status': 'SAP',
    'subBudgetType': null,
    'voucherDate': null,
    'voucherNumbers': []
  };
  let optionsCreateBooking = {
    method: 'POST',
    headers: {
      'cache-control': 'no-cache',
      'content-type': 'application/json',
      'x-auth': variables.token
    },
    body: JSON.stringify(_demoBooking)
  };
  let ret: promise.Promise<any> = fetch('http://localhost:8080/macoco-be/api/domain/bookings', optionsCreateBooking).then(res => res.json()).catch(e => fail(e));
  return await ret;
}

export async function createNegativBooking(id: number, value: number = 50000): promise.Promise<any> {
  let _demoAllocation = {
    'amountCent': value,
    'bookingDate': null,
    'bookingText': 'Test',
    'bookingTitle': '1-12',
    'budgetId': id,
    'creditDebit': '',
    'date': null,
    'isActive': true,
    'isNegative': true,
    'mainBudgetType': 'Personalbudget',
    'realAccount': '',
    'status': 'SAP',
    'subBudgetType': null,
    'voucherDate': null,
    'voucherNumbers': []
  };
  let optionsCreateAllocation = {
    method: 'POST',
    headers: {
      'cache-control': 'no-cache',
      'content-type': 'application/json',
      'x-auth': variables.token
    },
    body: JSON.stringify(_demoAllocation)
  };
  let ret: promise.Promise<any> = fetch('http://localhost:8080/macoco-be/api/domain/bookings', optionsCreateAllocation).then(res => res.json()).catch(e => fail(e));
  return await ret;
}

export async function createJobAllocation(id: number, value: number = 50000, activeDate: boolean = true): promise.Promise<any> {
  let start = activeDate ? '2010-02-28T00:00:00.000Z' : '2080-02-28T00:00:00.000Z';
  let _demoAllocation = {
    'amountCent': value,
    'budgetId': id,
    'date': null,
    'end': '2080-02-28T00:00:00.000Z',
    'extent': '20%',
    'isActive': true,
    'jobDefinition': 'lll',
    'mainBudgetType': 'Personalbudget',
    'start': start,
    'status': 'Besetzt',
    'subBudgetType': ''
  };
  let optionsCreateAllocation = {
    method: 'POST',
    headers: {
      'cache-control': 'no-cache',
      'content-type': 'application/json',
      'x-auth': variables.token
    },
    body: JSON.stringify(_demoAllocation)
  };
  let ret: promise.Promise<any> = fetch('http://localhost:8080/macoco-be/api/domain/joballocations', optionsCreateAllocation).then(res => res.json()).catch(e => fail(e));
  return await ret;
}

export async function createSubbudget(name: string, budget: any[], value: number = 50000): promise.Promise<any> {

  let _demoBudget = {
    'accountId': budget[0],
    'bookingIds': [],
    'budgetRaiseCent': 0,
    'financialBurdenCent': 0,
    'isActive': true,
    'jobAllocations': [],
    'parentBudgetId': budget[1],
    'plannedBurdenCent': 0,
    'residualBudgetCent': 0,
    'subBudgets': [],
    'type': name,
    'value': value,
    'year': 0,
    'yearlyBudgets': []
  };
  let optionsCreateBudget = {
    method: 'POST',
    headers: {
      'cache-control': 'no-cache',
      'content-type': 'application/json',
      'x-auth': variables.token
    },
    body: JSON.stringify(_demoBudget),
  };
  let ret: promise.Promise<any> = fetch('http://localhost:8080/macoco-be/api/domain/budgets', optionsCreateBudget);
  return await ret;
}