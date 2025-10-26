package innowise.java.web_store.repository;

import innowise.java.web_store.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Page<UserEntity> findAllByOrderBySurnameAscNameAsc(Pageable pageable);

    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<UserEntity> findByEmail(@Param("email") String email);

    @Modifying
    @Query("DELETE FROM UserEntity u WHERE u.id = :id")
    void deleteByIdJPQL(@Param("id") Long id);
}

