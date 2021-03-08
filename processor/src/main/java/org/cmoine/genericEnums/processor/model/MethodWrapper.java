package org.cmoine.genericEnums.processor.model;

import javax.lang.model.element.ExecutableElement;

public class MethodWrapper extends ExecutableElementWrapper{
    public MethodWrapper(TypeElementWrapper parent, ExecutableElement element) {
        super(parent, element);
    }


}
