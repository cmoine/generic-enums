package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.MethodTree;
import org.cmoine.genericEnums.processor.util.TreeUtil;

import javax.lang.model.element.Name;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractMethodTreeWrapper {
    protected final TypeElementWrapper parent;
    protected final MethodTree methodTree;

    public AbstractMethodTreeWrapper(TypeElementWrapper parent, MethodTree methodTree) {
        this.parent = parent;
        this.methodTree = methodTree;
    }

    public String getModifiers() {
        return TreeUtil.toString(methodTree.getModifiers());
    }

    public Name getName() {
        return methodTree.getName();
    }

    public List<?> getStatements() {
        return methodTree.getBody().getStatements().stream()
                .map(it -> new StatementTreeWrapper(it))
                .collect(Collectors.toList());
    }
}
