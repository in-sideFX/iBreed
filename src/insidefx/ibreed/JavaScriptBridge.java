package insidefx.ibreed;

import com.sun.webpane.webkit.JSObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.web.WebEngine;

/**
 *  Generic Java 2 JavaSCript bridge
 */
public class JavaScriptBridge {

    String fromJS;
    public SimpleStringProperty fromJSProperty;
    WebEngine   webEngine;
    
    public JavaScriptBridge(WebEngine we) {
        webEngine=we;
        
        fromJSProperty = new SimpleStringProperty(fromJS);
    }

    /**
     * Call from html page. Set the value into the property in order to fire
     * change events
     *
     * @param msgFromJS
     */
    public void sendFromJS(String msgFromJS) {
        fromJSProperty.set(msgFromJS);
    }
    /**
     * Execute the "jsToExecute" on the current page 
     * @param jsToExecute
     * @return the returned value from JavaScript if any
     */
    public Object sendToJS(String jsToExecute) {

        Object obj = webEngine.executeScript(jsToExecute);
        return obj;
    }
}
