package controller;

import database.AccountData;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import model.Account;

public class RegisterController {

    @FXML private TextField txt_fullname;
    @FXML private TextField txt_email;
    @FXML private TextField txt_user;
    @FXML private PasswordField txt_password;
    @FXML private PasswordField txt_confirm_password;
    @FXML private Label lb_error;

    private AccountData accountData = new AccountData();

    @FXML
    public void initialize() {
        lb_error.setVisible(false);
    }

    @FXML
    private void DangKy(ActionEvent event) {
        String fullname = txt_fullname.getText().trim();
        String email = txt_email.getText().trim();
        String username = txt_user.getText().trim();
        String password = txt_password.getText();
        String confirmPassword = txt_confirm_password.getText();

        if (fullname.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Mật khẩu xác nhận không khớp!");
            return;
        }

        if (accountData.usernameExists(username)) {
            showError("Tên đăng nhập đã tồn tại!");
            return;
        }

        Account newAccount = new Account(fullname, email, username, password, "customer");

        boolean success = accountData.addAccount(newAccount);
        if (success) {
            showSuccess("Đăng ký thành công! Vui lòng đăng nhập.");
            delayThenSwitchToLogin(event, 1500);
        } else {
            showError("Lỗi hệ thống, không thể đăng ký!");
        }
    }

    private void showError(String message) {
        lb_error.setText(message);
        lb_error.setVisible(true);
        lb_error.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        lb_error.setText(message);
        lb_error.setVisible(true);
        lb_error.setStyle("-fx-text-fill: green;");
    }

    private void delayThenSwitchToLogin(ActionEvent event, long millis) {
        Task<Void> waitTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(millis);
                return null;
            }
        };
        waitTask.setOnSucceeded(e -> switchToLogin(event));
        new Thread(waitTask).start();
    }

    private void switchToLogin(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
                Scene scene = new Scene(loader.load());
                stage.setTitle("Đăng nhập cửa hàng nước hoa");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showError("Lỗi khi chuyển về màn hình đăng nhập!");
            }
        });
    }

    @FXML
    private void QuayLaiDangNhap(ActionEvent event) {
        switchToLogin(event);
    }
}
