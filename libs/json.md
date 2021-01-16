# JSON

JavaScript Object Notation, useful for a variety of cases. What are the options for reading and writing it?

## Moshi

* Site: https://github.com/square/moshi
* Maven: https://mvnrepository.com/artifact/com.squareup.moshi

Moshi is a small, annotation-driven JSON library that offers Kotlin code-generation. It can use custom adapters to transform objects into new representations as well.

**Example**

```java
Moshi moshi = new Builder().build();
JsonAdapter<Car> adapter = moshi.adapter(Car.class);
// Read
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

## Jackson

* Site: https://github.com/FasterXML/jackson
* Maven: https://mvnrepository.com/artifact/com.fasterxml.jackson _(Core for JSON)_

Jackson used to be just a JSON library but now supports a variety of data formats. It tends to be near the top of performance benchmarks when configured properly. Configuring usually entails properly annotating types with [Jackson Annotations](https://github.com/FasterXML/jackson-annotations).

**Example**

```java
// Read
String json = "{\"brand\":\"Jeep\", \"doors\": 3}";
Car car = objectMapper.readValue(json, Car.class);    
// Read generic collection
json = "[{\"brand\":\"Jeep\", \"doors\": 3}]";
List<Car> listCar = objectMapper.readValue(jsonCarArray, new TypeReference<List<Car>>(){});
// Write
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.writeValue(new File("car.json"), car);
```

## Gson

* Site: https://github.com/google/gson
* Maven: https://mvnrepository.com/artifact/com.google.code.gson/gson

Probably one of the most popular JSON libraries, but its not received any updates in awhile and some other libraries now outperform it or offer other reasons to switch. All the configuration is done by calling methods on a `GsonBuilder`. Though for most cases the defaults should work well.

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
// Creating
JsonValue name = Json.value("Jeep");
JsonValue doors = Json.value(3);
// Writing
value.toString();
```
