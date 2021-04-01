# Generic Enums

[![Build Status](https://github.com/cmoine/generic-enums/actions/workflows/maven.yml/badge.svg)](https://github.com/cmoine/generic-enums/actions/workflows/maven.yml)

## What is Generic Enums

This is a Java annotation processor to provide Generic Enums. It is basically an attempt to implement the [JEP301](https://openjdk.java.net/jeps/301)

Example of generic enum:
```java
@GenericEnum
enum Primitive {
    BYTE(byte.class, (byte)0),
    SHORT(short.class, (short)0),
    INT(int.class, 0),
    FLOAT(float.class, 0f),
    LONG(long.class, 0L),
    DOUBLE(double.class, 0d),
    CHAR(char.class, 'a'),
    BOOLEAN(boolean.class, false),
    BOOLEAN_OBJECT(Boolean.class, false),
    STRING(String.class, "");

    private final Class<?> boxClass;
    @GenericEnumParam
    private final Object defaultValue;

    Primitive(Class<?> boxClass, @GenericEnumParam Object defaultValue) {
       this.boxClass = boxClass;
       this.defaultValue = defaultValue;
    }

    @GenericEnumParam
    public Object getDefaultValue() {
        return defaultValue;
    }
}
```

You can find other sample [here](https://github.com/cmoine/generic-enums/tree/main/it/src/main/java/org/cmoine/genericEnums). 

## Requirements

Generic Enums requires Java 1.8 or later.

## Using Generic Enums

### Maven

```xml
...
<properties>
    <generic.enums.version>0.4</generic.enums.version>
</properties>
...
<dependencies>
    <dependency>
        <groupId>io.github.cmoine</groupId>
        <artifactId>generic-enums-annotations</artifactId>
        <version>${generic.enums.version}</version>
    </dependency>
</dependencies>
...
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>io.github.cmoine</groupId>
                        <artifactId>generic-enums-annotations</artifactId>
                        <version>${generic.enums.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
...
```

### Gradle

```groovy
dependencies {
    compileOnly "io.github.cmoine:generic-enums-annotations:0.4"
    annotationProcessor "io.github.cmoine:generic-enums-processor:0.4"
}
```

## Licensing

MIT License

Copyright (c) 2021 Christophe Moine

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
