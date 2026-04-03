# DB ERD

> Entity 추가/변경 시 자동 업데이트됨

```mermaid
erDiagram
    %% Entity 추가 시 여기에 업데이트
```

## 설계 원칙

- PK: BIGINT AUTO_INCREMENT (`GenerationType.IDENTITY`)
- 공통 컬럼: `created_at`, `updated_at` (BaseEntity)
- Soft Delete 사용 시: `is_deleted`, `deleted_at`
- 외래키 네이밍: `{참조테이블}_id`
