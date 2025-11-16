package innowise.java.web_store.repository;

import innowise.java.web_store.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByIdIn(List<Long> ids);

    List<Order> findAllByStatusIn(List<String> statuses);

    List<Order> findAllByUserId(Long userId);
}