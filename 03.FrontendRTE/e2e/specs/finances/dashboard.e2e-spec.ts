import {A, cleanup} from '../../utilits/helper.e2e-util';
import {DashboardPo} from '../../pageobjects/dashboard/dashboard.po';
import {createDummys, setup} from '../../utilits/login.e2e-util';
import {FinancesOverviewPo} from '../../pageobjects/finances/finances.overview.po';
import {navigateToDashboard, navigateToFinances} from '../../utilits/navigation.e2e-util';
import {promise} from 'selenium-webdriver';

xdescribe('Dashboard UC19', function () {

  beforeEach(setup);
  afterEach(cleanup);

  beforeEach(createDummys);

  it('calculate all total budgets and compare all total values with the calculated values', A(async () => {
    await navigateToFinances().then(() => {

      return promise.all([
        FinancesOverviewPo.calculateTotalAmount(),
        FinancesOverviewPo.calculateSpentAmount(),
        FinancesOverviewPo.calculatePlanAmount(),
        FinancesOverviewPo.calculateSaldoAmount()
      ])
    }).then((res) => {
      expect(FinancesOverviewPo.getTotalAmountOfHeader()).toBe(res[0]);
      expect(FinancesOverviewPo.getSpentAmountOfHeader()).toBe(res[1]);
      expect(FinancesOverviewPo.getPlanAmountOfHeader()).toBe(res[2]);
      expect(FinancesOverviewPo.getSaldoAmountOfHeader()).toBe(res[3]);
    }).catch(fail)
  }));


  it('navigate to dashboard and check finance chart', A(async () => {
    await navigateToFinances().then(() => {
      return promise.all([
        FinancesOverviewPo.getTotalAmountOfHeader(),
        FinancesOverviewPo.getSpentAmountOfHeader(),
        FinancesOverviewPo.getPlanAmountOfHeader(),
        FinancesOverviewPo.getSaldoAmountOfHeader()
      ])
    }).then((res) => {
      return navigateToDashboard().then(() => {
        expect(DashboardPo.getTotalAmountOfFinancesContainer()).toBe(res[0]);
        expect(DashboardPo.getSpentAmountOfFinancesContainer()).toBe(res[1]);
        expect(DashboardPo.getPlanAmountOfFinancesContainer()).toBe(res[2]);
        expect(DashboardPo.getSaldoAmountOfFinancesContainer()).toBe(res[3]);
      })
    }).catch(fail)
  }));
});
