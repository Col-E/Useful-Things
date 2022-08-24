# JSON

JavaScript Object Notation, useful for a variety of cases. What are the options for reading and writing it?

## Gson

* Site: https://github.com/google/gson
* Maven: https://mvnrepository.com/artifact/com.google.code.gson/gson

Probably one of the most popular JSON libraries. All the configuration is done by calling methods on a `GsonBuilder`. Though for most cases the defaults should work well.

**Example** _(More at [gson::user-guide](https://github.com/google/gson/blob/master/UserGuide.md))_

```java
GsonBuilder builder = new GsonBuilder();
Gson gson = builder.create();
// Parse
String json = "{\"brand\":\"Jeep\", \"doors\": 3}";
Car car = gson.fromJson(json, Car.class);
// Write
json = gson.toJson(car);
```

**Pros**

- Simple and easy to understand [documentation/examples](https://github.com/google/gson/blob/master/UserGuide.md)
- Can bypass the need for defining a default constructor _([A problem for older JSON parsers](https://stackoverflow.com/questions/30568353/how-to-de-serialize-an-immutable-object-without-default-constructor-using-object/46601536))_
- [Can handle generic collections](https://stackoverflow.com/a/5554296/)

**Cons**

- [One of the slower of the popular JSON frameworks](https://www.ericthecoder.com/2020/10/13/benchmarking-gson-vs-jackson-vs-moshi-2020/)


## Moshi

* Site: https://github.com/square/moshi
* Maven: https://mvnrepository.com/artifact/com.squareup.moshi

Moshi is a small, annotation-driven JSON library that offers Kotlin code-generation. It can use custom adapters to transform objects into new representations as well.

**Example**

```java
Moshi moshi = new Builder().build();
JsonAdapter<Car> adapter = moshi.adapter(Car.class);
// Parse
Car car = adapter.fromJson(json);
// Write
json = adapter.toJson(car);
// Custom adapter allows changing JSON representation to a simple string (brand:doors)
// Usage: new Builder().add(new CarAdapter()).build();
class CarAdapter {
    @ToJson 
    String toJson(Car car) {
        return card.brand + ":" + car.doors;
    }
    
    @FromJson 
    Car fromJson(String car) {
        String[] split = car.split(":");
        if (split.length != 2) throw new JsonDataException("Unknown car format: " + car);
        return new Car(split[0], Integer.parseInt(split[1]));
    }
}
```

**Pros**

- Simple and easy to understand [documentation/examples](https://github.com/square/moshi)
- [Uses less memory and performs faster than GSON](https://proandroiddev.com/goodbye-gson-hello-moshi-4e591116231e)
- Can bypass the need for defining a default constructor _([A problem for older JSON parsers](https://stackoverflow.com/questions/30568353/how-to-de-serialize-an-immutable-object-without-default-constructor-using-object/46601536))_
- [Can handle generic collections](https://github.com/square/moshi/issues/78#issuecomment-140155920)

**Cons**

- Transitive dependency on Kotlin runtime

## Jackson

* Site: https://github.com/FasterXML/jackson
* Maven: https://mvnrepository.com/artifact/com.fasterxml.jackson _(Core for JSON)_

Jackson used to be just a JSON library but now supports a variety of data formats. It tends to be near the top of performance benchmarks when configured properly. Configuring usually entails properly annotating types with [Jackson Annotations](https://github.com/FasterXML/jackson-annotations). Personally I find the documentation on Jackson to be confusing to navigate due to the wide scope of the Jackson project and its many sub-modules.

**Example**

```java
ObjectMapper mapper = new ObjectMapper();
// Parse
String json = "{\"brand\":\"Jeep\", \"doors\": 3}";
Car car = mapper.readValue(json, Car.class);    
// Read generic collection
json = "[{\"brand\":\"Jeep\", \"doors\": 3}]";
List<Car> listCar = mapper.readValue(jsonCarArray, new TypeReference<List<Car>>(){});
// Transform to Json object
JsonNode node = mapper.readTree(json);
String brand = node.get("brand").asText();
// Write
mapper.writeValue(new File("car.json"), car);
```

**Pros**

- [One of the faster of the popular JSON libraries, but mostly if you manually configure it with annotations](https://www.ericthecoder.com/2020/10/13/benchmarking-gson-vs-jackson-vs-moshi-2020/)
- [Uses less memory and performs faster than GSON](https://proandroiddev.com/goodbye-gson-hello-moshi-4e591116231e)
- Can bypass the need for defining a default constructor _([A problem for older JSON parsers](https://stackoverflow.com/questions/30568353/how-to-de-serialize-an-immutable-object-without-default-constructor-using-object/46601536))_
- [Can handle generic collections](https://stackoverflow.com/a/9829502/)

**Cons**

- Official documentation is spread among multiple places and is generally hard to navigate compared to others. Thus they now maintain a list of [3rd party documentation instead](https://github.com/FasterXML/jackson-docs)
- [Requires configuration to use non-default constructors](https://stackoverflow.com/a/30568611/)

## Minimal JSON 

* Site: https://github.com/ralfstx/minimal-json
* Maven: https://mvnrepository.com/artifact/com.eclipsesource.minimal-json/minimal-json

As the name implies, this is a very minimal JSON parser/writer at only 33.4 KB. It does not support any type of type serialization, so you will have to implement type handling on your own.

**Example**

```java
// Parsing
JsonValue value = Json.parse(string);
if (value.isString()) {
    String string = value.asString();
} else if (value.isArray()) {
    // Can also: get(index), set(index, value), remove(index)
    JsonArray array = value.asArray();
    for (JsonValue arrayValue : array) {
        // ...
    }
} else if (value.isObject()) {
    JsonObject object = value.asObject();
    // Can also: set(name, value)
    String brand = object.get("brand").asString();
    int doors = object.get("doors").asInt();
}
// Creating new json objects
JsonValue name = Json.value("Jeep");
JsonValue doors = Json.value(3);
// Writing
value.toString();
```

**Pros**

- [Matches speed of Jackson](https://github.com/ralfstx/minimal-json#performance) and has a very minimal memory footprint.
- Simple and easy to understand [documentation/examples](https://github.com/ralfstx/minimal-json#usage)
- Very small dependency _(33 kb)_

**Cons**

- _(This is intentional, but for some is a con)_ Much more of a manual effort than automated type (de)serialization other libraries offer. No fancy type casting and conversion of POJOs.