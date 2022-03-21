package org.cmoine.genericEnums.processor.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.VariableTree;
import org.cmoine.genericEnums.processor.util.TreeUtil;

import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnumConstantTreeWrapper {
    private final VariableElement symbol;
    final TypeElementWrapper parent;
    private final NewClassTree newClassTree;
    private final ArrayList<ArgumentWrapper> arguments;
    private Map<String, String> typeBinding;

    public EnumConstantTreeWrapper(TypeElementWrapper parent, VariableTree variableDecl) {
        this.parent = parent;
        this.symbol = (VariableElement) TreeUtil.getSymbol(variableDecl);

        newClassTree = (NewClassTree) variableDecl.getInitializer();
        arguments = new ArrayList<>(newClassTree.getArguments().size());
        List<? extends ExpressionTree> fieldInitializerArguments = newClassTree.getArguments();
        for (int i = 0; i < fieldInitializerArguments.size(); i++) {
            ExpressionTree et = fieldInitializerArguments.get(i);
            arguments.add(new ArgumentWrapper(et, i));
        }
    }

    public String getName() {
        return symbol.getSimpleName().toString();
    }

    public List<ArgumentWrapper> getArguments() {
        return arguments;
    }

    public List<String> getTypes() {
        return new ArrayList<>(getTypeBinding().values());
    }

    public ClassBodyTreeWrapper getClassBody() {
        return newClassTree.getClassBody()==null ? null : new ClassBodyTreeWrapper(this, newClassTree.getClassBody());
    }

    public Map<String, String> getTypeBinding() {
        if(typeBinding==null) {
            Map<String, String> result = new LinkedHashMap<>();
            ConstructorTreeWrapper constructor = null;
            // TODO handle varargs
            for (ConstructorTreeWrapper ctw : parent.getConstructorTree()) {
                if (test(ctw.methodTree, arguments)) {
                    if (constructor == null)
                        constructor = ctw;
                    else
                        throw new IllegalArgumentException("Several constructor match for enum: " + parent.classTree.getSimpleName() + "." + getName());
                }
            }
            if (constructor == null)
                throw new IllegalArgumentException("Cannot find suitable constructor enum: " + parent.classTree.getSimpleName() + "." + getName());
            List<ArgumentWrapper> argumentOfInterest = arguments.stream().filter(it -> it.isClass).collect(Collectors.toList());
            List<String> genericParameters = constructor.getGenericParameters();
            if (argumentOfInterest.size() != genericParameters.size()) {
                // The constructor doesn't have the correct number of Class parameters, see if it has GenericEnumConstructorParam annotations.
                result = Streams.zip(genericParameters.stream(), constructor.getGenericParametersTypes().stream(), Maps::immutableEntry)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            } else {
                for (int i = 0; i < genericParameters.size(); i++) {
                  ArgumentWrapper argumentWrapper = argumentOfInterest.get(i);
                  String genericParameterName = genericParameters.get(i);
                  result.put(genericParameterName, argumentWrapper.type);
                }
            }
            typeBinding=result;
        }
        return typeBinding;
    }

    private boolean test(MethodTree methodTree, ArrayList<ArgumentWrapper> arguments) {
        if(methodTree.getParameters().size()!=arguments.size())
            return false;
        // TODO Check types

        return true;
    }
}
