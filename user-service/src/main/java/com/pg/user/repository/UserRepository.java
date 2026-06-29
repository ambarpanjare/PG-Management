package com.pg.user.repository;

import com.pg.user.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByAuthUserId(Long authUserId);

    Optional<UserProfile> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    boolean existsByAuthUserId(Long authUserId);

    @Query("""
            SELECT u FROM UserProfile u
            WHERE (:name IS NULL OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:city IS NULL OR LOWER(u.city) LIKE LOWER(CONCAT('%', :city, '%')))
            AND (:occupation IS NULL OR LOWER(u.occupation) LIKE LOWER(CONCAT('%', :occupation, '%')))
            AND (:isVerified IS NULL OR u.isVerified = :isVerified)
            """)
    Page<UserProfile> searchUsers(
            @Param("name") String name,
            @Param("city") String city,
            @Param("occupation") String occupation,
            @Param("isVerified") Boolean isVerified,
            Pageable pageable
    );
}
