package org.cmoine.genericEnums.processor;

import com.google.auto.service.AutoService;
import com.google.common.collect.Maps;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.sun.source.util.Trees;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.QualifiedNameable;
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
            final Map<TypeElement, Set<TypeElement>> groupedElements = groupElementsByContainer(roundEnv.getElementsAnnotatedWith(te));
            for (Map.Entry<TypeElement, Set<TypeElement>> entry : groupedElements.entrySet()) {
                generate(entry.getKey(), entry.getValue());
            }
        }
        return true;
    }

    /**
     * Collate annotated enum TypeElements by their top-level TypeElement. If the source enum is a top-level
     * (i.e. It's parent is a package) then its top-level element is itself.
     *
     * @param elementsAnnotatedWith A set of TypeElements annotated with {@link GenericEnum}.
     * @return A map of top-level TypeElements to source enum TypeElements.
     */
    private Map<TypeElement, Set<TypeElement>> groupElementsByContainer(
        Set<? extends Element> elementsAnnotatedWith) {
        final Map<TypeElement, Set<TypeElement>> groupedElements = new HashMap<>();

        for (Element element : elementsAnnotatedWith) {
            if (element.getKind() == ElementKind.ENUM) {
                if (element.getEnclosingElement() != null && element.getEnclosingElement().getKind() == ElementKind.CLASS) {
                    Set<TypeElement> elements = groupedElements.computeIfAbsent((TypeElement) element.getEnclosingElement(), (e) -> new LinkedHashSet<>());
                    elements.add((TypeElement) element);

                } else {
                    groupedElements.put((TypeElement) element,
                        new LinkedHashSet<>(Collections.singletonList((TypeElement) element)));
                }
            }
        }

        return groupedElements;
    }

    /**
     * Generate pseudo enum declarations using the specified top-level TypeElement and source enum TypeElements.
     *
     * @param topLevelTypeElement The source top-level TypeElement
     * @param enumElements The set of source enum TypeElements to process
     */
    private void generate(TypeElement topLevelTypeElement, Set<TypeElement> enumElements) {
        try {
            dump(topLevelTypeElement, 0);
            final String pkgName = getPackageName(topLevelTypeElement);
            final TypeElementWrapper typeElementWrapper = new TypeElementWrapper(trees, topLevelTypeElement);
            final JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(pkgName + "." + typeElementWrapper.getClassName(), topLevelTypeElement);
            final TemplateData dataModel = new TemplateData(getClass(),
                processingEnv,
                pkgName,
                typeElementWrapper,
                enumElements
                    .stream()
                    .map((el) -> new TypeElementWrapper(trees, el))
                    .collect(Collectors.toSet()));

            try (Writer writer = sourceFile.openWriter()) {
                Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
                cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(),
                        getClass().getPackage().getName().replace('.', '/'));
                cfg.setSharedVariable("instanceOf", new InstanceOfMethod());
                Template template = cfg.getTemplate("template.ftl");
                StringWriter source = new StringWriter();
                template.createProcessingEnvironment(dataModel, writer).process();
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

    /**
     * Get the name of the container package for the specified {@link QualifiedNameable}.
     *
     * @param qualifiedNameable the contained element
     * @return the name of the nearest parent package
     */
    private static String getPackageName(QualifiedNameable qualifiedNameable) {
        while (qualifiedNameable != null) {
            if (qualifiedNameable.getKind() == ElementKind.PACKAGE) {
                return qualifiedNameable.getQualifiedName().toString();
            }
            qualifiedNameable = (QualifiedNameable) qualifiedNameable.getEnclosingElement();
        }

        return "";
    }
}
