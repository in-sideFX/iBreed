package insidefx.ibreed.confirm;

import insidefx.ibreed.BasicStage;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

/**
 *
 * @author arnaud nouard
 */
public class ConfirmDialog extends BasicStage {

    @FXML
    private TextArea confirmTextArea;
    String message;
    Boolean answer;

    public ConfirmDialog(Window owner, String message) {
                super(owner,"ConfirmDialog.fxml",ResourceBundle.getBundle("insidefx/ibreed/confirm/ConfirmDialog"));

        this.message = message;
        initUI();
    }

    void initUI() {
        confirmTextArea.setText(message);
    }
    public Boolean getAnswser(){
        return answer;
    }
    @FXML
    private void handleOkButton(ActionEvent event) {
        super.hide();
        answer=Boolean.TRUE;
    }
     @FXML
    private void handleCancelButton(ActionEvent event) {
        super.hide();
        answer=Boolean.FALSE;
    }
}
