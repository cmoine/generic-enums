package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.VariableTree;
import org.cmoine.genericEnums.processor.util.TreeUtil;

import javax.lang.model.element.VariableElement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnumConstantTreeWrapper {
    private final VariableElement symbol;
    final TypeElementWrapper parent;
    private final NewClassTree newClassTree;
    private final List<ArgumentWrapper> arguments;
    private final ConstructorTreeWrapper constructor;
    private final List<ArgumentWrapper> ori;
    private Map<String, String> typeBinding;

    public EnumConstantTreeWrapper(TypeElementWrapper parent, VariableTree variableDecl) {
        this.parent = parent;
        this.symbol = (VariableElement) TreeUtil.getSymbol(variableDecl);

        newClassTree = (NewClassTree) variableDecl.getInitializer();
        ori = ArgumentWrapper.wrap(newClassTree.getArguments());
        constructor = parent.findMatchingConstructor(ori);
        if(constructor.getThisArguments().isEmpty()) {
            arguments = ori;
        } else {
            arguments = constructor.getThisArguments();
        }
    }

    public String getName() {
        return symbol.getSimpleName().toString();
    }

    public List<ArgumentWrapper> getArguments() {
        return arguments;
    }

    public List<String> getTypes() {
        return arguments.stream()
                .filter(it -> it.isClass)
                .map(it -> it.type)
                .collect(Collectors.toList());
    }

    public ClassBodyTreeWrapper getClassBody() {
        return newClassTree.getClassBody()==null ? null : new ClassBodyTreeWrapper(this, newClassTree.getClassBody());
    }

    public Map<String, String> getTypeBinding() {
        if(typeBinding==null) {
            Map<String, String> result = new LinkedHashMap<>();
            List<ArgumentWrapper> argumentOfInterest = ori.stream().filter(it -> it.isClass).collect(Collectors.toList());
            List<String> genericParameters = constructor.getGenericParameters();
            for (int i = 0; i < genericParameters.size(); i++) {
                ArgumentWrapper argumentWrapper = argumentOfInterest.get(i);
                String genericParameterName = genericParameters.get(i);
                result.put(genericParameterName, argumentWrapper.type);
            }
            typeBinding=result;
        }
        return typeBinding;
    }
}
