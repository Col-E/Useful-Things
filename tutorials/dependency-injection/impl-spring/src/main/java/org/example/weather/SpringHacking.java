package org.example.weather;

import org.objectweb.asm.*;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.AliasRegistry;
import org.springframework.expression.ParserContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Ignore everything in this class.
 * <br>
 * Once Spring v3 <i>(Which requires Java 17)</i> comes out officially {@link javax.inject.Inject} and {@link Autowired}
 * are interchangeable in the usage of Spring.
 * <br>
 * But for now I will use bytecode patching to retarget JSR-330 annotations to appropriate
 * {@link org.springframework.stereotype.Component} and {@link Autowired} references to simulate just being on Spring v3.
 */
public class SpringHacking {
	public static InputStream filterBytecode(InputStream is) throws IOException {
		ClassWriter writer = new ClassWriter(0);
		ClassReader reader = new ClassReader(is);
		InjectToSpringClassMapper mapper = new InjectToSpringClassMapper(writer);
		reader.accept(mapper, ClassReader.EXPAND_FRAMES);
		return new ByteArrayInputStream(writer.toByteArray());
	}

	public static class InjectToAutowiredMappingClassLoader extends URLClassLoader {
		private static final String SPRING_FILE_RESOURCE =
				"org.springframework.core.io.FileSystemResource";

		public InjectToAutowiredMappingClassLoader() {
			// These will tell this classloader to pull from the given URLs.
			// This in turn will mean this classloader will load copies of the Spring classes, which we can intercept.
			// We want this so that we can rewrite some spring logic that tries to load our unpatched bytecode.
			super(new URL[]{
					AnnotationConfigApplicationContext.class.getProtectionDomain().getCodeSource().getLocation(),
					SpringApplication.class.getProtectionDomain().getCodeSource().getLocation(),
					ConfigurationWarningsApplicationContextInitializer.class.getProtectionDomain().getCodeSource().getLocation(),
					ApplicationContextInitializer.class.getProtectionDomain().getCodeSource().getLocation(),
					AliasRegistry.class.getProtectionDomain().getCodeSource().getLocation(),
					TargetSource.class.getProtectionDomain().getCodeSource().getLocation(),
					ParserContext.class.getProtectionDomain().getCodeSource().getLocation(),
					Autowired.class.getProtectionDomain().getCodeSource().getLocation()
			});
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			if (name.startsWith("org.example") || name.startsWith("org.spring"))
				return findClass(name);
			return super.loadClass(name);
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			if (name.startsWith("org.example")) {
				// Patch the classes in our demo app to have the correct annotations
				try {
					InputStream filtered = filterBytecode(ClassLoader.getSystemResourceAsStream(name.replace('.', '/') + ".class"));
					byte[] classBytes = filtered.readAllBytes();
					return defineClass(name, classBytes, 0, classBytes.length);
				} catch (IOException ex) {
					throw new IllegalStateException("Failed to pull bytecode for class: " + name);
				}
			} else if (name.equals(SPRING_FILE_RESOURCE)) {
				// Patch the file source to intercept the InputStream from un-patched classes in the 'core' module
				try {
					ClassWriter writer = new ClassWriter(0);
					ClassReader reader = new ClassReader(name);
					SpringResourcePatcher patcher = new SpringResourcePatcher(writer);
					reader.accept(patcher, ClassReader.EXPAND_FRAMES);
					byte[] classBytes = writer.toByteArray();
					return defineClass(name, classBytes, 0, classBytes.length);
				} catch (IOException ex) {
					throw new IllegalStateException("Failed to patch spring resource logic: " + name);
				}
			}
			return super.findClass(name);
		}
	}

	/**
	 * The spring resource loader {@link InjectToAutowiredMappingClassLoader#SPRING_FILE_RESOURCE}
	 * will be reading our unpatched bytecode, so we need to patch it here too.
	 */
	private static class SpringResourcePatcher extends ClassVisitor {
		protected SpringResourcePatcher(ClassVisitor cb) {
			super(Opcodes.ASM9, cb);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
			MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
			if (name.equals("getInputStream")) {
				return new MethodVisitor(Opcodes.ASM9, mv) {
					@Override
					public void visitInsn(int opcode) {
						if (opcode == Opcodes.ARETURN) {
							// Patch value before return completes
							super.visitMethodInsn(Opcodes.INVOKESTATIC,
									SpringHacking.class.getName().replace('.', '/'),
									"filterBytecode",
									"(Ljava/io/InputStream;)Ljava/io/InputStream;", false);
						}
						super.visitInsn(opcode);
					}
				};
			}
			return mv;
		}
	}

	/**
	 * Any usage of {@link javax.inject.Singleton} on a class should be mapped to
	 * {@link org.springframework.stereotype.Component}.
	 */
	private static class InjectToSpringClassMapper extends ClassVisitor {
		private static final String INJECT_DESC = "Ljavax/inject/Inject;";
		private static final String SINGLETON_DESC = "Ljavax/inject/Singleton;";
		private static final String COMPONENT_DESC = "Lorg/springframework/stereotype/Component;";
		private static final String AUTOWIRED_DESC = "Lorg/springframework/beans/factory/annotation/Autowired;";

		protected InjectToSpringClassMapper(ClassVisitor cv) {
			super(Opcodes.ASM9, cv);
		}

		@Override
		public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
			if (INJECT_DESC.equals(descriptor) || SINGLETON_DESC.equals(descriptor))
				descriptor = COMPONENT_DESC;
			return super.visitAnnotation(descriptor, visible);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
			MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
			return new InjectToSpringMethodMapper(mv);
		}

		/**
		 * Any usage of {@link javax.inject.Inject} on a method should be mapped to
		 * {@link Autowired}.
		 */
		private static class InjectToSpringMethodMapper extends MethodVisitor {
			protected InjectToSpringMethodMapper(MethodVisitor mv) {
				super(Opcodes.ASM9, mv);
			}

			@Override
			public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
				if (INJECT_DESC.equals(descriptor))
					descriptor = AUTOWIRED_DESC;
				return super.visitAnnotation(descriptor, visible);
			}
		}
	}
}
