package innowise.java.web_store.repository;

import innowise.java.web_store.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findByOrderId(String orderId);

    List<Payment> findByUserId(Long userId);

    List<Payment> findByStatusIn(List<String> statuses);
}
