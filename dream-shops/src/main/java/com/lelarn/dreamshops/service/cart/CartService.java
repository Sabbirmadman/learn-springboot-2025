package com.lelarn.dreamshops.service.cart;

import com.lelarn.dreamshops.exceptions.ResourceNotFoundException;
import com.lelarn.dreamshops.model.Cart;
import com.lelarn.dreamshops.model.Product;
import com.lelarn.dreamshops.model.User;
import com.lelarn.dreamshops.repository.CartRepositiry;
import com.lelarn.dreamshops.repository.ProductRepository;
import com.lelarn.dreamshops.repository.UserRepository;
import com.lelarn.dreamshops.request.AddToCartRequest;
import com.lelarn.dreamshops.response.CartItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

    private final CartRepositiry cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository; // Assuming you have a UserRepository

    private static final String ACTIVE_CART_STATUS = "active";

    @Override
    @Transactional
    public CartItemResponse addItemToCart(AddToCartRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        // Check if product is already in the user's active cart
        Optional<Cart> existingCartItemOpt = cartRepository.findByUserAndProductAndStatus(user, product,
                ACTIVE_CART_STATUS);

        Cart cartItem;
        if (existingCartItemOpt.isPresent()) {
            // Update quantity if item already exists
            cartItem = existingCartItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            // Create new cart item
            cartItem = new Cart();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setStatus(ACTIVE_CART_STATUS); // Set status explicitly or rely on default constructor
        }

        // Basic check for inventory (optional, enhance as needed)
        if (product.getInventory() < cartItem.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
        }

        Cart savedCart = cartRepository.save(cartItem);
        return new CartItemResponse(savedCart);
    }

    @Override
    public List<CartItemResponse> getUserCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        List<Cart> cartItems = cartRepository.findByUserAndStatus(user, ACTIVE_CART_STATUS);
        return cartItems.stream()
                .map(CartItemResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CartItemResponse updateCartItemQuantity(Long cartItemId, int quantity, String username) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Cart cartItem = cartRepository.findByIdAndUser(cartItemId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found with id: " + cartItemId + " for user " + username));

        if (!ACTIVE_CART_STATUS.equals(cartItem.getStatus())) {
            throw new ResourceNotFoundException("Cart item is not active: " + cartItemId);
        }

        // Basic check for inventory (optional, enhance as needed)
        if (cartItem.getProduct().getInventory() < quantity) {
            throw new IllegalArgumentException("Not enough stock for product: " + cartItem.getProduct().getName());
        }

        cartItem.setQuantity(quantity);
        Cart updatedCart = cartRepository.save(cartItem);
        return new CartItemResponse(updatedCart);
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long cartItemId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Cart cartItem = cartRepository.findByIdAndUser(cartItemId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found with id: " + cartItemId + " for user " + username));

        if (!ACTIVE_CART_STATUS.equals(cartItem.getStatus())) {
            throw new ResourceNotFoundException("Cart item is not active: " + cartItemId);
        }

        cartRepository.delete(cartItem);

    }
}
