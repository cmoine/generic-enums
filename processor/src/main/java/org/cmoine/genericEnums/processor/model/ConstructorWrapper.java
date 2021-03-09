package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.StatementTree;
import com.sun.tools.javac.tree.JCTree;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

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

    //    public List<> getParameters() {
//        return thisInitializer.args;
//    }
//
//    public boolean isAlreadyInvokeThis() {
//        return thisInitializer!=null;
//        // return ((JCTree.JCMethodInvocation)expr).toString().startsWith("this");
//    }

}
