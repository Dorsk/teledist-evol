module fr.actia.teledist.evol {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires com.google.gson;
    requires org.kohsuke.github.api;
    requires scribejava.core;
    requires okhttp3;
    requires unboundid.ldapsdk;
    requires org.postgresql.jdbc;

    opens fr.actia.teledist.evol to javafx.fxml;
    exports fr.actia.teledist.evol;
}
