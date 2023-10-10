# MyCodeReview_backend

## Introduction
📎 배포 링크: https://my-code-review-frontend.vercel.app/

⏳ 개발 기간: 2023.04 ~ 진행 중

👨🏻‍💻 프로젝트 소개
  - 개인 프로젝트로 프론트부터 백엔드까지 혼자 모두 구현했습니다.
  - 코딩 테스트 문제 풀이를 정리하고 복습하고 공유하는 웹 사이트입니다.
    


## Architecture
![My Review](https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/d30cb707-3e95-4272-aed3-e356e20a8743)


## ERD
![MyCodeReview](https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/1d6ef414-2e7b-497e-80bd-f09b27d5cea0)


---
<br>

<details>
  <summary>📂 Version History</summary>

  ### [Version-1.1.0 [23.10.08] [latest]](https://github.com/Seung-IL-Bang/MyCodeReview_backend/pull/26)
  - Redis 캐싱 레이어 도입
  - 벌크 인서트 쿼리 추가
  - @Batchsize 적용

  ### [Version-1.0.1 [23.09.26]](https://github.com/Seung-IL-Bang/MyCodeReview_backend/pull/24)
  - 테스트 환경 통합
  - Board 엔티티 Validator 검증 과정 추가

  ### [Version-1.0.0 [23.09.14]](https://github.com/Seung-IL-Bang/MyCodeReview_backend/tree/version-1.0.0)
  - 첫 번째 릴리즈
</details>


## Description

🔎 목차
  - [단위 테스트 & 테스트 환경 최적화](#단위-테스트-&-테스트-환경-최적화)

---

## 단위 테스트 작성 & 테스트 환경 최적화

- Junit5와 Mockito를 사용하여 단위 테스트를 작성했습니다.
- 성공 케이스뿐만 아니라 실패 케이스도 고려하여 작성했습니다.


- 전체 테스트를 진행할 때 각 테스트 클래스들이 수행될 때 새로운 Spring Boot가 부팅되는 문제가 있었습니다. Spring Boot가 새롭게 부팅되는 것은 리소스 낭비이기 때문에 전체 테스트를 진행할 때 비효율적인 리소스 사용 문제가 있었습니다.
- 자원 낭비 문제 해결을 위해 공통적인 테스트 환경은 상위 추상 클래스를 생성하여 통합할 수 있도록 적용했습니다.

