package com.vn.keycap_server.service.favorite;

import com.vn.keycap_server.dto.response.favorite.ToggleFavoriteResponse;

public interface IFavoriteService {

    ToggleFavoriteResponse toggleFavorite(Long productId, Long userId);

}
