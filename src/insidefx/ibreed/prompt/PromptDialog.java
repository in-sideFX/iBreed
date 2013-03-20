package insidefx.ibreed.prompt;

import insidefx.ibreed.BasicStage;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

/**
 *
 * @author arnaud nouard
 */
public class PromptDialog extends BasicStage {

    @FXML
    private TextArea promptTextArea;
    @FXML
    private Label label;
    String message,defaultPrompt;

    public PromptDialog(Window owner, String message, String defaultPrompt) {
                super(owner,"PromptDialog.fxml",ResourceBundle.getBundle("insidefx/ibreed/prompt/PromptDialog"));

        this.message = message;
        this.defaultPrompt=defaultPrompt;
        
        initUI();
    }

    void initUI() {
        label.setText(message);
        promptTextArea.setPromptText(defaultPrompt);
    }
    public String getPromptText(){
        return promptTextArea.getText();
    }
    @FXML
    private void handleOkButton(ActionEvent event) {
        super.hide();
    }
}
