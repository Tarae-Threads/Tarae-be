# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Tarae backend — Kotlin + Spring Boot 3.5.13 application (com.taraethreads). Currently a bootstrap project with dependencies wired but no business logic implemented yet.

## Build & Run Commands

```bash
./gradlew build          # Build the project
./gradlew bootRun        # Run the application
./gradlew test           # Run all tests
./gradlew test --tests "com.taraethreads.tarae.SomeTest"  # Run a single test class
```

Requires JDK 17 (configured via Gradle toolchain).

## Tech Stack & Dependencies

- **Kotlin 1.9.25** with Spring Boot 3.5.13
- **Spring Data JPA** — ORM/database layer (no datasource configured yet)
- **Spring Security** — authentication/authorization
- **Thymeleaf** — server-side templating (with Spring Security extras)
- **Jackson Kotlin module** — JSON serialization
- **Spring Boot DevTools** — hot reload in dev

## Architecture Notes

- Base package: `com.taraethreads.tarae`
- Entry point: `TaraeApplication.kt`
- JPA entities are auto-opened via `allOpen` plugin (Entity, MappedSuperclass, Embeddable)
- `application.properties` currently only sets the app name — database, security, and other config still needed
