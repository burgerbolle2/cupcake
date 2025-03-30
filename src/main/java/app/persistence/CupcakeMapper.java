package app.persistence;

import app.entities.Bottom;
import app.entities.Cupcake;
import app.entities.OrderLine;
import app.entities.Top;
import app.exceptions.DatabaseException;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CupcakeMapper {

    public static Top getTopById(int topId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM top WHERE top_id = ?";
        try (Connection conn = connectionPool.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, topId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Top(rs.getInt("top_id"), rs.getString("name"), rs.getDouble("price"));
            } else {
                throw new DatabaseException("Top not found");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error: " + e.getMessage());
        }
    }

    public static Cupcake insertCupcake(Cupcake cupcake, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO cupcake (bottom_id, top_id, price) VALUES (?, ?, ?) RETURNING cupcake_id";

        try (Connection conn = connectionPool.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cupcake.getBottom().getBottomId());
            ps.setInt(2, cupcake.getTop().getTopId());
            ps.setDouble(3, cupcake.getPrice());  // Include price in the insert

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int generatedId = rs.getInt("cupcake_id"); // Get the generated cupcake_id
                cupcake.setCupcakeId(generatedId);  // Set the generated ID for the cupcake
                return cupcake;
            } else {
                throw new DatabaseException("Failed to insert cupcake.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error: " + e.getMessage());
        }
    }


    public static Bottom getBottomById(int bottomId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM bottom WHERE bottom_id = ?";
        try (Connection conn = connectionPool.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bottomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Bottom(rs.getInt("bottom_id"), rs.getString("name"), rs.getDouble("price"));
            } else {
                throw new DatabaseException("Bottom not found");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error: " + e.getMessage());
        }
    }
    public static List<Top> getAllTops(ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM top";
        List<Top> tops = new ArrayList<>();

        try (Connection conn = connectionPool.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tops.add(new Top(rs.getInt("top_id"), rs.getString("name"), rs.getDouble("price")));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error: " + e.getMessage());
        }

        return tops;
    }

    public static List<Bottom> getAllBottoms(ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM bottom";
        List<Bottom> bottoms = new ArrayList<>();

        try (Connection conn = connectionPool.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bottoms.add(new Bottom(rs.getInt("bottom_id"), rs.getString("name"), rs.getDouble("price")));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error: " + e.getMessage());
        }

        return bottoms;
    }

}
