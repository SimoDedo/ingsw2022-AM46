package it.polimi.ingsw.View.GUI.Application;

import javafx.scene.effect.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Effects {

    public static final Effect disabledAssistantShadow = new DropShadow(PlayerPane.sizeBoardV/15, Color.DIMGREY);
    public static final Effect enabledAssistantShadow = new DropShadow(PlayerPane.sizeBoardV/15, Color.LIGHTCYAN);
    public static final Effect hoveringAssistantShadow = new Glow(0.3);

    public static final Effect disabledStudentShadow = new DropShadow(StudentView.studentSize/2, Color.BLACK);
    public static final Effect enabledStudentShadow = new DropShadow(StudentView.studentSize/2, Color.LIGHTCYAN);
    public static final Effect hoveringStudentShadow = new Glow(3);

    public static final Effect disabledTableGLow = null;
    public static final Effect enabledTableGlow = new DropShadow(PlayerPane.sizeBoardV/15, Color.WHEAT);
    public static final Effect hoveringTableGlow = new DropShadow(PlayerPane.sizeBoardV/15, Color.WHEAT);

    public static final Effect disabledIslandShadow = new DropShadow(IslandTilePane.islandTileSize/12, Color.BLACK);
    public static final Effect enabledIslandShadow = new DropShadow(IslandTilePane.islandTileSize/12, Color.STEELBLUE);
    public static final Effect hoveringIslandShadow = new Glow(0.3);

    public static final Effect disabledCloudShadow = new DropShadow(IslandTilePane.islandTileSize/12, Color.BLACK);
    public static final Effect enabledCloudShadow = new DropShadow(IslandTilePane.islandTileSize/12, Color.WHEAT);
    public static final Effect hoveringCloudShadow = new Glow(0.3);

    public static final Effect disabledCharacterShadow = new DropShadow(CharacterPane.charHeight/10, Color.BLACK);
    public static final Effect enabledCharacterShadow = new DropShadow(CharacterPane.charHeight/10, Color.LIGHTCYAN);
    public static final Effect activatedCharacterShadow = new DropShadow(CharacterPane.charHeight/8, Color.RED);
    public static final Effect hoveringCharacterShadow = new Glow(0.3);

}
