package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;

import java.util.ArrayList;
import java.util.List;

public class ClassBodyTreeWrapper {
    private final List<MethodTreeWrapper> methods=new ArrayList<>();
    private final List<FieldTreeWrapper> fields=new ArrayList<>();

    public ClassBodyTreeWrapper(EnumConstantTreeWrapper parent, ClassTree classBody) {
        classBody.getMembers().forEach(m -> {
            if(m instanceof MethodTree) {
                methods.add(new MethodTreeWrapper(parent.parent, (MethodTree) m, parent));
            } else if(m instanceof VariableTree) {
                fields.add(new FieldTreeWrapper(parent.parent, (VariableTree) m));
            }
        });
    }

    public List<MethodTreeWrapper> getMethods() {
        return methods;
    }

    public List<FieldTreeWrapper> getFields() {
        return fields;
    }
}
