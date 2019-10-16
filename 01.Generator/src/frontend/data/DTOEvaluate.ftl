<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
{
  let data: ${type} = TypedJSON.parse(response.data, ${type});
  console.log('dto:', data);
  data.transform();
  return data;
}
