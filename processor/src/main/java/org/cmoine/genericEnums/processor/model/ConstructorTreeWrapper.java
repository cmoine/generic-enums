package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import org.cmoine.genericEnums.GenericEnumConstants;
import org.cmoine.genericEnums.processor.util.TreeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConstructorTreeWrapper extends AbstractMethodTreeWrapper {

    private final JCTree.JCMethodInvocation thisInitializer;

    public ConstructorTreeWrapper(TypeElementWrapper parent, MethodTree methodDecl) {
        super(parent, methodDecl);
        thisInitializer=thisInitializer();
    }

    private JCTree.JCMethodInvocation thisInitializer() {
        if(methodTree.getBody().getStatements().isEmpty())
            return null;

        StatementTree firstStatement = methodTree.getBody().getStatements().get(0);
        if(!(firstStatement instanceof JCTree.JCExpressionStatement))
            return null;

        JCTree.JCExpression expr = ((JCTree.JCExpressionStatement) methodTree.getBody().getStatements().get(0)).expr;
        if(!(expr instanceof JCTree.JCMethodInvocation))
            return null;

        JCTree.JCMethodInvocation jcMethodInvocation = (JCTree.JCMethodInvocation) expr;
        if(!jcMethodInvocation.toString().startsWith("this"))
            return null;

        return jcMethodInvocation;
    }

    public JCTree.JCMethodInvocation getThisInitializer() {
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
//        List<? extends TypeParameterTree> typeParameters = methodTree.getTypeParameters();
        List<? extends VariableTree> parameters = methodTree.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            VariableTree parameter = parameters.get(i);
//            TypeParameterTree parameterType = typeParameters.get(i);
            if(parameter.getType().toString().startsWith("Class")) {
                System.out.println("type=" + parameter.getType());
                String annotation = TreeUtil.getGenericParamName(parameter.getModifiers());
                if (annotation != null) {
                    result.add(annotation);
                } else if (((JCTree.JCVariableDecl) parameter).sym.asType().toString().startsWith(Class.class.getName())) {
                    result.add(Character.toString(genericParamName));
                    genericParamName++;
                }
            }
        }
        return result;
    }
}
