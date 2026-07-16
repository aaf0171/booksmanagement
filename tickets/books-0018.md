@DESCRIPTION
Feature: Role-Based Access Control (RBAC) — Roles table and Login-Role mapping

Description:
Introduce a proper role management system based on Role-Based Access Control (RBAC).

Currently, roles are hardcoded as `List.of("BORROWER")` in `AuthService.java`.
This ticket replaces that approach with a database-driven model: a `roles` table
containing role definitions and a `login_roles` join table mapping logins to roles.

The `Login` entity will hold a `@ManyToMany` relationship with `Role`, and
`AuthService` must resolve the actual roles from the database when generating
JWT tokens (login and refresh flows).

-------------------------------------------------------------------------------

Business rules

- A role is a named entity (e.g. BORROWER, LIBRARIAN, ADMIN).
- A login can have zero, one, or multiple roles.
- A role can be assigned to zero, one, or multiple logins.
- Roles are stored in a `roles` table with a unique `name` field.
- The mapping between logins and roles is stored in a `login_roles` join table.
- The JWT token `roles` claim must reflect the actual roles assigned to the logged-in user from the database.
- The `refresh` flow must also resolve roles from the database (not hardcode them).
- The existing `BORROWER` role must be created as a seed/initial data.

-------------------------------------------------------------------------------

Architecture

Role entity:
       │
       ▼
RoleRepository

Login entity (updated):
       │
       ├── Role (via @ManyToMany)
       ▼
RoleRepository

AuthService (updated):
       │
       ├── LoginRepository
       ├── PasswordEncoder
       ├── JwtTokenGenerator
       ├── RefreshTokenService
       └── Role (extracted from login.getRoles())

-------------------------------------------------------------------------------

Database schema

New table: roles

CREATE TABLE roles (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

New table: login_roles

CREATE TABLE login_roles (
    login_id BIGINT NOT NULL,
    role_id  BIGINT NOT NULL,
    PRIMARY KEY (login_id, role_id),
    CONSTRAINT fk_login_roles_login
        FOREIGN KEY (login_id)
        REFERENCES logins(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_login_roles_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_login_roles_role
    ON login_roles(role_id);

Seed data (initial roles):

INSERT INTO roles (id, name) VALUES
    (1, 'BORROWER'),
    (2, 'LIBRARIAN'),
    (3, 'ADMIN');

-------------------------------------------------------------------------------

Model changes

Role.java (new entity)

- @Entity, @Table(name = "roles")
- Fields: Long id (primary key), String name (unique, not null)

Login.java (updated)

- Add @ManyToMany relationship to Role:
  - @JoinTable(name = "login_roles",
              joinColumns = @JoinColumn(name = "login_id"),
              inverseJoinColumns = @JoinColumn(name = "role_id"))
  - FetchType.EAGER (roles must be loaded with login)
  - Field: Set<Role> roles = new HashSet<>()

-------------------------------------------------------------------------------

Service changes

AuthService.java

Login flow (login method):

Before:
    List<String> roles = List.of("BORROWER");

After:
    List<String> roles = login.getRoles().stream()
        .map(Role::getName)
        .toList();

Refresh flow (refresh method):

Before:
    List<String> roles = List.of("BORROWER");

After:
    List<String> roles = login.getRoles().stream()
        .map(Role::getName)
        .toList();

-------------------------------------------------------------------------------

Service contracts

RoleRepository

- Optional<Role> findByName(String name)
- List<Role> findAll()

AuthService

login(username, password): LoginResponseDTO
1. Find login by username.
2. Verify password with PasswordEncoder.
3. Check account is active.
4. Load roles from login.getRoles() (fetched via @ManyToMany).
5. Extract role names: `login.getRoles().stream().map(Role::getName).toList()`
6. Generate JWT with actual roles.
7. Generate and store refresh token.
8. Return both tokens.

refresh(refreshToken): RefreshResponseDTO
1. Validate refresh token.
2. Find login by loginId.
3. Load roles from login.getRoles() (fetched via @ManyToMany).
4. Extract role names.
5. Generate new JWT with actual roles.
6. Revoke old refresh token.
7. Return new access token.

-------------------------------------------------------------------------------

Acceptance criteria

✓ roles table exists with id (BIGINT PK) and name (VARCHAR UNIQUE NOT NULL).
✓ login_roles join table exists with login_id and role_id composite PK and foreign keys.
✓ Initial seed data: BORROWER (id=1), LIBRARIAN (id=2), ADMIN (id=3).
✓ Role.java entity exists with id and name fields.
✓ Login.java has a @ManyToMany Set<Role> roles relationship with FetchType.EAGER.
✓ login_roles join table is correctly mapped via @JoinTable.
✓ AuthService.login() resolves roles from database, not hardcoded.
✓ AuthService.refresh() resolves roles from database, not hardcoded.
✓ JWT token contains the correct roles for the authenticated user.
✓ A user with no roles gets an empty roles array in the JWT.
✓ A user with multiple roles gets all role names in the JWT.

-------------------------------------------------------------------------------

Deliverables

Generate:

- Role.java entity
- RoleRepository
- SQL migration for roles + login_roles tables
- SQL seed data (BORROWER, LIBRARIAN, ADMIN)
- Login.java (updated with @ManyToMany roles relationship)
- AuthService.java (updated to resolve roles from database in login and refresh)
- Domain unit tests
- Application service tests
- Repository integration tests
- REST controller integration tests
- OpenAPI documentation
- cURL examples

-------------------------------------------------------------------------------

Domain unit tests

✓ shouldFindRoleByName_BORROWER()
✓ shouldFindRoleByName_ADMIN()
✓ shouldFindRoleByName_LIBRARIAN()
✓ shouldReturnEmptyWhenRoleNotFound()
✓ shouldSaveRoleSuccessfully()
✓ shouldCreateLoginWithRoles()

-------------------------------------------------------------------------------

Application service tests

✓ shouldLoginWithCorrectRolesFromDatabase()
✓ shouldLoginWithMultipleRoles()
✓ shouldLoginWithEmptyRoles()
✓ shouldRefreshWithCorrectRolesFromDatabase()
✓ shouldRefreshWithUpdatedRoles()
✓ shouldReturn401WhenCredentialsAreInvalid()
✓ shouldReturn401WhenAccountIsNotActivated()

-------------------------------------------------------------------------------

Repository integration tests

✓ shouldSaveRole()
✓ shouldFindRoleByName()
✓ shouldFindAllRoles()
✓ shouldReturnEmptyWhenRoleNotFound()
✓ shouldSaveLoginWithRoles()
✓ shouldFindLoginWithRolesFetched()
✓ shouldPersistLoginRoleMapping()

-------------------------------------------------------------------------------

OpenAPI

No new endpoints. JWT token payload changes:

{
  "sub": "12345",
  "login": "jc.dusse",
  "roles": ["BORROWER", "LIBRARIAN"],
  "iat": 1784100000,
  "exp": 1784100900
}

- roles now reflects the actual database-assigned roles (not hardcoded)

@EXECUTION
max_iterations: 10
