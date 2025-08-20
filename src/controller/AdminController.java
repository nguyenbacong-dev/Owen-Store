package controller;

import database.PerfumeDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Perfume;

import java.io.File;

public class AdminController {

    @FXML private TableView<Perfume> tableProduct;
    @FXML private TableColumn<Perfume, String> col_code, col_name, col_fragrant;
    @FXML private TableColumn<Perfume, Integer> col_quan;
    @FXML private TableColumn<Perfume, Double> col_price;
    @FXML private TableColumn<Perfume, String> col_image;

    @FXML private TextField txt_search, txt_code, txt_name, txt_quan, txt_price, txt_fragrant;
    @FXML private ImageView image;
    @FXML private Label lbl_greeting;

    private String imagePath;
    private final ObservableList<Perfume> products = FXCollections.observableArrayList();
    private final PerfumeDatabase db = new PerfumeDatabase();

    @FXML
    public void initialize() {
        col_code.setCellValueFactory(new PropertyValueFactory<>("code"));
        col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        col_quan.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        col_fragrant.setCellValueFactory(new PropertyValueFactory<>("scent"));
        col_image.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

        col_image.setCellFactory(col -> new TableCell<>() {
            private final ImageView imgView = new ImageView();
            {
                imgView.setFitWidth(100);
                imgView.setFitHeight(80);
                imgView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null) {
                    setGraphic(null);
                } else {
                    imgView.setImage(new Image("file:" + path));
                    setGraphic(imgView);
                }
            }
        });

        tableProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txt_code.setText(newVal.getCode());
                txt_name.setText(newVal.getName());
                txt_quan.setText(String.valueOf(newVal.getQuantity()));
                txt_price.setText(String.valueOf(newVal.getPrice()));
                txt_fragrant.setText(newVal.getScent());

                imagePath = newVal.getImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        image.setImage(new Image("file:" + imagePath));
                    } catch (Exception e) {
                        image.setImage(null);
                    }
                } else {
                    image.setImage(null);
                }
            }
        });

        loadProducts();
    }

    public void setAdminName(String name) {
        if (lbl_greeting != null) {
            lbl_greeting.setText("Xin chào, " + name + "!");
        }
    }

    private void loadProducts() {
        products.setAll(db.getAllPerfumes());
        tableProduct.setItems(products);
    }

    @FXML
    private void addProduct() {
        try {
            String code = txt_code.getText().trim();
            String name = txt_name.getText().trim();
            int quantity = Integer.parseInt(txt_quan.getText().trim());
            double price = Double.parseDouble(txt_price.getText().trim());
            String scent = txt_fragrant.getText().trim();
            String img = (imagePath == null || imagePath.isEmpty()) ? "" : imagePath;

            Perfume p = new Perfume(code, name, quantity, price, scent, img);
            if (db.addPerfume(p)) {
                loadProducts();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Mã sản phẩm đã tồn tại hoặc lỗi khi thêm.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng nhập đúng định dạng số cho Số lượng và Giá.");
        }
    }

    @FXML
    private void updateProduct() {
        try {
            String code = txt_code.getText().trim();
            String name = txt_name.getText().trim();
            int quantity = Integer.parseInt(txt_quan.getText().trim());
            double price = Double.parseDouble(txt_price.getText().trim());
            String scent = txt_fragrant.getText().trim();
            String img = (imagePath == null || imagePath.isEmpty()) ? "" : imagePath;

            Perfume p = new Perfume(code, name, quantity, price, scent, img);
            if (db.updatePerfume(p)) {
                loadProducts();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Không thể cập nhật sản phẩm.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Số lượng và giá phải là số.");
        }
    }

    @FXML
    private void deleteProduct() {
        String code = txt_code.getText().trim();
        if (code.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn sản phẩm để xóa.");
            return;
        }

        // Hộp thoại xác nhận trước khi xóa
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn xóa sản phẩm này?");
        ButtonType ok = new ButtonType("Xóa", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(ok, cancel);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ok) {
                if (db.deletePerfume(code)) {
                    loadProducts();
                    clearForm();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Xóa thất bại. Vui lòng thử lại.");
                }
            }
        });
    }

    @FXML
    private void ChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Ảnh", "*.jpg", "*.png", "*.jpeg"));
        File file = fileChooser.showOpenDialog(image.getScene().getWindow());
        if (file != null) {
            imagePath = file.getAbsolutePath();
            image.setImage(new Image("file:" + imagePath));
        }
    }

    @FXML
    private void searchProduct() {
        String key = txt_search.getText().trim();
        if (key.isEmpty()) {
            loadProducts();
        } else {
            products.setAll(db.searchPerfume(key));
            tableProduct.setItems(products);
        }
    }

    private void clearForm() {
        txt_code.clear();
        txt_name.clear();
        txt_quan.clear();
        txt_price.clear();
        txt_fragrant.clear();
        image.setImage(null);
        imagePath = null;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) tableProduct.getScene().getWindow();
            stage.setTitle("Đăng nhập");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Đăng xuất thất bại. Vui lòng thử lại.");
        }
    }
}
