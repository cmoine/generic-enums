package ${packageName};

<#if generatedTypeAvailable>
import ${generatedType};

</#if>

<#if !generatedTypeAvailable>/*</#if>
@Generated(
    value = "${processorName}",
    date = "${.now?string("yyyy-MM-dd'T'HH:mm:ssZ")}",
    comments = "version: ${version!"[N/A]"}, compiler: javac, environment: Java ${runtimeVersion} (${runtimeVendor})"
)<#if !generatedTypeAvailable>*/</#if>
public class ${className}<${typeElement.genericParameterName}> {
<#list typeElement.enumConstants as enumConstant>
    public static final ${className}<${enumConstant.type}> ${enumConstant.name}=new ${className}<>(${enumConstant.arguments?join(', ')});
</#list>

<#list typeElement.fields as field>
        ${field.modifiers} ${field.type} ${field.name};
</#list>

<#list typeElement.constructors as constructor>
    private ${className}(${constructor.parameters?map(it -> it.type+' '+it.name)?join(', ')}) {
<#list constructor.statements as statement>
        ${statement}
</#list>
    }
</#list>

    public static ${className}[] values() {
        return new ${className}[]{${typeElement.enumConstants?map(it -> it.name)?join(', ')}};
    }
}