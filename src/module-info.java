/**
 * Файл, отвечающий за пивязку используемых библиотек к пакету с проектами.
 */
module testfx {
    requires javafx.controls;
    requires javafx.graphics;
    requires org.apache.logging.log4j;
    requires jdk.unsupported.desktop;
    exports com.example;
}