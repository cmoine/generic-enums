package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.*;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import org.cmoine.genericEnums.GenericEnumParam;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MethodWrapper extends ElementWrapper<ExecutableElement> {
    final TypeElementWrapper parent;
    final BlockTree body;

    // https://stackoverflow.com/questions/6373145/accessing-source-code-from-java-annotation-processor
    private static class CodeAnalyzerTreeScanner extends TreePathScanner<Object, Trees> {
        private final Predicate<MethodTree> matcher;
        BlockTree body;

        public CodeAnalyzerTreeScanner(Predicate<MethodTree> matcher) {
            this.matcher = matcher;
        }

        @Override
        public Object visitMethod(MethodTree node, Trees trees) {
            if(matcher.test(node)) {
                body=node.getBody();
            }
            return super.visitMethod(node, trees);
        }
    }

    public MethodWrapper(TypeElementWrapper parent, ExecutableElement symbol, Predicate<MethodTree> matcher) {
        super(symbol);
        this.parent = parent;
        CodeAnalyzerTreeScanner codeScanner = new CodeAnalyzerTreeScanner(matcher);
        TreePath tp = parent.trees.getPath(symbol.getEnclosingElement());

        codeScanner.scan(tp, parent.trees);
        body = codeScanner.body;
    }

    public List<?> getParameters() {
        return element.getParameters().stream()
                .map(it -> new ParameterWrapper(this, it))
                .collect(Collectors.toList());
    }

    public List<? extends StatementTree> getStatements() {
        return body.getStatements();
    }

    public String getReturnType() {
        if(element.getAnnotation(GenericEnumParam.class)!=null) {
            return parent.getGenericParameterName();
        } else {
            return element.getReturnType().toString();
        }
    }
}
