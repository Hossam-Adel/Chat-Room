
package client;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 *
 * @author Hossam
 */
public class ComboBoxFactory implements Callback<ListView<String>, ListCell<String>> {
    @Override
    public ListCell<String> call(ListView<String> param) {
        return new ListCell<String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (status != null && !empty) {

                    try {
//                        ImageView view = new ImageView(new Image(setIcon(item).toURI().toURL().toString(), 100, 100, true, true));
                        Circle circle = setIcon(status);
                        HBox rect = new HBox();
                        Text text = new Text(status);
                        rect.getChildren().addAll(circle, text);
                        setGraphic(rect);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } else {
                    setGraphic(null);

                }
            }
        };
    }
    public Circle setIcon(String status) {
        Circle stat= new Circle() ;
        if (status.equals("online")) {
             stat = new Circle(5.0,Color.GREEN);

        } else if (status.equals("busy")) {
             stat = new Circle(5.0,Color.YELLOW);
            
        }else if(status.equals("away")){
             stat = new Circle(5.0,Color.ORANGE);
        }else if(status.equals("offline")){
             stat = new Circle(5.0,Color.RED);
        }
        return stat;
    }
}
