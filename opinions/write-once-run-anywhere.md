# Write Once, Run Anywere: _Please keep it this way_

Its been the catch-phrase of Java for years. You can take any standard Java code, compile it once to a `.class` _(or `.jar` for portability of multiple classes)_ and it will work on any computer with Java installed. Now of course, there are some limitations to this assumption. The local installed Java version has to meet the minimum language level of the compiled program and any libraries must be included or bundled with the application. In most cases library bundling can be entirely automated with a build tool like maven or gradle. So in most cases it _"Just Works"_ sorta deal. So when is it not? When would we want to be more specific about the target platform?

## Native libraries

Lets say you want to use something like OpenGL. You will need some native bindings. Working on one machine is pretty simple. If I'm on Windows x64 I just will include the bindings for my target architecture in my build config. Then I can bundle them into my output `jar` using a build tool. So now I got a few `.dll` files in my program. What if I want to support Windows x86 too? I can include the bindings for that too. And OpenGL when used via LWJGL puts the `.dll` files in unique paths so there is no conflict. Easy peasy. This can easily scale up for any platform I want that is natively supported by OpenGL/LWJGL. Sadly though, things are not always this simple. 

JavaFX is a modern UI framework for Java. Having used Swing and peeking at SWT a bit, it is my hands down favorite. It used to be bundled in the Oracle JDK, and still is in a few others, but relying on it being included is not a smart choice, and will just confuse users. So how about we try bundling it as we did with LWJGL? Unfortunately bundling natives for it is not as clean of a process as it is when using LWJGL. Following the example of bundling for both Windows x64 and x86 a problem is immediately noticeable. There is a path conflict when trying to include the native bindings for both. Both bindings include files such as `glass.dll` but each is complied for a different architecture! You can't just bundle two files of the same name, and the names cannot be changed either since their lookup inside the code is by file name. So you have a predicament. If you want to use a build tool it will only be able to target one architecture for any given OS. This kinda ruins the whole _"Write Once, Run Anywhere"_ motto. Are there any solutions to this? Well...

### Solution 1. Specifying bindings at runtime

This is the path I took for my program, Recaf. If JavaFX is not detected, it will be downloaded and inserted into the system classpath using some reflection hackery. 

The benefits:

* Multiplatform with only one release artifact.
* Smaller release artifact.
* Bypasses the problem of binding name conflicts since the system downloads only one binding for the current architecture.
* Allows introspective oprations based on the application jar file.
  * In Recaf's case: `-javaagent:path/to/recaf.jar`

The cons:

* It uses reflection on internal types, which aren't guaranteed to stay the same across versions. But it does work from 8-17 ATM.
* Requires either _(And this seems to confuse some users)_:
  * Java 8 with JavaFX bundled
  * Java 11 or higher

### Solution 2. Create one release per target architecture

Using tools like JLink you could take an application and bundle it and just the necessary modules into a custom runtime image that should be relatively small compared to a complete JDK installation. This image contains the just the JVM, the modules our app uses, any necessary libraries for the current platform, and lastly our application.

The benefits:

* Users do not need to have Java installed _(No fussing over versions/vendors)_
* Only loads core Java modules needed to run.

The cons:

* **Images are not multi-platform**, as they bundle the JVM and libraries for your current platform.
  * If you want to support multiple platforms you need to use JLink once on each different target platform.
  * I don't own every platform under the sun, and I don't want to pay an online service to run a build for every release I want to make for every platform.

JPackage is very similar in this regard.

### Non-solution 3. Petition JavaFX authors make native file paths unique

Basically, just point at LWJGL and ask them to adopt this measure, then wait until its implemented. This sort of thing could take a lot of time depending on discussion points, and they could opt to just say no. 

If I were a maintainer, I would have an OS/Arch lookup tool and use that to create directory prefixes inside [this function](https://github.com/openjdk/jfx/blob/86b854dc367fb32743810716da5583f7d59208f8/modules/javafx.graphics/src/main/java/com/sun/glass/utils/NativeLibLoader.java#L330) when calling `System.load(path)`. They already have the OS part for determining file extensions. Then once that's done the build scripts would need to move things to the new expected locations for making release artifacts.

### Non-solution 4. Don't use libraries with this problem

Again, JavaFX is my favorite UI framework available in the JVM. The other frameworks just don't do it for me. But that'll be for another post. But to keep things short:

* The default look-and-feels are awful. Seriously, look at Swing's Metal. The only viable default is the system look-and-feel. If you want to look nice, go get something custom. JavaFX CSS is a total breeze to make look nice.
* The lack of the observable pattern and rich listener support.

## Some community opinions that inspired this post

From: [_"Giving up JRE and requiring to bundle JVM in the app considered harmful?"_](https://www.reddit.com/r/java/comments/q33vq2/giving_up_jre_and_requiring_to_bundle_jvm_in_the/)

The original poster _(OP)_ of this reddit thread and the author of the linked email chain seem to have a similar opinion to my own. Jar files are great, but still have some shortcomings. However for some reason people see these small number of shortcomings as a reason to completely ditch the entire _"Write Once, Run Anywhere"_ model and suggest solutions like JLink and JPackage. I get it, these tools are cool and do have their use cases. But to suggest them as _"the new release standard"_ is frankly just stupid. 

### File Size 

Having to bundle around ~50 megabytes for a simple program with a small number of bundled libraries is stupid if you can just have a 1-5 megabyte jar file instead. And what if that application wants to implement an update feature? Are you going to redownload a new Java image every time you want to update the app? Sure, I have space on my SSD. Sure, my internet speed is decently fast. But that train of though is also stupid. Just because you are capable of eatingt McDonalds dinner every day doesn't mean you should. The optimal solution would be to do something better in the first place.

And I see this mentality everywhere. From that reddit thread:

> Electron bundles entire web browser and yet it's the most popular UI framework. Just bundle JVM and get on with the day.

No, Electron sucks because of this. The idea of having a dozen chromium installations on a single machine is stupid. I don't want to have a dozen applications on my computer each with 20+ sub-processes running at all times. I don't want space on my SSD wasted by duplicate versions of chromium. If anything they should look to how running a jar works. You install the framework and then when you run the app, _"It just works"_. 

> The reason why Electron chose to embed its own webview is version control - this way, they control, what the webview provides, how it behaves. Relying on an old, possibly outdated or unsupported webview is a PITA.

Its not a crazy ask to tell a user it is required to update software. Users can be dumb or averse to updates, I know. But these are a minority, albeit a loud one. But the users who are capable of using their eyeballs and reading will just do it most of the time. Because Java is supposed to follow the _"Write Once, Run Anywhere"_ model, you can just update it and stuff will continue to work _(Minus reflection on internal API's which you shouldn't be doing anyways)_. We have wonderful vendors like Adoptium, Amazon, Zulu, Bellsoft, and many others who release these updates without the old bullshit _"Ask toolbar"_ that the old Oracle installers used to bundle. Adoptium even knows which versions to show you the second you land on their sites. Its quick and simple.

### Platform Availability

Again, beating a dead horse here, Java is supposed to follow _"Write Once, Run Anywhere"_ and once you install the JDK on your system so long as the applicaiton being run isn't from a newer release it will just work. But the whole _"Why not make a Java runtime image?"_ notion breaks this. Here's a comment from the reddit thread:

> I've bought the latest and brightest MacBook Air M1. Under the old model the only thing I would need to do to have an application work on my new laptop would be to install the JRE. Right now I have to wait for all application vendors to provide me with the platform specific bundle. Even if JDK for this platform is already available - I still cannot run all Java applications on my laptop.

So in this case an application vendor is making a Java program, which has multiple releases. This would imply that in a normal release model it would be possible to create a multi-platform release. But instead they chose to only make specific images for a select number of target OS/architectures. So if you are unforunate enough to not be one of those target systems you're out of luck. The application in a normal release model would work, but now it won't. 

As somebody in the replies to this says:

> It does not affect them only if the application author specifically doesn't want to allow running with your own runtime. But if they do want to allow it, of course you can bring your own if you like it that way better.

But thats not neccesarily the case all the time. Its not as simple as pointing to your own installation of Java. Consider JavaFX. It has more than just one architecture target per OS. If the application developer does not create an image release for _each of those_ then the user would be tasked with replacing the natives on their own with the correct versions. To get these natives you'd need to go to [the artifact release](https://mvnrepository.com/artifact/org.openjfx/javafx-graphics), find [the version for your system](https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/17/), extract the natives, then replace the ones in the vendor's app with those.

This isn't something a user should be doing. Asking any non-technical user to do this is asking too much of the average user.

And this is all assuming the vendor is using a tool like JLink or JPackage. If they use something more like [Install4J](https://www.ej-technologies.com/products/install4j/overview.html) where the vendor does not include a release for a user's system the process to modify the existing release to support the current system is also a hassle normal users should not have to go through.

### The linked email thread

> Technically, making a launcher for a new generation of fat JAR that supports JPMS better is quite easy. However, that doesn't solve a whole lot of other problems, like proper online updates or how to keep the underlying "JRE" up to date.

While installing an update for Java is easy, I don't think there are many use cases where an application _needs_ to constantly be using the latest release of the JDK. Rather, its commonly the application itself and its libraries that need to be updated.

And [the responses](https://mail.openjdk.java.net/pipermail/jigsaw-dev/2021-October/014731.html) to the questions posed in the email thread I agree with quite a lot too.

> > Why do you need it to be a single file? 
>
> Because it's convenient. In any case, a single file is more convenient  than multiple files. I believe this is the reason why fatjar is popular. A single file is more robust (easier to ensure integrity), easier to distribute, easier to deploy, easier to copy and move without missing, more portable...We can avoid many simple but boring actions, e.g., unzip, enter and exit folders, create shortcuts or launch scripts, specify replication override policies, and more. These piecemeal steps are very simple, but we get tired of it when we often do them. Using single file distribution can avoid many of them.

How this isn't common sense and just understood as a major point of not moving towards always bundling things is beyond me.

> > Why an installable jlinked image doesn't meet your workflow
>
> We need to prepare a separate distribution for each of them: Seven mainstream platforms (Windows x86, Windows x86-64, Windows AArch64, Linux x86-64, Linux AArch64, Mac OS x86-64, Mac OS AArch64), some non mainstream platforms but we have users (Linux ARM32, Linux MIPS64el, Linux LoongArch64, FreeBSD x86-64), and some platforms with potential users (Linux PPC64le, Linux s390x, AIX PPC64le, and more).
>
> This is crazy. This will make our build process more cumbersome. We have to pay service providers from tens to thousands of times the cost of storage and bandwidth. What will we get? Worse cross platform, more bloated software, more cumbersome use steps and longer download time, but there is no better user experience.

Already covered this, but yes. 

All around these answers really hit the nail on the head as to why I think moving away from _"Write Once, Run Anywhere"_ is a terrible idea.