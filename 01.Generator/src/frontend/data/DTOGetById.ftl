<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
{
  return new Promise((resolve, reject) => {
    commandManager.addCommand(new ${type}_getById(objectId), (dto: IDTO) => {
      if (dto instanceof ${type}DTO) {
        resolve(dto);
      } else {
reject(dto);
      }
    });
  });
}
