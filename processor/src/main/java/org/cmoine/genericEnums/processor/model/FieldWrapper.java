package org.cmoine.genericEnums.processor.model;

import org.cmoine.genericEnums.GenericEnumParam;

import javax.lang.model.element.VariableElement;

public class FieldWrapper extends ElementWrapper<VariableElement> {
    private final TypeElementWrapper parent;

    public FieldWrapper(TypeElementWrapper parent, VariableElement element) {
        super(element);
        this.parent = parent;
    }

    @Override
    public String getType() {
        if(element.getAnnotation(GenericEnumParam.class)!=null) {
            return parent.getGenericParameterName();
        } else {
            return super.getType();
        }
    }
}
