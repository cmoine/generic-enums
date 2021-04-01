package org.cmoine.genericEnums.processor.model;

import org.cmoine.genericEnums.GenericEnumConstants;
import org.cmoine.genericEnums.GenericEnumParam;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class InterfaceWrapper {
    private final TypeMirror typeMirror;

    public InterfaceWrapper(TypeMirror typeMirror) {
        this.typeMirror = typeMirror;
    }

    @Override
    public String toString() {
        return toString(typeMirror);
    }

    private static String toString(TypeMirror typeMirror) {
        DeclaredType declaredType= (DeclaredType) typeMirror;
        StringBuilder buf = new StringBuilder();
        System.out.println("> " + declaredType.getAnnotationMirrors().stream().map(it -> it.toString()).collect(Collectors.joining(", ")));
        Optional<? extends AnnotationMirror> annotation = declaredType.getAnnotationMirrors().stream()
                .filter(it -> it.toString().startsWith("@" + GenericEnumParam.class.getName())).findFirst();
        if(annotation.isPresent()) {
            Optional<String> first = annotation.get().getElementValues().entrySet().stream()
                    .filter(it -> "value()".equals(it.getKey().toString()))
                    .map(it -> Objects.toString(it.getValue().getValue()))
                    .findFirst();
            String value=first.isPresent() ? first.get() : GenericEnumConstants.GENERIC_NAME;
            buf.append(value);
        } else {
            buf.append(((QualifiedNameable) declaredType.asElement()).getQualifiedName());
        }

        if (!declaredType.getTypeArguments().isEmpty()) {
            buf.append('<');
            buf.append(declaredType.getTypeArguments().stream().map(InterfaceWrapper::toString).collect(Collectors.joining(", ")));
            buf.append('>');
        }
        return buf.toString();
    }
}
