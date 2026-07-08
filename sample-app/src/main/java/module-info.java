module mdview.fx.sample.app {
    requires co.bitshifted.mdviewfx;
    requires javafx.controls;
    requires javafx.fxml;

    opens co.bitshifted.mdviewfx.sample to javafx.graphics;
}