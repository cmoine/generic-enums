${pad}${method.modifiers} ${method.returnType} ${method.name}(${method.parameters?map(it -> it.type+' '+it.name)?join(', ')}) <#if method.abstract>;<#else> {
<#list method.statements as statement>
${pad}    ${statement?trim}
</#list>
${pad}}</#if>
