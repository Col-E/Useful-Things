# Debugging

An assortment of problems I've encountered with debugging Gradle builds and how to solve them.

## I can't use the interactive debugger in my groovy gradle build file

Do you have a message like `Cannot resolve constructor 'new groovy.lang.GroovyClassLoader(_$_18parentLoader)'`? 
Here's what the issue was for me. My project within IntelliJ didn't have 'Groovy' support, so the interactive
debugger would break trying to evaluate things.

Anyways, this can be fixed:

- Right click the project
- Choose 'Add Framework Support'
- Choose 'Groovy'

Now try again, it _should_ work.

Do not that this downloads 14 MB of groovy dependencies to `libs/` in your project root.