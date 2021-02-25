package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.ExpressionTree;
import com.sun.tools.javac.tree.JCTree;

public class ArgumentWrapper {
    private final EnumConstantWrapper parent;
    final ExpressionTree expr;
    final boolean isClass;

    public ArgumentWrapper(EnumConstantWrapper parent, ExpressionTree expr, int index) {
        this.parent = parent;
        this.expr = expr;
        isClass=expr instanceof JCTree.JCFieldAccess && "class".equals(((JCTree.JCFieldAccess) expr).name.toString());
    }

    @Override
    public String toString() {
        // boolean needCast=parent.parent.getConstructors().get(0).
        return // "/*" + expr.getClass().getSimpleName() + ":"
               // + (expr instanceof JCTree.JCFieldAccess ? ((JCTree.JCFieldAccess)expr).selected : "")
               // + (expr instanceof JCTree.JCFieldAccess ? ((JCTree.JCFieldAccess)expr).name : "")
               // + "*/" +
                expr.toString();
    }
}
