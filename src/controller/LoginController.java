package controller;

import database.AccountData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.stage.Stage;
import model.Account;

public class LoginController {

    @FXML private TextField txt_user;
    @FXML private PasswordField txt_password_field;
    @FXML private TextField txt_pass;
    @FXML private CheckBox check_show;
    @FXML private Label lb_error;

    private final AccountData accountData = new AccountData();

    //Hiển thị hoặc ẩn mật khẩu
    @FXML
    private void AnHienMatKhau(ActionEvent event) {
        boolean isShow = check_show.isSelected();
        if (isShow) {
            txt_pass.setText(txt_password_field.getText());
            txt_pass.setVisible(true);
            txt_password_field.setVisible(false);
        } else {
            txt_password_field.setText(txt_pass.getText());
            txt_password_field.setVisible(true);
            txt_pass.setVisible(false);
        }
    }

    //Lấy mật khẩu dựa vào checkbox
    private String getPassword() {
        return check_show.isSelected() ? txt_pass.getText() : txt_password_field.getText();
    }

    //Xử lý đăng nhập
    @FXML
    private void DangNhap(ActionEvent event) {
        lb_error.setVisible(false);  // Reset lỗi

        String user = txt_user.getText().trim();
        String pass = getPassword().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập và mật khẩu!");
            return;
        }

        Account account = accountData.login(user, pass);
        if (account != null) {
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader loader;

                if ("admin".equalsIgnoreCase(account.getRole())) {
                    loader = new FXMLLoader(getClass().getResource("/view/main_admin.fxml"));
                    Scene scene = new Scene(loader.load());
                    stage.setTitle("Quản lý cửa hàng nước hoa - Admin");
                    stage.setScene(scene);

                } else {
                    loader = new FXMLLoader(getClass().getResource("/view/main_customer.fxml"));
                    Scene scene = new Scene(loader.load());

                    //Truyền tên khách hàng cho CustomerController
                    controller.CustomerController customerController = loader.getController();
                    customerController.setCustomerName(account.getFullname());

                    stage.setTitle("Cửa hàng nước hoa - Khách hàng");
                    stage.setScene(scene);
                }

                stage.setResizable(true);
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                showError("Lỗi khi tải giao diện chính!");
            }
        } else {
            showError("Sai tên đăng nhập hoặc mật khẩu!");
        }
    }

    //Mở giao diện đăng ký
    @FXML
    private void openRegister(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/register.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setTitle("Đăng ký tài khoản");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi khi tải giao diện đăng ký!");
        }
    }

    private void showError(String msg) {
        lb_error.setText(msg);
        lb_error.setVisible(true);
    }
}
