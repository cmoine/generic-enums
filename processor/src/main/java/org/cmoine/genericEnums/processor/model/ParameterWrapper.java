package org.cmoine.genericEnums.processor.model;

import org.cmoine.genericEnums.GenericEnumParam;

import javax.lang.model.element.VariableElement;

/**
 * @deprecated Replaced by {@link ParameterTreeWrapper}
 */
@Deprecated
public class ParameterWrapper {
    private final ExecutableElementWrapper parent;
    private final VariableElement symbol;

    public ParameterWrapper(ExecutableElementWrapper parent, VariableElement symbol) {
        this.parent = parent;
        this.symbol = symbol;
    }

    public String getName() {
        return symbol.getSimpleName().toString();
    }

    public String getType() {
        GenericEnumParam annotation = symbol.getAnnotation(GenericEnumParam.class);
        if(annotation !=null) {
            return annotation.value();
        } else {
            return symbol.asType().toString();
        }
    }
}
