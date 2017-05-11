package ${packageName};

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
	public void set${field.field?cap_first}S(String ${field.field?uncap_first}) {
		Double ${field.field?uncap_first}s = Double.parseDouble(${field.field?uncap_first});
		set${field.field?cap_first}(${field.field?uncap_first}s);
	}

    </#list>
}