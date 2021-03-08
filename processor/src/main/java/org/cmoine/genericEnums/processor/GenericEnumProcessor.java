package org.cmoine.genericEnums.processor;

import com.google.auto.service.AutoService;
import com.google.common.collect.Maps;
import com.sun.source.util.Trees;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;
import org.cmoine.genericEnums.GenericEnum;
import org.cmoine.genericEnums.processor.model.TypeElementWrapper;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes("org.cmoine.genericEnums.GenericEnum")
public class GenericEnumProcessor extends AbstractProcessor {
    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement te : annotations) {
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(te);
            for (Element elt : elementsAnnotatedWith) {
                generate((TypeElement) elt);
            }
        }
        return true;
    }

    private void generate(TypeElement typeElement) {
        try {
            dump(typeElement, 0);
            String pkgName = getPackageName(typeElement);

            String className = typeElement.getAnnotation(GenericEnum.class).name().replace("%", typeElement.getSimpleName());
            // String className = typeElement.getSimpleName().toString() + "Ext";
            JavaFileObject sourceFile = processingEnv.getFiler()
                    .createSourceFile(pkgName + "." + className,
                            typeElement);
            try (Writer writer = sourceFile.openWriter()) {
                Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
                cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(),
                        getClass().getPackage().getName().replace('.', '/'));
                Template template = cfg.getTemplate("template.ftl");
                template.createProcessingEnvironment(new TemplateData(getClass(),
                        processingEnv, pkgName, className, new TypeElementWrapper(trees, typeElement)), writer).process();
            } catch (TemplateException e) {
                print(e);
            }
        } catch (IOException e) {
            print(e);
        }
    }

    private void dump(Element element, int tabCount) {
        char[] tabs = new char[tabCount * 2];
        Arrays.fill(tabs, ' ');
        Map<String, Object> attributes = new TreeMap<>();
        List<Element> children = new ArrayList<>();
        children.addAll(element.getEnclosedElements());
        attributes.put("simpleName", element.getSimpleName());
        if (element instanceof ExecutableElement) {
            ExecutableElement executableElement = (ExecutableElement) element;
            children.addAll(executableElement.getTypeParameters());
            children.addAll(executableElement.getParameters());
        }
//        if (element instanceof Symbol) {
//            Symbol symbol = (Symbol) element;
//            attributes.put("type", symbol.type);
            if (element instanceof VariableElement) {
                VariableElement varSymbol = (VariableElement) element;
//                attributes.put("declarationAttributes", varSymbol.getDeclarationAttributes());
//                attributes.put("rawAttributes", varSymbol.getRawAttributes());
//                attributes.put("classInitTypeAttributes", varSymbol.getClassInitTypeAttributes());
//                attributes.put("constValue", varSymbol.getConstValue());
                // attributes.put("type", varSymbol.getTypeParameters());
                // if(varSymbol.getConstantValue()!=null)
                attributes.put("constantValue", varSymbol.getConstantValue());
//                if(ElementKind.ENUM_CONSTANT.equals(element.getKind())) {
//                    CodeAnalyzerTreeScanner codeScanner = new CodeAnalyzerTreeScanner();
//                    TreePath tp = this.trees.getPath(varSymbol.getEnclosingElement());
//
//                    codeScanner.setFieldName(element.getSimpleName().toString());
//                    codeScanner.scan(tp, this.trees);
//                    String fieldInitializer = ((NewClassTree)codeScanner.getFieldInitializer()).getArguments();
//                    attributes.put("fieldInitializer", fieldInitializer);
//                }
//                Type type = varSymbol.asType();
//                if(type!=null) {
//                    Element typeElement = processingEnv.getTypeUtils().asElement(type);
//                    if(typeElement!=null)
//                        children.add(typeElement);
//                }
//            } else if (symbol instanceof MethodSymbol) {
//                Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) symbol;
//                children.addAll(methodSymbol.getParameters());
            }
//        }

        System.out.println(new String(tabs)
                + element.getClass().getSimpleName()
                + "(" + attributes.entrySet().stream()
                .map(entry -> Maps.immutableEntry(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString()))
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", ")) + ")");
        children.forEach(child -> dump(child, tabCount + 1));
    }

//    private Map<String, Object> getAttributes(Element element) {
//        Map<String, Object> map=new TreeMap<>();
//        map.put("simpleName", element.getSimpleName());
//        if(element instanceof Symbol) {
//            map.put("type", ((Symbol) element).type);
//            chi
//        }
//        return map;
//    }
//
//    private List<? extends Element> children(Element element) {
//        if(element instanceof Symbol.MethodSymbol) {
//            return ((Symbol.MethodSymbol)element).getParameters();
//        }
//
//        return element.getEnclosedElements();
//    }

    private void print(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, stringWriter.toString());
    }

    private static String getPackageName(TypeElement te) {
        String qualifiedName = te.getQualifiedName().toString();
        int index = qualifiedName.lastIndexOf('.');
        if (index > 0)
            return qualifiedName.substring(0, index);
        return qualifiedName;
    }
}
