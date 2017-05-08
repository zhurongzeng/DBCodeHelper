<#list fieldList as field>
paramJson.put("${field.field?lower_case}", loanListDto.get${field.field?cap_first}());// ${field.description}
</#list>