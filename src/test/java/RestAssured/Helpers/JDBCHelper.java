package RestAssured.Helpers;

import RestAssured.DTO.ResponseObject;

import java.sql.*;

/**
 * Шаблонный пример обращения к БД.
 * Пример описывает правильное закрытие ресурсов. Под ресурсом подразумевается соединение с БД.
 * В main описан пример вызова метода выполнения запроса.
 */

public class JDBCHelper {

    private String url = "jdbc:oracle:thin:@//rnd-db-01.ftc.ru:1548/b0d3238";
    private String user = "userName";
    private String password = "password";
    private String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";


    public Object execute(String request) {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement st = conn.createStatement();
             ResultSet result = st.executeQuery(request)) {
            if (result.next()) {
                return ResponseObject.builder()
                        .chrome(result.getString("chrome"))
                        .safari(result.getString("safari"))
                        .firefox(result.getString("firefox"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("Отсутствуют данные удовлетворяющие запрос: " + request);
    }

    public static void main(String[] args) {
        ResponseObject responseObject = (ResponseObject) new JDBCHelper().execute("select * from tableName");
    }
}
