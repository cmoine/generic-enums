package org.cmoine.genericEnums.processor.model;

import com.sun.source.util.Trees;
import org.cmoine.genericEnums.GenericEnum;

import javax.lang.model.element.*;
import java.util.List;
import java.util.stream.Collectors;

public class TypeElementWrapper {
    final Trees trees;
    private final TypeElement typeElement;
//
//    private static class FunctionMatcher implements Predicate<MethodTree> {
//        private final ExecutableElement element;
//
//        public FunctionMatcher(ExecutableElement element) {
//            this.element = element;
//        }
//
//        @Override
//        public boolean test(MethodTree methodTree) {
//            return false;
//        }
//    }

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

    public List<ExecutableElementWrapper> getConstructors() {
        return typeElement.getEnclosedElements().stream()
                .filter(it -> it.getKind().equals(ElementKind.CONSTRUCTOR))
                .map(it -> new ConstructorWrapper(this, (ExecutableElement) it))
                .collect(Collectors.toList());
    }

    public List<ExecutableElementWrapper> getMethods() {
        return typeElement.getEnclosedElements().stream()
                .filter(it -> it.getKind().equals(ElementKind.METHOD))
                .filter(it -> isValidMethod((ExecutableElement)it))
                .map(it -> new MethodWrapper(this, (ExecutableElement) it))
                .filter(it -> it.methodTree!=null)
                .collect(Collectors.toList());
    }

    private boolean isValidMethod(ExecutableElement it) {
        if("values".equals(it.getSimpleName().toString())
                && it.getParameters().size()==0
                && it.getModifiers().contains(Modifier.STATIC))
            return false;

        if("valueOf".equals(it.getSimpleName().toString())
                && it.getParameters().size()==1
                // && it.getParameters().get(0).getSimpleName().toString().equals("java.lang.String")
                && it.getModifiers().contains(Modifier.STATIC))
            return false;

        return true;
    }

//    private boolean isValidMethod(MethodTree m) {
//        if(m.getReturnType()==null)
//            return false;
//
//        if("values".equals(m.getName().toString())
//                && m.getParameters().size()==0
//                /*&& m.getModifiers().getFlags().contains(Modifier.STATIC)*/)
//            return false;
//
//        return true;
//    }

    public String getGenericParameterName() {
        return typeElement.getAnnotation(GenericEnum.class).genericTypeName();
    }
}
