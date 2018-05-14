/*
 * @FonoConnectHelper :: main
 * version: b11.4.18
 */
package fonoconnecthelper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Mateus
 */
public class FonoConnectHelper extends Application {
    static Stage mainStage;
    
    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("fch.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setTitle("FonoConnectHelper - version: b11.4.18 @ Mateus");
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
