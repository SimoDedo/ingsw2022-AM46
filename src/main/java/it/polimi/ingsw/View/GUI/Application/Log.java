package it.polimi.ingsw.View.GUI.Application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Text;


public class Log extends TextArea {
    private StringBuilder log;

    public Log() {
        log = new StringBuilder("Log");
        this.skinProperty().addListener(new ChangeListener<Skin<?>>() { //TODO: this should be done with CSS, for now it works

            @Override
            public void changed(
                    ObservableValue<? extends Skin<?>> ov, Skin<?> t, Skin<?> t1) {
                if (t1 != null && t1.getNode() instanceof Region) {
                    Region r = (Region) t1.getNode();
                    r.setBackground(Background.EMPTY);

                    r.getChildrenUnmodifiable().stream().
                            filter(n -> n instanceof Region).
                            map(n -> (Region) n).
                            forEach(n -> n.setBackground(Background.EMPTY));

                    r.getChildrenUnmodifiable().stream().
                            filter(n -> n instanceof Control).
                            map(n -> (Control) n).
                            forEach(c -> c.skinProperty().addListener(this));
                }
            }
        });

        this.setPrefSize(500, 100);
        //this.setStyle("-fx-background-color: rgba(0,163,255,0);");
        this.setStyle("-fx-control-inner-background: rgba(0,163,255,0);");
        this.addText("testasdniopniopasdnopasdnopasdnopasdnopasdnopasdnopnopasdnopasdnopasdnopasdnopasdnopasdasdasdasdasdasdasdasdasdasdasdasdasd");
        this.addText("test");
        this.addText("test");
        this.addText("test");
        this.addText("test");
        this.addText("test");
        this.addText("test");
    }

    public void addText(String message){
        log.insert(0, message + "\n");
        this.setText(log.toString());
        this.setScrollTop(Double.MAX_VALUE);
    }
}
