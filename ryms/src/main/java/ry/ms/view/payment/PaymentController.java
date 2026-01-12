package ry.ms.view.payment;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ry.ms.businessLogic.payment.PaymentService;
import com.stripe.model.PaymentIntent;

public class PaymentController {

    @FXML
    private Label amountLabel;
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField expiryField;
    @FXML
    private TextField cvcField;
    @FXML
    private Label statusLabel;
    @FXML
    private Button payButton;

    private PaymentService paymentService;
    private long amountInCents;
    private Runnable onSuccess;
    private Runnable onCancel;

    public PaymentController() {
        this.paymentService = new PaymentService();
    }

    public void setAmount(double amount) {
        this.amountInCents = (long) (amount * 100);
        this.amountLabel.setText(String.format("$%.2f", amount));
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    @FXML
    private void handlePayment() {
        // In a real app, we would use Stripe.js or similar to tokenize card details
        // securely.
        // Sending raw card data to backend is not recommended for PCI compliance.
        // Since this is a desktop app/test, we ironically proceed by creating a
        // PaymentIntent
        // and confirming it later (or just creating it to simulate the handshake).

        // For this MVP:
        // 1. Create PaymentIntent on Backend (PaymentService).
        // 2. Assume "success" if no exception, as we aren't implementing the full
        // client-side confirmation flow here.

        statusLabel.setText("Processing payment...");
        statusLabel.setStyle("-fx-text-fill: blue;");
        payButton.setDisable(true);

        new Thread(() -> {
            try {
                // Creates intent
                PaymentIntent intent = paymentService.createPaymentIntent(amountInCents, "usd");

                // If successful:
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("Payment Successful! ID: " + intent.getId());
                    statusLabel.setStyle("-fx-text-fill: green;");
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("Payment Failed: " + e.getMessage());
                    statusLabel.setStyle("-fx-text-fill: red;");
                    payButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleCancel() {
        if (onCancel != null) {
            onCancel.run();
        }
    }
}
