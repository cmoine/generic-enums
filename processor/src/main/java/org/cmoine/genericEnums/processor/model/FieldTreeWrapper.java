package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.VariableTree;
import org.cmoine.genericEnums.processor.util.TreeUtil;

public class FieldTreeWrapper {
    private final TypeElementWrapper typeElementWrapper;
    private final VariableTree variableDecl;

    public FieldTreeWrapper(TypeElementWrapper typeElementWrapper, VariableTree variableDecl) {
        this.typeElementWrapper = typeElementWrapper;
        this.variableDecl = variableDecl;
    }

    public String getModifiers() {
        return TreeUtil.toString(variableDecl.getModifiers());
    }

    public String getType() {
        String genericParamName = TreeUtil.getGenericParamName(variableDecl.getModifiers());
        if(genericParamName!=null)
            return genericParamName;
        else
            return variableDecl.getType().toString();
    }

    public String getName() {
        return variableDecl.getName().toString();
    }

    public ExpressionTree getInitializer() {
        return variableDecl.getInitializer();
    }
}
