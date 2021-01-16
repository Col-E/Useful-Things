# How to choose libraries for a task

Some things to consider:

1. Which library will let me write the simplest code?
2. Can I use the library with its license terms?
3. Do I care about file size, and if so, how large is this dependency?

## Writing simple code

If you want to retain your sanity when working on a project over a longer span of time try to keep things simple. If you take a month long break and come back you'll thank yourself later. With this in mind how does this affect your choice for choosing a library? It depends on the library. 

**Utilities**: For utility code, the decision is usually to include a super common `utility` dependency or to write your own utility methods. It is probably a good idea to add a dependency for something like `commons-io` rather than make your own. This way all the file IO you'll ever need has been written and tested by somebody else. It won't clutter up your project in a miscellaneous `util` package and usually you'll only call it with simple one-liners. The decision here is really to include a utility library or to not.

**Minor Features**: For minor features, then the decision is usually between different libraries offering the same general capabilities but with slightly different bonus features and varying performances. Using JSON as an example consider what it looks like to parse and write back JSON from some object types. If you do not need complex types to be serialized something like Moshi will probably be a better choice than a more complex dependency like Jackson. If you do not need extra bells and whistles do not add them. 

**Major Features**: For complex features like networking, then the decision is usually down to what sort of architecture best suites your needs. Depending on the complexity of your project you may have more or less options. If you're making a tiny chat application you have plenty of options for networking libraries. But if you're going for something more complex like a distributed internet-of-things application, there's usually only a few good options. Skim over the documentation/examples of each option that offers support for whatever features you need. Usually these will include some code options which will give you an idea on how simple or complex it is to implement the library into your project.

In each case, separation of duties will help you keep the clutter down. When working with more than one library think about how you could separate the usage of each library into its own class for your purposes. Keeping classes small and focused on one feature will make it easy for new people _(or you after a month long break)_ to understand whats going on in your code.

## Licensing

For personal projects that won't ever see the light of day, it usually doesn't matter what the license of a library is. 

For public projects, or even commercial projects then it does matter. You can use a service like [TLDR-Legal](https://tldrlegal.com/) to check if a given license allows commercial usage, and how to properly give credit to the library author. In the worst case scenario, the project is not legally compatible with yours and if you ignore that its up to the will of the library author to spare you or to sue you.

## File Size

While file size is increasingly less important as internet gets faster and storage space gets larger, it can be used as a sort of complexity metric. If you have a simple program that is only 100 lines long, but you require a dependency on a library that is 20 MB then you probably can find a library that does the same thing but in a much more minimal implementation. Typically this will align with the simplicity aspect of the first point mentioned above.