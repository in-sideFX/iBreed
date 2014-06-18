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

import insidefx.ibreed.alert.AlertDialog;
import insidefx.ibreed.confirm.ConfirmDialog;
import insidefx.ibreed.prompt.PromptDialog;
import insidefx.undecorator.Undecorator;
import insidefx.undecorator.UndecoratorScene;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.PromptData;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import netscape.javascript.JSObject;

/**
 * WebView helper Manage all handlers, JS 2 Java bridge, page loading
 *
 * @author arnaud nouard
 */
public class WebViewInjector {

    static final Logger LOGGER = Logger.getLogger("iBreed");

    public static void inject(final Stage stage, final WebView webView, final JavaScriptBridge js2JavaBridge) {
        inject(stage, webView, js2JavaBridge, null);
    }

    public static void inject(final Stage stage, final WebView webView, final JavaScriptBridge js2JavaBridge, final String customCSS) {
        // Default settings
        webView.setContextMenuEnabled(false);
        webView.setVisible(false);
        webView.setFontSmoothingType(FontSmoothingType.LCD);

//        webView.setOnTouchMoved(new EventHandler<TouchEvent>() {
//            @Override
//            public void handle(TouchEvent t) {
//               // System.err.print(" touched ");
//            }
//        });
        // Gestures
        webView.setOnSwipeLeft(new EventHandler<SwipeEvent>() {
            @Override
            public void handle(SwipeEvent t) {
                WebEngine engine = webView.getEngine();
                if (engine.getHistory().getCurrentIndex() > 0) {
                    engine.getHistory().go(engine.getHistory().getCurrentIndex() - 1);
                }
            }
        });
        webView.setOnSwipeRight(new EventHandler<SwipeEvent>() {
            @Override
            public void handle(SwipeEvent t) {
                WebEngine engine = webView.getEngine();
                if (engine.getHistory().getCurrentIndex() < engine.getHistory().getMaxSize()) {
                    engine.getHistory().go(engine.getHistory().getCurrentIndex() + 1);
                }
            }
        });

        // Disable menu
        final WebEngine webEngine = webView.getEngine();
        /*
         * Drag and drop URL support
         */
//        webView.onDragDroppedProperty().addListener(new ChangeListener<EventHandler<? super DragEvent>>() {
//            @Override
//            public void changed(ObservableValue<? extends EventHandler<? super DragEvent>> ov, EventHandler<? super DragEvent> t0, EventHandler<? super DragEvent> t) {
//                // Avoid to catch html5 dnd gestures
//                if (((DragEvent) t).getSource() != webView) {
//                    String url = ((DragEvent) t).getDragboard().getUrl();
//                    if (url != null) {
//                        webEngine.load(url.toString());
//                    }
//                }
//            }
//        });

        webEngine.setConfirmHandler(new Callback<String, Boolean>() {
            @Override
            public Boolean call(String p) {
                ConfirmDialog promptStage = new ConfirmDialog(stage, p);
                  if (customCSS != null) {
                    ((UndecoratorScene) promptStage.getScene()).removeDefaultStylesheet();
                    ((UndecoratorScene) promptStage.getScene()).getUndecorator().getStylesheets().add(customCSS);    // For background color
                    ((UndecoratorScene) promptStage.getScene()).getUndecorator().getStageDecorationNode().getStylesheets().add(customCSS); // For decoration
                }
                promptStage.showAndWait();
                return promptStage.getAnswser();
            }
        });
        /*
         *  When a page requests for a popup window 
         */
        webEngine.setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>() {
            @Override
            public WebEngine call(PopupFeatures p) {
                WebViewStage webViewStage = new WebViewStage(stage,customCSS);
                webViewStage.setResizable(p.isResizable());
                webViewStage.getWebView().setContextMenuEnabled(p.hasMenu());
                if (customCSS != null) {
                    ((UndecoratorScene) webViewStage.getScene()).removeDefaultStylesheet();
                    ((UndecoratorScene) webViewStage.getScene()).getUndecorator().getStylesheets().add(customCSS);    // For background color
                    ((UndecoratorScene) webViewStage.getScene()).getUndecorator().getStageDecorationNode().getStylesheets().add(customCSS); // For decoration
                }
                return webViewStage.getWebEngine();
            }
        });
        /*
         *  When a page wants an alert window
         */
        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> msg) {
                AlertDialog alertStage = new AlertDialog(stage, msg.getData());
              if (customCSS != null) {
                    ((UndecoratorScene) alertStage.getScene()).removeDefaultStylesheet();
                    ((UndecoratorScene) alertStage.getScene()).getUndecorator().getStylesheets().add(customCSS);    // For background color
                    ((UndecoratorScene) alertStage.getScene()).getUndecorator().getStageDecorationNode().getStylesheets().add(customCSS); // For decoration
                }
                alertStage.show();
            }
        });
        /*
         *  When a page prompts the user
         */
        webEngine.setPromptHandler(new Callback<PromptData, String>() {
            @Override
            public String call(PromptData p) {
                PromptDialog promptStage = new PromptDialog(stage, p.getMessage(), p.getDefaultValue());
               if (customCSS != null) {
                    ((UndecoratorScene) promptStage.getScene()).removeDefaultStylesheet();
                    ((UndecoratorScene) promptStage.getScene()).getUndecorator().getStylesheets().add(customCSS);    // For background color
                    ((UndecoratorScene) promptStage.getScene()).getUndecorator().getStageDecorationNode().getStylesheets().add(customCSS); // For decoration
                }
                promptStage.showAndWait();
                return promptStage.getPromptText();
            }
        });
        webEngine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> t) {
                // TODO: Fire event
                //System.out.println(t.toString());
            }
        });
        /*
         *  When:
         * window.innerWidth, window.innerHeight,
         window.outerWidth, window.outerHeight,
         window.screenX, window.screenY,
         window.screenLeft, window.screenTop     
         */
        webEngine.setOnResized(new EventHandler<WebEvent<javafx.geometry.Rectangle2D>>() {
            @Override
            public void handle(WebEvent<javafx.geometry.Rectangle2D> ev) {
                Rectangle2D r = ev.getData();
                if (stage.getScene() instanceof UndecoratorScene) {
                    // Undecorator adds borders for shadow and resize
                    Undecorator undecorator = ((UndecoratorScene) stage.getScene()).getUndecorator();
                    int shadowBorderSize = undecorator.getShadowBorderSize();
                    stage.setWidth(r.getWidth() + shadowBorderSize);
                    stage.setHeight(r.getHeight() + shadowBorderSize);
                } else {
                    stage.setWidth(r.getWidth());
                    stage.setHeight(r.getHeight());
                }
            }
        });

        /*
         * Load management
         */
        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> ov,
                    Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    try {
                        if (js2JavaBridge != null) {
                            // Inject Java Script to Java bridge object automatically
                            // “window.toJava” 
                            JSObject win = (JSObject) webEngine.executeScript("window");
                            win.setMember("toJava", js2JavaBridge);

                            //  Errors management (optional)
                            if (win.getMember("onerror") == null) {
                                webEngine.executeScript("window.onerror=function(e) { window.status=e;}");
                            }
                        }
                        webView.setVisible(true);

                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "While trying to inject JS2J object", ex);
                    }
                }
            }
        });
    }
}
