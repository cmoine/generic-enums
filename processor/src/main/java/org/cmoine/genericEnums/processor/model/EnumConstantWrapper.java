package org.cmoine.genericEnums.processor.model;

import com.google.common.primitives.Primitives;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;

import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnumConstantWrapper {
    final TypeElementWrapper parent;
    private final VariableElement symbol;
    private final List<ArgumentWrapper> arguments;

    // https://stackoverflow.com/questions/6373145/accessing-source-code-from-java-annotation-processor
    private static class CodeAnalyzerTreeScanner extends TreePathScanner<Object, Trees> {
        private String fieldName;

        private ExpressionTree fieldInitializer;

        @Override
        public Object visitVariable(VariableTree variableTree, Trees trees) {
            if (variableTree.getName().toString().equals(this.fieldName)) {
                this.fieldInitializer = variableTree.getInitializer();
            }

            return super.visitVariable(variableTree, trees);
        }
    }

    public EnumConstantWrapper(TypeElementWrapper parent, VariableElement symbol) {
        this.parent = parent;
        this.symbol = symbol;
        CodeAnalyzerTreeScanner codeScanner = new CodeAnalyzerTreeScanner();
        TreePath tp = parent.trees.getPath(symbol.getEnclosingElement());

        codeScanner.fieldName=getName();
        codeScanner.scan(tp, parent.trees);
        NewClassTree fieldInitializer = (NewClassTree) codeScanner.fieldInitializer;
        arguments = new ArrayList<>(fieldInitializer.getArguments().size());
        List<? extends ExpressionTree> fieldInitializerArguments = fieldInitializer.getArguments();
        for (int i = 0; i < fieldInitializerArguments.size(); i++) {
            ExpressionTree et = fieldInitializerArguments.get(i);
            arguments.add(new ArgumentWrapper(this, et, i));
        }
    }

    public String getName() {
        return symbol.getSimpleName().toString();
    }

    public List<ArgumentWrapper> getArguments() {
        return arguments;
    }

    public String getType() {
        // return Primitives.allPrimitiveTypes().stream().map(Objects::toString).collect(Collectors.joining(", "));
        String type = arguments.stream()
                .filter(it -> it.isClass)
                .map(it -> ((JCTree.JCFieldAccess) it.expr).selected.toString())
                .findFirst().orElseThrow(IllegalArgumentException::new);
        // Handle primitive types
        for(Class<?> clazz: Primitives.allPrimitiveTypes()) {
            if(clazz.toString().equals(type)) {
                return Primitives.wrap(clazz).getSimpleName();
            }
        }
        return type;
    }
}
