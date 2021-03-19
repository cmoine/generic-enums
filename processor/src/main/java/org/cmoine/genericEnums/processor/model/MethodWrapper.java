package org.cmoine.genericEnums.processor.model;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

/**
 * @deprecated Replaced by {@link MethodTreeWrapper}
 *
 */
@Deprecated
public class MethodWrapper extends ExecutableElementWrapper{
    public MethodWrapper(TypeElementWrapper parent, ExecutableElement element) {
        super(parent, element);
    }

    public boolean isAbstract() {
        return element.getModifiers().contains(Modifier.ABSTRACT);
    }
}
