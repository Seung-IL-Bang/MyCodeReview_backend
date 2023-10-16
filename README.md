# MyCodeReview_backend

## Introduction
📎 배포 링크: https://my-code-review-frontend.vercel.app/

⏳ 개발 기간: 2023.04 ~ 진행 중

👨🏻‍💻 프로젝트 소개
  - 개인 프로젝트로 프론트부터 백엔드 및 무중단 배포까지 모두 구현한 프로젝트입니다.
  - 코딩 테스트 문제 풀이를 정리하고 복습하고 공유할 수 있는 웹 사이트입니다.
    

## Architecture
![My Review](https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/571540ca-b92c-475c-972b-9933b1d989ce)



## ERD
![MyCodeReview](https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/1d6ef414-2e7b-497e-80bd-f09b27d5cea0)


---
<br>

<details>
  <summary>📂 Version History</summary>

  ### [Version-1.1.2 [23.10.16] [latest]](https://github.com/Seung-IL-Bang/MyCodeReview_backend/pull/37)
  - Dummy Data 추가 기능
  - Review 작성 및 수정 시 엔티티 검증 추가
  - Board 단일 조회시 작성자 식별 불가능 오류 수정

  ### [Version-1.1.1 [23.10.13]](https://github.com/Seung-IL-Bang/MyCodeReview_backend/pull/30)
  - 좋아요 기능에 대한 테스트 코드 수정 및 추가 & Swagger info 수정
  - @entitygraph 추가 & 중복되는 JWT 검증 로직 제거

  ### [Version-1.1.0 [23.10.08]](https://github.com/Seung-IL-Bang/MyCodeReview_backend/pull/26)
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
  - [성능 테스트를 위한 Dummy Data 벌크 인서트](#성능-테스트를-위한-dummy-data-벌크-인서트)
  - [단위 테스트 작성 & 테스트 환경 최적화](#단위-테스트-작성--테스트-환경-최적화)
  - [AWS Pipeline & Nginx를 활용한 무중단 배포](#aws-pipeline--nginx를-활용한-무중단-배포)
  - [N+1 문제 해결](#n1-문제-해결)
  - [낙관적 락을 이용한 좋아요 기능 동시성 제어](#낙관적-락을-이용한-좋아요-기능-동시성-제어)
  - [QueryDSL을 이용한 동적 쿼리 처리](#querydsl을-이용한-동적-쿼리-처리)
  - [Swagger를 사용한 API 문서화](#swagger를-사용한-api-문서화)

---

## 성능 테스트를 위한 Dummy Data 벌크 인서트

- 실제 배포환경에서 애플리케이션의 성능 테스트를 위해 DB에 어느 정도 dummy data가 필요했습니다.
- dummy data를 DB에 저장하기 위해 EasyRandom 라이브러리를 이용해서 객체들을 쉽게 생성했습니다.
- JPA에서는 수 많은 쿼리를 한 번에 보내주는 벌크 인서트 쿼리 기능을 지원하지 않아서 JDBC를 사용하여 벌크 인서트 쿼리를 구현했습니다.
- 일반 사용자가 배포환경에서 벌크 인서트 API를 호출하면 안 되기 때문에, ADMIN 권한을 가진 계정만 호출 할 수 있도록 로직을 구현했습니다.
  
<img width="1094" alt="bulkinsert" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/9e5e86d0-04e2-4408-a4aa-dbdb7b0c80ba">

- 아래 이미지는 벌크 인서트 쿼리의 API를 테스트하는 `Swagger UI` 입니다.
- Parameters들을 조절해가면서 적절한 Dummy Data 양을 DB에 저장할 수 있습니다.
- 주의해야 할 점은 너무 많은 양의 데이터를 한 번에 시도하면 문제가 발생할 수 있습니다. 발생 가능한 문제로는 두 가지가 있습니다.
  - `Heap Space Out Of Memory`: 더미 데이터를 대량으로 삽입하려고 할 때, 한 번에 너무 많은 객체가 생성되어 힙 메모리를 초과하는 경우 힙 메모리 부족 문제가 발생할 수 있습니다.
  - `504 Gateway Time-Out`: 서버가 요청을 처리하는 데 필요한 시간이 너무 길어서 게이트웨이나 프록시가 시간 초과로 응답을 중단할 수 있습니다.

<p align="center">
  <img width="608" alt="bulkswagger" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/7e31e9ed-7c28-4fbf-a520-60ba9c560d05">
</p>

<br/>

## 단위 테스트 작성 & 테스트 환경 최적화

- Junit5와 Mockito를 사용하여 단위 테스트를 작성했습니다.
- 성공 케이스뿐만 아니라 실패 케이스도 고려하여 작성하도록 노력했습니다.

<img width="298" alt="단위테스트작성" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/a897e5c5-82e5-450d-b138-7b81a298f61f">


- 전체 테스트를 진행할 때 각 테스트 클래스들이 수행되면서 새로운 Spring Boot가 부팅되는 문제가 있었습니다. Spring Boot가 새롭게 부팅되는 것은 리소스 낭비이기 때문에 전체 테스트를 수행할 때 비효율적인 리소스 사용 문제가 있을 것이라 예상했습니다.
- 자원 낭비 문제 해결을 위해 공통적인 테스트 환경은 상위 추상 클래스를 생성하여 통합할 수 있도록 적용했습니다.
- 테스트 환경을 통합함으로써 Spring Boot가 부팅되는 횟수가 7회에서 1회로 줄어들었습니다.

<img width="1233" alt="테스트통합설명" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/257782ab-bcf5-44ac-a232-67968d569b6f">

- 테스트 환경 통합함으로써 리소스 사용량의 변화를 확인하고 싶었습니다. 이를 확인할 수 있도록 도와주는 VisualVM이라는 도구를 찾을 수 있었습니다.
- VisualVM은 JVM을 실시간 모니터링할 수 있는 오픈소스 기반의 GUI 도구입니다.
- 해당 도구를 사용하여 전체 테스트가 실행될 때 테스트 환경 통합 전후로 어느 정도의 리소스 사용량 변화가 있는지 파악할 수 있었습니다.

<img width="1025" alt="테스트통합설명2" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/92968154-b26f-4c7c-a800-20fe3a6ae00d">

- CPU 사용량은 거의 비슷했지만, 힙 메모리와 스레드 개수에서 차이를 확인할 수 있었습니다.
- <u>전체 힙 메모리 사용량</u>은 대략 165MB에서 113MB로 <u>32% 정도 감소</u>했습니다.
- <u>전체 사용되는 스레드 개수</u>는 810개에서 424개로 <u>48% 정도 감소</u>했습니다.
- 또한, VisualVM이 전체 테스트를 모니터링 하는 시간도 23s -> 18s 감소했습니다.
- 결과적으로 테스트 환경을 통합한 것이 **전체 테스트를 수행하는 데 드는 비용을 줄일 수 있었습니다.**

<br/>

## AWS Pipeline & Nginx를 활용한 무중단 배포
![image](https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/d2fcfb9c-a6d1-4620-be94-140c27d8cc34)

- 개발을 진행하면서 새로운 코드를 서버에 수동으로 매번 통합하는 것은 번거로운 작업이었습니다.
- 수동으로 배포시 실수로 인해 제대로 배포가 진행되지 않는 경우도 있었으며, 배포가 진행될 때마다 애플리케이션이 중단되어야만 했습니다.
- 비효율적인 수동 배포와 서버가 중단되는 문제점을 해결하고자, AWS Pipeline과 Nginx를 도입했습니다.
- AWS Pipeline은 [Github 소스 - CodeBuild - CodeDeploy]로 구성되며 새롭게 추가된 코드를 자동으로 EC2 인스턴스에 배포할 수 있도록 도와주는 CI/CD 파이프라인입니다.
- Nginx를 사용하여 현재 사용하고 있는 애플리케이션과 IDLE(휴식) 애플리케이션을 교차 사용해가면서 Nginx Reload를 통해 서버가 무중단 배포될 수 있도록 구현했습니다.

<br/>

## N+1 문제 해결
[N+1 문제에 대한 탐구 정리글](https://devlog-seung-il-bang.vercel.app/orm-jpa-n+1-problem)
- 사용자 본인의 게시물 목록을 조회해오는 로직에서 게시물들의 태그 목록들을 집계하는 순간 N+1 문제가 발생하는 것을 발견했습니다.
- 처음 시도한 해결 방법으로는 연관 엔티티에 `@Batchsize` 애너테이션을 적용하였고, 쿼리에 IN 절이 추가되어 N+1 문제를 어느 정도 완화시킬 수 있었습니다.
- 하지만, 완전히 N+1 문제가 해결되는 것이 아니었습니다. 다음과 같은 문제가 여전히 존재했습니다.
  1. Batch size를 적절하게 정하기 어려웠고, size에 따라 문제 해결 정도가 달랐습니다.
  2. size를 크게 할 수록 데이터베이스와의 라운드트립 횟수를 줄일 수 있었지만, 너무 큰 size는 DB에 부하를 증가시킬 뿐만 아니라 힙 메모리 사용량이 많아지게 되어 Out of Memory 발생을 야기할 가능성이 있었습니다.
- 결과적으로 `@Batchsize`를 통한 완전한 해결은 불가능했지만 어느 정도 완화시킬 수는 있었습니다.
- 다른 해결 방법으로는 `@EntityGroup`과 `Fetch Join` 등이 있었습니다.
- 둘 다 효과적으로 N+1 문제를 해결 할 수 있지만, 사용성이 더 편한 `@EntityGroup` 방법을 사용하기로 결정했습니다.
- `@EntityGroup` 사용으로 필요한 연관 엔티티만 Eager Loading 방식으로 로드할 수 있었고 덕분에 N+1 문제도 해결할 수 있었습니다.

<br/>

## 낙관적 락을 이용한 좋아요 기능 동시성 제어
- 게시글의 좋아요 기능은 동시다발적인 요청이 발생 가능하기 때문에 동시성 이슈로 인해 데이터 정합성이 깨질 우려가 있습니다.
- 데이터 정합성을 보장하기 위해서 낙관적 락을 구현할 수 있도록 해주는 `@Version` 애너테이션을 게시글 엔티티 필드에 적용했습니다.
- 동시성 문제로 인해 업데이트에 실패했을 경우 좋아요 요청을 자동으로 재시도하는 로직도 구현했습니다.

```java
  try {
      likesService.postLike(likeRequestDTO);
  } catch (ObjectOptimisticLockingFailureException e) { // 좋아요 트랜잭션이 실패한 경우 ObjectOptimisticLockingFailureException 예외 발생
      int maxAttempt = 3;
      for (int attempt = 1; attempt <= maxAttempt; attempt++) { // 최대 3회까지 좋아요 재시도
          try {
              likesService.postLike(likeRequestDTO);
              break;
          } catch (ObjectOptimisticLockingFailureException oe) {
              if (attempt == maxAttempt) {
                  throw new BusinessLogicException(ExceptionCode.LOCKING_FAILURE); // 3회 재시도 후에도 실패할 경우 유저에게 '잠시 후 재시도 요청' 알림
              }
          }
      }
  }
```

- Future 인터페이스를 사용하여 멀티 스레드를 활용한 동시성 이슈 테스트를 진행했습니다.
  1. 동시성 이슈 발생 시 낙관적 락킹 예외가 잘 발생하는지 테스트 했습니다.
  2. 동시성 이슈 발생 시 자동 재요청 로직이 제대로 작동하여 데이터 정합성이 잘 보장되는지 테스트 했습니다.

<img width="489" alt="동시성 테스트 통과" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/7851a40d-9e36-4631-85a2-a0ad6df44821">

<br/>

## QueryDSL을 이용한 동적 쿼리 처리
- 제목, 태그, 난이도 등을 조합한 검색을 위해 사용자의 입력값에 따라 쿼리의 조건이 바뀌는 동적 쿼리를 QueryDSL을 통해 구현했습니다.
- QueryDSL 사용으로 다음과 같은 장점을 얻을 수 있었습니다.
  1. 타입 안정성: 컴파일 타임에 쿼리의 오류를 미리 발견할 수 있었습니다.
  2. 코드 자동완성: IDE에서 지원하는 필드 또는 메서드에 대해 자동완성을 지원받아, 개발 생산성을 높일 수 있었습니다.
  3. 동적 쿼리 작성 용이성: 조건이 바뀌는 동적 쿼리를 쉽고 간결하게 작성할 수 있었습니다.
  4. 유지 보수: 쿼리를 Java 코드로 작성하기 때문에 쉽게 유지보수할 수 있었습니다.

<br/>

## Swagger를 사용한 API 문서화
- 예전에 진행했던 팀프로젝트의 경우 초기에 API 문서를 수동으로 관리했었습니다. 수동으로 관리하게 되면 기능의 추가나 수정이 생겼을 경우 매번 API 문서도 직접 수정해야 했습니다. 반복적인 수정 작업은 비효율적이며 실수가 생길 가능성이 컸습니다.
- 이런 문제를 예방하기 위해 해당 프로젝트에서는 API 문서 자동화를 도입하기로 결정했습니다.
- API 자동 문서화를 지원하는 라이브러리로는 Spring Rest Docs와 Swagger 두 개의 후보가 있었고, 각각 장단점을 비교하여 결정하기로 했습니다.
- Spring Rest Docs는 테스트 코드와 함께 사용되어 정확한 문서를 생성하지만, 사용하는데 학습 난이도가 꽤 있는 편이고 테스트 코드를 구성하는 데 복잡할 수 있습니다. 반면에 Swagger는 진입장벽이 낮을뿐더러 UI가 사용자 친화적이며 빠르게 문서를 생성할 수 있고, API 테스트도 UI에서 바로 가능한 이유로 개인 프로젝트에서는 Spring Rest Docs보다 Swagger가 더욱 적합하다고 판단하여 Swagger를 도입하기로 했습니다.
- 간단한 의존성 추가와 설정 클래스만으로 Swagger를 도입할 수 있었고 API 문서화를 자동으로 잘 정리할 수 있었습니다. 결국 API 문서 자동화 덕분에 개발 생산성을 높일 수 있었습니다.

