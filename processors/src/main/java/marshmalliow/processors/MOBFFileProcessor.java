package marshmalliow.processors;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;

import marshmalliow.annotations.MOBFFile;

@SupportedAnnotationTypes("marshmalliow.annotations.MOBFFile")
@SupportedSourceVersion(SourceVersion.RELEASE_18)
@AutoService(javax.annotation.processing.Processor.class)
public class MOBFFileProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for(final Element element : roundEnv.getElementsAnnotatedWith(MOBFFile.class)) {
			if(element.getKind() != ElementKind.CLASS) continue;
			generateBuilderClass((TypeElement) element);
		}
		
		return true;
	}
	
	private void generateBuilderClass(TypeElement classElement) {
		final String packageName = processingEnv.getElementUtils().getPackageOf(classElement).getQualifiedName().toString();
		final String className = classElement.getSimpleName().toString();
		final String builderClassName = className + "Builder";
		
		final StringBuilder builderClassContent = new StringBuilder();
		
		builderClassContent.append("package ").append(packageName).append(";\n\n");
		builderClassContent.append("import marshmalliow.core.directory.DirectoryRegistry;\n");
		builderClassContent.append("import marshmalliow.core.directory.Directory;\n");
		builderClassContent.append("import marshmalliow.core.binary.data.MOBFFileHeader;\n");
		builderClassContent.append("import marshmalliow.core.binary.registry.DataTypeRegistry;\n");
		builderClassContent.append("import marshmalliow.core.binary.utils.CompressionType;\n");
		builderClassContent.append("import marshmalliow.core.binary.AbstractMOBFFileBuilder;\n\n");
		builderClassContent.append("import marshmalliow.core.file.FileType;\n\n");
		
		builderClassContent.append("public class ").append(builderClassName).append(" extends AbstractMOBFFileBuilder<").append(className).append("> {\n");
		builderClassContent.append("    private final DirectoryRegistry registry;\n");
		
		builderClassContent.append("    private ").append(builderClassName).append("(DirectoryRegistry registry) { this.registry = registry; }\n\n");
		
		builderClassContent.append("    public ").append(builderClassName).append(" directory(Directory directory) {\n").append("        this.directory = directory;\n").append("        return this;\n").append("    }\n\n");
		builderClassContent.append("    public ").append(builderClassName).append(" directoryId(String directoryId) {\n").append("        this.directoryId = directoryId;\n").append("        return this;\n").append("    }\n\n");
		builderClassContent.append("    public ").append(builderClassName).append(" registry(DataTypeRegistry registry) {\n").append("        this.dataTypeRegistry = registry;\n").append("        return this;\n").append("    }\n\n");
		builderClassContent.append("    public ").append(builderClassName).append(" header(MOBFFileHeader header) {\n").append("        this.header = header;\n").append("        return this;\n").append("    }\n\n");
		builderClassContent.append("    public ").append(builderClassName).append(" compression(CompressionType compression) {\n").append("        this.compression = compression;\n").append("        return this;\n").append("    }\n\n");
		builderClassContent.append("    public ").append(builderClassName).append(" name(String n) {\n");
		builderClassContent.append("        if(!n.toLowerCase().endsWith(FileType.MOBF.getExtension())) n += FileType.MOBF.getExtension();\n");
		builderClassContent.append("        this.name = n;\n").append("        return this;\n").append("    }\n\n");
		
		builderClassContent.append("    public static ").append(builderClassName).append(" builder(DirectoryRegistry registry) {\n").append("        return new ").append(builderClassName).append("(registry);\n").append("    }\n\n");
		
		builderClassContent.append("    @Override\n");
		builderClassContent.append("    public ").append(className).append(" build() {\n");
		builderClassContent.append("        if(this.name == null || this.name.isBlank()) throw new IllegalStateException(\"File name cannot be null or blank.\");\n");
		builderClassContent.append("        if(this.directory == null && directoryId != null) {\n");
		builderClassContent.append("            this.directory = registry.get(directoryId).orElseThrow(() -> new IllegalStateException(\"No directory found with id: \" + directoryId));\n");
		builderClassContent.append("        }\n");
		builderClassContent.append("        if(this.directory == null) throw new IllegalStateException(\"No directory provided or resolved by registry.\");\n");
		builderClassContent.append("        return new ").append(className).append("(this);\n");
		builderClassContent.append("    }\n");
		builderClassContent.append("}\n");
		

		try {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating builder for class " + className);
			final JavaFileObject file = processingEnv.getFiler().createSourceFile(packageName + "." + builderClassName);
			try (final Writer writer = file.openWriter()) {
				writer.write(builderClassContent.toString());
			}
		} catch (IOException e) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to generate builder for class "+ className +" with error" + e.getMessage());
		}
		
	}

}
