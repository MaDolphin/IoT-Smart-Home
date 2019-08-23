<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
{
  return new Promise((resolve, reject) => {
    commandManager.addCommand(new ${type}_update(this), (dto: IDTO) => {
      if (dto instanceof IdDTO) {
        resolve(dto);
      } else {
reject(dto);
      }
    });
  });
}
