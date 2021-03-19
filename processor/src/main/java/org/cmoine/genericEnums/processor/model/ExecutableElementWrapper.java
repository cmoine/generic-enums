package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import org.cmoine.genericEnums.GenericEnumParam;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @deprecated Replaced by {@link AbstractMethodTreeWrapper}
 */
@Deprecated
public abstract class ExecutableElementWrapper extends ElementWrapper<ExecutableElement> {
    final TypeElementWrapper parent;
    MethodTree methodTree;

    // https://stackoverflow.com/questions/6373145/accessing-source-code-from-java-annotation-processor
    private class CodeAnalyzerTreeScanner extends TreePathScanner<Object, Trees> {
        @Override
        public Object visitMethod(MethodTree node, Trees trees) {
            if(test(node)) {
                if(methodTree!=null)
                    throw new InternalError("Several matches:\n"+methodTree+"\nAND\n"+node);
                methodTree=node;
            }
            return super.visitMethod(node, trees);
        }

        private boolean test(MethodTree node) {
            if(((JCTree.JCMethodDecl)node).sym!=element)
                return false;

            boolean isMethod = (node.getKind() == Tree.Kind.METHOD) && node.getReturnType()!=null;
            boolean isConstructor = (node.getKind() == Tree.Kind.METHOD) && node.getReturnType()==null;
            boolean isMethod2 = element.getKind() == ElementKind.METHOD;
            boolean isConstructor2 = element.getKind() == ElementKind.CONSTRUCTOR;
            if(isMethod != isMethod2 && isConstructor != isConstructor2)
                return false;
            if(isMethod && !node.getName().toString().equals(element.getSimpleName().toString()))
                return false;
            if(node.getParameters().size()!=element.getParameters().size())
                return false;

            return true;
        }
    }

    public ExecutableElementWrapper(TypeElementWrapper parent, ExecutableElement element/*, Predicate<MethodTree> matcher*/) {
        super(element);
        this.parent = parent;
        CodeAnalyzerTreeScanner codeScanner = new CodeAnalyzerTreeScanner();
        if(parent==null)
            throw new NullPointerException();
        if(parent.trees==null)
            throw new NullPointerException();
        if(element==null)
            throw new NullPointerException();
        TreePath tp = parent.trees.getPath(element.getEnclosingElement());

        codeScanner.scan(tp, parent.trees);
    }

    public List<?> getParameters() {
        return element.getParameters().stream()
                .map(it -> new ParameterWrapper(this, it))
                .collect(Collectors.toList());
    }

    public List<?> getStatements() {
        return methodTree.getBody().getStatements();
    }

    public String getReturnType() {
        GenericEnumParam annotation = element.getAnnotation(GenericEnumParam.class);
        if(annotation !=null) {
            return annotation.value();
        } else {
            return element.getReturnType().toString();
        }
    }
}
