# Understanding Java Compilation and Execution

When you have a Java source file and want to turn it into runnable code you first need to compile it. For a single file this is quite easy. Here's an example:

```java
class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
```

With this file saved as `HelloWorld.java` all you need to do is run `javac HelloWorld.java`. This will create `HelloWorld.class` in the same directory. You can then run the code via `java HelloWorld`, which will print `Hello` to the console.

So, what steps actually are in this process?

## The steps of `javac`

When you run `javac HelloWorld` what really is going on? You could look up compiler theory or look at the [actual javac source](https://github.com/openjdk/jdk/tree/master/src/jdk.compiler) for a in-depth explaination, but here's the TLDR:

1. The source text is broken into a series of words _(called 'tokens')_ in a process called [Tokenization](https://en.wikipedia.org/wiki/Lexical_analysis#Tokenization). For the above example the series of tokens would be `class`, `HelloWorld`, `{`, `public`, `static`, `void` `main`, `(`, `String[]`, `args`, `)`, `{`, `System`, `out`, `println`, `(`, `"Hello"`, `)`, `;`, `}`, `}`
2. These tokens are then analyzed and checked to see if they follow certain patterns. One such rule could require that after any `{` is found then an expression of some sort must be written, followed by a matching `}`. Expressions can be a variety of things, but in this case that will be our print call: `System.out.println("Hello");` 
3. Once the tokens are matched to patterns these patterns are stitched together to form familiar logical concepts. A class contains fields and methods. So our pattern for matching a class will thus also contain sub-patterns for matching fields and methods. This sort of hierarchy of rules creates a tree structure of patterns, so we'll refer to it as a _"ParseTree"_. To create a _"ParseTree"_ we can use the patterns of text and transform them into a structure. For example we can say that if we see the keyword `if` then it must be followed by `(`, `<expression>`, `) <expression>`. We can group this into an object called `IfTree` which has the methods `getCondition()` and `getStatement()`. With that in mind you can see this is actually what Javac outlines in [`src/jdk.compiler/share/classes/com/sun/source/tree`](https://github.com/openjdk/jdk/tree/master/src/jdk.compiler/share/classes/com/sun/source/tree). Now that we are working in terms of objects with logical operations we can more easily analyze the text these objects originate from. This is where the class file format comes in. We know that we will eventually want to output a binary file that follows the class file structure, so what do we need to do that? 
   - Names of all referenced symbols
   - How are those symbols used? Are they classes? Field names? String constants?
   - Defined fields with modifiers and optional assigned values
   - Defined methods with modifiers, parameters, code block if there is no `abstract` modifier 
      - Every expression contained by the method
4. So we operate on our logical tree structures to collect symbols and how they're used. This lets us help form the constant pool, which is where all of the data in a class is defined. We need to do this first pass/collection stage before we do any further analysis. Once its done though, then for things like the methods we look at the contained expressions that define the method's logic. These expressions will translate to instructions that reference the symbols by their index in the constant pool. In our example we have the following:
   1. `System.out` is a field reference. We know `System` is actually `java.lang.System`. And there's a static `out` field in `System` of the type `java.io.PrintStream` so our first instruction effectively becomes `GETSTATIC java/lang/System.out:java/io/PrintStream`. I say effctively because that snippet is simplified. Its really actually `GETSTATIC <int>` where the integer parameter points to a entry in the constant pool, `cp_fieldref`. This pool entry points to two other entries. One for the class that defines the field and another that defines the name and type of the field. Each of these will point to `cp_utf8` constants, essentially strings. But because they are each logically `cp_class` and `cp_nametype` we know that in their usage the text of the `cp_utf8` actually _means something_. For the rest of the steps I'll continue using the simplified display.
   2. Instead of doing the method call, we need to first understand the method call's parameters. That is a single string for us, `"Hello"`.  This becomes `LDC "Hello"`.
   3. Now that we parsed the parameters we look at the method name. We know the prior value of `System.out` from before is `java.io.PrintStream` so we look in that class for `println(String)`. Once we find our match we crate our third instruction `INVOKEVIRTUAL java/io/PrintStream.println(String)void`
   4. The method has no further content, so we add the last instruction `RETURN`
5. This series of steps is done for every element of the class until we have transformed our _"ParseTree"_ content into a new structure that outlines the class file format. Once the information is all filled in and we have nothing left to parse from our tree we can write it out as our completed `.class` file

## Running the generated class

To recap, we now have a class file which was built from a collection of text tokens from a source file. The tokens are actually in the generated class and the constant pool explains how each of these tokens are used. Then the content of method bodies which defines all of our logic is a series of instructions which have arguments that are basically all pointers back into the constant pool. So something like getting a static field is `GETSTATIC <int>` where the integer parameter points to a `cp_fieldref` in the constant pool. The same goes for method calls, new type instantiations, etc. 

So if the class file is just a binary structure where code points to a bunch of string constants, how does it actually run? Well, the Java Virtual Machine reads in this structure and compiles it to your computer's specific machine code on the fly. You can take the same Java class file and run it on a `x86`, `aarch64`, or `arm32` based CPU and it'll all work the exact same. The JVM will generate different low level machine code but it functions the same. This means for the purposes of reverse engineering Java software you only ever need to understand the `class` structure and never the machine code that the JVM generates. This makes reversing Java based programs quite easy, of which I'll cover in later posts.

## So how does our print call actually work?

The instructions for our main method were previously described when I discussed how the compiler transformed _"ParseTree"_ values into something that matches the class file structure. For convience here it is again:

```Java
GETSTATIC java/lang/System.out:java/io/PrintStream
LDC "Hello"
INVOKEVIRTUAL java/io/PrintStream.println:(String)void
RETURN
```

Java methods contain a _"stack"_.  The stack works just like any stack data type. You push things on to it and they go on the top. You can pop things off the top too. But you can't pull things from arbitrary locations. Its all based on whats on top. Java's instructions can push values onto the stack and consume them, taking them off the stack. Lets follow along with the code from above:

1. `GETSTATIC` fetches the static field `out` in `System` and pushes the value onto the stack.
2. `LDC` pushes the string `"Hello"` onto the stack. 
3. `INVOKEVIRTUAL` invokes a method on an instance value on the stack, and also consumes parameter values from the stack.
   - Our stack looks like [`PrintStream`, `String`] at this point.
   - `println` takes a `String` parameter, leaving us with [`PrintStream`]
   - `println` requires a `PrintStream` context, leaving us with an empty stack []
   - `println` returns `void` so it does not push anything back onto the stack.
   - With the context and paramters we can now invoke the `println` call.
4. `RETURN` indicates the method is done and execution stops.

Pretty simple stuff actually once you get the visual down in your mind.

## Class file disassembly

So far I've just explained what goes into making a class file with words, and the instructions have been using a simplified format for convenience. Here's the actual contents of our `HelloWorld.class` as disassembled by `javap` _(The disassembler bundled with all JDK releases)_

```java
class HelloWorld
  minor version: 0
  major version: 52  // Java 8
  flags: ACC_SUPER   // Default flag
Constant pool:
   #1 = Methodref          #6.#15         // java/lang/Object."<init>":()V
   #2 = Fieldref           #16.#17        // java/lang/System.out:Ljava/io/PrintStream;
   #3 = String             #18            // Hello
   #4 = Methodref          #19.#20        // java/io/PrintStream.println:(Ljava/lang/String;)V
   #5 = Class              #21            // HelloWorld
   #6 = Class              #22            // java/lang/Object
   #7 = Utf8               <init>
   #8 = Utf8               ()V
   #9 = Utf8               Code
  #10 = Utf8               LineNumberTable
  #11 = Utf8               main
  #12 = Utf8               ([Ljava/lang/String;)V
  #13 = Utf8               SourceFile
  #14 = Utf8               HelloWorld.java
  #15 = NameAndType        #7:#8          // "<init>":()V
  #16 = Class              #23            // java/lang/System
  #17 = NameAndType        #24:#25        // out:Ljava/io/PrintStream;
  #18 = Utf8               Hello
  #19 = Class              #26            // java/io/PrintStream
  #20 = NameAndType        #27:#28        // println:(Ljava/lang/String;)V
  #21 = Utf8               HelloWorld
  #22 = Utf8               java/lang/Object
  #23 = Utf8               java/lang/System
  #24 = Utf8               out
  #25 = Utf8               Ljava/io/PrintStream;
  #26 = Utf8               java/io/PrintStream
  #27 = Utf8               println
  #28 = Utf8               (Ljava/lang/String;)V
{
  HelloWorld();
    descriptor: ()V
    flags:
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return

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

Now you can actually see what I meant when I said `GETSTATIC <int>`. If you follow the constant pool references most things eventually decompose into text content defined by `cp_utf8` entries. 

But you probably have some new questions now that you can see this disassembly. 

### Why are there two methods here? The `HelloWorld.java` only defined a main method!

Well, yes but by default classes come with _"default constructors"_. They're public and take no arguments. Since our class doesn't extend anything the parent type is `java/lang/Object` and all constructors must eventually call back to it. Constructors by the way also use the reserved name `<init>` inside class files. When you do `new Foo()` the instructions generated are actually:

```java
NEW Foo
DUP
INVOKESPECIAL Foo.<init>()V
// Free do operate on newly made Foo instance
```

The `NEW` instruction creates an instance, then we duplicate the reference on the stack. On one reference we consume it when we invoke the constructor. This leaves one reference to the object on the stack that has been properly initialized by calling its constructor.

### Why is there a `java/io/PrintStream` and `Ljava/io/PrintStream;`?  

In source Java uses `.` between package names and classes. But in the bytecode `/` is used to separate between package names instead. 

As for why does one have a `L` prefix and `;` suffix, that is the descriptor format. In order to differentiate multple types in a row this prefix/suffix pattern is made. So if you have a method with two types of args such as `void test(Foo f, Bar b)` the descriptor becomes `(LFoo;LBar;)V` instead of `(FooBar)V`. Without this you'd have no way of knowing where one type stops and another begins.

### What does `[` mean in the main method `descriptor` section?

Each `[` indicates one dimension of an array. For example `String[]` is `[Ljava/lang/String;` and `String[][]` is `[[Ljava/lang/String;`

## Further Reading

This post is really just the tip of the ice berg. There is plenty of additional content you may want to check out before moving forward so that you have a grasp of how things in the class file and JVM work. Here's some excellent articles:

- https://blog.jamesdbloom.com/JVMInternals.html
- https://blog.jamesdbloom.com/JavaCodeToByteCode_PartOne.html

And of course, there's the specificaiton itself:

* Specs as of Java 17: https://docs.oracle.com/javase/specs/jvms/se17/html/index.html
* [Chapter 2. The Structure of the Java Virtual Machine](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-2.html)
  * [PC register](http://docs.oracle.com/javase/specs/jvms/se17/html/jvms-2.html#jvms-2.5.1)
  * [Stack](http://docs.oracle.com/javase/specs/jvms/se17/html/jvms-2.html#jvms-2.5.2)
  * [Native Stack](http://docs.oracle.com/javase/specs/jvms/se17/html/jvms-2.html#jvms-2.5.6),
  * [Local Variables](http://docs.oracle.com/javase/specs/jvms/se17/html/jvms-2.html#jvms-2.6.1)
  * [Operand Stacks](http://docs.oracle.com/javase/specs/jvms/se17/html/jvms-2.html#jvms-2.6.2)
  * [Dynamic Linking](http://docs.oracle.com/javase/specs/jvms/se17/html/jvms-2.html#jvms-2.6.3)
  * [Constant Pool](http://docs.oracle.com/javase/specs/jvms/se17/html/jvms-2.html#jvms-2.5.5)
  * [Frame](http://docs.oracle.com/javase/specs/jvms/se17/html/jvms-2.html#jvms-2.6)
  * [Heap](http://docs.oracle.com/javase/specs/jvms/se17/html/jvms-2.html#jvms-2.5.3)
  * [Method area](http://docs.oracle.com/javase/specs/jvms/se17/html/jvms-2.html#jvms-2.5.4)
* [Chapter 3. Compiling for the Java Virtual Machine](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-3.html)
* [Chapter 4. The `class` File Format](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html)
* [Chapter 6. The Java Virtual Machine Instruction Set](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-6.html)
* [Terminology](http://openjdk.java.net/groups/hotspot/docs/HotSpotGlossary.html)
