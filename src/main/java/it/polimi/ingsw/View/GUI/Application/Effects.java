package it.polimi.ingsw.View.GUI.Application;

import javafx.scene.effect.*;
import javafx.scene.paint.Color;

/**
 * Utility class that stores various pre-built effects (e.g. drop shadows and glows) for the GUI elements.
 */
public class Effects {

    public static final Effect disabledAssistantEffect = new DropShadow(PlayerPane.sizeBoardV/15, Color.DIMGREY);
    public static final Effect enabledAssistantEffect = new DropShadow(PlayerPane.sizeBoardV/15, Color.CORAL);
    public static final Effect hoveringAssistantEffect = new Glow(0.3);

    public static final Effect disabledStudentEffect = new DropShadow(StudentView.studentSize/2, Color.BLACK);
    public static final Effect enabledStudentEffect = new DropShadow(StudentView.studentSize/2, Color.LIGHTCYAN);
    public static final Effect hoveringStudentEffect = new Glow(3);

    public static final Effect disabledTableEffect = null;
    public static final Effect enabledTableEffect = new DropShadow(PlayerPane.sizeBoardV/15, Color.WHEAT);
    public static final Effect hoveringTableEffect = new DropShadow(PlayerPane.sizeBoardV/15, Color.WHEAT);

    public static final Effect disabledIslandEffect = new DropShadow(IslandTilePane.islandTileSize/12, Color.BLACK);
    public static final Effect enabledIslandEffect = new DropShadow(IslandTilePane.islandTileSize/12, Color.FORESTGREEN);
    public static final Effect hoveringIslandEffect = new Glow(0.3);

    public static final Effect disabledCloudEffect = new DropShadow(IslandTilePane.islandTileSize/12, Color.BLACK);
    public static final Effect enabledCloudEffect = new DropShadow(IslandTilePane.islandTileSize/12, Color.STEELBLUE);
    public static final Effect hoveringCloudEffect = new Glow(0.3);

    public static final Effect disabledCharacterEffect = new DropShadow(CharacterPane.charHeight/10, Color.BLACK);
    public static final Effect enabledCharacterEffect = new DropShadow(CharacterPane.charHeight/10, Color.LIGHTCYAN);
    public static final Effect activatedCharacterEffect = new DropShadow(CharacterPane.charHeight/8, Color.RED);
    public static final Effect hoveringCharacterEffect = new Glow(0.3);

}
