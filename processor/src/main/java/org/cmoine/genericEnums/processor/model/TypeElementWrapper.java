package org.cmoine.genericEnums.processor.model;

import com.sun.source.util.Trees;
import org.cmoine.genericEnums.GenericEnum;

import javax.lang.model.element.*;
import java.util.List;
import java.util.stream.Collectors;

public class TypeElementWrapper {
    final Trees trees;
    private final TypeElement typeElement;

    public TypeElementWrapper(Trees trees, TypeElement typeElement) {
        this.trees = trees;
        this.typeElement = typeElement;
    }

    public List<EnumConstantWrapper> getEnumConstants() {
        return typeElement.getEnclosedElements().stream()
                .filter(it -> it.getKind().equals(ElementKind.ENUM_CONSTANT))
                .map(it -> new EnumConstantWrapper(this, (VariableElement) it))
                .collect(Collectors.toList());
    }

    public List<FieldWrapper> getFields() {
        return typeElement.getEnclosedElements().stream()
                .filter(it -> it.getKind().equals(ElementKind.FIELD))
                .map(it -> new FieldWrapper(this, (VariableElement) it))
                .collect(Collectors.toList());
    }

    public List<ConstructorWrapper> getConstructors() {
        return typeElement.getEnclosedElements().stream()
                .filter(it -> it.getKind().equals(ElementKind.CONSTRUCTOR))
                .map(it -> new ConstructorWrapper(this, (ExecutableElement) it))
                .collect(Collectors.toList());
    }

    public String getGenericParameterName() {
        return typeElement.getAnnotation(GenericEnum.class).genericTypeName();
    }
}
