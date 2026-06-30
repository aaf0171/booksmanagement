package com.books.repository;

import com.books.dto.DocumentSearchDTO;
import com.books.model.Document;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DocumentsSearchRepositoryDatabaseTest {

    @Autowired
    private DocumentsSearchRepositoryDatabase documentsSearchRepository;

    @Autowired
    private EntityManager entityManager;

    private void cleanDocuments() {
        entityManager.createNativeQuery("DELETE FROM documents").executeUpdate();
        entityManager.clear();
    }

    @Test
    @DisplayName("should_execute_fulltext_search")
    void shouldExecuteFulltextSearch() {
        cleanDocuments();

        Document doc1 = Document.builder()
                .title("Le Petit Prince")
                .subtitle("Un conte poétique")
                .documentType(Document.DocumentType.BOOK)
                .description("Un conte sur l'amour et l'amitié")
                .publisher("Gallimard")
                .publicationYear(1943)
                .build();
        Document doc2 = Document.builder()
                .title("1984")
                .description("Un roman dystopique sur la surveillance")
                .publisher("Signet Classics")
                .publicationYear(1949)
                .build();
        Document doc3 = Document.builder()
                .title("Harry Potter")
                .description("Un roman de sorciers")
                .publisher("Gallimard")
                .publicationYear(1997)
                .build();

        documentsSearchRepository.save(doc1);
        documentsSearchRepository.save(doc2);
        documentsSearchRepository.save(doc3);

        List<DocumentSearchDTO> results = documentsSearchRepository.searchByTitle("Harry");
        assertEquals(1, results.size());
        DocumentSearchDTO result = results.get(0);
        assertNotNull(result);
        assertEquals("Harry Potter", result.getTitle());
    }

    @Test
    @DisplayName("should_apply_all_filters_correctly")
    void shouldApplyAllFiltersCorrectly() {
        cleanDocuments();

        Document doc1 = Document.builder()
                .title("Le Petit Prince")
                .documentType(Document.DocumentType.BOOK)
                .description("Un conte poétique")
                .publisher("Gallimard")
                .publicationYear(1943)
                .build();
        Document doc2 = Document.builder()
                .title("Harry Potter")
                .documentType(Document.DocumentType.BOOK)
                .description("Un roman magique")
                .publisher("Gallimard")
                .publicationYear(1997)
                .build();
        Document doc3 = Document.builder()
                .title("Dune")
                .documentType(Document.DocumentType.BOOK)
                .description("Un roman de science-fiction")
                .publisher("Les Mille et Une Nuits")
                .publicationYear(1965)
                .build();

        documentsSearchRepository.save(doc1);
        documentsSearchRepository.save(doc2);
        documentsSearchRepository.save(doc3);

        List<DocumentSearchDTO> resultsByTitle = documentsSearchRepository.searchByTitle("Harry");
        assertEquals(1, resultsByTitle.size());
        assertEquals("Harry Potter", resultsByTitle.get(0).getTitle());

        List<DocumentSearchDTO> resultsByYear = documentsSearchRepository.searchByPublicationYear(1943);
        assertEquals(1, resultsByYear.size());
        assertEquals("Le Petit Prince", resultsByYear.get(0).getTitle());

        List<DocumentSearchDTO> resultsByPublisher = documentsSearchRepository.searchByPublisher("Mille");
        assertEquals(1, resultsByPublisher.size());
        assertEquals("Dune", resultsByPublisher.get(0).getTitle());
    }
}
