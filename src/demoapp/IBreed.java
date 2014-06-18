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
package demoapp;

import insidefx.ibreed.JavaScriptBridge;
import insidefx.ibreed.WebViewInjector;
import insidefx.undecorator.Undecorator;
import insidefx.undecorator.UndecoratorScene;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.FadeTransitionBuilder;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * iBreed Light Hybrid framework demo.
 *
 * @author arnaud nouard (In-SideFX blog)
 */
public class IBreed extends Application {

    final Logger LOGGER = Logger.getLogger("iBreed");
    @FXML
    private WebView webView;
    @FXML
    private TextField urlTxt;
    @FXML
    private Label dragMeFX;
    @FXML
    private ImageView keyUp;
    @FXML
    private Button btnTime;
    UndecoratorScene undecoratorScene;
    JavaScriptBridge javaScriptBridge;
    FadeTransition fadeOutTransition;

    @Override
    public void start(final Stage stage) throws Exception {
        // FileHandler handler = new FileHandler("iBreed.log");
        // LOGGER.addHandler(handler);

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
        setAsHybrid(stage);
        stage.setTitle("iBreed");
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
     *
     * @param stage
     */
    public void setAsHybrid(Stage stage) {
        // The generic object for JS and JavaFX interop
        javaScriptBridge = new JavaScriptBridge(webView.getEngine());
        javaScriptBridge.fromJSProperty.addListener(new ChangeListener<String>() {
            FadeTransition fillTransition;

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                // Something happened on JS side, so show it!
                if (fillTransition != null) {
                    fillTransition.playFromStart();
                } else {
                    fillTransition = FadeTransitionBuilder.create()
                            .node(keyUp)
                            .duration(Duration.millis(150))
                            .cycleCount(2)
                            .fromValue(0.2)
                            .toValue(1)
                            .autoReverse(true)
                            .build();
                    fillTransition.play();
                }
            }
        });

        // WebView customization (handlers...)
        WebViewInjector.inject(stage, webView, javaScriptBridge);
        // Move the stage when a drag is detected in the webview
        //undecoratorScene.setAsStageDraggable(stage, webView);

        // Hide URL textfield on main UI if needed
        urlTxt.setVisible(!Boolean.getBoolean("ibreed.hideURL"));

        // Default URL to load
        final String url = getClass().getResource("page.html").toExternalForm();
        LOGGER.log(Level.INFO, "Loading: ", url);

        webView.getEngine().load(url);

        // Reflect the current URL in the text field
        webView.getEngine().getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> ov,
                    Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    urlTxt.setText(webView.getEngine().getLocation());
                    urlTxt.setStyle("-fx-background-color:white;;-fx-border-color: #bababa;-fx-border-width: 2px;-fx-border-style: solid;");
                    // urlTxt.setStyle("-fx-background-color:white;");
                } else if (newState == Worker.State.FAILED) {
                    urlTxt.setStyle("-fx-background-color:red;");
                    LOGGER.log(Level.SEVERE, "Error while loading: ", url);
                }
            }
        });

        // Manage URL Textfield visibility
        urlTxt.setOpacity(0);

        urlTxt.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(fadeOutTransition!=null){
                    fadeOutTransition.stop();
                }
                FadeTransition fadeInTransition = FadeTransitionBuilder.create()
                        .duration(Duration.millis(200))
                        .node(urlTxt)
                        .fromValue(urlTxt.getOpacity())
                        .toValue(1)
                        .cycleCount(1)
                        .autoReverse(false)
                        .build();
                fadeInTransition.play();
            }
        });
        urlTxt.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean focused) {
                // Hide on focus lost
                if (!focused) {
                    FadeTransition fadeOutTransition = FadeTransitionBuilder.create()
                            .duration(Duration.millis(600))
                            .node(urlTxt)
                            .fromValue(urlTxt.getOpacity())
                            .toValue(0)
                            .cycleCount(1)
                            .autoReverse(false)
                            .build();
                    fadeOutTransition.play();
                }
            }
        });


        urlTxt.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (!urlTxt.focusedProperty().get()) {
                    fadeOutTransition = FadeTransitionBuilder.create()
                            .duration(Duration.millis(600))
                            .node(urlTxt)
                            .fromValue(urlTxt.getOpacity())
                            .toValue(0)
                            .cycleCount(1)
                            .autoReverse(false)
                            .build();
                    fadeOutTransition.play();
                }
            }
        });


        urlTxt.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (urlTxt.getOpacity() != 1) {
                    urlTxt.setOpacity(1);
                }
            }
        });
        dragMeFX.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent t) {
                dragMeFX.setOpacity(1); // Remove drag effect

            }
        });
        dragMeFX.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent t) {
                dragMeFX.setOpacity(1); // Remove drag effect
            }
        });
    }

    @FXML
    private void handleDragDetected(MouseEvent event) {
        event.setDragDetect(true);
        Dragboard startDragAndDrop = dragMeFX.startDragAndDrop(TransferMode.COPY);
        ClipboardContent clipboardContent = new ClipboardContent();
        dragMeFX.setOpacity(0.4); // Drag effect
        URI toURI;
        try {
            toURI = getClass().getResource("up_64_FX.png").toURI(); // Harcoded currently
            clipboardContent.putString(toURI.toURL().toExternalForm());
            startDragAndDrop.setContent(clipboardContent);
        } catch (URISyntaxException | MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, "Drag init failed", ex);
        }

        event.consume();
    }

    @FXML
    private void handleDragDropped(DragEvent event) {
        dragMeFX.setOpacity(1); // Remove drag effect
    }
    @FXML
    private void handleDragDone(DragEvent event) {
        dragMeFX.setOpacity(1); // Remove drag effect
    }

    @FXML
    private void handleOnMouseDragged(MouseEvent event) {
        // Do nothing onthis draggable node: to avoid the stage to be dragged
        event.consume();
    }

    /**
     * If user enters new url
     *
     * @param event
     */
    @FXML
    private void onUrlTxtChanged(ActionEvent event) {
        String url = urlTxt.getText();
        webView.getEngine().load(url);
    }

    @FXML
    private void onTimeClicked(ActionEvent event) {
        javaScriptBridge.sendToJS("sentFromJava(\"JavaFX gives time: " + Calendar.getInstance().getTime().toString() + "\")");
    }

    public static void main(String[] args) {
        launch(args);
    }
}