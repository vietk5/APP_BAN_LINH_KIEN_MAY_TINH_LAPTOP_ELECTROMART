package com.example.baitap01_nhom6_ui_login_register_forgetpass.singleton;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.CartItem;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final List<CartItem> cartItems = new ArrayList<>();

    private CartManager() {}

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addProduct(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        cartItems.add(new CartItem(product, 1, true));
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void removeItem(CartItem cartItem) {
        cartItems.remove(cartItem);
    }

    public void clearCart() {
        cartItems.clear();
    }
}
