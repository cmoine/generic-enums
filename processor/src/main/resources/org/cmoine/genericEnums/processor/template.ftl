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
public <#if typeElement.abstract>abstract </#if>class ${className}<${typeElement.genericParameterNames?join(', ')}> <#if typeElement.interfaces?has_content>implements ${typeElement.interfaces?join(', ')}</#if>{
<#list typeElement.enumConstantTree as enumConstant>
<#assign enums = enumConstant.arguments />
<#assign enums += ['"'+enumConstant.name+'"'] />
<#assign enums += [enumConstant?index] />
    public static final ${className}<${enumConstant.types?join(', ')}> ${enumConstant.name}=new ${className}<${enumConstant.types?join(', ')}>(${enums?join(', ')})<#if enumConstant.classBody??> {
<#list enumConstant.classBody.members as member>
<#list member?split('\n') as line>            ${line}</#list>${instanceOf(member, 'com.sun.source.tree.VariableTree')?then(';', '')}
</#list>
<#list enumConstant.classBody.fields as field>
<#assign pad="            "/>
<#include "fieldTree.ftl"/>
</#list>
<#list enumConstant.classBody.methods as method>
<#assign pad="            "/>
<#include "methodTree.ftl"/>
</#list>
        }</#if>;
</#list>
    private final String __enum_name__;
    private final int __ordinal__;
<#list typeElement.fieldTree as field>
<#assign pad="    "/>
<#include "fieldTree.ftl"/>
</#list>

<#list typeElement.constructorTree as constructor>
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

<#list typeElement.methodTree as method>
<#assign pad="    "/>
<#include "methodTree.ftl"/>
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