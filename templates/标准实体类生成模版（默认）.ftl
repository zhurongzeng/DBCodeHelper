package ${packageName};

<#list packageList as package>
import ${package};
</#list>

/**
 * ${classDescription}Model∂‘œÛ
 *
 * @author ${author}
 * @date ${date}
 */
public class ${className} {
    <#list fieldList as field> 
	/**
	 * ${field.description}
	 */
	private ${field.type} ${field.field?uncap_first};

    </#list>
    <#list fieldList as field>
	/**
	 * ${field.description}
	 */
	public void set${field.field?cap_first}(${field.type} ${field.field?uncap_first}) {
		this.${field.field?uncap_first} = ${field.field?uncap_first};
	}

	public ${field.type} get${field.field?cap_first}() {
		return this.${field.field?uncap_first};
	}

    </#list>
}