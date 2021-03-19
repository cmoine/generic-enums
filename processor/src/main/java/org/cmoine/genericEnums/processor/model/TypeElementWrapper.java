package org.cmoine.genericEnums.processor.model;

import com.google.common.collect.ImmutableList;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;

import javax.lang.model.element.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TypeElementWrapper {
    final Trees trees;
    private final TypeElement typeElement;
    private final List<ConstructorWrapper> constructors;
    private final List<EnumConstantWrapper> enumConstants;
    private final List<EnumConstantTreeWrapper> enumConstantTree;
    private final List<FieldWrapper> fields;
    private final List<MethodWrapper> methods;
    private final Set<String> genericParameterNames;
    private final List<MethodTreeWrapper> methodTree;
    private final List<ConstructorTreeWrapper> constructorTree;
    private final List<FieldTreeWrapper> fieldTree;
    ClassTree classTree;

    // https://stackoverflow.com/questions/6373145/accessing-source-code-from-java-annotation-processor
    private class CodeAnalyzerTreeScanner extends TreePathScanner<Object, Trees> {
        @Override
        public Object visitClass(ClassTree node, Trees trees) {
            if(node.getSimpleName().equals(typeElement.getSimpleName())) {
                if(classTree!=null)
                    throw new InternalError("Several matches:\n"+classTree+"\nAND\n"+node);
                classTree=node;
            }
            return super.visitClass(node, trees);
        }
    }

    public TypeElementWrapper(Trees trees, TypeElement typeElement) {
        this.trees = trees;
        this.typeElement = typeElement;

        CodeAnalyzerTreeScanner codeScanner = new CodeAnalyzerTreeScanner();
        TreePath tp = trees.getPath(typeElement);
        codeScanner.scan(tp, trees);
        if(classTree==null)
            throw new NullPointerException();

        ImmutableList.Builder<ConstructorWrapper> constructorBuilder=ImmutableList.builder();
        ImmutableList.Builder<EnumConstantWrapper> enumConstantBuilder=ImmutableList.builder();
        ImmutableList.Builder<FieldWrapper> fieldsBuilder=ImmutableList.builder();
        ImmutableList.Builder<MethodWrapper> methodsBuilder=ImmutableList.builder();
        ImmutableList.Builder<MethodTreeWrapper> methodTreeBuilder=ImmutableList.builder();
        ImmutableList.Builder<ConstructorTreeWrapper> constructorTreeBuilder=ImmutableList.builder();
        ImmutableList.Builder<EnumConstantTreeWrapper> enumConstantTreeBuilder=ImmutableList.builder();
        ImmutableList.Builder<FieldTreeWrapper> fieldTreeBuilder=ImmutableList.builder();
        Set<String> genericParameterNames=null;
        for(Element element: typeElement.getEnclosedElements()) {
            switch(element.getKind()) {
                case ENUM_CONSTANT: {
                    enumConstantBuilder.add(new EnumConstantWrapper(this, (VariableElement) element));
                    break;
                }
                case FIELD: {
                    fieldsBuilder.add(new FieldWrapper(this, (VariableElement) element));
                    break;
                }
                case CONSTRUCTOR: {
//                    ConstructorWrapper constructorWrapper = new ConstructorWrapper(this, (ExecutableElement) element);
//                    if(genericParameters==null) {
//                        genericParameters=constructorWrapper.getGenericParameters();
//                    } else {
//                        String genericParameterNames = toString(genericParameters);
//                        String newGenericParameterNames = toString(constructorWrapper.getGenericParameters());
//                        if(!genericParameterNames.equals(newGenericParameterNames)) {
//                            throw new IllegalArgumentException("All constructors must have the same generic parameters: "+genericParameterNames+"<>"+newGenericParameterNames+"(class="+typeElement.getQualifiedName()+")");
//                        }
//                    }
//                    constructorBuilder.add(constructorWrapper);
                    break;
                }
                case METHOD: {
                    if(isValidMethod((ExecutableElement)element)) {
                        MethodWrapper methodWrapper = new MethodWrapper(this, (ExecutableElement) element);
                        if(methodWrapper.methodTree!=null) {
                            methodsBuilder.add(methodWrapper);
                        }
                    }
                }
            }
        }
        for(Tree tree: classTree.getMembers()) {
            if(tree instanceof MethodTree) {
                JCTree.JCMethodDecl methodDecl= (JCTree.JCMethodDecl) tree;
                Symbol.MethodSymbol methodSymbol = methodDecl.sym;
                if(ElementKind.METHOD.equals(methodSymbol.getKind())) {
                    if(isValidMethod(methodSymbol)) {
                        MethodTreeWrapper methodWrapper = new MethodTreeWrapper(this, methodDecl, null);
                        methodTreeBuilder.add(methodWrapper);
                    }
                } else if(ElementKind.CONSTRUCTOR.equals(methodSymbol.getKind())) {
                    ConstructorTreeWrapper constructorWrapper = new ConstructorTreeWrapper(this, methodDecl);
                    if(genericParameterNames==null) {
                        genericParameterNames=new TreeSet<>(constructorWrapper.getGenericParameters());
                    } else {
                        String oldGenericParameterNames = toString(genericParameterNames);
                        String newGenericParameterNames = toString(new TreeSet<>(constructorWrapper.getGenericParameters()));
                        if(!oldGenericParameterNames.equals(newGenericParameterNames)) {
                            throw new IllegalArgumentException("All constructors must have the same generic parameters: "+oldGenericParameterNames+"<>"+newGenericParameterNames+"(class="+typeElement.getQualifiedName()+")");
                        }
                    }
                    constructorTreeBuilder.add(constructorWrapper);
                }
            } else if (tree instanceof VariableTree) {
                JCTree.JCVariableDecl variableDecl= (JCTree.JCVariableDecl) tree;
                Symbol.VarSymbol sym = variableDecl.sym;
                if(ElementKind.ENUM_CONSTANT.equals(sym.getKind())) {
                    enumConstantTreeBuilder.add(new EnumConstantTreeWrapper(this, variableDecl));
                } else if(ElementKind.FIELD.equals(sym.getKind())) {
                    fieldTreeBuilder.add(new FieldTreeWrapper(this, variableDecl));
                }
            }
        }
        constructors=constructorBuilder.build();
        constructorTree=constructorTreeBuilder.build();
        enumConstants=enumConstantBuilder.build();
        enumConstantTree=enumConstantTreeBuilder.build();
        fields=fieldsBuilder.build();
        methods=methodsBuilder.build();
        methodTree=methodTreeBuilder.build();
        fieldTree=fieldTreeBuilder.build();
        if(genericParameterNames.isEmpty()) {
            throw new IllegalArgumentException("The class "+typeElement.getQualifiedName()+" must have at least one constructor which define a Generic Parameter");
        }
        this.genericParameterNames=genericParameterNames;
    }

    private String toString(Set<String> parameters) {
        return parameters.stream().collect(Collectors.joining(", "));
    }

    public List<EnumConstantWrapper> getEnumConstants() {
        return enumConstants;
    }

    public List<FieldWrapper> getFields() {
        return fields;
    }

    public List<ConstructorWrapper> getConstructors() {
        return constructors;
    }

    public List<ConstructorTreeWrapper> getConstructorTree() {
        return constructorTree;
    }

    public List<MethodWrapper> getMethods() {
        return methods;
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

    public boolean isAbstract() {
        return getMethodTree().stream().anyMatch(it -> it.isAbstract());
    }

    public Set<String> getGenericParameterNames() {
        return genericParameterNames;
    }

//    public Map<String, String> getGenericParameters() {
//        return genericParameters;
//    }

    public List<InterfaceWrapper> getInterfaces() {
        return typeElement.getInterfaces().stream().map(InterfaceWrapper::new).collect(Collectors.toList());
    }

    public List<MethodTreeWrapper> getMethodTree() {
        return methodTree;
    }

    public List<FieldTreeWrapper> getFieldTree() {
        return fieldTree;
    }

    public List<EnumConstantTreeWrapper> getEnumConstantTree() {
        return enumConstantTree;
    }
}
