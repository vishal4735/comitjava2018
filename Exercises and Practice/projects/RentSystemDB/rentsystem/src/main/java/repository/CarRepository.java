package repository;

import com.sun.xml.internal.ws.util.xml.ContentHandlerToXMLStreamWriter;
import domain.Car;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CarRepository {

    private void closeJDBCConnection(Connection conn){
        try {
            conn.close();
        }catch (SQLException e){
            System.out.println("Problems during close connection");
        }
    }

    private Connection makeJDBCConnection() {
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/rentsystemclass?serverTimezone=UTC", "root", "root");
            if (connection != null) {
                log("Connection Successful! Enjoy. Now it's time to push data");
            } else {
                log("Failed to make connection!");
            }
        } catch (SQLException e) {
            log("MySQL Connection Failed!");
            e.printStackTrace();
            return null;
        }
        return connection;
    }

    // Simple log utility
    private static void log(String string) {
        System.out.println(string);

    }

    public void addCar(Car car){
        Connection connection = makeJDBCConnection();

        try {
            String sql = "INSERT INTO cars(id,year,plate,model,brand,color) VALUES(?,?,?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, car.getId());
            pstmt.setInt(2, car.getYear());
            pstmt.setString(3, car.getPlate());
            pstmt.setString(4, car.getModel());
            pstmt.setString(5, car.getBrand());
            pstmt.setString(6, car.getColor());

            pstmt.executeUpdate();

        } catch (SQLException e){
            log(e.getMessage());
        } finally {
            closeJDBCConnection(connection);
        }
    }

    public void modifyCar(Car car) throws Exception{
        Connection connection = makeJDBCConnection();

        try{
            String sql = "UPDATE cars set year = ?,plate = ?,model = ?,brand = ?,color = ? WHERE cars.id = ?";

            PreparedStatement pstmt = connection.prepareStatement(sql);

            pstmt.setInt(1, car.getYear());
            pstmt.setString(2, car.getPlate());
            pstmt.setString(3, car.getModel());
            pstmt.setString(4, car.getBrand());
            pstmt.setString(5, car.getColor());
            pstmt.setString(6, car.getId());

            if(pstmt.executeUpdate() == 0){
                throw new Exception("The Update wasn't executed!");
            }
        } catch (SQLException e){
            log(e.getMessage());
        } finally {
            closeJDBCConnection(connection);
        }

    }

    public void removeCar(Car car) throws Exception {
        Connection connection = makeJDBCConnection();

        try{

            String sql = "DELETE FROM cars WHERE cars.id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, car.getId());

            if(pstmt.executeUpdate() == 0){
                throw new Exception("The delete wasn't executed!");
            }
        } catch (SQLException e){
            log(e.getMessage());
        }

    }

    public Car findCarById(String id){
        Car car = null;

        Connection connection = makeJDBCConnection();

        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM cars WHERE cars.id=?");
            statement.setString(1,id);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                int year = resultSet.getInt("year");
                String color = resultSet.getString("color");
                String brand = resultSet.getString("brand");
                String model = resultSet.getString("model");
                String plate = resultSet.getString("plate");

                car = new Car(id, plate, color, year, brand, model);
            }
        } catch(SQLException e){
            log(e.getMessage());
        } finally {
            closeJDBCConnection(connection);
        }

        return car;

    }


    public List<Car> findAll() {
        List<Car> cars = new ArrayList<Car>();
        Connection conn = makeJDBCConnection();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement
                    .executeQuery("SELECT * FROM cars");

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                int year = resultSet.getInt("year");
                String color = resultSet.getString("color");
                String brand = resultSet.getString("brand");
                String model = resultSet.getString("model");
                String plate = resultSet.getString("plate");

                cars.add(new Car(id, plate, color, year, brand, model));
            }

        } catch (SQLException e) {
            log(e.getMessage());
        } finally {
            closeJDBCConnection(conn);
        }
        return cars;
    }
}
