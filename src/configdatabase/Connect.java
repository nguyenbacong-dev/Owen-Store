package configdatabase;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connect {
    public static Connection getConnect() throws Exception {
        String url = "jdbc:ucanaccess://C:/Users/bacon/eclipse-workspace/NguyenBaCong_687542/resources/NguyenBaCong687542.accdb";
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        return DriverManager.getConnection(url);
    }
}
