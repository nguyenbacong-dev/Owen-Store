package database;

import configdatabase.Connect;
import model.Account;

import java.sql.*;

public class AccountData {

    //trả về Account nếu đúng, null nếu sai
    public Account login(String username, String password) {
        String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
        try (Connection c = Connect.getConnect();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, username);
            p.setString(2, password);

            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return getAccountFromResultSet(r);
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi đăng nhập:");
            e.printStackTrace();
        }
        return null;
    }

    //tạo tài khoản mới
    public boolean addAccount(Account account) {
        if (usernameExists(account.getUsername())) return false;

        String sql = "INSERT INTO account (fullname, email, username, password, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = Connect.getConnect();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, account.getFullname());
            p.setString(2, account.getEmail());
            p.setString(3, account.getUsername());
            p.setString(4, account.getPassword());
            p.setString(5, account.getRole());

            return p.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Lỗi khi thêm tài khoản:");
            e.printStackTrace();
        }
        return false;
    }

    //kiểm tra username đã tồn tại (dùng khi đăng ký)
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM account WHERE username = ?";
        try (Connection c = Connect.getConnect();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, username);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) return r.getInt(1) > 0;
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi kiểm tra username:");
            e.printStackTrace();
        }
        return false;
    }

    private Account getAccountFromResultSet(ResultSet r) throws SQLException {
        return new Account(
                r.getString("fullname"),
                r.getString("email"),
                r.getString("username"),
                r.getString("password"),
                r.getString("role")
        );
    }
}
