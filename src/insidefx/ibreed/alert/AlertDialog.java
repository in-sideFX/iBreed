package insidefx.ibreed.alert;

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
public class AlertDialog extends BasicStage {

    @FXML
    private TextArea alertTextArea;
    String message;

    public AlertDialog(Window owner, String msg) {
        super(owner, "AlertDialog.fxml", ResourceBundle.getBundle("insidefx/ibreed/alert/AlertDialog"));
        message = msg;
        initUI();
    }

    void initUI() {
        alertTextArea.setText(message);
    }

    @FXML
    private void handleOkButton(ActionEvent event) {
        super.hide();
    }
}
