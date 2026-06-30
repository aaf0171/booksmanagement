@READ_MODEL
name: ActiveLoan
dto: ActiveLoanDTO

@PROJECTION
description:
- Active loans with book and borrower info

@SOURCE
tables:
- loans l
- books b
- borrowers br

@RELATIONS
- l.book_id = b.id
- l.borrower_id = br.id

@FILTER
- l.return_date IS NULL

@SELECT
fields:
- l.id AS id
- b.title AS bookTitle
- br.name AS borrowerName
- l.due_date AS dueDate

@REPOSITORY
name: LoansRepositoryDatabase
type: JpaRepository<Loan, Long>
read_methods:
- findActiveLoans(): ActiveLoanDTO[]

@TECH
- Spring Boot 3
- nativeQuery: true
- @Transactional(readOnly = true)
- @Primary

@EXECUTION
step1: generate entity
step2: generate repository
step3: generate service
step4: generate test
step5: run test
step6: fix until success
max_iterations: 10