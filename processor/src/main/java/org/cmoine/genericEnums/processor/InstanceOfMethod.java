package org.cmoine.genericEnums.processor;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.util.List;

// https://stackoverflow.com/a/33246066/5846877
public class InstanceOfMethod implements TemplateMethodModelEx {
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 2) {
            throw new TemplateModelException("Wrong arguments for method 'instanceOf'. Method has two required parameters: object and class");
        } else {
            Object object = ((WrapperTemplateModel) arguments.get(0)).getWrappedObject();
            Object arg2 = arguments.get(1);
            if(arg2 instanceof WrapperTemplateModel) {
                Object p2 = ((WrapperTemplateModel) arg2).getWrappedObject();
                if (p2 instanceof Class) {
                    Class c = (Class) p2;
                    return c.isAssignableFrom(object.getClass());
                } else {
                    throw new TemplateModelException("Wrong type of the second parameter. It should be Class. Found: " + p2.getClass());
                }
            } else {
                String p2 = ((SimpleScalar) arg2).getAsString();
                try {
                    Class c = getClass().getClassLoader().loadClass(p2);
                    return c.isAssignableFrom(object.getClass());
                } catch (ClassNotFoundException e) {
                    throw new TemplateModelException("Unable to find class " + p2);
                }
            }
        }
    }
}
