@READ_MODEL
name: DocumentSearch
dto: DocumentSearchDTO

@PROJECTION
description:
- Search documents using fulltext index and structured filters

@SOURCE
tables:
- documents d

@FILTER
- MATCH(d.title, d.subtitle, d.description, d.publisher) AGAINST (:query IN NATURAL LANGUAGE MODE)
  OR :query IS NULL OR :query = ''
- (:title IS NULL OR d.title LIKE CONCAT('%', :title, '%'))
- (:subtitle IS NULL OR d.subtitle LIKE CONCAT('%', :subtitle, '%'))
- (:documentType IS NULL OR d.document_type = :documentType)
- (:isbn IS NULL OR d.isbn = :isbn)
- (:publisher IS NULL OR d.publisher LIKE CONCAT('%', :publisher, '%'))
- (:publicationYear IS NULL OR d.publication_year = :publicationYear)

@SELECT
fields:
- d.id AS id
- d.title AS title
- d.subtitle AS subtitle
- d.document_type AS documentType
- d.isbn AS isbn
- d.publisher AS publisher
- d.publication_year AS publicationYear
- d.description AS description
- d.created_at AS createdAt

@INDEX
- FULLTEXT idx_documents_search (title, subtitle, description, publisher)

@REPOSITORY
name: DocumentsSearchRepositoryDatabase
type: JpaRepository<Document, Long>
read_methods:
- searchDocuments(query, title, subtitle, documentType, isbn, publisher, publicationYear): DocumentSearchDTO[]

@SERVICE
name: DocumentSearchService
methods:
- searchDocuments(params): DocumentSearchDTO[]
- validateSearchParams(params): void

@CONTROLLER
name: DocumentSearchController
base_path: /api/documents/search
endpoints:
- GET / : searchDocuments

request_params:
- query (optional string)
- title (optional string)
- subtitle (optional string)
- documentType (optional enum BOOK, DVD, GAME, DEVICE, OTHER)
- isbn (optional string)
- publisher (optional string)
- publicationYear (optional int)

response:
- 200: DocumentSearchDTO[]

@TESTS
unit:
- DocumentSearchServiceTest:
  - should_return_documents_by_fulltext_query
  - should_filter_by_title
  - should_filter_by_document_type
  - should_return_empty_when_no_match

integration:
- DocumentsSearchRepositoryDatabaseTest:
  - should_execute_fulltext_search
  - should_apply_all_filters_correctly

controller:
- DocumentSearchControllerTest:
  - should_return_200_with_results
  - should_return_empty_list_when_no_match
  - should_accept_optional_filters

@TECH
- Spring Boot 3
- Spring Data JPA (nativeQuery = true)
- MySQL FULLTEXT search
- @Transactional(readOnly = true)
- DTO projection (interface-based or class-based)
- Pageable optional support

@EXECUTION
step1: generate DTO
step2: generate repository with nativeQuery FULLTEXT
step3: generate service layer
step4: generate controller REST
step5: generate unit tests
step6: generate integration tests
step7: run all tests
step8: fix until success
max_iterations: 10