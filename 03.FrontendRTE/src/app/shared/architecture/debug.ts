import { Logger } from '@upe/logger';

export interface IModelData {
  model: any;
  added: boolean;
  updated: boolean;
  deleted: boolean;
}

export const DATA: Map<string, IModelData> = new Map<string, IModelData>();

const LOGGER: Logger = new Logger({name: 'Debugger'});

export function setEntry(model: any): void {
  if (DATA.has(model.internId)) {
    LOGGER.error('model is already added', model);
  } else {
    DATA.set(model.internId, {
      model: model,
      added: !!model.id,
      updated: false,
      deleted: false
    });
  }
}

export function deleteModel(model: any): void {
  if (!DATA.has(model.internId)) {
    LOGGER.error('model is not added', model);
  } else {
    const entry = DATA.get(model.internId);
    if (entry) {
      if (entry.deleted) {
        LOGGER.error('model is already marked as deleted');
      } else {
        entry.deleted = true;
      }
    }
  }
}

export function updateModel(model: any): void {
  if (!DATA.has(model.internId)) {
    LOGGER.error('modl is not added', model);
  } else {
    const entry = DATA.get(model.internId);
    if (entry) {
      entry.updated = true;
    }
  }
}

export function addModel(model: any): void {
  if (!DATA.has(model.internId)) {
    LOGGER.error('modl is not added', model);
  } else {
    const entry = DATA.get(model.internId);
    if (entry) {
      if (entry.added) {
        LOGGER.error('model is already marked as added');
      } else {
        entry.added = true;
      }
    }
  }
}

/*if (!window['dataInit']) {

  window['dataInit'] = true;

  window['printDeleted'] = function () {
    LOGGER.debug('deleted', window['getDeleted']());
  };

  window['getDeleted'] = function () {
    return Array.from(DATA.values()).filter((entry: IModelData) => entry.deleted);
  };

  window['printAdded'] = function () {
    LOGGER.debug('added', window['getAdded']());
  };

  window['getAdded'] = function () {
    return Array.from(DATA.values()).filter((entry: IModelData) => entry.added);
  };

  window['printUpdated'] = function () {
    LOGGER.debug('updated', window['getUpdated']());
  };

  window['getUpdated'] = function () {
    Array.from(DATA.values()).filter((entry: IModelData) => entry.updated);
  };

  window['printActive'] = function () {
    LOGGER.debug('active', window['getActive']());
  };

  window['getActive'] = function () {
    return Array.from(DATA.values()).filter((entry: IModelData) => !entry.deleted);
  };

  window['printBookings'] = function (active: boolean = true) {
    LOGGER.debug('bookings', window['getExpenses'](active));
  };

  window['getExpenses'] = function (active: boolean = true) {
    Array.from(DATA.values()).filter((entry: IModelData) => entry.model.voucherDate && (!active || !entry.deleted));
  };

  window['printBudgets'] = function (active: boolean = true) {
    LOGGER.debug('budgets', window['getBudgets'](active));
  };

  window['getBudgets'] = function (active: boolean = true) {
    return Array.from(DATA.values()).filter((entry: IModelData) => entry.model.value && (!active || !entry.deleted));
  };

  window['printAccounts'] = function (active: boolean = true) {
    LOGGER.debug('accounts', window['getAccounts'](active));
  };

  window['getAccounts'] = function (active: boolean = true) {
    return Array.from(DATA.values()).filter((entry: IModelData) => entry.model.budget && (!active || !entry.deleted));
  };

  window['query'] = function (filter: (model: any) => boolean) {
    return Array.from(DATA.values()).filter(filter);
  };

  window['queryAccounts'] = function (filter: (model: any) => boolean, active: boolean = true) {
    return window['getAccounts'](active).filter(filter);
  };

  window['queryBudgets'] = function (filter: (model: any) => boolean, active: boolean = true) {
    return window['getBudgets'](active).filter(filter);
  };

  window['queryBookings'] = function (filter: (model: any) => boolean, active: boolean = true) {
    return window['getExpenses'](active).filter(filter);
  };

  window['log'] = function (data: any) {
    LOGGER.debug('.', data);
  };

  window['count'] = function (active: boolean = true) {
    LOGGER.debug('count', Array.from(DATA.values()).filter((entry: IModelData) => (!active || !entry.deleted)).length);
  };

  window['dabbles'] = function (active: boolean = true) {
    const found: number[] = [];
    let dabbles = 0;
    Array.from(DATA.values())
      .filter((entry: IModelData) => (!active || !entry.deleted))
      .forEach((entry: IModelData) => {
        if (entry.model.id) {
          if (found.indexOf(entry.model.id) !== -1) {
            dabbles++;
          } else {
            found.push(entry.model.id);
          }
        }
      });
    LOGGER.debug('dabbles', dabbles);
  }

}*/
