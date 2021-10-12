package org.example;

public class JavaFxDemo {
	public static void main(String[] args) {
		// Putting the JavaFX implementation of "Application" in
		// another class allows it to instantiate it via reflection
		// dynamically.
		//
		// It may seem odd to do so, but if you do not it fails
		// to figure out how to launch the "Application".
		//
		// See: https://stackoverflow.com/a/58498686/
		App.launch(App.class, args);
	}
}
