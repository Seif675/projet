package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SingletonConnection {
    private static Connection connect;
    private static Properties props = new Properties();

    static {
        try (InputStream input = SingletonConnection.class.getClassLoader().getResourceAsStream("dao/conf.properties")) {
            props.load(input);
            String url = props.getProperty("jdbc.url");
            String user = props.getProperty("jdbc.user");
            String password = props.getProperty("jdbc.password");

            connect = DriverManager.getConnection(url, user, password);
            connect.setAutoCommit(true);
        } catch (IOException | SQLException e) {
            throw new ExceptionInInitializerError("Erreur d'initialisation: " + e.getMessage());
        }
    }

    public static synchronized Connection getInstance() throws SQLException {
        if (connect == null || connect.isClosed()) {
            String url = props.getProperty("jdbc.url");
            String user = props.getProperty("jdbc.user");
            String password = props.getProperty("jdbc.password");
            connect = DriverManager.getConnection(url, user, password);
        }
        return connect;
    }
}
