package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.*;
import org.cmoine.genericEnums.GenericEnumConstants;
import org.cmoine.genericEnums.processor.util.TreeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class ConstructorTreeWrapper extends AbstractMethodTreeWrapper {

  private final MethodInvocationTree thisInitializer;

  private List<String> genericParameters;

  private List<ArgumentWrapper> thisArguments;

  public ConstructorTreeWrapper(TypeElementWrapper parent, MethodTree methodDecl) {
    super(parent, methodDecl);
    thisInitializer = thisInitializer();
    // Initialize GenericParameters
  }

  private MethodInvocationTree thisInitializer() {
    if (methodTree.getBody().getStatements().isEmpty()) return null;

    StatementTree firstStatement = methodTree.getBody().getStatements().get(0);
    if (!(firstStatement instanceof ExpressionStatementTree)) return null;

    ExpressionTree expr =
        ((ExpressionStatementTree) methodTree.getBody().getStatements().get(0)).getExpression();
    if (!(expr instanceof MethodInvocationTree)) return null;

    MethodInvocationTree jcMethodInvocation = (MethodInvocationTree) expr;
    if (!jcMethodInvocation.toString().startsWith("this")) return null;

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
    initializeGenericParameters();
    return genericParameters;
  }

  public List<ArgumentWrapper> getThisArguments() {
    initializeGenericParameters();
    return thisArguments;
  }

  private void initializeGenericParameters() {
    List<String> tmp = new ArrayList<>();
    char genericParamName = GenericEnumConstants.GENERIC_NAME.charAt(0);
    List<? extends VariableTree> parameters = methodTree.getParameters();
    boolean genericParameterFound=false;
    for (int i = 0; i < parameters.size(); i++) {
      VariableTree parameter = parameters.get(i);
      if (parameter.getType().toString().startsWith("Class")) {
        String annotation = TreeUtil.getGenericParamName(parameter.getModifiers());
        if (annotation != null) {
          tmp.add(annotation);
        } else if (TreeUtil.getSymbol(parameter)
                .asType()
                .toString()
                .startsWith(Class.class.getName())) {
          tmp.add(Character.toString(genericParamName));
          genericParamName++;
          genericParameterFound=true;
        }
      }
    }
    if((!genericParameterFound) && getThisInitializer()!=null) {
      // Maybe we can infer from this(..)
      thisArguments = ArgumentWrapper.wrap(getThisInitializer().getArguments());
      ConstructorTreeWrapper matchingConstructor = parent.findMatchingConstructor(
              thisArguments);
      genericParameters = matchingConstructor.getGenericParameters();
    } else {
      genericParameters = tmp;
      thisArguments=emptyList();
    }
  }
}
