export type ObjClasses = 'account'
    | 'user'
    | 'personal'
    | 'employeeaccount'
    | 'joballocation'
    | 'roleassignment'
    | '';

const objClassesMap: Map<ObjClasses, string> = new Map<ObjClasses, string>([
  ['account', 'Finanzen'],
  ['user', 'Benutzer'],
  ['personal', 'Personal'],
  ['employeeaccount', 'Personalkonto'],
  ['joballocation', 'Stellenzuweisung'],
  ['roleassignment', 'Rollenvergabe']
]);

export default objClassesMap;