<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "type")}
{
  return new Promise((resolve, reject) => {
commandManager.addCommand(new ${className}_getAll(), (dto: IDTO) => {
if (dto instanceof ${type}) {
        resolve(dto);
      } else {
reject(dto);
      }
    });
  });
}
