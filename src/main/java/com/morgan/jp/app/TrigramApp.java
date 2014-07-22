package com.morgan.jp.app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.morgan.jp.trigram.generate.TrigramTextGenerator;
import com.morgan.jp.trigram.loader.TrigramMapAdjWordDictionary;

/**
 * 
 * @author toantran
 * 
 * 
 *         Generates the trigram generator user interface
 */
public class TrigramApp extends Application {
	private static final int window_height = 700;
	private static final int window_width = 600;
	private Text outputText;
	private File inputFile;
	private Pane rootGroup;
	private String trigram;

	private static final Logger log = LoggerFactory.getLogger(TrigramApp.class);

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(final Stage stage) throws Exception {
		log.info("Starting user interface");

		stage.setTitle("Trigram Generator");

		final FileChooser fileChooser = new FileChooser();

		final Button setFileButton = new Button("Choose Input Text File");
		final Button generateButton = new Button("Generate Trigram");
		final Button saveButton = new Button("Save Trigram");
		outputText = new Text();

		// Set input file button
		setFileButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(final ActionEvent e) {
				log.info("Setting input file");
				File file = fileChooser.showOpenDialog(stage);
				if (file != null) {
					openFile(file);
					generateButton.setDisable(false);
				}
			}
		});

		// Generate trigram action
		generateButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(final ActionEvent e) {
				log.info("Generating Trigram");
				TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
				try {
					trigramDictionary.loadAdjWordDictionary(inputFile);
					TrigramTextGenerator trigramGenerator = new TrigramTextGenerator(
							trigramDictionary);
					trigram = trigramGenerator.generateNewText();
					outputText.setText(trigram);
				} catch (Exception e1) {
					outputText.setFill(Color.RED);
					outputText.setText(e1.getMessage());
					log.error("Error loading input file", e1);
					outputText.setFill(Color.BLACK);
				}
				saveButton.setDisable(false);
			}
		});

		// Save button action
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(final ActionEvent e) {
				log.info("Saving Trigram string to file");
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
						"TXT files (*.txt)", "*.txt");
				fileChooser.getExtensionFilters().add(extFilter);
				File file = fileChooser.showSaveDialog(stage);

				if (file != null) {
					try {
						saveFile(file);
					} catch (IOException e1) {
						outputText.setFill(Color.RED);
						outputText.setText(e1.getMessage());
						outputText.setFill(Color.BLACK);
						log.error("Error saving file", e1);
					}
				}
			}
		});

		rootGroup = new Pane();
		HBox hb = new HBox();
		hb.setStyle("-fx-border-color: black;");
		hb.getChildren().addAll(outputText);
		hb.setSpacing(10);

		ScrollPane sp = new ScrollPane();
		sp.setLayoutX(50);
		sp.setLayoutY(50);
		sp.setPrefWidth(window_width - 100);
		sp.setPrefHeight(window_height - 200);
		sp.setContent(hb);

		// Horizontal box for buttons
		HBox btnBox = new HBox();
		btnBox.setSpacing(10);
		btnBox.setLayoutX(85);
		btnBox.setLayoutY(window_height - 40);
		btnBox.getChildren().addAll(setFileButton, generateButton, saveButton);

		rootGroup.getChildren().addAll(sp, btnBox);
		generateButton.setDisable(true);
		saveButton.setDisable(true);

		stage.setScene(new Scene(rootGroup, window_width, window_height));
		stage.show();
	}

	/**
	 * Sets input text file for trigram generation
	 * 
	 * @param file Input file
	 */
	private void openFile(File file) {
		this.inputFile = file;
	}

	/**
	 * Saves content to file object paramater
	 * 
	 * @param file
	 *            File object to content to
	 * @throws IOException
	 *             Exception when error writing out to file
	 */
	private void saveFile(File file) throws IOException {
		FileWriter fileWriter = null;
		fileWriter = new FileWriter(file);
		fileWriter.write(trigram);
		fileWriter.close();
	}

}
