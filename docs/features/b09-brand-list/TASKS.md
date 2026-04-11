# TASKS: 브랜드 목록 조회 API

- [x] T1. DTO 작성 (완료일: 2026-04-11)
  - 목적: 응답 구조 정의
  - 작업 내용: `BrandItem`, `BrandTypeGroup`, `BrandGroupResponse` DTO 작성
  - 예상 변경 파일: `place/dto/BrandGroupResponse.kt`
  - 완료 기준: DTO 클래스 컴파일 성공

- [x] T2. BrandService 그룹핑 로직 구현 (TDD) (완료일: 2026-04-11)
  - 목적: 브랜드를 타입별로 그룹핑하는 서비스 로직
  - 작업 내용: `BrandService.getBrandsGroupedByType()` 구현, 단위 테스트 작성
  - 예상 변경 파일: `place/service/BrandService.kt`, `BrandServiceTest.kt`
  - 완료 기준: 단위 테스트 통과

- [x] T3. BrandController API 엔드포인트 구현 (TDD) (완료일: 2026-04-11)
  - 목적: `GET /api/brands` 엔드포인트 노출
  - 작업 내용: `BrandController` 작성, 통합 테스트 작성
  - 예상 변경 파일: `place/controller/BrandController.kt`, `BrandControllerTest.kt`
  - 완료 기준: 통합 테스트 통과, Swagger 문서 확인
