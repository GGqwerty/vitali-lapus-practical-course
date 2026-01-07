package innowise.java.web_store.repository;

import innowise.java.web_store.entity.CardInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

    Page<CardInfo> findAllByOrderByExpirationDateAscHolderAsc(Pageable pageable);

    @Query(value = "SELECT * FROM card_info WHERE id = :id", nativeQuery = true)
    Optional<CardInfo> findByIdNative(@Param("id") Long id);

    @Query("DELETE FROM CardInfo c WHERE c.id = :id")
    void deleteByIdJPQL(@Param("id") Long id);
}