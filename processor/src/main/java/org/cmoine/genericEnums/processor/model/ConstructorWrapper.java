package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.StatementTree;
import com.sun.tools.javac.tree.JCTree;
import org.cmoine.genericEnums.GenericEnumParam;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.Set;
import java.util.TreeSet;

/**
 * @deprecated Replaced by {@link ConstructorTreeWrapper}
 */
@Deprecated
public class ConstructorWrapper extends ExecutableElementWrapper {
    private final JCTree.JCMethodInvocation thisInitializer;

    public ConstructorWrapper(TypeElementWrapper parent, ExecutableElement element) {
        super(parent, element);
        thisInitializer=thisInitializer();
    }

    private JCTree.JCMethodInvocation thisInitializer() {
        System.out.println(element);
        if(element.getKind() != ElementKind.CONSTRUCTOR)
            return null;

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

    public Set<String> getGenericParameterNames() {
        Set<String> result=new TreeSet<>();
        for(VariableElement variableElement: element.getParameters()) {
            GenericEnumParam annotation = variableElement.getAnnotation(GenericEnumParam.class);
            if(annotation !=null) {
                result.add(annotation.value());
            } else if(Class.class.getName().equals(variableElement.asType().toString())) {
                result.add(annotation.value());
            }
        }
        return result;
    }
}
