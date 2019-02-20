${tc.signature("constraints", "message")}
{
  let constraintFailed = false;
  <#list constraints as c>
  ${c}
  </#list>

  if (constraintFailed) {
    throw new ValidationError(${message});
  }
}