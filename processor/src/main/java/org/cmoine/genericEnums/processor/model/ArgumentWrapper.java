package org.cmoine.genericEnums.processor.model;

import com.google.common.primitives.Primitives;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import java.util.ArrayList;
import java.util.List;

public class ArgumentWrapper {
    final ExpressionTree expr;
    final boolean isClass;
    final String type;

    private ArgumentWrapper(ExpressionTree expr, int index) {
        this.expr = expr;
        isClass=expr instanceof MemberSelectTree && "class".equals(((MemberSelectTree) expr).getIdentifier().toString());
        if(isClass) {
            String type = ((MemberSelectTree) expr).getExpression().toString();
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

    public static List<ArgumentWrapper> wrap(List<? extends ExpressionTree> args) {
        List<ArgumentWrapper> arguments = new ArrayList<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            ExpressionTree et = args.get(i);
            arguments.add(new ArgumentWrapper(et, i));
        }
        return arguments;
    }
}
