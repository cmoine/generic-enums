package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import org.cmoine.genericEnums.processor.util.TreeUtil;

public class ArgumentWrapper {
    final ExpressionTree expr;
    final boolean isClass;
    final String type;

    public ArgumentWrapper(ExpressionTree expr, int index) {
        this.expr = expr;
        isClass=expr instanceof MemberSelectTree && "class".equals(((MemberSelectTree) expr).getIdentifier().toString());
        if(isClass) {
            String type = ((MemberSelectTree) expr).getExpression().toString();
            type = TreeUtil.getBoxedClassName(type);
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
