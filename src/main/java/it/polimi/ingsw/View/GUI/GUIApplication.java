package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.View.GUI.Application.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Class for creating the GUI application and displaying it to the user. Its state is modified by the GUI controller.
 * It implements the singleton design pattern meaning that only one can exist at a time.
 */
public class GUIApplication extends Application implements ObservableGUI{

    /**
     * The static instance of this GUI.
     */
    private static GUIApplication instance;

    /**
     * The main scenes of the GUI.
     */
    private Scene loginScene, gameSetupScene, mainScene;

    /**
     * The log for all actions taken.
     */
    private Log log;

    /**
     * The main stage of the GUI.
     */
    private Stage stage;

    /**
     * The observer of this GUI element.
     */
    private ObserverGUI observer;

    /**
     * Constructor of the GUI application.
     */
    public GUIApplication() {
        instance = this;
    }

    /**
     * Executor service that runs the given runnable in the run later method of the Platform
     */
    public final static Executor runLaterExecutor = Platform::runLater;

    /**
     * Main method to retrieve the instance of the GUI Application.
     * @return The instance of the GUI Application.
     */
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
                Thread.sleep(50); // 450 is actually fine, but it might run slightly slower on some devices so...
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    @Override
    public void setObserver(ObserverGUI observer) {
        this.observer = observer;
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

    /**
     * Creates the login scene, i.e. the screen where the user can type the IP and port to connect, and the nickname with
     * which to connect to the lobby.
     */
    public void createLoginScene() {
        VBox root = new VBox();
        root.setBackground(new Background(new BackgroundImage(
                new Image("/general/bg1_unfocused.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true)
        )));
        AnchorPane anchorPane = setupScene(root);

        VBox gridContainer = new VBox();
        gridContainer.setAlignment(Pos.CENTER);
        gridContainer.setPadding(new Insets(20.0, 0.0, 0.0, 0.0));
        anchorPane.getChildren().add(gridContainer);
        AnchorPane.setRightAnchor(gridContainer, 0.0);
        AnchorPane.setBottomAnchor(gridContainer, 0.0);
        AnchorPane.setLeftAnchor(gridContainer, 0.0);
        AnchorPane.setTopAnchor(gridContainer, 0.0);

        Image iconPlusName = new Image("/general/iconPlusName.png", 0.0, 0.0, true, true);
        ImageView imageView = new ImageView(iconPlusName);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(400);
        imageView.setCache(true);
        anchorPane.getChildren().add(imageView);
        AnchorPane.setLeftAnchor(imageView, 100.0);
        AnchorPane.setTopAnchor(imageView, 150.0);

        Label loginSceneTitle = new Label("Login");
        loginSceneTitle.setFont(Font.font("Eras Demi ITC", 30));
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
                observer.notifyIP();
            }
        });
        ipPane.add(portField, 1, 1);

        Button connectButton = new Button("Connect");
        connectButton.setId("connectButton");
        connectButton.setOnMouseClicked(mouseEvent -> observer.notifyIP());
        ipPane.add(connectButton, 1, 2);

        Label nickLabel = new Label("Nickname");
        nickPane.add(nickLabel, 0, 3);
        TextField nickField = new TextField();
        nickField.setId("nickField");
        nickField.setPrefSize(350.0, 50.0);
        nickField.setPromptText("Insert your unique nickname...");
        nickField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if ( ! ((TextField) (lookup("nickField"))).getText().equals("") ) {
                    observer.notifyNickname();
                }
            }
        });
        nickPane.add(nickField, 1, 3);

        Button nickButton = new Button("Login");
        nickButton.setOnMouseClicked(mouseEvent -> {
            if ( ! ((TextField) (lookup("nickField"))).getText().equals("") ) {
                observer.notifyNickname();
            }
        });
        nickPane.add(nickButton, 1, 4);
        nickPane.setDisable(true);

        loginScene = new Scene(root);
    }

    /**
     * Creates the game setup scene, i.e. the screen where the user can select the game mode, tower color and wizard.
     */
    public void createGameSetupScene() {
        VBox root = new VBox();
        root.setId("gameSetupRoot");
        root.setBackground(new Background(new BackgroundImage(
                new Image("/general/bg7_unfocused.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true)
        )));
        AnchorPane anchorPane = setupScene(root);

        log = new Log();
        anchorPane.getChildren().add(log);
        AnchorPane.setRightAnchor(log, 30.0);
        AnchorPane.setTopAnchor(log, 30.0);

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
        gameSettingsButton.setOnMouseClicked(mouseEvent -> observer.notifyGameSettings());
        gameSettingsButton.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) observer.notifyGameSettings();
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
            observer.notifyTowerColor();
            observer.notifyWizardType();
        });
        towerWizardButton.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                observer.notifyTowerColor();
                observer.notifyWizardType();
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

    /**
     * Creates the main scene of the game, i.e. the screen where the user can play the game.
     */
    public void createMainScene() {
        VBox root = new VBox();
        root.setPrefSize(stage.getWidth(), stage.getHeight());
        root.setStyle("-fx-font-size: 14pt");

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setId("mainContentPane");
        anchorPane.setStyle("-fx-font-family: 'Gill Sans MT'");
        root.getChildren().add(anchorPane);
        setupScrollingBackground(anchorPane);

        GridPane mainGrid = new GridPane();
        mainGrid.setId("mainGrid");
        mainGrid.setAlignment(Pos.TOP_CENTER);
        anchorPane.getChildren().add(mainGrid);
        AnchorPane.setRightAnchor(mainGrid, 0.0);
        AnchorPane.setBottomAnchor(mainGrid, 0.0);
        AnchorPane.setLeftAnchor(mainGrid, 0.0);
        AnchorPane.setTopAnchor(mainGrid, 0.0);
        mainGrid.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
        mainGrid.setHgap(20.0);
        mainGrid.setVgap(10.0);

        // turn order box and log
        mainGrid.add(new TurnOrderPane(), 0, 0);

        // archipelago
        ArchipelagoPane archipelagoPane = new ArchipelagoPane();
        archipelagoPane.setObserver(observer);
        mainGrid.add(archipelagoPane, 0, 1);

        /*
        debug button for merge animation
        Button debugButton = new Button("DEBUG");
        debugButton.setOnMouseClicked(mouseEvent -> controller.debugFunction1());
        mainGrid.add(debugButton, 0, 1);
         */

        //Player boards
        VBox players = new VBox(10.0);
        players.setId("players");
        players.setAlignment(Pos.CENTER);

        mainGrid.add(players, 1, 1);

        //mainGrid.setGridLinesVisible(true);

        mainScene = new Scene(root);
    }

    /**
     * Creates a player by creating its player pane and all of its elements.
     * @param gameMode The game mode of the current game.
     * @param nickname The nickname of the player to which belongs the board.
     * @param nickID The numeric ID associated with this nickname.
     * @param entranceID The ID of the player's entrance
     * @param tablesIDs The IDs of the player's tables.
     * @param towerColor The tower color of the player.
     * @param wizardType The wizard type of the player.
     * @param isMainPlayer True if this player is the player playing on this GUI.
     */
    public void createPlayer(GameMode gameMode, String nickname, int nickID , int entranceID, HashMap<Color,
            Integer> tablesIDs, TowerColor towerColor, WizardType wizardType, boolean isMainPlayer) {

        VBox players = (VBox) this.lookup("players");
        PlayerPane player = new PlayerPane(nickname, nickID,isMainPlayer);
        player.setObserver(observer);
        player.setNickname(nickname);
        player.createBoard(entranceID, tablesIDs, towerColor);
        player.createDiscardCoin(gameMode.equals(GameMode.EXPERT), isMainPlayer, wizardType);
        player.createAssistantContainerPane(isMainPlayer);
        players.getChildren().add(0, player);
    }

    /**
     * Creates the archipelago pane and all of its elements (islands, characters, bags and clouds).
     * @param numOfPlayers The number of players playing this game.
     * @param gameMode The game mode of this game.
     * @param islandIDs The IDs of the island tiles.
     * @param cloudIDs The IDs of the clouds.
     * @param characterIDs The IDs of the characters
     * @param motherNatureIsland The island containing mother nature.
     */
    public void createArchipelago(int numOfPlayers, GameMode gameMode,List<Integer> islandIDs, List<Integer> cloudIDs,
                                  List<Integer> characterIDs, int motherNatureIsland){
        ArchipelagoPane archipelagoPane = ((ArchipelagoPane) this.lookup("archipelagoPane"));
        archipelagoPane.createIslands(motherNatureIsland, islandIDs);
        archipelagoPane.createClouds(numOfPlayers, cloudIDs);
        if(gameMode.equals(GameMode.EXPERT)){
            CharacterDetailPane characterDetailPane = new CharacterDetailPane();
            characterDetailPane.setId("characterDetailPane");
            characterDetailPane.addCharacters(characterIDs);

            mainScene.addEventHandler(KeyEvent.KEY_PRESSED, e ->{
                if (e.getCode() == KeyCode.H && ! characterDetailPane.isActive()) {
                    characterDetailPane.setVisible(true);
                    characterDetailPane.setActive(true);
                    disableGlobal();
                }
                else if((e.getCode() == KeyCode.H || e.getCode() == KeyCode.ESCAPE) && characterDetailPane.isActive()){
                    characterDetailPane.setVisible(false);
                    characterDetailPane.setActive(false);
                    reEnableGlobal();
                }

            });
            AnchorPane.setRightAnchor(characterDetailPane, 50.0);
            AnchorPane.setTopAnchor(characterDetailPane, 50.0);
            AnchorPane mainContentPane = (AnchorPane) this.lookup("mainContentPane");
            mainContentPane.getChildren().add(characterDetailPane);

            archipelagoPane.createCharacterAndHeap(characterIDs);
        }
    }

    /**
     * Setups the stage of the APPLICATION.
     */
    public void setupStage() {
        stage.setMaximized(true);
        stage.setTitle("Eriantys AM46");
        stage.getIcons().add(new Image("/general/icon.png"));
        stage.setOnCloseRequest(windowEvent -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really wish to close Eriantys?", ButtonType.YES, ButtonType.NO);
            ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
            if (result.equals(ButtonType.NO)) windowEvent.consume();
            else {
                Platform.exit();
                observer.notifyClose();
            }
        });
        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (KeyCode.F11.equals(event.getCode())) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
    }

    /**
     * Setups the scene by adding elements to the root.
     * @param root The root of the scene.
     * @return an AnchorPane now nested in the root.
     */
    private AnchorPane setupScene(VBox root) {
        root.setPrefSize(stage.getWidth(), stage.getHeight());
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
                    Version: v1.0.0
                    Date: 28/06/2022""");
            aboutDialog.showAndWait();
        });
        helpMenu.getItems().add(about);
        menuBar.getMenus().add(helpMenu);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setId("mainContentPane");
        anchorPane.setStyle("-fx-font-family: 'Gill Sans MT'");
        root.getChildren().addAll(menuBar, anchorPane);
        return anchorPane;
    }

    /**
     * Setups a grid so that it is correctly placed and sized.
     * @param grid The grid to set up.
     */
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

    /**
     * Setups a scrolling background to the given anchor pane.
     * @param anchorPane The anchor pane that needs a scrolling background.
     */
    public void setupScrollingBackground(AnchorPane anchorPane) {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double bgWidth = screenBounds.getWidth();
        double bgHeight = screenBounds.getHeight();
        Image bgImage1 = new Image("/general/bg6_unfocused.png", bgWidth, bgHeight, false,
                false, false);
        Image bgImage2 = new Image("/general/bg6_flipped.png", bgWidth, bgHeight, false,
                false, false);
        ImageView bg1 = new ImageView(bgImage1);
        ImageView bg2 = new ImageView(bgImage2);
        anchorPane.getChildren().addAll(bg1, bg2);
        TranslateTransition trans1 = new TranslateTransition(Duration.minutes(2.0), bg1);
        trans1.setFromX(0);
        trans1.setToX(bgWidth);
        // trans1.setCycleCount(Animation.INDEFINITE);
        TranslateTransition trans2 = new TranslateTransition(Duration.minutes(2.0), bg2);
        trans2.setFromX(-bgWidth+1.0);
        trans2.setToX(1.0);
        // trans2.setCycleCount(Animation.INDEFINITE);
        ParallelTransition parTrans = new ParallelTransition(trans1, trans2);
        parTrans.setAutoReverse(true);
        parTrans.setCycleCount(Animation.INDEFINITE);
        parTrans.play();
    }

    /**
     * Looks for any node within the scene graph based on the specified ID.
     * If more than one node matches the specified ID, this function returns the first of them.
     * If no nodes are found with this ID, then null is returned.
     * @param id The ID of the object to look.
     * @return The object found, null if none matched the ID.
     */
    public Node lookup(String id) {
        return stage.getScene().lookup("#" + id);
    }

    /**
     * Returns the main content pane of a scene.
     * @param scene The scene in which to look for.
     * @return The main content pane of a scene.
     */
    public Node getContent(Scene scene) {
        return scene.lookup("#mainContentPane");
    }

    /**
     * Applies a fade in animation to a node.
     * @param node The node to fade in.
     */
    public void fadeIn(Node node) {
        DoubleProperty opacity = node.opacityProperty();
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                new KeyFrame(new Duration(500), new KeyValue(opacity, 1.0))
        );
        fadeIn.play();
    }

    /**
     * Applies a fade out animation to a node.
     * @param node The node to fade out.
     */
    public void fadeOut(Node node) {
        DoubleProperty opacity = node.opacityProperty();
        Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                new KeyFrame(new Duration(500), new KeyValue(opacity, 0.0))
        );
        fadeOut.play();
    }

    /**
     * Switches the scene to the login scene.
     */
    public void switchToLogin() {
        stage.setScene(loginScene);
        stage.show();
        stage.setTitle("Eriantys AM46: Login");
    }

    /**
     * Switches the scene to the game setup scene.
     */
    public void switchToGameSetup() {
        stage.setScene(gameSetupScene);
        stage.show();
        stage.setTitle("Eriantys AM46: Game setup");
        fadeIn(getContent(gameSetupScene));
    }

    /**
     * Switches the scene to the main playing scene.
     */
    public void switchToMain() {
        stage.setScene(mainScene);
        ((GridPane) this.lookup("mainGrid")).add(log, 1, 0);
        stage.show();
        stage.setTitle("Eriantys AM46: Game");
        fadeIn(getContent(mainScene));

    }

    /**
     * Disables the main content pane.
     * This method doesn't disable each element individually but instead disables the main content pane;
     * this means that previously enabled elements contained in the pane will still be enabled after calling
     * enableGlobal.
     */
    public void disableGlobal(){
        this.lookup("mainContentPane").setDisable(true);
    }

    /**
     * Enables the main content pane.
     * This method doesn't enable each element individually but instead enables the main content pane;
     * this means that only elements that were originally enabled will be enabled after this method returns,
     * while all other elements will remain disabled.
     */
    public void reEnableGlobal(){
        this.lookup("mainContentPane").setDisable(false);
    }
}

