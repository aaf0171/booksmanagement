@COMMAND_MODEL
name: CreateDocumentWithItems
aggregate: Document

@DESCRIPTION
- Create a document (bibliographic record)
- Optionally create associated physical items based on provided item labels
- Items are generated during initial creation only

@INPUT_DTO
name: DocumentWithItemsDTO

fields:
- title: String (required)
- subtitle: String (optional)
- documentType: BOOK | DVD | GAME | DEVICE | OTHER
- isbn: String (optional)
- publisher: String (optional)
- publicationYear: Integer (optional)
- language: String (optional)
- description: String (optional)
- coverUrl: String (optional)

- items: String[] (optional)
  description: "List of item identifiers or labels (e.g. barcode, copy name, or inventory label)"

@DOMAIN_MODEL
new_entity:
- DocumentWithItems
  purpose:
  - transient command object (NOT persisted)
  - used to orchestrate Document + Item creation

@MAPPING_RULES
- DocumentWithItemsDTO → DocumentWithItems
- DocumentWithItems → Document entity + Item entities

@SOURCE
tables:
- documents d
- items i

@TRANSACTION
- REQUIRED (single transaction boundary)

@BUSINESS_RULES
- Document is created once
- Items are created only during initial creation if items list is not empty
- Each item is linked to created document.id
- If items is empty → only Document is created
- If item label already exists for same document → ignore or reject (configurable)

@PROCESS_FLOW
step1: validate DTO
step2: create Document
step3: persist Document
step4: if items not empty:
    - map each item label → Item entity
    - set document_id FK
    - persist all items
step5: return DocumentWithItemsResponseDTO

@OUTPUT_DTO
name: DocumentWithItemsResponseDTO

fields:
- document: DocumentDTO
- items: ItemDTO[]

@REPOSITORY
DocumentRepository:
- save(Document): Document

ItemRepository:
- saveAll(List<Item>)
- existsByDocumentIdAndLabel(String label): boolean

@SERVICE
name: CreateDocumentWithItemsService

methods:
- create(DocumentWithItemsDTO): DocumentWithItemsResponseDTO

@CONTROLLER
name: DocumentCommandController

endpoint:
- POST /api/documents

request:
- DocumentWithItemsDTO

response:
- 201 Created → DocumentWithItemsResponseDTO

@TESTS
unit:
- should_create_document_only_when_no_items
- should_create_document_and_items_when_items_provided
- should_link_items_to_document
- should_fail_on_invalid_document_type

integration:
- should_persist_document_and_items_in_single_transaction
- should rollback if item creation fails

controller:
- should_return_201_with_document_and_items
- should_accept_empty_items_list

@TECH
- Spring Boot 3
- JPA Hibernate
- @Transactional (mandatory)
- Lombok allowed
- DTO mapping via manual mapper or MapStruct

@ARCHITECTURE_NOTE
- DocumentWithItems is NOT a JPA entity
- It is a command aggregation model used only at write-time
- Document remains the only persisted aggregate root

@EXECUTION
step1: generate DTO + command model
step2: generate mapper
step3: generate service transaction
step4: generate repositories
step5: generate controller
step6: generate tests
step7: run tests
step8: fix until green
max_iterations: 10