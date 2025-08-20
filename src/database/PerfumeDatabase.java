package database;

import configdatabase.Connect;
import model.Perfume;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerfumeDatabase {

    //lấy tất cả sản phẩm từ bảng PRODUCT
    public List<Perfume> getAllPerfumes() {
        List<Perfume> list = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCT";

        try (Connection conn = Connect.getConnect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapToPerfume(rs));
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lấy danh sách nước hoa:");
            e.printStackTrace();
        }
        return list;
    }

    //tìm kiếm nước hoa theo mã hoặc tên
    public List<Perfume> searchPerfume(String keyword) {
        List<Perfume> list = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCT WHERE NAME LIKE ? OR MASP LIKE ?";

        try (Connection conn = Connect.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToPerfume(rs));
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm kiếm nước hoa:");
            e.printStackTrace();
        }
        return list;
    }

    //thêm nước hoa mới vào bảng
    public boolean addPerfume(Perfume p) {
        String sql = "INSERT INTO PRODUCT (MASP, NAME, QUANTITY, PRICE, FRAGRANCE, PRODUCTIMAGEPATH) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Connect.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getCode());
            stmt.setString(2, p.getName());
            stmt.setInt(3, p.getQuantity());
            stmt.setDouble(4, p.getPrice());
            stmt.setString(5, p.getScent());
            stmt.setString(6, p.getImagePath());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi thêm nước hoa:");
            e.printStackTrace();
        }
        return false;
    }

    //cập nhật thông tin nước hoa
    public boolean updatePerfume(Perfume p) {
        String sql = "UPDATE PRODUCT SET NAME = ?, QUANTITY = ?, PRICE = ?, FRAGRANCE = ?, PRODUCTIMAGEPATH = ? WHERE MASP = ?";

        try (Connection conn = Connect.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getName());
            stmt.setInt(2, p.getQuantity());
            stmt.setDouble(3, p.getPrice());
            stmt.setString(4, p.getScent());
            stmt.setString(5, p.getImagePath());
            stmt.setString(6, p.getCode());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi cập nhật nước hoa:");
            e.printStackTrace();
        }
        return false;
    }

    //xóa nước hoa theo mã
    public boolean deletePerfume(String code) {
        String sql = "DELETE FROM PRODUCT WHERE MASP = ?";

        try (Connection conn = Connect.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xóa nước hoa:");
            e.printStackTrace();
        }
        return false;
    }

    //hàm chuyển đổi 1 dòng ResultSet sang đối tượng Perfume
    private Perfume mapToPerfume(ResultSet rs) throws SQLException {
        return new Perfume(
                rs.getString("MASP"),
                rs.getString("NAME"),
                rs.getInt("QUANTITY"),
                rs.getDouble("PRICE"),
                rs.getString("FRAGRANCE"),
                rs.getString("PRODUCTIMAGEPATH") // Đây là đường dẫn ảnh được lưu
        );
    }
}
