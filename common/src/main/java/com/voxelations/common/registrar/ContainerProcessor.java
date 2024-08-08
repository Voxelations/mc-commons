package com.voxelations.common.registrar;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("com.voxelations.common.registrar.Container")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class ContainerProcessor extends AbstractProcessor {

    public static final String PATH = "META-INF/containers";

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (roundEnvironment.processingOver()) return true;

        // Get all the containers
        List<String> containers = roundEnvironment.getElementsAnnotatedWith(Container.class).stream()
                .filter(it -> it.getKind() == ElementKind.CLASS)
                .map(it -> it.asType().toString())
                .toList();

        // Can't do anything if there aren't any containers
        if (containers.isEmpty()) return true;
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Found " + containers.size() + " containers");

        // Write out all the containers
        try (BufferedWriter writer = new BufferedWriter(processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", PATH).openWriter())) {
            writer.write(String.join("\n", containers));
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
