package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import org.cmoine.genericEnums.processor.util.TreeUtil;

import java.util.stream.Collectors;

public class InterfaceTreeWrapper {
    private final Tree tree;

    public InterfaceTreeWrapper(Tree tree) {
        this.tree = tree;
    }

    @Override
    public String toString() {
        return toString(tree);
    }

    private static String toString(Tree tree) {
        if(tree instanceof ParameterizedTypeTree) {
            ParameterizedTypeTree ptt= (ParameterizedTypeTree) tree;
            return ptt.getType() + "<" + ptt.getTypeArguments().stream().map(InterfaceTreeWrapper::toString).collect(Collectors.joining(", ")) + ">";
        } else if(tree instanceof AnnotatedTypeTree) {
            AnnotatedTypeTree att= (AnnotatedTypeTree) tree;
            String genericParamName = TreeUtil.getGenericParamName(att.getAnnotations());
            if(genericParamName!=null)
                return genericParamName;
        }
        return tree.getClass().toString();
//        DeclaredType declaredType= (DeclaredType) typeMirror;
//        StringBuilder buf = new StringBuilder();
//        System.out.println("> " + declaredType.getAnnotationMirrors().stream().map(it -> it.toString()).collect(Collectors.joining(", ")));
//        Optional<? extends AnnotationMirror> annotation = declaredType.getAnnotationMirrors().stream()
//                .filter(it -> it.toString().startsWith("@" + GenericEnumParam.class.getName())).findFirst();
//        if(annotation.isPresent()) {
//            Optional<String> first = annotation.get().getElementValues().entrySet().stream()
//                    .filter(it -> "value()".equals(it.getKey().toString()))
//                    .map(it -> Objects.toString(it.getValue().getValue()))
//                    .findFirst();
//            String value=first.isPresent() ? first.get() : GenericEnumConstants.GENERIC_NAME;
//            buf.append(value+ "/*" + annotation.get().getElementValues().entrySet().stream().map(it -> it.getKey()+"="+it.getValue()).collect(Collectors.joining(", ")) + "*/");
//        } else {
//            buf.append(((Symbol) declaredType.asElement()).getQualifiedName());
//        }
//
//        if (!declaredType.getTypeArguments().isEmpty()) {
//            buf.append('<');
//            buf.append(declaredType.getTypeArguments().stream().map(InterfaceWrapper::toString).collect(Collectors.joining(", ")));
//            buf.append('>');
//        }
//        return buf.toString();
    }
}
