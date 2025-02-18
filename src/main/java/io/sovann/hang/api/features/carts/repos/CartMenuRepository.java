package io.sovann.hang.api.features.carts.repos;

import io.sovann.hang.api.features.carts.entities.Cart;
import io.sovann.hang.api.features.carts.entities.CartMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartMenuRepository extends JpaRepository<CartMenu, UUID> {
    List<CartMenu> findAllByCartId(UUID cartId);

    List<CartMenu> findAllByCartIn(List<Cart> carts);

    Optional<CartMenu> findByIdAndCartId(UUID id, UUID cartId);

    void deleteAllByCartId(UUID cartId);
}
