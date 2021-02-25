package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.*;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.lang.model.element.ExecutableElement;
import java.util.List;
import java.util.stream.Collectors;

public class ConstructorWrapper {
    final TypeElementWrapper parent;
    private final ExecutableElement symbol;
    private final BlockTree body;

    // https://stackoverflow.com/questions/6373145/accessing-source-code-from-java-annotation-processor
    private static class CodeAnalyzerTreeScanner extends TreePathScanner<Object, Trees> {
        BlockTree body;

        @Override
        public Object visitMethod(MethodTree node, Trees trees) {
            if(node.getReturnType()==null) {
                body=node.getBody();
            }
            return super.visitMethod(node, trees);
        }
    }

    public ConstructorWrapper(TypeElementWrapper parent, ExecutableElement symbol) {
        this.parent = parent;
        this.symbol = symbol;
        CodeAnalyzerTreeScanner codeScanner = new CodeAnalyzerTreeScanner();
        TreePath tp = parent.trees.getPath(symbol.getEnclosingElement());

        codeScanner.scan(tp, parent.trees);
        body = codeScanner.body;
    }

    public List<?> getParameters() {
        return symbol.getParameters().stream()
                .map(it -> new ParameterWrapper(this, it))
                .collect(Collectors.toList());
    }

    public List<? extends StatementTree> getStatements() {
        return body.getStatements();
    }
}
