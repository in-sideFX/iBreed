package insidefx.ibreed;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * A simple WebView with an "Undecorator" stage
 *
 * @author arnaud nouard
 */
public class WebViewStage extends BasicStage {

    @FXML
    private WebView webView;
    static final public int WINDOW_OFFSET=35;
    
    public WebViewStage(Window owner) {
        super(null, "webviewstage.fxml", null, Modality.NONE);  // No owner since new Stage

        setAsHybrid();
        
        // Add offset to avoid superpostion
        double x = owner.getX();
        double y = owner.getY();
        super.setX(x + WINDOW_OFFSET);
        super.setY(y + WINDOW_OFFSET);
        show();
    }

    protected final void setAsHybrid() {
        WebViewInjector.inject(this, webView, null);
    }

    public WebView getWebView() {
        return webView;
    }

    public WebEngine getWebEngine() {
        return webView.getEngine();
    }
}
