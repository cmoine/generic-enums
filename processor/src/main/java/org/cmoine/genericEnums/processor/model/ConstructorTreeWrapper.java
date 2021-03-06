package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.*;
import org.cmoine.genericEnums.GenericEnumConstants;
import org.cmoine.genericEnums.processor.util.TreeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConstructorTreeWrapper extends AbstractMethodTreeWrapper {

    private final MethodInvocationTree thisInitializer;

    public ConstructorTreeWrapper(TypeElementWrapper parent, MethodTree methodDecl) {
        super(parent, methodDecl);
        thisInitializer=thisInitializer();
    }

    private MethodInvocationTree thisInitializer() {
        if(methodTree.getBody().getStatements().isEmpty())
            return null;

        StatementTree firstStatement = methodTree.getBody().getStatements().get(0);
        if(!(firstStatement instanceof ExpressionStatementTree))
            return null;

        ExpressionTree expr = ((ExpressionStatementTree) methodTree.getBody().getStatements().get(0)).getExpression();
        if(!(expr instanceof MethodInvocationTree))
            return null;

        MethodInvocationTree jcMethodInvocation = (MethodInvocationTree) expr;
        if(!jcMethodInvocation.toString().startsWith("this"))
            return null;

        return jcMethodInvocation;
    }

    public MethodInvocationTree getThisInitializer() {
        return thisInitializer;
    }

    public List<?> getParameters() {
        return methodTree.getParameters().stream()
                .map(it -> new ParameterTreeWrapper(parent, it, null))
                .collect(Collectors.toList());
    }

    public List<String> getGenericParameters() {
        List<String> result=new ArrayList<>();
        char genericParamName= GenericEnumConstants.GENERIC_NAME.charAt(0);
        List<? extends VariableTree> parameters = methodTree.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            VariableTree parameter = parameters.get(i);
            if(parameter.getType().toString().startsWith("Class")) {
                String annotation = TreeUtil.getGenericParamName(parameter.getModifiers());
                if (annotation != null) {
                    result.add(annotation);
                } else if (TreeUtil.getSymbol(parameter).asType().toString().startsWith(Class.class.getName())) {
                    result.add(Character.toString(genericParamName));
                    genericParamName++;
                }
            }
        }
        return result;
    }
}
