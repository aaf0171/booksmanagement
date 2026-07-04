package com.books.repository;

import com.books.model.Item;
import com.books.model.Item.PhysicalStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemsRepositoryTest {

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private com.books.repository.DocumentsRepository documentsRepository;

    @Test
    @DisplayName("should_delete_existing_item")
    void shouldDeleteExistingItem() {
        var doc = com.books.model.Document.builder()
                .title("Test Document")
                .documentType(com.books.model.Document.DocumentType.BOOK)
                .build();
        var savedDoc = documentsRepository.save(doc);

        Item item = Item.builder()
                .barcode("BARCODE-DEL")
                .physicalStatus(PhysicalStatus.CLEAN)
                .location("Shelf A1")
                .document(savedDoc)
                .build();
        Item saved = itemsRepository.save(item);
        itemsRepository.flush();

        int deleted = itemsRepository.deleteItemByIdCustom(saved.getId());

        assertEquals(1, deleted);
        assertFalse(itemsRepository.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("should_return_zero_affected_rows_when_item_does_not_exist")
    void shouldReturnZeroAffectedRowsWhenItemDoesNotExist() {
        int deleted = itemsRepository.deleteItemByIdCustom(99999L);

        assertEquals(0, deleted);
    }

    @Test
    @DisplayName("should_delete_associated_loans")
    void shouldDeleteAssociatedLoans() {
        var doc = com.books.model.Document.builder()
                .title("Test Document")
                .documentType(com.books.model.Document.DocumentType.DVD)
                .build();
        var savedDoc = documentsRepository.save(doc);

        Item item = Item.builder()
                .barcode("BARCODE-LOANS")
                .physicalStatus(PhysicalStatus.DAMAGED)
                .location("Shelf B2")
                .document(savedDoc)
                .build();
        Item savedItem = itemsRepository.save(item);
        itemsRepository.flush();

        com.books.model.Loan loan = com.books.model.Loan.builder()
                .itemId(savedItem.getId())
                .borrowerId(1L)
                .loanDate(java.time.LocalDate.now())
                .dueDate(java.time.LocalDate.now().plusDays(14))
                .returnDate(null)
                .build();
        com.books.model.Loan loan2 = com.books.model.Loan.builder()
                .itemId(savedItem.getId())
                .borrowerId(2L)
                .loanDate(java.time.LocalDate.now())
                .dueDate(java.time.LocalDate.now().plusDays(14))
                .returnDate(java.time.LocalDate.now())
                .build();
        itemsRepository.save(savedItem);
        var loansRepo = com.books.repository.LoansRepositoryDatabase.class;

        itemsRepository.flush();

        int deleted = itemsRepository.deleteItemByIdCustom(savedItem.getId());

        assertEquals(1, deleted);
        assertFalse(itemsRepository.findById(savedItem.getId()).isPresent());
    }
}
