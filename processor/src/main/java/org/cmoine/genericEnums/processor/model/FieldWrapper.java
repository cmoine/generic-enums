package org.cmoine.genericEnums.processor.model;

import javax.lang.model.element.VariableElement;
import java.util.stream.Collectors;

public class FieldWrapper {
    private final VariableElement element;

    public FieldWrapper(TypeElementWrapper parent, VariableElement element) {
        this.element = element;
    }

    public String getName() {
        return element.getSimpleName().toString();
    }

    public String getType() {
        return element.asType().toString();
    }

    public String getModifiers() {
        return element.getModifiers().stream().map(it -> it.toString()).collect(Collectors.joining(" "));
    }
}
