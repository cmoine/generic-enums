package org.cmoine.genericEnums.processor;

import org.cmoine.genericEnums.processor.model.TypeElementWrapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Manifest;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;

public class TemplateData {
    private final TypeElementWrapper typeElement;
    private final String packageName;
    private final Class<? extends AbstractProcessor> processor;
    private final String className;
    private final Manifest manifest;
    private final Elements elementUtils;
    private final boolean generatedTypeAvailable;
    private final String generatedType;

    public TemplateData(Class<? extends AbstractProcessor> processor, ProcessingEnvironment processingEnv, String packageName,
                        String className, TypeElementWrapper typeElement) throws IOException {
        this.processor=processor;
        this.className = className;
        manifest=getManifest();
        this.packageName=packageName;
        this.typeElement=typeElement;
        boolean sourceVersionAtLeast9=processingEnv.getSourceVersion().compareTo(SourceVersion.RELEASE_6) > 2;
        elementUtils=processingEnv.getElementUtils();
        if (sourceVersionAtLeast9 && isTypeAvailable("javax.annotation.processing.Generated")) {
            this.generatedType="javax.annotation.processing.Generated";
            this.generatedTypeAvailable=true;
        } else if (isTypeAvailable("javax.annotation.Generated")) {
            this.generatedType="javax.annotation.Generated";
            this.generatedTypeAvailable=true;
        } else {
            this.generatedType=null;
            this.generatedTypeAvailable=false;
        }
    }
    
    /**
     * Determines if the type with the given full qualified name is part of the classpath
     *
     * @param canonicalName Name of the type to be checked for availability
     * @return true if the type with the given full qualified name is part of the classpath.
     */
    public boolean isTypeAvailable(String canonicalName) {
        return null != elementUtils.getTypeElement( canonicalName );
    }
    
    private static String asClassFileName(String className) {
        return className.replace( '.', '/' ) + ".class";
    }
    
    private static URL createManifestUrl(String classFileName, URL resource) throws MalformedURLException {
        String classUrlString = resource.toExternalForm();
        String baseFileUrl = classUrlString.substring( 0, classUrlString.length() - classFileName.length() );
        return new URL( baseFileUrl + "META-INF/MANIFEST.MF" );
    }
    
    private static Manifest openManifest(String classFileName, URL resource) {
        if ( resource == null ) {
            return null;
        }
        try {
            URL manifestUrl = createManifestUrl( classFileName, resource );
            return new Manifest( manifestUrl.openStream() );
        }
        catch ( IOException e ) {
            return null;
        }
    }
    
    private Manifest getManifest() {
        String classFileName = asClassFileName(getClass().getName() );
        URL resource = getClass().getClassLoader().getResource( classFileName );
        return openManifest( classFileName, resource );
    }

    public String getPackageName() {
        return packageName;
    }

    public String getProcessorName() {
        return processor.getName();
    }

    public String getVersion() {
        if(manifest==null)
            return null;

        return manifest.getMainAttributes().getValue("Implementation-Version");
    }

    public String getRuntimeVersion() {
        return System.getProperty("java.version");
    }

    public String getRuntimeVendor() {
        return System.getProperty("java.vendor");
    }
    
    public String getGeneratedType() {
        return generatedType;
    }

    public boolean isGeneratedTypeAvailable() {
        return generatedTypeAvailable;
    }

    public TypeElementWrapper getTypeElement() {
        return typeElement;
    }

    public String getClassName() {
        return className;
    }
}
