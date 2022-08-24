# Java Decompiling

So by this point you should have a general grasp of how the source to bytecode compilation process works. But how do we go backwards? Its not that different really. We can still operate off of a token model. All of the data we need is right there in the constant pool. We can make similar tree structures to the previously described _"ParseTree"_ but instead pulling information from the class file. 

Lets take a look at our hello world again. I'm going to only focus on the main method since constant pool information is presented inline through comments.

```java
class HelloWorld
{
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #3                  // String Hello
         5: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return
}
```

So how do we go from those 4 instructions to `System.out.println("Hello")`?

## The steps of decompilation

There is no one way to create a decompiler, but this is a common approach:

**1: Convert the binary to an intermediate representation**

Before anything complex happens we need to make a model of the program exactly how it is on the file system. For a Java class this could mean parsing the class file header, version info, constant pool, fields, methods, and class-level attributes. ASM's tree API is a very good example of this; check out the [fields of ClassNode](https://asm.ow2.io/javadoc/org/objectweb/asm/tree/ClassNode.html#field.summary) and how they mirror the class file specification. At this point we can print this structure out and we would have a dissassembler. Evolving into a decompiler requires a bit more work.

**2: Build control flow graph of methods**

Next up we use the IR and analyze how different instructions change the flow of code in each method. For example, if we see an `IFEQ` instruction we know we likely have something along the lines of `if (value) { code }`. The question is how do we know what belongs inside that hypothetical if statement's curly braces and what belongs after it? The process of semantic analysis lets us figure this out. Using the IR we can step through a method from the beginning and see where we have control flow modifying instructions like that `IFEQ`.  Since the `IFEQ` instruction jumps to some offset in the code if the stack's top value is equal to `0` then we know that everything between this instruction and that destination will be jumped over  if the value is not `0`. So with that in mind an `IFEQ` pointing to an offset `5` instructions forward means that the  `4` instructions between represent the content of that `{ code }`. We will separate this code from the rest as its own _"block"_. This process gets repeated for all sorts of control flow modifying instructions and attributes. Switch statements, all sorts of conditional jumps, throwing exceptions and their associated catch blocks, that all needs to be sliced up into smaller and smaller blocks. 

These blocks do not inherently have _"nesting"_ to them as you would expect from code such as `if (arg1) { if (arg2) { nested-code } }`. The instruction order may jump around with something like `GOTO`. So once we have all these blocks it still is a challenge to track how they interact with one another and transform this knowledge into a source code level concept like scopes, but without going too into depth this is generally how that is done. 

By this step we now have blocks and know which instructions belong in each block. This collection of blocks and how they connect to one another is called a Control Flow Graph (CFG).

**3: Combining IR with Pattern Matching & Stack Analysis**

Now that we have have instructions in smaller contained blocks we can look for patterns at these smaller scales. Consider we have a series of instructions like:
```java
ICONST_0
ISTORE i
```
This is clearly `int i = 0`. Its an incredibly basic two-instruction pattern. But we can have more than one instruction contribute to the value being assigned. So what about:
```java
ICONST_3
ICONST_5
INVOKESTATIC Util.multiply(II)I
ISTORE sum
```
This is clearly `int sum = Util.multiply(5, 3)`. But now we have the value of the variable as a whole expression instead of a constant value. This is where we introduce recursion. 

First we can start with some pseudo-code: `int sum = <3, 5, Util.multiply(II)I>`

At this point we have 3 IR values, one for the method call and two for the arguments. If we recognize a pattern of moving parameters into a method call's constructor we get: `int sum = <Util.multiply(5,3)I>`

Then lastly for the method call we have an `INVOKESTATIC` which makes things easy since there is no contect required. We simply put in the method's defining class name before it and we're done. We can assume this becomes `Util.multiply(5, 3)`. This process repreats again and again until there are no more matches. We could have `multiply` take on other calls to `multiply` as arguments. But with the patterns we've recognized here a few iterations is all we should need to transform a simple method call like this into an expression.

From before we also have our CFG. Lets say these were the `4` instructions mentioned before that are between the `IFEQ` and some offset. We add another pattern for loading the `IFEQ` argument off the stack and get:
```java
if (value) {
    sum = Util.multiply(5, 3);
}
```

Now just to reiterate these code snippets are representations of IR. The IR models this behavior shown in the code snippets here, so we would have things like `ConditionalIR` which takes a `Expression` argument and has a body of `Expression`. The model looks like:
```java
if (Expression[Condition]) {
    Expression[Body]
}
```

Which brings us to our last step.

**4: Printing the IR model to text**

By this step we have matched all the patterns we can. Our IR model is likely rather complex so that it can model all sorts of things like `if` statements, `else if`, `switch`, `for`, `try {} catch(...) {}`, etc. 

But with enough pattern work it's all there. Meaning its only a matter of printing these models into text. And that is incredibly easy work.

## What decompilers are out there?

Here are some popular options:

- [CFR](http://www.benf.org/other/cfr/)
- [FernFlower](https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler/engine) - And forks from the Minecraft modding community ([ForgeFlower](https://github.com/MinecraftForge/ForgeFlower), [Quilt](https://github.com/QuiltMC/quiltflower))
- [JADX](https://github.com/skylot/jadx)
- [Krakatau](https://github.com/Storyyeller/Krakatau)
- [JD](http://java-decompiler.github.io/)
- [Procyon](https://github.com/mstrobel/procyon)

If you're looking for a decompiler graphical user interface instead of a command line tool, JD and JADX come with independent applications. CFR, FernFlower, and Procyon are bundled in [Recaf](https://github.com/Col-E/Recaf). 

It is worth noting that some decompilers handle certain situations better than others. You can find a good paper going into detail on this in ["The Strengths and Behavioral Quirks of Java Bytecode Decompilers" - DOI:10.1109/scam.2019.00019](https://arxiv.org/abs/1908.06895). 

## Can you rely a decompiler?

***No***. 

It is true that compared to other languages Java decompilation is very accurate when its straight from `javac`'s output. But that's where it ends. While the results may be very promising in normal code once you introduce obfuscation things can get messy very fast. Not all decompilers are as lenient as others. Some may try their best when encountering obfuscated code and give you something _(albeit probably misleading in some capacity)_ while others may just outright refuse and print nothing. 

Misleading decompilation can be the result of a bug in the decompiler software, lack of support for a new language feature, or in the worst case the inability to represent a bytecode-level concept as source code. There are some things that are valid in Java bytecode that do not have any equivalent representation in source code. In these cases the best thing a decompiler can do is warn you that it tried it's best but the output is still wrong.

You will want to be able to read JVM bytecode in cases where the decompiler cannot output useful results. This may sound scary but it really isn't. The Java class file spec can be read in under an hour _(Especially skimming over redundant information)_. Its a simple format and the instruction set is very small compared to something like x86. A few items may seem a bit scary at first but if you try compiling your own Java source code and comparing it against the resulting bytecode it should help you out if the wording of the specification is confusing.

## How to deal with obfuscation?

If you are lucky the obfuscation can be dealt with by [existing tools](https://github.com/GenericException/SkidSuite/blob/master/deobfuscation.md). But these tools may not always be up-to-date and in other cases you may encounter a new obfuscation technique not supported by these tools. This is where understanding bytecode is paramount. If you wish to clear up custom obfuscation you will need to be able to write your own transformers for their patterns. The good news is you don't have to start from scratch, you can fork an open-source tool like [java-deobfuscator](https://github.com/java-deobfuscator/deobfuscator) or [threadtear](https://github.com/GraxCode/threadtear) and use one of their existing transformers as a base. Plus then you don't have to write any UI since it comes provided with the project.

## Further Reading

- [Anatomy of a Java Decompiler](https://members.accu.org/index.php/articles/1850) - Authors of [CFR](https://github.com/leibnitz27/cfr) and [Procyon](https://github.com/mstrobel/procyon),  [Lee Benfield](https://github.com/leibnitz27) and [Mike Strobel](https://github.com/mstrobel) talk about the anatomy of a decompiler in much greater detail than done here. 
- [SkidSuite](https://github.com/GenericException/SkidSuite) - A collection of tools and articles pertaining to JVM reverse engineering. Contains dozens of obfuscated samples to look at as well.
- ["Covert Java: Techniques for Decompiling, Patching, and Reverse Engineering"](https://www.amazon.com/Covert-Java-Techniques-Decompiling-Engineering/dp/0672326388) - An older book from 2004 meaning the referenced tools are outdated, but many of the concepts are still very relevant today. 
