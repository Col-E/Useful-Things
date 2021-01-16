# Java Bytecode

Parsing and writing Java bytecode, what are the options and where do they shine?

## Objectweb ASM

* Site: https://asm.ow2.io/
* Maven: https://mvnrepository.com/artifact/org.ow2.asm

ASM gives you a low level representation of classes that is minimal, fast, and very flexible. ASM allows you to edit classes with a visitor pattern, or an object representation with its tree api.

The visitor api allows you to make changes to a class file with a single pass. The process is creating a `ClassReader` from some given bytecode in `byte[]`, then accept a implementation of a `ClassVisitor`. A visitor may take another visitor as a constructor parameter, which means you can chain visitors together. One visitor can change a specific item, and the next visitor in the chain can do something else entirely. But they are all invoked in a single pass so long as you chain them together. The same pattern follows with visiting methods and fields. 

**Visitor Example**

```java
private static byte[] patchDirectoryLookup(byte[] classBytecode) {
    int api = Opcodes.ASM9;
    // Declare a writer visitor that will generate bytecode as we visit and adapt existing code
    // We use "COMPUTE_FRAMES" so that ASM will generate stackframes for us (They are a huge pain otherwise)
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    // Create a reader and visit the class with a custom visitor
    ClassReader cr = new ClassReader(classBytecode);
    cr.accept(new ClassVisitor(api, cw) {
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            // Create the default method-visitor
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            // Check if the method takes a string and yields a file.
            if (desc.equals("(Ljava/lang/String;)Ljava/io/File;")) {
                // Return a new visitor that takes in the default visitor as a fallback.
                // This will allow the classwriter visitor to be notified of any operations we invoke on this new visitor.
                return new MethodVisitor(api, mv) {
                    // Overriding "visitCode" will allow us to prepend anything we want to the method body.
                    @Override
                    public void visitCode() {
                        // Visit the instruction pattern that will yield "return new File(System.getProperty("user.dir"))"
                        // This will force the method to return the current directory as a file instead of whatever the default was.
                        visitTypeInsn(Opcodes.NEW, "java/io/File");
                        visitInsn(Opcodes.DUP);
                        visitLdcInsn("user.dir");
                        visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "getProperty", "(Ljava/lang/String;)Ljava/lang/String;", false);
                        visitMethodInsn(Opcodes.INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
                        visitInsn(Opcodes.ARETURN);
                    }
                };
            }
            // Return the visitor
            return mv;
        }
    }, 0); // No-flags, we don't need "EXPAND_FRAMES" here since we use "COMPUTE_FRAMES" in the writer.
    return cw.toByteArray();
}
```

The tree api creates an object representation of a class that you can manipulate however you want. Do be aware though that each node type extends the associated visitor type. `ClassNode extends ClassVisitor`, `MethodNode extends MethodVisitor` and so on. So you can still use them in the visitor api. This is made obvious since you need the visitor api to generate a `ClassNode` initially and again to write it back to `byte[]`. Once you have the node type, you can interact with it any way you like.

**Tree Example**

```java
public void scan(byte[] classBytecode) {
    // Get the node
    ClassReader reader = new ClassReader(ClassReader);
    ClassNode node = new ClassNode();
    reader.accept(node, readFlags);
    // Scan for fields that are an int with constant value assigned to 0x80000001
    for (FieldNode fn : cn.fields) {
        if (fn.desc.equals("I") && fn.value != null) { 
            if (fn.value.equals(0x80000001)) {
                System.out.println(node.name + " " + fn.name + " == HKLU");
            }
        }
    }
    // Scan for methods that invoke JNA methods
    for (MethodNode mn : cn.methods) {
        // Iterate instructions of method
        for (AbstractInsnNode insn : mn.instructions) {
            if (insn.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode) insn;
                // Break iteration of the instructions looping if found, move on to the next method
                if (min.owner.startsWith("com/sun/jna/")) {
                    System.out.println(node.name + " " + mn.name + " == JNA Usage");
                    break;
                }
            }
        }
    }
}
```

As you can probably tell, the visitor and tree apis present vastly different ways to interact with classes. Depending on your use-case you may want to choose one over the other. The visitor API may be fast and minimal, but to do something like targeted searching in the example above the tree API may result in code that is easier to understand. A visitor implementation would have to create multiple visitors for the class, fields, and methods. However for making a simple change like in the first example, the visitor API is the most efficient.

## Javassist

* Site: https://www.javassist.org/
* Maven: https://mvnrepository.com/artifact/org.javassist/javassist

Javassist gives a mixed-level representation of class files. The primary draw for Javassit is that it lets you insert code into classes using source code. No bytecode knowledge needed at all. The process is getting a `ClassPool` instance to work with, then getting the class from the pool _(or adding a class to it)_ then simply calling the right `addX` and `insertX` methods with relevant source-like parameters.

**Example**

```java
private static void example() {
    // The default pool lets you get classes from the current runtime. 
    // You may want to use separate pools depending on your use case, similar to using separate classloaders.
    ClassPool cp = ClassPool.getDefault();
    CtClass cc = cp.get("com.example.MyClass");
    byte[] modified = patch(cc);
    // Or if you have bytecode, you can create a new class from an input stream using any ClassPool
    byte[] classBytecode = getBytecode("com.example.MyClass");
    cc = cp.makeClass(new ByteArrayInputStream(classBytecode));
    modified = patch(cc);
    // You can also add entirely new methods.
    // The "Javac" class is rather limited in scope. For "java.lang" classes you need to specify the full qualified name like "java.io.File"
    cc.addMethod((CtMethod) new Javac(cc).compile(
        "public void hello() {" +
        "    System.out.println(\"Hello!\");" +
        "}"));
}

private static byte[] patch(CtClass cc) {
    try {
        // Add: System.out.println($METHOD_NAME + " invoked");
        CtMethod[] methods = cc.getDeclaredMethods()
        for (CtMethod meth : methods) {
            meth.insertBefore("System.out.println(" + meth.getName() + " invoked\");");
        }
        return cc.toBytecode();
    } catch (Exception ex) {
        ex.printStackTrace();
        return byteCode;
    }
}
```

While Javassist does have lower-level capabilities, its a bit more verbose to work with than ASM's tree api. If you want more fine-tune control I would suggest using ASM.

## Honorable Mentions

Items worth mentioning, but I do not have enough experience to give a detailed opinion on.

* [ByteBuddy](https://bytebuddy.net)