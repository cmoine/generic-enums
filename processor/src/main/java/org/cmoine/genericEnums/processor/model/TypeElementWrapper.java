package org.cmoine.genericEnums.processor.model;

import com.google.common.collect.ImmutableList;
import com.sun.source.tree.*;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import org.cmoine.genericEnums.processor.util.TreeUtil;

import javax.lang.model.element.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TypeElementWrapper {
    //    final Trees trees;
    private final TypeElement typeElement;
    private final List<EnumConstantTreeWrapper> enumConstantTree;
    private final Set<String> genericParameterNames;
    private final List<MethodTreeWrapper> methodTree;
    private final List<ConstructorTreeWrapper> constructorTree;
    private final List<FieldTreeWrapper> fieldTree;
    CompilationUnitTree compilationUnitTree;
    ClassTree classTree;

    // https://stackoverflow.com/questions/6373145/accessing-source-code-from-java-annotation-processor
    private class CodeAnalyzerTreeScanner extends TreePathScanner<Object, Trees> {
        @Override
        public Object visitClass(ClassTree node, Trees trees) {
            if (node.getSimpleName().equals(typeElement.getSimpleName())) {
                if (classTree != null)
                    throw new IllegalArgumentException("Several matches:\n" + classTree + "\nAND\n" + node);
                classTree = node;
            }
            return super.visitClass(node, trees);
        }
    }

    public TypeElementWrapper(Trees trees, TypeElement typeElement) {
        this.typeElement = typeElement;

        CodeAnalyzerTreeScanner codeScanner = new CodeAnalyzerTreeScanner();
        TreePath tp = trees.getPath(typeElement);
        compilationUnitTree = tp.getCompilationUnit();
        codeScanner.scan(tp, trees);
        if (classTree == null) throw new NullPointerException();

        ImmutableList.Builder<MethodTreeWrapper> methodTreeBuilder = ImmutableList.builder();
        ImmutableList.Builder<ConstructorTreeWrapper> constructorTreeBuilder = ImmutableList.builder();
        ImmutableList.Builder<EnumConstantTreeWrapper> enumConstantTreeBuilder =
                ImmutableList.builder();
        ImmutableList.Builder<FieldTreeWrapper> fieldTreeBuilder = ImmutableList.builder();
        for (Tree tree : classTree.getMembers()) {
            if (tree instanceof MethodTree) {
                MethodTree methodDecl = (MethodTree) tree;
                ExecutableElement methodSymbol = (ExecutableElement) TreeUtil.getSymbol(methodDecl);
                if (ElementKind.METHOD.equals(methodSymbol.getKind())) {
                    if (isValidMethod(methodSymbol)) {
                        MethodTreeWrapper methodWrapper = new MethodTreeWrapper(this, methodDecl, null);
                        methodTreeBuilder.add(methodWrapper);
                    }
                } else if (ElementKind.CONSTRUCTOR.equals(methodSymbol.getKind())) {
                    ConstructorTreeWrapper constructorWrapper = new ConstructorTreeWrapper(this, methodDecl);
                    constructorTreeBuilder.add(constructorWrapper);
                }
            }
        }
        constructorTree = constructorTreeBuilder.build();
        for (Tree tree : classTree.getMembers()) {
            if (tree instanceof VariableTree) {
                VariableTree variableDecl = (VariableTree) tree;
                Element sym = TreeUtil.getSymbol(variableDecl);
                if (ElementKind.ENUM_CONSTANT.equals(sym.getKind())) {
                    enumConstantTreeBuilder.add(new EnumConstantTreeWrapper(this, variableDecl));
                } else if (ElementKind.FIELD.equals(sym.getKind())) {
                    fieldTreeBuilder.add(new FieldTreeWrapper(this, variableDecl));
                }
            }
        }
        enumConstantTree = enumConstantTreeBuilder.build();
        methodTree = methodTreeBuilder.build();
        fieldTree = fieldTreeBuilder.build();
        Set<String> genericParameterNames = null;
        for(ConstructorTreeWrapper constructorWrapper: constructorTree) {
            if (genericParameterNames == null) {
                genericParameterNames = new TreeSet<>(constructorWrapper.getGenericParameters());
            } else {
                String oldGenericParameterNames = toString(genericParameterNames);
                String newGenericParameterNames =
                        toString(new TreeSet<>(constructorWrapper.getGenericParameters()));
                if (!oldGenericParameterNames.equals(newGenericParameterNames)) {
                    throw new IllegalArgumentException(
                            "All constructors must have the same generic parameters: "
                                    + oldGenericParameterNames
                                    + "<>"
                                    + newGenericParameterNames
                                    + "(class="
                                    + typeElement.getQualifiedName()
                                    + ")");
                }
            }
        }
        if (genericParameterNames.isEmpty()) {
            throw new IllegalArgumentException(
                    "The class "
                            + typeElement.getQualifiedName()
                            + " must have at least one constructor which define a Generic Parameter");
        }
        this.genericParameterNames = genericParameterNames;
    }

    private String toString(Set<String> parameters) {
        return parameters.stream().collect(Collectors.joining(", "));
    }

    public List<ConstructorTreeWrapper> getConstructorTree() {
        return constructorTree;
    }

    private boolean isValidMethod(ExecutableElement it) {
        if ("values".equals(it.getSimpleName().toString())
                && it.getParameters().size() == 0
                && it.getModifiers().contains(Modifier.STATIC)) return false;

        if ("valueOf".equals(it.getSimpleName().toString())
                && it.getParameters().size() == 1
                && it.getModifiers().contains(Modifier.STATIC)) return false;

        return true;
    }

    public boolean isAbstract() {
        return getMethodTree().stream().anyMatch(it -> it.isAbstract());
    }

    public Set<String> getGenericParameterNames() {
        return genericParameterNames;
    }

    public List<InterfaceWrapper> getInterfaces() {
        return typeElement.getInterfaces().stream()
                .map(InterfaceWrapper::new)
                .collect(Collectors.toList());
    }

    public List<InterfaceTreeWrapper> getInterfaceTree() {
        return classTree.getImplementsClause().stream()
                .map(InterfaceTreeWrapper::new)
                .collect(Collectors.toList());
    }

    public List<?> getImports() {
        return compilationUnitTree.getImports();
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

    public ConstructorTreeWrapper findMatchingConstructor(
            List<ArgumentWrapper> arguments) {
        ConstructorTreeWrapper result = null;
        for (ConstructorTreeWrapper ctw : getConstructorTree()) {
            if (test(ctw.methodTree, arguments)) {
                if (result == null) result = ctw;
                else throw new IllegalArgumentException("Several constructor match for " + classTree.getSimpleName() + "(" + arguments.stream().map(Object::toString).collect(Collectors.joining(", ")) +")");
            }
        }
        if (result == null)
            throw new IllegalArgumentException("Cannot find suitable constructor for " + classTree.getSimpleName() + "(" + arguments.stream().map(Object::toString).collect(Collectors.joining(", ")) +")");
        return result;
    }

    private boolean test(MethodTree methodTree, List<ArgumentWrapper> arguments) {
        if (methodTree.getParameters().size() != arguments.size()) return false;
        // TODO Check types

        return true;
    }
}
