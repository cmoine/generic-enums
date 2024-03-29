package org.cmoine.genericEnums.processor;

import com.google.auto.service.AutoService;
import com.google.common.collect.Maps;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
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
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        ProcessingEnvironment processingEnvUnwrapped=jbUnwrap(processingEnv);
        processingEnvUnwrapped=gradleUnwrap(processingEnvUnwrapped);
        if (!processingEnvUnwrapped.getClass().getName().equals("com.sun.tools.javac.processing.JavacProcessingEnvironment")) {
            throw new IllegalArgumentException(processingEnv.getClass().getName()
                    +"<>com.sun.tools.javac.processing.JavacProcessingEnvironment");
        }
        super.init(processingEnvUnwrapped);
        trees = Trees.instance(processingEnvUnwrapped);
    }

    private static ProcessingEnvironment gradleUnwrap(ProcessingEnvironment processingEnv) {
        try {
            Field field = processingEnv.getClass().getDeclaredField("delegate");
            field.setAccessible(true);
            processingEnv=(ProcessingEnvironment) field.get(processingEnv);
        } catch (Exception ignored) {
        }
        return processingEnv;
    }

    private static ProcessingEnvironment jbUnwrap(ProcessingEnvironment wrapper) {
        try {
            final Class<?> apiWrappers = wrapper.getClass().getClassLoader().loadClass("org.jetbrains.jps.javac.APIWrappers");
            final Method unwrapMethod = apiWrappers.getDeclaredMethod("unwrap", Class.class, Object.class);
            return (ProcessingEnvironment) unwrapMethod.invoke(null, ProcessingEnvironment.class, wrapper);
        }
        catch (Throwable ignored) {}
        return wrapper;
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
                cfg.setSharedVariable("instanceOf", new InstanceOfMethod());
                Template template = cfg.getTemplate("template.ftl");
                StringWriter source=new StringWriter();
                template.createProcessingEnvironment(new TemplateData(getClass(),
                        processingEnv, pkgName, className, new TypeElementWrapper(trees, typeElement)), writer).process();
                String formattedSource = new Formatter().formatSource(source.toString());
                writer.write(formattedSource);
            } catch (TemplateException | FormatterException e) {
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
        if (element instanceof VariableElement) {
            VariableElement varSymbol = (VariableElement) element;
            attributes.put("constantValue", varSymbol.getConstantValue());
        }

        System.out.println(new String(tabs)
                + element.getClass().getSimpleName()
                + "(" + attributes.entrySet().stream()
                .map(entry -> Maps.immutableEntry(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString()))
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", ")) + ")");
        children.forEach(child -> dump(child, tabCount + 1));
    }

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
