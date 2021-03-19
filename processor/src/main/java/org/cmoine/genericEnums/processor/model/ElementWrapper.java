package org.cmoine.genericEnums.processor.model;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import java.util.stream.Collectors;

public class ElementWrapper<T extends Element> {
    protected final T element;

    public ElementWrapper(T element) {
        this.element = element;
    }

    public String getModifiers() {
        return element.getModifiers().stream().map(it -> it.toString()).collect(Collectors.joining(" "));
    }

    public Name getName() {
        return element.getSimpleName();
    }

    public String getType() {
        return element.asType().toString();
    }
}
