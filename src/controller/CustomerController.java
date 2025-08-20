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
import javafx.stage.Stage;
import model.Perfume;

public class CustomerController {

    @FXML private TextField txt_search;
    @FXML private TableView<Perfume> tableProduct;
    @FXML private TableColumn<Perfume, String> col_code, col_name, col_fragrant, col_image;
    @FXML private TableColumn<Perfume, Integer> col_quan;
    @FXML private TableColumn<Perfume, Double> col_price;
    @FXML private ListView<String> listCart;
    @FXML private Spinner<Integer> spinner_quantity;
    @FXML private Label lbl_greeting;

    private final ObservableList<Perfume> products = FXCollections.observableArrayList();
    private final ObservableList<String> cart = FXCollections.observableArrayList();
    private final PerfumeDatabase perfumeDb = new PerfumeDatabase();

    private String customerName;

    public void initialize() {
        col_code.setCellValueFactory(new PropertyValueFactory<>("code"));
        col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        col_fragrant.setCellValueFactory(new PropertyValueFactory<>("scent"));
        col_quan.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
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
                    try {
                        imgView.setImage(new Image("file:" + path));
                        setGraphic(imgView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        loadProducts();
        listCart.setItems(cart);

        if (spinner_quantity != null) {
            spinner_quantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        }

        if (lbl_greeting != null && customerName != null) {
            lbl_greeting.setText("Xin chào, " + customerName + "!");
        }
    }

    public void setCustomerName(String name) {
        this.customerName = name;
        if (lbl_greeting != null) {
            lbl_greeting.setText("Xin chào, " + name + "!");
        }
    }

    @FXML
    private void timKiem() {
        String key = txt_search.getText().trim();
        if (key.isEmpty()) {
            loadProducts();
        } else {
            products.setAll(perfumeDb.searchPerfume(key));
            tableProduct.setItems(products);
        }
    }

    @FXML
    private void removeFromCart() {
        String selected = listCart.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Xác nhận xóa");
            confirm.setHeaderText(null);
            confirm.setContentText("Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?");
            ButtonType ok = new ButtonType("Xóa", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirm.getButtonTypes().setAll(ok, cancel);

            confirm.showAndWait().ifPresent(response -> {
                if (response == ok) {
                    cart.remove(selected);
                }
            });
        }
    }

    @FXML
    private void checkout() {
        if (cart.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Giỏ hàng của bạn đang trống!");
            return;
        }

        int totalQuantity = 0;
        double totalPrice = 0;

        for (String item : cart) {
            try {
                String[] parts = item.split(" - ");
                int quantity = Integer.parseInt(parts[1].replace("SL: ", "").trim());
                double price = Double.parseDouble(parts[2].replace("Giá: ", "").replace("VND", "").replace(",", "").trim());
                totalQuantity += quantity;
                totalPrice += price;
            } catch (Exception e) {
                // Bỏ qua lỗi nếu có
            }
        }

        showAlert(Alert.AlertType.INFORMATION,
                "Thanh toán thành công!\n" +
                "Tổng sản phẩm: " + totalQuantity + " (" + cart.size() + " loại sản phẩm)\n" +
                "Tổng tiền: " + String.format("%,.0f", totalPrice) + " VND\n" +
                "Cảm ơn bạn đã mua hàng tại OWEN!");

        cart.clear();
    }

    @FXML
    private void addToCart() {
        Perfume selected = tableProduct.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn một sản phẩm để thêm vào giỏ.");
            return;
        }

        int quantity = spinner_quantity.getValue();
        if (quantity <= 0) {
            showAlert(Alert.AlertType.WARNING, "Số lượng phải lớn hơn 0.");
            return;
        }

        String cartItem = selected.getName() + " - SL: " + quantity + " - Giá: " +
                String.format("%,.0f", selected.getPrice() * quantity) + " VND";
        cart.add(cartItem);
    }

    private void loadProducts() {
        products.setAll(perfumeDb.getAllPerfumes());
        tableProduct.setItems(products);
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(content);
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
            showAlert(Alert.AlertType.ERROR, "Không thể đăng xuất.");
        }
    }
}
