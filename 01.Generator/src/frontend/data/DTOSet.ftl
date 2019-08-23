<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "attr")}
{
  return new Promise((resolve, reject) => {
    commandManager.addCommand(new ${type}_set${attr?cap_first}(this.id, this.${attr}), (dto: IDTO) => {
      if (dto instanceof IdDTO) {
resolve();
      } else {
reject(dto);
      }
    });
  });
}
