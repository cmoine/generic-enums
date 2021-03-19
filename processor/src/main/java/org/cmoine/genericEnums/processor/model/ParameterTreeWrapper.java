package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.VariableTree;
import org.cmoine.genericEnums.processor.util.TreeUtil;

public class ParameterTreeWrapper {
    private final TypeElementWrapper parent;
    private final VariableTree variableTree;
    private final EnumConstantTreeWrapper constructorTreeWrapper;

    public ParameterTreeWrapper(TypeElementWrapper parent, VariableTree variableTree, EnumConstantTreeWrapper enumConstantTreeWrapper) {
        this.parent = parent;
        this.variableTree = variableTree;
        this.constructorTreeWrapper = enumConstantTreeWrapper;
    }

    public String getName() {
        return variableTree.getName().toString();
    }

    public String getType() {
        String genericParamName = TreeUtil.getGenericParamType(variableTree.getModifiers(), constructorTreeWrapper);
        if(genericParamName!=null) {
            return genericParamName;
        } else {
            return variableTree.getType().toString();
        }
    }
}
