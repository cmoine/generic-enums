package org.cmoine.genericEnums.processor.util;

import com.sun.source.tree.*;
import org.cmoine.genericEnums.GenericEnumConstants;
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
}
