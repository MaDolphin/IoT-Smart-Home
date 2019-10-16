<#-- (c) https://github.com/MontiCore/monticore -->
{
  if (typeName) {
    if (DTOTypeResolver.types.has(typeName)) {
      return DTOTypeResolver.types.get(typeName);
    } else {
      throw new ErrorDTO('0xF0101', MontiGemError.create('0xF0101', 'Unknown typeName', 'Unknown type ' + typeName));
    }
  } else {
    throw new ErrorDTO('0xF0100', MontiGemError.create('0xF0100', 'No typeName given', 'No typeName given'));
  }
}
