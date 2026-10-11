package com.farmgoat.registration.repository;

import com.farmgoat.registration.model.Goat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GoatRepository extends JpaRepository<Goat, Long> {
    Optional<Goat> findByTagNumberIgnoreCase(String tagNumber);

    @Query("select g from Goat g join g.farmer f " +
            "where lower(g.tagNumber) like lower(concat('%', :query, '%')) " +
            "or lower(g.name) like lower(concat('%', :query, '%')) " +
            "or lower(g.breed) like lower(concat('%', :query, '%')) " +
            "or lower(f.fullName) like lower(concat('%', :query, '%')) " +
            "order by g.id desc")
    List<Goat> search(@Param("query") String query);
}
