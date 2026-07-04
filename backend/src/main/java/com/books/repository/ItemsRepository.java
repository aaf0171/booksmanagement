package com.books.repository;

import com.books.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemsRepository extends JpaRepository<Item, Long> {

    boolean existsByDocumentIdAndBarcode(Long documentId, String barcode);

    boolean existsByBarcode(String barcode);

    java.util.Optional<Item> findByBarcode(String barcode);

    long countByDocumentId(Long documentId);

    void deleteByDocumentId(Long documentId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Item i WHERE i.id = :id")
    int deleteItemByIdCustom(@Param("id") Long id);
}
