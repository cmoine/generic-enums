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
<#assign enums = enumConstant.arguments />
<#assign enums += ['"'+enumConstant.name+'"'] />
<#assign enums += [enumConstant?index] />
    public static final ${className}<${enumConstant.type}> ${enumConstant.name}=new ${className}<>(${enums?join(', ')});
</#list>
    private final String __enum_name__;
    private final int __ordinal__;
<#list typeElement.fields as field>
    ${field.modifiers} ${field.type} ${field.name};
</#list>

<#list typeElement.constructors as constructor>
<#assign params = constructor.parameters?map(it -> it.type+' '+it.name) />
<#assign params += ["String __enum_name__"] />
<#assign params += ["int __ordinal__"] />
    private ${className}(${params?join(', ')}) {
<#list constructor.statements as statement>
<#if statement?index==0 && constructor.thisInitializer??>
<#assign args = constructor.thisInitializer.arguments />
<#assign args += ["__enum_name__"] />
<#assign args += ["__ordinal__"] />
        this(${args?join(', ')});
<#else>
        ${statement}
</#if>
</#list>
<#if !constructor.thisInitializer??>
        this.__enum_name__=__enum_name__;
        this.__ordinal__=__ordinal__;
</#if>
    }
</#list>

<#list typeElement.methods as method>
    ${method.modifiers} ${method.returnType} ${method.name}(${method.parameters?map(it -> it.type+' '+it.name)?join(', ')}) {
<#list method.statements as statement>
        ${statement}
</#list>
    }
</#list>

    public static ${className}[] values() {
        return new ${className}[]{${typeElement.enumConstants?map(it -> it.name)?join(', ')}};
    }

    public static ${className} valueOf(String name) {
<#list typeElement.enumConstants as enumConstant>
        if("${enumConstant.name}".equals(name)) {
            return ${enumConstant.name};
        }
</#list>
        throw new IllegalArgumentException("No enum constant ${packageName}.${className}."+name);
    }

    public String name() {
        return this.__enum_name__;
    }

    public int ordinal() {
        return this.__ordinal__;
    }

    @Override
    public String toString() {
        return name();
    }
}