package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.MethodTree;
import org.cmoine.genericEnums.processor.util.TreeUtil;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.stream.Collectors;

public class MethodTreeWrapper extends AbstractMethodTreeWrapper {
    private final EnumConstantTreeWrapper enumConstantTreeWrapper;

    public MethodTreeWrapper(TypeElementWrapper parent, MethodTree methodTree, EnumConstantTreeWrapper enumConstantTreeWrapper) {
        super(parent, methodTree);
        this.enumConstantTreeWrapper = enumConstantTreeWrapper;
    }

    public boolean isAbstract() {
        return methodTree.getModifiers().getFlags().contains(Modifier.ABSTRACT);
    }

    public String getType() {
        return methodTree.getReturnType().toString();
    }

    public List<?> getParameters() {
        return methodTree.getParameters().stream()
                .map(it -> new ParameterTreeWrapper(parent, it, enumConstantTreeWrapper))
                .collect(Collectors.toList());
    }

    public String getReturnType() {
        String genericParamName = TreeUtil.getGenericParamType(methodTree.getModifiers(), enumConstantTreeWrapper);
        if(genericParamName!=null) {
            return genericParamName;
        } else {
            return methodTree.getReturnType().toString();
        }
    }
}
