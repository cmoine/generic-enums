package org.cmoine.genericEnums.processor.model;

import com.google.common.primitives.Primitives;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.tools.javac.tree.JCTree;

public class ArgumentWrapper {
    final ExpressionTree expr;
    final boolean isClass;
    final String type;

    public ArgumentWrapper(ExpressionTree expr, int index) {
        this.expr = expr;
        isClass=expr instanceof MemberSelectTree && "class".equals(((MemberSelectTree) expr).getIdentifier().toString());
        if(isClass) {
            String type = ((JCTree.JCFieldAccess) expr).selected.toString();
            for(Class<?> clazz: Primitives.allPrimitiveTypes()) {
                if(clazz.toString().equals(type)) {
                    type =Primitives.wrap(clazz).getSimpleName();
                    break;
                }
            }
            this.type=type;
        } else {
            this.type=null;
        }
    }

    @Override
    public String toString() {
        return expr.toString();
    }
}
