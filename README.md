# MyCodeReview_backend

## Introduction
📎 배포 링크: https://my-code-review-frontend.vercel.app/

⏳ 개발 기간: 2023.04 ~ 진행 중

👨🏻‍💻 프로젝트 소개
  - 개인 프로젝트로 프론트부터 백엔드까지 혼자 모두 구현했습니다.
  - 코딩 테스트 문제 풀이를 정리하고 복습하고 공유하는 웹 사이트입니다.
    


## Architecture
![My Review](https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/571540ca-b92c-475c-972b-9933b1d989ce)



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

<img width="298" alt="스크린샷 2023-10-10 오후 3 49 31" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/a897e5c5-82e5-450d-b138-7b81a298f61f">


- 전체 테스트를 진행할 때 각 테스트 클래스들이 수행될 때 새로운 Spring Boot가 부팅되는 문제가 있었습니다. Spring Boot가 새롭게 부팅되는 것은 리소스 낭비이기 때문에 전체 테스트를 진행할 때 비효율적인 리소스 사용 문제가 있었습니다.
- 자원 낭비 문제 해결을 위해 공통적인 테스트 환경은 상위 추상 클래스를 생성하여 통합할 수 있도록 적용했습니다.
- Spring Boot가 부팅되는 횟수가 7번에서 1번으로 줄어들었습니다.

<img width="1233" alt="테스트통합설명" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/257782ab-bcf5-44ac-a232-67968d569b6f">

- 테스트 환경 통합에 대한 리소스 사용률 비교를 위해 VisualVM 도구를 사용했습니다.
- VisualVM은 JVM을 실시간 모니터링할 수 있는 오픈소스 기반의 GUI 도구입니다.
- 해당 도구를 사용하여 전체 테스트가 실행될 때 테스트 환경 통합 전후로 어느 정도의 변화가 있는지 파악했습니다.

<img width="921" alt="테스트통합설명2" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/83a8fb71-ec1a-4241-ba34-e711fe0bbf52">

- CPU 사용량은 거의 비슷했지만, 힙 메모리와 스레드 개수에서 차이를 확인할 수 있었습니다.
- <u>전체 힙 메모리 사용량</u>은 대략 165MB에서 113MB로 <u>32% 정도 감소</u>했습니다.
- <u>전체 사용되는 스레드 개수</u>는 810개에서 424개로 <u>48% 정도 감소</u>했습니다.
- 또한, VisualVM이 전체 테스트를 모니터링 하는 시간도 23s -> 18s 감소했습니다.
- 결과적으로 테스트 환경을 통합한 것이 **전체 테스트를 수행하는 데 드는 비용을 줄일 수 있었습니다.**



