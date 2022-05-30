package it.polimi.ingsw.View.GUI;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.Executor;

/**
 * Class for creating the GUIController and displaying it to the user, showing changes in the game based on
 * method calls from the GUIController class.
 */
public class GUIApplication extends Application {

    private static GUIApplication instance;

    private Scene loginScene, gameSetupScene, mainScene;

    private Stage stage;

    private GUIController controller;

    public final static Executor runLaterExecutor = Platform::runLater;

    public GUIApplication() {
        instance = this;
    }

    public static GUIApplication getInstance() {
        /*
        CountDownLatch latch = new CountDownLatch(1);
        if (instance == null) {
            new Thread(() -> {
                Application.launch(GUIApplication.class);
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return instance;
        */
        if (instance == null) {
            new Thread(() -> Application.launch(GUIApplication.class)).start();
        }
        while (instance == null) {
            try {
                Thread.sleep(100); // 450 is actually fine, but it might run slightly slower on some devices so...
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public void setController(GUIController controller) {
        this.controller = controller;
    }

    @Override
    public void start(Stage mainStage) {
        stage = mainStage;
        setupStage();
        createLoginScene();
        switchToLogin();
        runLaterExecutor.execute(this::createGameSetupScene);
        runLaterExecutor.execute(this::createMainScene);
    }

    public void createLoginScene() {
        VBox root = new VBox();
        AnchorPane anchorPane = setupScene(root);
        anchorPane.setBackground(new Background(new BackgroundImage(
                new Image("/general/bg1_unfocused.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true)
        )));
        VBox gridContainer = new VBox();
        gridContainer.setAlignment(Pos.CENTER);
        gridContainer.setPadding(new Insets(20.0, 0.0, 0.0, 0.0));
        anchorPane.getChildren().add(gridContainer);
        AnchorPane.setRightAnchor(gridContainer, 0.0);
        AnchorPane.setBottomAnchor(gridContainer, 0.0);
        AnchorPane.setLeftAnchor(gridContainer, 0.0);
        AnchorPane.setTopAnchor(gridContainer, 0.0);

        Image iconPlusName = new Image("/general/iconPlusName.png");
        ImageView imageView = new ImageView(iconPlusName);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(400);
        imageView.setSmooth(true);
        imageView.setCache(true);
        anchorPane.getChildren().add(imageView);
        AnchorPane.setLeftAnchor(imageView, 50.0);
        AnchorPane.setTopAnchor(imageView, 150.0);

        Label loginSceneTitle = new Label("Login");
        loginSceneTitle.setFont(Font.font("Eras Demi ITC", 30));
        // loginSceneTitle.setStyle("-fx-font-family: 'Era'; -fx-font-size: 30; -fx-font-weight: bolder");
        gridContainer.getChildren().add(loginSceneTitle);

        GridPane ipPane = new GridPane();
        gridContainer.getChildren().add(ipPane);
        ipPane.setId("ipPane");
        setupGrid(ipPane);

        GridPane nickPane = new GridPane();
        gridContainer.getChildren().add(nickPane);
        nickPane.setId("nickPane");
        nickPane.setAlignment(Pos.CENTER);
        nickPane.setHgap(50.0);
        nickPane.setVgap(50.0);
        setupGrid(nickPane);

        Label ipLabel = new Label("IP");
        ipPane.add(ipLabel, 0, 0);
        Label portLabel = new Label("Port");
        ipPane.add(portLabel, 0, 1);
        final TextField ipField = new TextField();
        ipField.setId("ipField");
        ipField.setPrefSize(350.0, 50.0);
        ipField.setPromptText("Insert the IP to connect to. Default IP: localhost");
        ipField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (ipField.getText().equals("")) ipField.setText("127.0.0.1");
                lookup("portField").requestFocus();
            }
        });
        ipPane.add(ipField, 1, 0);
        final TextField portField = new TextField();
        portField.setId("portField");
        portField.setPrefSize(350.0, 50.0);
        portField.setPromptText("Insert the port to connect to. Default port: 4646");
        portField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (portField.getText().equals("")) portField.setText("4646");
                controller.connectToIP();
            }
        });
        ipPane.add(portField, 1, 1);

        Button connectButton = new Button("Connect");
        connectButton.setId("connectButton");
        connectButton.setOnMouseClicked(mouseEvent -> controller.connectToIP());
        ipPane.add(connectButton, 1, 2);

        Label nickLabel = new Label("Nickname");
        nickPane.add(nickLabel, 0, 3);
        TextField nickField = new TextField();
        nickField.setId("nickField");
        nickField.setPrefSize(350.0, 50.0);
        nickField.setPromptText("Insert your unique nickname...");
        nickField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if ( ! ((TextField) (lookup("nickField"))).getText().equals("") )
                    controller.connectWithNickname();
            }
        });
        nickPane.add(nickField, 1, 3);

        Button nickButton = new Button("Login");
        nickButton.setOnMouseClicked(mouseEvent -> {
            if ( ! ((TextField) (lookup("nickField"))).getText().equals("") ) {
                controller.connectWithNickname();
            }
        });
        nickPane.add(nickButton, 1, 4);
        nickPane.setDisable(true);

        loginScene = new Scene(root);
    }

    public void createGameSetupScene() {
        VBox root = new VBox();
        AnchorPane anchorPane = setupScene(root);
        anchorPane.setBackground(new Background(new BackgroundImage(
                new Image("/general/bg2_unfocused.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true)
        )));
        VBox gridContainer = new VBox();
        gridContainer.setAlignment(Pos.CENTER);
        gridContainer.setPadding(new Insets(20.0, 0.0, 0.0, 0.0));
        anchorPane.getChildren().add(gridContainer);
        AnchorPane.setRightAnchor(gridContainer, 0.0);
        AnchorPane.setBottomAnchor(gridContainer, 0.0);
        AnchorPane.setLeftAnchor(gridContainer, 0.0);
        AnchorPane.setTopAnchor(gridContainer, 0.0);

        Label gameSetupSceneTitle = new Label("Game setup");
        gameSetupSceneTitle.setFont(Font.font("Eras Demi ITC", FontWeight.BOLD, 30));
        gridContainer.getChildren().add(gameSetupSceneTitle);

        GridPane gameSettingsPane = new GridPane();
        gameSettingsPane.setId("gameSettingsPane");
        gridContainer.getChildren().add(gameSettingsPane);
        setupGrid(gameSettingsPane);
        gameSettingsPane.setDisable(true);

        Label numOfPlayersLabel = new Label("Number of players");
        gameSettingsPane.add(numOfPlayersLabel, 0, 0);
        Label gameModeLabel = new Label("Game mode");
        gameSettingsPane.add(gameModeLabel, 0, 1);

        ChoiceBox<String> numChoice = new ChoiceBox<>();
        numChoice.setId("numChoice");
        numChoice.getItems().setAll("2", "3", "4");
        numChoice.setPrefSize(350.0, 50.0);
        numChoice.setOnAction(actionEvent -> lookup("gameModeChoice").requestFocus());
        gameSettingsPane.add(numChoice, 1, 0);
        ChoiceBox<String> gameModeChoice = new ChoiceBox<>();
        gameModeChoice.setId("gameModeChoice");
        gameModeChoice.getItems().setAll("Standard", "Expert");
        gameModeChoice.setPrefSize(350.0, 50.0);
        gameModeChoice.setOnAction(actionEvent -> lookup("gameSettingsButton").requestFocus());
        gameSettingsPane.add(gameModeChoice, 1, 1);

        Button gameSettingsButton = new Button("Confirm game setup");
        gameSettingsButton.setId("gameSettingsButton");
        gameSettingsButton.setOnMouseClicked(mouseEvent -> controller.sendGameSettings());
        gameSettingsButton.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) controller.sendGameSettings();
        });
        gameSettingsPane.add(gameSettingsButton, 1, 2);

        GridPane towerWizardPane = new GridPane();
        towerWizardPane.setId("towerWizardPane");
        gridContainer.getChildren().add(towerWizardPane);
        setupGrid(towerWizardPane);

        Label towerColorLabel = new Label("Tower color");
        towerWizardPane.add(towerColorLabel, 0, 0);
        Label wizardLabel = new Label("Wizard type");
        wizardLabel.setId("wizardLabel");
        towerWizardPane.add(wizardLabel, 0, 1);

        ChoiceBox<String> colorChoice = new ChoiceBox<>();
        colorChoice.setId("colorChoice");
        colorChoice.getItems().setAll("White", "Grey", "Black");
        colorChoice.setPrefSize(350.0, 50.0);
        colorChoice.setOnAction(actionEvent -> lookup("wizardChoice").requestFocus());
        towerWizardPane.add(colorChoice, 1, 0);
        ChoiceBox<String> wizardChoice = new ChoiceBox<>();
        wizardChoice.setId("wizardChoice");
        wizardChoice.getItems().setAll("Samurai", "Witch", "Mage", "King");
        wizardChoice.setPrefSize(350.0, 50.0);
        wizardChoice.setOnAction(actionEvent -> lookup("towerWizardButton").requestFocus());
        towerWizardPane.add(wizardChoice, 1, 1);
        Button towerWizardButton = new Button("Confirm choices");
        towerWizardButton.setId("towerWizardButton");
        towerWizardButton.setOnMouseClicked(mouseEvent -> {
            controller.sendTowerColor();
            controller.sendWizardType();
        });
        towerWizardButton.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                controller.sendTowerColor();
                controller.sendWizardType();
            }
        });
        towerWizardPane.add(towerWizardButton, 1, 2);
        Label connectionMessage = new Label();
        connectionMessage.setId("connectionMessage");
        connectionMessage.setFont(new Font("Gill Sans MT", 28));
        connectionMessage.setDisable(true);
        towerWizardPane.add(connectionMessage, 1, 3);

        // mainGrid.setGridLinesVisible(true);
        gameSetupScene = new Scene(root);
    }

    public void createMainScene() {
        VBox root = new VBox();
        AnchorPane anchorPane = setupScene(root);
        anchorPane.setBackground(new Background(new BackgroundImage(
                new Image("/general/bg3_unfocused.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true)
        )));
        GridPane mainGrid = new GridPane();
        mainGrid.setId("mainGrid");
        mainGrid.setAlignment(Pos.CENTER);
        anchorPane.getChildren().add(mainGrid);
        AnchorPane.setRightAnchor(mainGrid, 0.0);
        AnchorPane.setBottomAnchor(mainGrid, 0.0);
        AnchorPane.setLeftAnchor(mainGrid, 0.0);
        AnchorPane.setTopAnchor(mainGrid, 0.0);
        mainGrid.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        mainGrid.setHgap(10.0);
        mainGrid.setVgap(10.0);

        mainGrid.add(new ArchipelagoPane(), 1, 1);

        Button debugButton = new Button("DEBUG");
        debugButton.setOnMouseClicked(mouseEvent -> controller.utilityFunction());
        mainGrid.add(debugButton, 0, 0);

        //Player boards
        VBox players = new VBox();
        players.setSpacing(1);
        players.setAlignment(Pos.CENTER);
        players.getChildren().add(new PlayerPane(1, "soadopasd"));
        players.getChildren().add(new PlayerPane(2, "292doih"));
        players.getChildren().add(new PlayerPane(3, "123709asdasd"));
        players.getChildren().add(new PlayerPane(4, "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"));
        mainGrid.add(players, 2, 1);

        mainGrid.setGridLinesVisible(true);
        mainScene = new Scene(root);
    }

    public void setupStage() {
        stage.setTitle("Eriantys AM46");
        stage.getIcons().add(new Image("/general/icon.png"));
        stage.setOnCloseRequest(windowEvent -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really wish to close Eriantys?", ButtonType.YES, ButtonType.NO);
            ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
            if (result.equals(ButtonType.NO)) windowEvent.consume();
            else {
                Platform.exit();
                controller.close();
            }
        });
    }

    private AnchorPane setupScene(VBox root) {
        root.setPrefSize(1366.0, 760.0);
        root.setStyle("-fx-font-size: 14pt");

        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-font-size: 10pt");
        Menu helpMenu = new Menu("Help");
        helpMenu.setId("helpMenu");
        MenuItem about = new MenuItem("About Eriantys");
        about.setOnAction(actionEvent -> {
            Alert aboutDialog = new Alert(Alert.AlertType.INFORMATION);
            aboutDialog.setTitle("About Eriantys AM46");
            aboutDialog.setHeaderText("About Eriantys AM46");
            aboutDialog.setContentText("""
                    Online implementation of the tabletop game Eriantys produced by Cranio Creations.
                    Made by group AM46: Pietro Beghetto, Simone de Donato, Gregorio Dimaglie.
                    Version: v0.9.3
                    Date: 17/05/2022""");
            aboutDialog.showAndWait();
        });
        helpMenu.getItems().add(about);
        menuBar.getMenus().add(helpMenu);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-font-family: 'Gill Sans MT'");
        root.getChildren().addAll(menuBar, anchorPane);
        return anchorPane;
    }

    private void setupGrid(GridPane grid) {
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setHalignment(HPos.RIGHT);
        labelColumn.setHgrow(Priority.SOMETIMES);
        labelColumn.setPrefWidth(300.0);
        ColumnConstraints inputColumn = new ColumnConstraints();
        inputColumn.setHalignment(HPos.LEFT);
        inputColumn.setHgrow(Priority.SOMETIMES);
        inputColumn.setPrefWidth(500.0);
        grid.getColumnConstraints().addAll(labelColumn, inputColumn);
        grid.setPadding(new Insets(50.0, 50.0, 50.0, 50.0));
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(50.0);
        grid.setVgap(50.0);
    }

    public Node lookup(String id) {
        return stage.getScene().lookup("#" + id);
    }

    public Node getContent(Scene scene) {
        return scene.getRoot().getChildrenUnmodifiable().get(1);
    }

    public void fadeIn(Node node) {
        DoubleProperty opacity = node.opacityProperty();
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                new KeyFrame(new Duration(400), new KeyValue(opacity, 1.0))
        );
        fadeIn.play();
    }

    public void fadeOut(Scene scene) {
        DoubleProperty opacity = scene.getRoot().opacityProperty();
        Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                new KeyFrame(new Duration(500), new KeyValue(opacity, 0.0))
        );
        fadeOut.play();
    }

    public void switchToLogin() {
        stage.setScene(loginScene);
        stage.show();
        stage.setTitle("Eriantys AM46: Login");
        fadeIn(getContent(loginScene));
    }

    public void switchToGameSetup() {
        /*
        new Thread(() -> {
            fadeOut(loginScene);
        }).start();
        synchronized (this) {
            try {
                wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */
        stage.setScene(gameSetupScene);
        stage.show();
        stage.setTitle("Eriantys AM46: Game setup");
        fadeIn(getContent(gameSetupScene));

    }

    public void switchToMain() {
        stage.setScene(mainScene);
        stage.show();
        stage.setTitle("Eriantys AM46: Game");
        fadeIn(getContent(mainScene));
        //vvvv DEBUG vvvvv
        ((PlayerPane)this.lookup("playerPane" + 1)).enableSelectAssistant();
        ((PlayerPane)this.lookup("playerPane" + 2)).enableSelectAssistant();
        ((PlayerPane)this.lookup("playerPane" + 3)).enableSelectAssistant();
        ((PlayerPane)this.lookup("playerPane" + 4)).enableSelectAssistant();
    }

}

