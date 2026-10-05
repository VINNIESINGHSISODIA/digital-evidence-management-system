# Secure Digital Evidence Management System

A Spring Boot backend for law enforcement to **store, track, and verify digital evidence** with a tamper-evident chain of custody. Every piece of evidence is encrypted at rest, fingerprinted for integrity, access-controlled by role, and every action is written to an immutable-by-design audit trail — aligning with CJIS-style security principles.

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [Security Design](#security-design)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Roadmap / Future Improvements](#roadmap--future-improvements)

---

## Overview

Digital evidence is only admissible in court if its **chain of custody** can be proven — who accessed it, when, and that it was never altered. A shared drive offers none of that. This system provides:

- **Confidentiality** — evidence files are encrypted at rest (AES).
- **Integrity** — each file is hashed (SHA-256) so any tampering is detectable.
- **Accountability** — every action (upload, view, download, verify) is recorded in an audit log.
- **Access control** — stateless JWT authentication with role-based authorization.

Files are stored encrypted on disk; their metadata, hashes, and the full audit trail live in PostgreSQL. Read-heavy case lookups are cached in Redis, and the whole stack runs via Docker Compose.

---

## Features

- **Role-based user management** — five roles (Admin, Investigator, Officer, Lab, Guest).
- **JWT authentication** — stateless, signed tokens; BCrypt-hashed passwords.
- **Case management** — create, update, and track cases with status (Open, In Progress, Closed, Archived).
- **Evidence handling** — upload with automatic encryption + hashing, view, download, and integrity verification.
- **Case assignments** — assign users to cases with a defined role (Investigator / Officer).
- **Audit trail** — every action logged with user, action, timestamp, IP, and the case/evidence involved.
- **Redis caching** — cached case reads to reduce database load and latency.
- **Containerized** — app, PostgreSQL, and Redis orchestrated with Docker Compose.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21+ |
| Framework | Spring Boot (Spring Web, Spring Security, Spring Data JPA) |
| Authentication | JWT (jjwt), BCrypt |
| Cryptography | AES (encryption at rest), SHA-256 (integrity) |
| Database | PostgreSQL |
| ORM | Hibernate / JPA |
| Cache | Redis (Lettuce client) |
| Build | Maven |
| Containerization | Docker, Docker Compose |

---

## Architecture

A layered architecture where each layer has a single responsibility:

```
Client (HTTP + JWT)
        │
        ▼
Security Filter Chain  ──  JwtAuthFilter
   (authenticates: verifies token, sets security context)
        │
        ▼
Controller Layer  (@RestController)
   (authorizes via @PreAuthorize, maps request/response)
        │
        ▼
Service Layer  (@Service)
   (business logic, encryption, hashing, audit)
        │
        ├──────────────► Redis (cached reads)
        │
        ├──────────────► Encrypted files (disk)
        │
        ▼
Repository Layer  (Spring Data JPA → Hibernate)
        │
        ▼
PostgreSQL  (metadata + audit log)
```

- **Authenticate first, authorize second** — the JWT filter establishes identity in the `SecurityContextHolder` before any role check runs.
- **Files on disk, metadata in the database** — large binaries never bloat the database; PostgreSQL stores the path, type, size, and hash.
- **Stateless** — the JWT carries identity, so the app can be scaled horizontally behind a load balancer.

---

## Database Schema

Five normalized tables with foreign-key referential integrity:

| Table | Purpose | Key relationships |
|---|---|---|
| `users` | Accounts + roles | referenced by all other tables |
| `cases` | Case files | `created_by` → users |
| `evidence` | Evidence metadata | `case_id` → cases, `uploaded_by` → users |
| `case_assignments` | User ↔ case (many-to-many) | `case_id`, `user_id`, `assigned_by` → users |
| `audit_logs` | Action history | `user_id`, `case_id`, `evidence_id` |

- Users create many cases and upload many pieces of evidence.
- Each piece of evidence belongs to one case.
- `case_assignments` resolves the many-to-many between users and cases.
- `audit_logs` records every action against the user, case, and evidence involved.

---

## Security Design

- **Passwords** — hashed with **BCrypt** (salted, slow); never stored in plaintext.
- **Authentication** — stateless **JWT**; the filter verifies the signature, reloads the user from the database, and checks the account is active on every request.
- **Authorization** — method-level `@PreAuthorize` role checks on sensitive endpoints; all non-auth endpoints require authentication.
- **Encryption at rest** — evidence files are **AES-encrypted** before being written to disk.
- **Integrity** — a **SHA-256** hash is computed on upload and re-checked on verification to detect tampering.
- **Audit trail** — every action is appended to `audit_logs` with full context for chain-of-custody.

---

## Getting Started

### Prerequisites
- Docker and Docker Compose
- (For local non-Docker runs: Java 21+, Maven, PostgreSQL, Redis)

### Run with Docker Compose

```bash
# clone
git clone <your-repo-url>
cd evidence-management-system

# build and start the full stack (app + PostgreSQL + Redis)
docker compose up --build
```

The API will be available at `http://localhost:8080`.

### Run locally (without Docker)

```bash
# ensure PostgreSQL and Redis are running locally
# set the required environment variables (see Configuration)
mvn clean package
java -jar target/*.jar
```

---

## API Endpoints

### Auth — `/api/auth`
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/register` | Register a new user | Public |
| POST | `/login` | Authenticate and receive a JWT | Public |

### Cases — `/api/cases`
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/` | Create a case | Authenticated |
| GET | `/` | List all cases | Authenticated |
| GET | `/{id}` | Get a case by ID | Authenticated |
| PUT | `/{id}` | Update a case | Authenticated |

### Evidence — `/api/evidence`
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/upload` | Upload evidence (encrypted + hashed) | Authenticated |
| GET | `/case/{caseId}` | List evidence for a case | Authenticated |
| GET | `/{id}` | Get evidence metadata | Authenticated |
| GET | `/{id}/view` | View (decrypted) evidence | Authenticated |
| GET | `/{id}/download` | Download evidence | Admin |
| GET | `/{id}/verify` | Verify file integrity | Authenticated |
| DELETE | `/{id}` | Delete evidence | Admin |

### Users — `/api/users`
| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/` | List users | Admin |
| GET | `/{id}` | Get a user | Admin |
| PUT | `/{id}/deactivate` | Deactivate a user | Admin |
| PUT | `/{id}/role` | Change a user's role | Admin |

### Assignments — `/api/assignments`
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/` | Assign a user to a case | Admin |
| GET | `/case/{caseId}` | List assignments for a case | Authenticated |
| PUT | `/{id}/deactivate` | Deactivate an assignment | Admin |

### Audit Logs — `/api/audit-logs`
| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/` | All audit logs | Admin |
| GET | `/case/{caseId}` | Logs for a case | Authenticated |
| GET | `/evidence/{evidenceId}` | Logs for a piece of evidence | Authenticated |
| GET | `/user/{userId}` | Logs for a user | Authenticated |

---

## Roadmap / Future Improvements

Planned hardening and scaling work:

- **Cryptography** — migrate AES from ECB to **AES-GCM** (authenticated encryption with a per-file nonce); adopt **per-file keys** via envelope encryption.
- **Secrets** — move keys to a secrets manager / KMS (Vault, AWS Secrets Manager) with rotation.
- **Authorization** — add **resource-level access control** so users only access cases they're assigned to (enforcing the `case_assignments` table in the auth path).
- **Registration** — default new users to a low-privilege role; make role elevation admin-only.
- **Auth** — add **refresh tokens** with a shorter-lived access token.
- **Integrity** — **hash-chain** audit entries and restrict update/delete to make the audit log provably immutable.
- **Transactions** — wrap multi-step evidence upload in a transaction for atomicity across the file write and database writes.
- **Storage** — move evidence files to **object storage (S3)** and process encryption/hashing **asynchronously** via a queue for scale.
- **Operations** — add rate limiting, file-type validation (magic-byte checks), health checks, and metrics/monitoring.

---

## License

This is a portfolio / learning project.
