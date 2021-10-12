package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
	private static final int WIDTH = 300;
	private static final int HEIGHT = 200;

	@Override
	public void start(Stage stage) throws Exception {
		BorderPane pane = new BorderPane();
		pane.setCenter(new Label("Hello JavaFX"));

		Scene scene = new Scene(pane, WIDTH, HEIGHT);
		stage.setScene(scene);
		stage.show();
	}
}
