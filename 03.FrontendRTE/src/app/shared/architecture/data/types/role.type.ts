/* (c) https://github.com/MontiCore/monticore */

export type RoleType = 'admin'
    | 'leser'
    | 'accountManager'
    | 'accountAnleger'
    | 'planAccountAnleger'
    | 'accountAdmin'
    | 'facultaetManager'
    | 'personal'
    | '';

const rolesMap: Map<RoleType, string> = new Map<RoleType, string>([
  ['admin', 'Administrator'],
  ['leser', 'Leser'],
  ['accountManager', 'Kontomanager'],
  ['accountAnleger', 'Kontoanleger'],
  ['planAccountAnleger', 'Plankontoanleger'],
  ['accountAdmin', 'Finanzadministrator'],
  ['personal', 'Personalverantwortlicher'],
  ['facultaetManager', 'Fakultät-Manager']
]);

export default rolesMap;

