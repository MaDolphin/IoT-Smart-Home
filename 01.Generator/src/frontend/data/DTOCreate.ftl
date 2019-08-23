<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
{
  return new Promise((resolve, reject) => {
    commandManager.addCommand(new ${type}_create(this), (dto: IDTO) => {
      if (dto instanceof IdDTO) {
        this.id = dto.objectId;
        resolve(dto);
      } else {
reject(dto);
      }
    });
  });
}
