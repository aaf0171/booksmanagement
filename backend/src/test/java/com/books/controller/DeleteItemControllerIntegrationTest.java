package com.books.controller;

import com.books.model.Document;
import com.books.model.Document.DocumentType;
import com.books.model.Item;
import com.books.model.Item.PhysicalStatus;
import com.books.model.Loan;
import com.books.exception.ItemInUseException;
import com.books.exception.ItemNotFoundException;
import com.books.repository.DocumentsRepository;
import com.books.repository.ItemsRepository;
import com.books.repository.LoansRepositoryDatabase;
import com.books.service.DeleteItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DeleteItemControllerIntegrationTest {

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private LoansRepositoryDatabase loansRepository;

    @Autowired
    private DeleteItemService deleteItemService;

    private Long itemWithActiveLoanId;
    private Long itemWithoutLoanId;

    @BeforeEach
    void setUp() {
        documentsRepository.deleteAll();
        itemsRepository.deleteAll();
        loansRepository.deleteAll();

        Document doc = Document.builder()
                .title("Document pour suppression")
                .documentType(DocumentType.BOOK)
                .build();
        Document savedDoc = documentsRepository.save(doc);

        Item itemNoLoan = Item.builder()
                .barcode("ITEM-NO-LOAN")
                .physicalStatus(PhysicalStatus.CLEAN)
                .location("Shelf A1")
                .document(savedDoc)
                .build();
        Item savedNoLoan = itemsRepository.save(itemNoLoan);
        itemsRepository.flush();
        itemWithoutLoanId = savedNoLoan.getId();

        Item itemWithLoan = Item.builder()
                .barcode("ITEM-WITH-LOAN")
                .physicalStatus(PhysicalStatus.DAMAGED)
                .location("Shelf B2")
                .document(savedDoc)
                .build();
        Item savedWithLoan = itemsRepository.save(itemWithLoan);
        itemsRepository.flush();

        Loan activeLoan = Loan.builder()
                .itemId(savedWithLoan.getId())
                .borrowerId(1L)
                .loanDate(java.time.LocalDate.now())
                .dueDate(java.time.LocalDate.now().plusDays(14))
                .returnDate(null)
                .build();
        loansRepository.save(activeLoan);
        loansRepository.flush();
        itemWithActiveLoanId = savedWithLoan.getId();
    }

    @Test
    @DisplayName("DELETE existing item without active loan returns 204")
    void deleteExistingItemReturns204() {
        assertDoesNotThrow(() -> deleteItemService.delete(itemWithoutLoanId));
        assertFalse(itemsRepository.findById(itemWithoutLoanId).isPresent());
    }

    @Test
    @DisplayName("DELETE unknown item returns 404 via exception")
    void deleteUnknownItemReturns404() {
        ItemNotFoundException ex = assertThrows(ItemNotFoundException.class,
                () -> deleteItemService.delete(99999L));

        assertTrue(ex.getMessage().contains("999"));
    }

    @Test
    @DisplayName("DELETE item with active loan returns 409 via exception")
    void deleteItemWithActiveLoanReturns409() {
        ItemInUseException ex = assertThrows(ItemInUseException.class,
                () -> deleteItemService.delete(itemWithActiveLoanId));

        assertTrue(ex.getMessage().contains("active loan"));
        assertTrue(itemsRepository.findById(itemWithActiveLoanId).isPresent());
    }

    @Test
    @DisplayName("DELETE should actually remove item from database")
    void deleteShouldActuallyRemoveItem() {
        deleteItemService.delete(itemWithoutLoanId);

        assertFalse(itemsRepository.findById(itemWithoutLoanId).isPresent());
    }

    @Test
    @DisplayName("DELETE should not affect other items")
    void deleteShouldNotAffectOtherItems() {
        deleteItemService.delete(itemWithoutLoanId);

        assertTrue(itemsRepository.findById(itemWithActiveLoanId).isPresent());
        assertFalse(itemsRepository.findById(itemWithoutLoanId).isPresent());
    }

    @Test
    @DisplayName("DELETE should not affect parent document")
    void deleteShouldNotAffectDocument() {
        Long docId = itemWithoutLoanId;
        var item = itemsRepository.findById(itemWithoutLoanId).orElseThrow();
        Long parentId = item.getDocument().getId();

        deleteItemService.delete(itemWithoutLoanId);

        assertTrue(documentsRepository.findById(parentId).isPresent());
    }
}
