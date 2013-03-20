package insidefx.ibreed;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Window;

/**
 * A simple WebView in an "Undecorator" stage
 * @author arnaud nouard
 */
public class WebViewStage extends BasicStage {

    @FXML
    private WebView webView;

    public WebViewStage(Window owner) {
        super(owner, "ibreed.fxml", null);
        initUI();
        show();
    }

    public void initUI() {
        WebViewInjector.customize(this, webView);
    }

    public WebView getWebView() {
        return webView;
    }

    public WebEngine getWebEngine() {
        return webView.getEngine();
    }
}
