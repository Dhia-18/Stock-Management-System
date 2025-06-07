package main.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.Database.DatabaseConnection;
import main.Entities.Admin;

public class AdminModel {
    public Admin loadAdmin() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT * FROM admin";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        connection.close();
        return(result.next() ? new Admin(result.getString("username"), result.getString("password"), result.getString("first_name"), result.getString("last_name"), result.getString("email"), result.getString("avatarPath")) : null);
    }

    public void updateAdmin(Admin user) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "UPDATE admin SET password = ?, first_name = ?, last_name = ?, email = ?, avatarPath = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, user.getPassword());
        statement.setString(2, user.getFirstName());
        statement.setString(3, user.getLastName());
        statement.setString(4, user.getEmail());
        statement.setString(5, user.getAvatarPath());
        statement.executeUpdate();

        connection.close();
    }
}
