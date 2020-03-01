<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("dtoClass", "dtos", "opts", "lists")}
{
  const copiedDTO = new ${dtoClass}();
  <#list dtos as dto>
    const ${dto.getName()} = this.${dto.getName()}.copy();
    copiedDTO.${dto.getName()} = ${dto.getName()};
  </#list>
  <#list opts as opt>
    if(this.${opt.getName()} != null) {
    const ${opt.getName()} = this.${opt.getName()}.copy();
    copiedDTO.${opt.getName()} = ${opt.getName()};
    } else {
    copiedDTO.${opt.getName()} = null;
    }
  </#list>
  <#list lists as list>
    const ${list.getName()} = this.${list.getName()}.map(dto => dto.copy());
    copiedDTO.${list.getName()} = ${list.getName()};
  </#list>
  return copiedDTO;
}
