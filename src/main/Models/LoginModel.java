package main.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.Database.DatabaseConnection;
import main.Entities.Admin;

public class LoginModel {
    public boolean validLogin(Admin admin) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, admin.getUsername());
        statement.setString(2, admin.getPassword());
        ResultSet result = statement.executeQuery();
        return(result.next());
    }

    public String getEmail() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT email FROM admin";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        if(result.next()){
            return(result.getString("Email"));
        }
        return(null);
    }
}
