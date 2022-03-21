package org.cmoine.genericEnums.processor.util;

import com.google.common.primitives.Primitives;
import com.sun.source.tree.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import org.cmoine.genericEnums.GenericEnumConstants;
import org.cmoine.genericEnums.GenericEnumConstructorParam;
import org.cmoine.genericEnums.GenericEnumConstructorParams;
import org.cmoine.genericEnums.GenericEnumParam;
import org.cmoine.genericEnums.processor.model.EnumConstantTreeWrapper;

import javax.lang.model.element.Element;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TreeUtil {
    private TreeUtil() {
    }

    public static String toString(ModifiersTree modifiersTree) {
        return modifiersTree==null ? "" : modifiersTree.getFlags().stream().map(it -> it.toString()).collect(Collectors.joining(" "));
    }

    public static String getGenericParamType(ModifiersTree modifiersTree, EnumConstantTreeWrapper enumConstantTreeWrapper) {
        String genericParamName = getGenericParamName(modifiersTree);
        String type = enumConstantTreeWrapper==null ? null : enumConstantTreeWrapper.getTypeBinding().get(genericParamName);
        if(type!=null)
            return type;

        if(genericParamName!=null)
            return genericParamName + (enumConstantTreeWrapper==null ?
                    "" :
                    "/*" + genericParamName + ":" + enumConstantTreeWrapper.getTypeBinding()
                            .entrySet().stream()
                            .map(it -> it.getKey()+"->"+it.getValue())
                            .collect(Collectors.joining(", ")) + "*/");

        return null;
    }

    public static String getGenericParamName(ModifiersTree modifiersTree) {
        return getGenericParamName(modifiersTree.getAnnotations());
    }

    public static String getGenericParamName(List<? extends AnnotationTree> annotations) {
        Optional<? extends AnnotationTree> first = annotations.stream().filter(it -> GenericEnumParam.class.getSimpleName().equals(it.getAnnotationType().toString())).findFirst();
        if(first.isPresent()) {
            for(ExpressionTree arg: first.get().getArguments()) {
                if(arg instanceof LiteralTree) {
                    return ((LiteralTree)arg).getValue().toString();
                } else if(arg instanceof AssignmentTree) {
                    AssignmentTree assignmentTree= (AssignmentTree) arg;
                    if("value".equals(assignmentTree.getVariable().toString())) {
                        return ((LiteralTree)assignmentTree.getExpression()).getValue().toString();
                    }
                }
            }
            return GenericEnumConstants.GENERIC_NAME;
        } else {
            return null;
        }
    }

    public static Element getSymbol(Tree tree) {
        try {
            Field sym = tree.getClass().getDeclaredField("sym");
            return (Element) sym.get(tree);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

  /**
   * Get the list of 'key' values from the specified list of annotations. Loop through the specified
   * annotation extracting the values associated with 'key' from any
   * &#64;GenericEnumConstructorParam annotations.
   *
   * @param annotations the annotations to search
   * @param key the name of the field to collate
   * @return The list of values, or an empty list if none are found.
   */
  public static List<String> getGenericConstructorParams(
      List<? extends AnnotationTree> annotations, String key) {
        List<? extends AnnotationTree> annotationTrees = annotations.stream().filter(it -> GenericEnumConstructorParam.class.getSimpleName().equals(it.getAnnotationType().toString())).collect(
            Collectors.toList());

        if (annotationTrees.isEmpty()) {
            final Optional<? extends AnnotationTree> first = annotations.stream().filter(
                it -> GenericEnumConstructorParams.class.getSimpleName()
                    .equals(it.getAnnotationType().toString())).findFirst();

            if (first.isPresent()) {
                AssignmentTree assignmentTree = (AssignmentTree) first.get().getArguments().get(0);

                if (assignmentTree.getExpression() instanceof AnnotationTree) {
                    annotationTrees = Collections.singletonList((AnnotationTree) assignmentTree.getExpression());

                } else if (assignmentTree.getExpression() instanceof NewArrayTree) {
                    NewArrayTree newArrayTree = (NewArrayTree)assignmentTree.getExpression();
                    annotationTrees = newArrayTree.getInitializers()
                        .stream()
                        .map(AnnotationTree.class::cast)
                        .collect(Collectors.toList());
                }
            }
        }

        final List<String> arguments = new ArrayList<>();
        char name = 'T';
        for (AnnotationTree annotationTree : annotationTrees) {
            boolean argumentAdded = false;

            for(ExpressionTree arg: annotationTree.getArguments()) {
                if (arg instanceof LiteralTree) {
                    LiteralTree literalTree = (LiteralTree) arg;
                    arguments.add(literalTree.getValue().toString());
                    argumentAdded = true;

                } else if (arg instanceof AssignmentTree) {
                    AssignmentTree assignmentTree= (AssignmentTree) arg;

                    if(key.equals(assignmentTree.getVariable().toString())) {
                        if (assignmentTree.getExpression() instanceof LiteralTree) {
                            LiteralTree literalTree = (LiteralTree)assignmentTree.getExpression();
                            arguments.add(literalTree.getValue().toString());
                            argumentAdded = true;

                        } else if (assignmentTree.getExpression() instanceof MemberSelectTree) {
                            MemberSelectTree memberSelectTree = (MemberSelectTree) assignmentTree.getExpression();
                            String type = memberSelectTree.getExpression().toString();
                            arguments.add(getBoxedClassName(type));
                            argumentAdded = true;
                        }
                    }
                }
            }

            if (!argumentAdded && "name".equals(key)) {
                arguments.add(String.valueOf(name));
                name++;
            }
        }

        return arguments;
    }

    /**
     * Does the specified list of annotations contains the specified annotationClass?
     *
     * @param annotations the list to check
     * @param annotationClass the annotation class to find
     * @return <code>true</code> if the list contains the specified annotation, <code>false</code> otherwise.
     */
    public static <T extends Annotation> boolean hasAnnotation(List<? extends AnnotationTree> annotations, Class<T> annotationClass) {
        Optional<? extends AnnotationTree> first = annotations.stream().filter(it -> annotationClass.getSimpleName().equals(it.getAnnotationType().toString())).findFirst();

        return first.isPresent();
    }

  /**
   * Get the boxed class name for the specified type. If type does not represent a primitive type,
   * then the original value is returned.
   *
   * @param type A simple name of a class
   * @return The boxed class name of type, or type if it is not a primitive.
   */
   public static String getBoxedClassName(final String type) {
        for(Class<?> clazz: Primitives.allPrimitiveTypes()) {
            if(clazz.toString().equals(type)) {
                return Primitives.wrap(clazz).getSimpleName();
            }
        }
        return type;
    }
}
