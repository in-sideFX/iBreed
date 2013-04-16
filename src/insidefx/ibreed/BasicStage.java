package insidefx.ibreed;

import insidefx.undecorator.Undecorator;
import insidefx.undecorator.UndecoratorScene;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * Basic Dialog
 *
 * @author arnaud nouard
 */
abstract public class BasicStage extends Stage {

    final Logger LOGGER = Logger.getLogger("iBreed");
    UndecoratorScene scene;

    public BasicStage(final Window owner, String fxml, ResourceBundle rb) {
        this(owner, fxml, rb, Modality.WINDOW_MODAL);
    }

    public BasicStage(final Window owner, String fxml, ResourceBundle rb, Modality modality) {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
            if (rb != null) {
                fxmlLoader.setResources(rb);
            }
            fxmlLoader.setController(this);
            root = (Pane) fxmlLoader.load();
            root.getStylesheets().add("jfxtras/css/JMetroLightTheme.css");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Root UI not found", ex);
        }

        scene = new UndecoratorScene(this, StageStyle.UTILITY, root, null);
        setScene(scene);
        super.initModality(modality);
        super.initOwner(owner);

        // No API to center on stage??
        if (owner != null) {
            super.setOnShown(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    center(owner);
                }
            });
        }
        // Set sizes based on client area's sizes
        Undecorator undecorator = scene.getUndecorator();
        setMinWidth(undecorator.getMinWidth());
        setMinHeight(undecorator.getMinHeight());
        setWidth(undecorator.getPrefWidth());
        setHeight(undecorator.getPrefHeight());
        if (undecorator.getMaxWidth() > 0) {
            setMaxWidth(undecorator.getMaxWidth());
        }
        if (undecorator.getMaxHeight() > 0) {
            setMaxHeight(undecorator.getMaxHeight());
        }
        sizeToScene();
    }

    void center(Window owner) {
//        if (owner != null) {
        double x = owner.getX() + (owner.getWidth() / 2) - (getWidth() / 2);
        double y = owner.getY() + (owner.getHeight() / 2) - (getHeight() / 2);
        super.setX(x);
        super.setY(y);
//        }else{
//            centerOnScreen();
//        }
    }
}
