<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
{
  return new Promise((resolve, reject) => {
    commandManager.addCommand(new ${type}_delete(this.id), (dto: IDTO) => {
      if (dto instanceof IdDTO) {
        resolve(dto);
      } else {
reject(dto);
      }
    });
  });
}
