module es.cifpcarlos3.spellwalker {
    requires javafx.controls;
    requires javafx.fxml;


    opens es.cifpcarlos3.spellwalker to javafx.fxml;
    exports es.cifpcarlos3.spellwalker;
}