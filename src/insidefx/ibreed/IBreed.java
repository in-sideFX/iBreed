/*
 * BSD
 * Copyright (c) 2013, Arnaud Nouard
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * Neither the name of the In-SideFX nor the
 names of its contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package insidefx.ibreed;

import insidefx.undecorator.Undecorator;
import insidefx.undecorator.UndecoratorScene;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Why? Reuse instead of rewriting. Leverage JavaFX platform (desktop
 * integration, file system, back-end, frameworks...) and HTML5 universal
 * content.
 *
 * @author arnaud nouard (In-SideFX blog)
 */
public class IBreed extends Application {

    @FXML
    private WebView webView;
    @FXML
    private TextField urlTxt;
    UndecoratorScene undecoratorScene;
    public static final Logger LOGGER = Logger.getLogger("iBreed");

    @Override
    public void start(final Stage stage) throws Exception {
        Pane root = null;
        // UI part of the decoration
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ibreed.fxml"));
            fxmlLoader.setController(this);
            root = (Pane) fxmlLoader.load();
            root.getStylesheets().add("jfxtras/css/JMetroLightTheme.css");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Root UI not found", ex);
        }

        undecoratorScene = new UndecoratorScene(stage, root);
        undecoratorScene.setFadeInTransition();
        initUI(stage);
        stage.setScene(undecoratorScene);
        stage.sizeToScene();
        stage.toFront();
        stage.centerOnScreen();

        /*
         * Fade transition on window closing request
         */
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                t.consume();    // Do not hide
                undecoratorScene.setFadeOutTransition();
            }
        });

        // Set minimum size based on client area's minimum sizes
        Undecorator undecorator = undecoratorScene.getUndecorator();
        stage.setMinWidth(undecorator.getMinWidth());
        stage.setMinHeight(undecorator.getMinHeight());

        stage.setWidth(undecorator.getPrefWidth());
        stage.setHeight(undecorator.getPrefHeight());
        if (undecorator.getMaxWidth() > 0) {
            stage.setMaxWidth(undecorator.getMaxWidth());
        }
        if (undecorator.getMaxHeight() > 0) {
            stage.setMaxHeight(undecorator.getMaxHeight());
        }
        stage.show();
    }
    /**
     * Customize the user interface
     * @param stage 
     */
    public void initUI(Stage stage) {
        WebViewInjector.customize(stage, webView);

        //webView.getEngine().load("file:///C:/work/dev/BootMetro/bootmetro-0.6.0/hub.html");
        // Default URL to load
        String url = getClass().getResource("page.html").toString();
        webView.getEngine().load(url);

        // Reflect the current URL in the text field
        webView.getEngine().getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> ov,
                    Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    urlTxt.setText(webView.getEngine().getLocation());
                }
            }
        });
    }

    /**
     * If user enter new url
     * @param event
     */
    @FXML
    private void onUrlTxtChanged(ActionEvent event) {
        String url = urlTxt.getText();
        webView.getEngine().load(url);
    }
}