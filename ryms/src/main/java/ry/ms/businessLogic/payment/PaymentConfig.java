package ry.ms.businessLogic.payment;

public class PaymentConfig {
    private static final String STRIPE_SECRET_KEY = "rk_test_51SoUtVHRvrZz05eE7i4IClhR7rvx2vPBsBhpgCTm6r5D0jYVTRcJj4xRI9ySTFiOnNylLG7jL52rdR0d53B7oBKn000LST3Muk";

    public static String getSecretKey() {
        return STRIPE_SECRET_KEY;
    }
}
