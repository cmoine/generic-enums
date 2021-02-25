package org.cmoine.genericEnums.processor.model;

import org.cmoine.genericEnums.GenericEnumParam;
import org.cmoine.genericEnums.processor.TemplateData;

import javax.lang.model.element.VariableElement;

public class ParameterWrapper {
    private final ConstructorWrapper parent;
    private final VariableElement symbol;

    public ParameterWrapper(ConstructorWrapper parent, VariableElement symbol) {
        this.parent = parent;
        this.symbol = symbol;
    }

    public String getName() {
        return symbol.getSimpleName().toString();
    }

    public String getType() {
        if(symbol.getAnnotation(GenericEnumParam.class)!=null) {
            return parent.parent.getGenericParameterName();
        } else {
            return symbol.asType().toString();
        }
    }
}
