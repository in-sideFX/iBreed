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

import com.sun.webpane.webkit.JSObject;
import insidefx.ibreed.alert.AlertDialog;
import insidefx.ibreed.confirm.ConfirmDialog;
import insidefx.ibreed.prompt.PromptDialog;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.DragEvent;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.PromptData;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * WebView helper
 *
 * @author arnaud nouard
 */
public class WebViewInjector {

    public static void customize(final Stage stage, final WebView webView) {
        webView.setContextMenuEnabled(false);
        webView.setVisible(false);

        // Disable menu
        final WebEngine webEngine = webView.getEngine();
        /*
         * Drag and drop URL support
         */
        webView.onDragDroppedProperty().addListener(new ChangeListener<EventHandler<? super DragEvent>>() {
            @Override
            public void changed(ObservableValue<? extends EventHandler<? super DragEvent>> ov, EventHandler<? super DragEvent> t0, EventHandler<? super DragEvent> t) {
                // Avoid to catch html5 dnd gestures
                if (((DragEvent)t).getSource() != webView) {
                    String url = ((DragEvent)t).getDragboard().getUrl();
                    if (url != null) {
                        webEngine.load(url.toString());
                    }
                }
            }
        }); 

        webEngine.setConfirmHandler(new Callback<String, Boolean>() {
            @Override
            public Boolean call(String p) {
                ConfirmDialog promptStage = new ConfirmDialog(stage, p);
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
                WebViewStage webViewStage = new WebViewStage(stage);
                webViewStage.setResizable(p.isResizable());
                webViewStage.getWebView().setContextMenuEnabled(p.hasMenu());
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
                promptStage.showAndWait();
                return promptStage.getPromptText();
            }
        });
        webEngine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> t) {
                System.err.println(t.toString());
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
                stage.setWidth(r.getWidth());
                stage.setHeight(r.getHeight());
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
                    // Inject Java Script to Java bridge object
                    JSObject win =
                            (JSObject) webEngine.executeScript("window");
                    //  win.setMember("js2j", new JavaScriptBridge());
                    webView.setVisible(true);
                }
            }
        });
    }
}
