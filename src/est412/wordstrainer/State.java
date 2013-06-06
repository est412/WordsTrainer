package est412.wordstrainer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class State {
	StringProperty name = new SimpleStringProperty();
	StringProperty code = new SimpleStringProperty();
	ObjectProperty<Object> link;
	BooleanProperty active = new SimpleBooleanProperty();
}
