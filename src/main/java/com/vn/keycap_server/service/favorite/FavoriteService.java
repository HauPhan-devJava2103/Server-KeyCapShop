package com.vn.keycap_server.service.favorite;

import org.springframework.stereotype.Service;

import com.vn.keycap_server.dto.response.favorite.ToggleFavoriteResponse;
import com.vn.keycap_server.exception.ResourceNotFoundException;
import com.vn.keycap_server.modal.Product;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.modal.Wishlist;
import com.vn.keycap_server.repository.ProductRepository;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService implements IFavoriteService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;

    @Override
    public ToggleFavoriteResponse toggleFavorite(Long productId, Long userId) {

        // Check Existing Product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check Existing User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check Existing Favorite
        var existingFavorite = wishlistRepository.findByProductIdAndUserId(productId, userId);

        if (existingFavorite.isPresent()) {
            wishlistRepository.deleteByProductIdAndUserId(productId, userId);
            return ToggleFavoriteResponse.builder()
                    .isFavorite(false)
                    .build();

        } else {
            Wishlist wishlist = Wishlist.builder()
                    .user(user)
                    .product(product)
                    .build();
            wishlistRepository.save(wishlist);

            return ToggleFavoriteResponse.builder()
                    .isFavorite(true)
                    .build();
        }
    }

}
