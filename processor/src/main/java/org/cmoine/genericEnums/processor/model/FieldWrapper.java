package org.cmoine.genericEnums.processor.model;

import org.cmoine.genericEnums.GenericEnumParam;

import javax.lang.model.element.VariableElement;

/**
 * @deprecated Replaced by {@link FieldTreeWrapper}
 */
@Deprecated
public class FieldWrapper extends ElementWrapper<VariableElement> {
    private final TypeElementWrapper parent;

    public FieldWrapper(TypeElementWrapper parent, VariableElement element) {
        super(element);
        this.parent = parent;
    }

    @Override
    public String getType() {
        GenericEnumParam annotation = element.getAnnotation(GenericEnumParam.class);
        if(annotation !=null) {
            return annotation.value();
        } else {
            return super.getType();
        }
    }
}
