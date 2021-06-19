# How to choose libraries for a task

Some things to consider:

1. Which library will let me write the simplest code?
2. Can I use the library with its license terms?
3. Do I care about file size, and if so, how large is this dependency?

## Writing simple code

If you want to retain your sanity when working on a project over a longer span of time try to keep things simple. If you take a month long break and come back you'll thank yourself later. With this in mind how does this affect your choice for choosing a library? It depends on the use case. 

**Utilities**: For utility code, general purpose libraries like the Apache Commons and Guava cut down the amount of boiler-plate and static utility methods in your code-base. If you find yourself only writing one or two static methods in a few classes you probably won't need libraries like these. However, if you have an entire package of random `XyzUtil` classes you may want to look into library alternatives.

**Features**: For features, choose a library that has an API that matches your own preferences. Things to look for also include good documentation, general popularity _(active user-base/developers)_, and test coverage. For example, if your application needs to read and write JSON, then you may choose a newer library like Moshi rather than Gson since it is more actively maintained, and reported bugs/features are more likely to be fixed/addressed. 

**Frameworks**: For frameworks like advanced networking stacks, the advice from picking features applies here as well. Though, there are usually only a few options if your architecture is more niche. With an important element of your application like this, you may want to create a small demo application to test out the libraries capabilities. Don't be afraid to slightly change your design ideas too. Forcing yourself to use a bad library because it fits your requirements will just make development feel like a pain in the butt. Check services like [libhunt](https://java.libhunt.com/) to see if there are similar libraries for your purpose.

In each case, separation of duties will help you keep the clutter down. When working with more than one library think about how you could separate the usage of each library into its own class for your purposes. Keeping classes small and focused on one feature will make it easy for new people _(or you after a month long break)_ to understand what's going on in your code.

## Licensing

For personal projects that won't ever see the light of day, it usually doesn't matter what the license of a library is. However if you one day decide you want your project to be public then you got a problem.

For public projects, or even commercial projects then it does matter. You can use a service like [TLDR-Legal](https://tldrlegal.com/) to check if a given license allows commercial usage, and how to properly give credit to the library author. In the worst case scenario, the project is not legally compatible with yours and if you ignore that its up to the will of the library author to spare you or to sue you.

## File Size

While file size is increasingly less important as internet gets faster and storage space gets larger, it can be used as a sort of complexity metric. If you have a simple program that is only 100 lines long, but you require a dependency on a library that is 20 MB then you probably can find a library that does the same thing but in a much more minimal implementation. Typically this will align with the simplicity aspect of the first point mentioned above.