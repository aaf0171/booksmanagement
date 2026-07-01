package com.books.repository;

import com.books.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemsRepository extends JpaRepository<Item, Long> {

    boolean existsByDocumentIdAndBarcode(Long documentId, String barcode);
}
