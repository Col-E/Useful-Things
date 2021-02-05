# Creating libraries in context of verbosity and design complexity.

Before I dive right into things, I separate this into multiple pieces.

1. High level design
   - How the API is intended to be used
   - _The external stuff that the user will be programming with_
2. Middle level design
   - How the API is designed
   - _The internal stuff that does all the work_
3. Low level design
   - How implementation is handled
   - _The specific details of how we write our methods and such_

For giving examples I will stick to JSON parsing. Now, onwards!

## High level design

There may be only one true JSON specification, but there are a near infinite number of ways we can present this to a user. Consider the following mockup ideas for reading a `Car` type from JSON:

```java
// Reader instance that takes JSON as string
car = new JsonReader(string).create(Car.class);
// Static method that takes in expected type and JSON string
car = JsonReader.create(Car.class, string);
// Factory builder of a type, can create instances when given JSON string
car = new JsonProducer(Car.class).from(string);
```

These examples all make a fair amount of sense. They all take in a type and some JSON text and yield a new instance of `Car`. This means we will have to make our internal logic more complex to handle this. What would happen if we wanted to minimize the complexity of our own library? We can push the burden of JSON to object mapping to the user. Now users will write code like:

```java
// Reader instance takes JSON, turns it into a node-tree representation, then we can call "transform" to conver the root of the tree into a car
car = new JsonReader(string).transform(root -> new Car(
    root.getStringOrDefault("brand", "?"),
    root.getStringOrDefault("model", "?"),
    root.getIntOrDefault("year", currentYear()),
    root.getArray("colors").map(array -> Arrays.toList(array))
));
// Factory builder where pattern lets us isolate the "transform" from before.
// Add multiple transformers to a "JsonReader" to support multiple custom types.
JsonTransformer<Car> producer = new JsonTransformer<Car>(Car.class) {
	@Override
    Car transform(JsonRoot root) {
        return new Car(
   			root.getStringOrDefault("brand", "?"),
    		root.getStringOrDefault("model", "?"),
    		root.getIntOrDefault("year", currentYear()),
    		root.getArray("colors").map(array -> Arrays.toList(array))
		);
    }
};
car = new JsonReader().addTransformer(car).read(string);
```

As you can see, keeping our own library less complex made the user's code more verbose. They now need to write a lot more than a single line to create a `Car`. This doesn't scale well if the user needs to convert lots of types and they will likely be frustrated and choose a different library.

We can mitigate a lot of this by providing the simpler front end as a default, with the ability to optionally specify more specific behavior like with our `JsonTransformer` case. Our library will become more complex in the backend in order to make users lives more simple. In addition to JSON parsing we now need to dynamically create instances of objects and update their values. 

## Middle level design

Working off of the examples above lets say we have the following JSON that represents the given class:

```json
{
    "brand": "Ford"
    "model": "Mustang"
    "year": 2021
    "colors": [
    	"black", "grey", "white"
    ]
}
```

```java
class Car {
    private final String brand, model;
    private final int year;
    private final Set<Color> colors;
    
    Car(String brand, String model, int year, Set<Color>) { /* set the fields */ }
}
```

Assuming we have a node-tree representation of the JSON, how do we get an instance of `Car`?

If we use the high-level design of forcing users to deal with it, then we don't have to write any code for the middle-level design and our job is done. Nice and clean right? Yes, but then users won't like our library because its inconvenient. 

What if we use the high-level design of always automatically converting it with some mysterious black magic? This is great for most simple users, but a few will then complain about the lack of control.

So now we decide to make things simple by default, but allow advanced users to plug in their own overrides for certain cases. What does this look like for us? Here's one possible way:

```java
car = new JsonProducer(Car.class)
	.withTransfomer(root -> ...) // the optional piece
	.from(string);
```

By default when we create the `JsonProducer` of a given type `T` we need to create a transformer that understands what the fields are of the type and how to populate them. If we require the type has a default constructor we can do all of this with simple reflection. Create a new instance via reflection. Set all the fields with reflection. 

Some tricks we can use at the cost of extra complexity _(Transitive dependencies or repackaging)_: 

- If you wanna cheat you can even use [Objenesis](http://objenesis.org/) to bypass requiring a default constructor.
- We can use [ReflectASM](https://github.com/EsotericSoftware/reflectasm) to optimize setting the values of non-private fields.

While these tricks are specific only to this JSON example, adding dependencies to a library does increase project verbosity in the sense that users applications will need to bundle anything we depend on. This is how you get transitive dependency bloat in a project.

## Low level design

Which option do you prefer?

```java
// Option 1:
updated = apples.removeIf(Apple::isRotten);
// Option 2:
for (int i = apples.size() - 1; i >= 0; i--)
    if (apples.get(i).isRotten()) {
        apples.remove(i);
        updated = true;
    }
```

The first option is clearly much less verbose and achieves the same outcome. 

`TODO: Continue writing article on lower level verbosity with examples`