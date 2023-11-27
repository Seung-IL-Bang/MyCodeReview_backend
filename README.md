# MyCodeReview_backend

## Introduction
📎 배포 링크: https://my-code-review-frontend.vercel.app/

⏳ 개발 기간: 2023.04 ~ 진행 중

👨🏻‍💻 프로젝트 소개
  - 개인 프로젝트로 프론트부터 백엔드 및 CI/CD까지 모두 구현한 프로젝트입니다.
  - 코딩 테스트 문제 풀이를 정리하고 복습하고 공유할 수 있는 웹 사이트입니다.
    

## Architecture
![v4 0 0My Review](https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/04dcf008-8315-4384-87c2-81057f7ed483)





## ERD
![MyCodeReview](https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/1d6ef414-2e7b-497e-80bd-f09b27d5cea0)


---
<br>

<details>
  <summary>📂 Version History</summary>

  ### [Version-4.0.0 [23.11.22] [latest]](https://github.com/Seung-IL-Bang/MyCodeReview_backend/pull/69)
  - Logback을 이용한 KafkaAppender 구현
  - 실시간 로그 모니터링 시스템 구축을 위한 Kafka + ELK 스택 도입
  - 쓰기 작업 및 로그인 API에 대한 이벤트 로그 전송

  ### [Version-3.0.0 [23.11.5]](https://github.com/Seung-IL-Bang/MyCodeReview_backend/pull/67)
  - Database Replication: 읽기 전용 복제본 생성
  - ProxySQL을 통한 쿼리 분산
  - 읽기 쿼리는 Master/Slave 분산
  - 쓰기 쿼리는 Master로 라우팅

  ### [Version-2.0.0 [23.10.31]](https://github.com/Seung-IL-Bang/MyCodeReview_backend/pull/48)
  - 도커 컴포즈를 이용한 CI/CD 구축
  - Auto Scaling Group 생성
  - Target Tracking Policy 에 따른 Scale Out & Scale In (criteria: CPU 90%)

  ### [Version-1.1.3 [23.10.26]](https://github.com/Seung-IL-Bang/MyCodeReview_backend/pull/39)
  - 좋아요 기능의 동시성 유발 테스트 환경 개선
  - 자동 배포 시 Health Check 로직: 기존 port 넘버 체크 -> Actuator로 수정
  - buildspec.yml, appspec.yml 수정
  - 직렬화 & 역직렬화 테스트 케이스 추가
  - cache stampede 방지를 위한 캐싱 로직에 Lock 메커니즘 추가.

  ### [Version-1.1.2 [23.10.16]](https://github.com/Seung-IL-Bang/MyCodeReview_backend/pull/37)
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

<details>
  <summary>📂 서비스 주요 기능</summary>

  ### Board
    - 게시글 조회
    - 게시글 생성
    - 게시글 수정
    - 게시글 삭제
    - 게시글 리스트 조회
    - 제목, 난이도, 태그별 동적 쿼리
    - 본인이 작성한 게시글 리스트 조회
    - 본인이 좋아요한 게시글 리스트 조회

  ### Comment
    - 댓글 생성
    - 댓글 수정
    - 댓글 삭제

  ### Likes
    - 게시글 '좋아요' 하기
    - 게시글 '좋아요' 취소

  ### Member
    - 회원 정보 조회

  ### Reply
    - 답글 생성
    - 답글 수정
    - 답글 삭제

  ### Review
    - 리뷰 조회
    - 리뷰 생성
    - 리뷰 수정
    - 리뷰 삭제
</details>

## Description

🔎 목차
  - [성능 테스트를 위한 Dummy Data 벌크 인서트](#성능-테스트를-위한-dummy-data-벌크-인서트)
  - [인덱스를 통한 쿼리 성능 개선하기](#인덱스를-통한-쿼리-성능-개선하기)
  - [DB 부하를 줄이기 위한 캐싱](#db-부하를-줄이기-위한-캐싱)
  - [단위 테스트 작성 & 테스트 환경 최적화](#단위-테스트-작성--테스트-환경-최적화)
  - [AWS Pipeline & Nginx를 활용한 무중단 배포](#aws-pipeline--nginx를-활용한-무중단-배포)
  - [도커 컴포즈와 오토 스케일링 도입](#도커-컴포즈와-오토-스케일링-도입)
  - [Database Replication & ProxySQL 쿼리 분산](#database-replication--proxysql-쿼리-분산)
  - [Kafka & ELK 스택을 이용한 로그 모니터링 시스템 구축](#kafka--elk-스택을-이용한-로그-모니터링-시스템-구축)
  - [OAuth2 & JWT 기반 로그인 기능](#oauth2--jwt-기반-로그인-기능)
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
  <img width="500" height="500" alt="bulkswagger" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/7e31e9ed-7c28-4fbf-a520-60ba9c560d05">
</p>

<br/>

## 인덱스를 통한 쿼리 성능 개선하기
- 애플리케이션의 성능을 올리기 위해서 인덱스 생성을 통해 조회 쿼리의 성능을 개선했습니다.
- 현재 서비스되고 있는 기능들의 쿼리를 조사하여, 성능 개선을 기대할 만한 테이블의 필드에 인덱스를 생성해주었습니다.
- JMeter 도구를 사용하여 인덱스를 생성하기 전과 후의 성능 비교를 진행했습니다.
- 성능 테스트는 아래와 같은 과정으로 진행했습니다.
  1. 기준 설정
     - 인덱스 생성 전의 성능을 측정하여 기준점을 설정합니다.
     - 동일한 환경, 동일한 데이터셋, 동일한 쿼리를 사용하여 테스트를 수행합니다.
     - **Number of Threads**: 100명의 사용자가 동시에 요청을 보내도록 설정했습니다.
     - **Ramp-Up Period(seconds)**: 10초로 설정하여, 100명의 사용자가 10초 동안 점진적으로 증가하는 시나리오로 설정했습니다.
     - **Loop Count**: 충분한 결과를 수집하여 일정한 평균값을 얻어내기 위해, 무한 반복으로 설정했습니다.
  2. 인덱스 생성
     - 성능 향상을 위해 적절한 필드에 인덱스를 걸어줍니다.
  3. 테스트 수행
     - 인덱스 생성 후, 동일한 환경과 쿼리로 성능 테스트를 다시 수행합니다.
     - 주로 응답 시간(response time)과 처리량(throughput)을 측정합니다.
  4. 결과 비교
     - 인덱스 생성 전후의 성능 측정 결과를 비교하여 성능 향상을 평가합니다.

<img width="2030" alt="index" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/e879d160-214c-41e8-ab5f-0ba886e2acfd">

<br/>

## DB 부하를 줄이기 위한 캐싱
- 게시글 목록 조회는 많은 사용자들이 사용할 API로 DB의 많은 부하가 발생할 것으로 예상하여, DB 부하를 줄이고자 캐시 저장소를 도입하기로 결정했습니다.
- 캐시 저장소로 Memcached와 Redis 둘 중에 어떤 것을 사용해야 할지 장단점을 비교하여 결정했습니다.
- Memcached는 안정적이고 빠른 응답 속도를 가진다는 장점이 있지만, 데이터 복구 미지원 및 다양한 캐싱 요구 사항에 대응하기에는 부족한 측면이 있었습니다.
- 반면에 Redis는 다양한 데이터 구조를 지원하여 다양한 캐싱 요구 사항에 대응할 수 있으며, RDB, AOF 등을 통해 시스템 장애나 재시작시에 데이터를 보존할 수 있습니다.
- Spring에서도 공식적으로 Redis를 지원하고 있기도 하고 활발한 커뮤니티가 존재하기 때문에 참고할 자료가 풍부했습니다.
- 결과적으로 Spring Boot 환경에서 Redis는 다양한 캐싱 요구 사항과 확장성, 지속성 등의 면에서 Memcached보다 더 나은 선택이 될 것 같아 Redis를 사용하기로 결정했습니다.
- 스케일 아웃 방식의 분산 환경으로 확장하는 것을 고려했을 때 로컬 캐싱 전략보다 글로벌 캐싱 전략으로 AWS의 ElastiCache Redis를 사용하기로 결정했습니다.
  - 게시글 목록을 캐싱하는 것은 DB의 부하를 줄일 수 있었으나, 해당 데이터의 키 생성 조건에 때문에 캐싱 데이터를 업데이트 하는 것에는 어려움이 존재했습니다.
  - 따라서 데이터 정합성이 깨지는 경우를 최소화하면서도 Cache Rate을 적당히 유지 하기 위해 게시글 목록에 해당하는 캐싱 데이터의 TTL을 7초로 짧게 설정했고, 만약 데이터 정합성이 깨진 게시글을 조회한다면 알맞은 예외 처리를 진행하도록 구현해놨습니다.
  
<img width="1077" alt="diffCaching" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/f755259a-9e99-440f-8d06-7502c97a67fd">

- 성능 테스트는 아래와 같은 과정으로 진행했습니다.
  1. 기준 설정
     - 캐싱 도입 전의 성능을 측정하여 기준점을 설정합니다.
     - 동일한 환경, 동일한 데이터셋, 동일한 API로 사용하여 테스트를 수행합니다.
     - **Number of Threads**: 50명의 사용자가 동시에 요청을 보내도록 설정했습니다.
     - **Ramp-Up Period(seconds)**: 10초로 설정하여, 50 사용자가 10초 동안 점진적으로 증가하는 시나리오로 설정했습니다.
     - **Loop Count**: 충분한 결과를 수집하여 일정한 평균값을 얻어내기 위해, 무한 반복으로 설정했습니다.
  2. 캐싱 적용
     - 성능 향상을 위해 적절한 메소드에 캐싱 로직을 추가합니다.
  3. 테스트 수행
     - 캐싱 적용 후, 동일한 조건으로 성능 테스트를 다시 수행합니다.
     - 주로 응답 시간(response time)과 처리량(throughput)을 측정합니다.
  4. 결과 비교
     - 캐싱 생성 전후의 성능 측정 결과를 비교하여 성능 향상을 평가합니다.

- 캐싱을 적용하여 성능을 개선해나가는 과정은 순탄하지 않은 경험이었습니다.
- 부하 테스트 도중 CPU가 100%로 치솟아 `504 Gateway Timeout`이 되는 에러, 직렬화 과정 중 발생하는 `LazyInitializationException` 에러로 인한 500 서버 에러 등 여러 문제를 직면한 경험이었습니다.
- CPU 사용량이 급격하게 늘어나는 것을 방지하기 위해 `불필요한 fetchJoin 제거`, `캐시 키 생성 최적화`, `DTO 간소화`, `커넥션 풀 이용`, `락을 이용한 cache stampede 방지` 등 여러 방면에서 CPU 사용량을 줄이려고 노력했고 덕분에 응답 시간의 급격한 증가를 예방할 수 있었습니다.
- DB 연결 및 해제 과정은 리소스를 많이 사용하기 때문에 커넥션 풀을 통해 미리 생성된 연결을 재사용하여 CPU 부하를 줄이려고 했습니다. 테스트를 여러 번 진행하면서 연결 풀의 크기를 조절하였고 최적의 성능을 보이는 수준에서 연결 풀 크기를 정했습니다.
- 수 많은 요청이 캐시 항목에 의존하고 있을 때, 그 항목이 만료되면 모든 요청이 동시에 백엔드 시스템에 접근하여 데이터를 재계산하려고 시도하는 `cache stampede`가 발생할 수 있습니다. 이로 인해 백엔드 시스템이나 데이터베이스에 큰 부하가 발생할 수 있기 때문에 `Reentrant Lock` 을 적용하여 하나의 스레드만이 재계산 과정을 거치도록 부하를 줄일 수 있도록 했습니다. 또한 락을 대기하는 스레들 또한 중복되는 계산 로직을 회피하기 위해 임계 구역 내부에 캐싱이 가능한지 더블 체크 하도록 로직을 구현했습니다.
- `LazyInitializationException`에러를 해결하는 과정에서 연관 관계에 있는 객체 또는 필드들을 직렬화&역직렬화 때 어떻게 초기화해줘야 하는지 배울 수 있었고, 그 과정에서 지연 로딩을 구현하게 해주는 Hibernate 프록시 객체에 대해서 알 수 있게 되었습니다.
- 또한 서비스 특징을 고려하여 Redis의 maxmemory-policy 설정을 LRU에서 LFU로 변경했습니다. 일부 항목이 빈번하게 액세스되더라도, 잠시 동안 액세스되지 않으면 캐시에서 제거될 위험이 있기 때문에 빈번하게 액세스되는 항목은 캐시에 오랜 시간 동안 유지될 수 있도록 ElastiCache 파라미터 그룹에서 LFU로 변경해주었습니다.

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

## 도커 컴포즈와 오토 스케일링 도입
- 기존 단일 인스턴스로 서버를 운영할 경우 여러 가지 문제점이 존재했습니다.
   1. 확장성 부족: 단일 서버는 트래픽이 증가할 때 리소스를 즉각적으로 확장하는 데 한계가 있습니다. 이로 인해 서비스 지연이나 비정상적으로 종료되버릴 경우 서비스 전체가 중단될 위험이 있습니다.
   2. 가용성 제한: 서버에 장애가 발생하면 전체 서비스에 영향이 생길 수 밖에 없습니다. 이로 인해 서비스의 안정성이 떨어지게 됩니다.
   3. 유지보수 어려움: 애플리케이션의 업데이트나 패치 시 Nginx를 통해 무중단 배포를 진행하지만 두 개의 동일한 애플리케이션이 실행되어야 한다는 리소스 부담이 존재합니다.
   4. 환경 일관성의 부재: 개발 및 테스트, 프로덕션 환경 간의 설정 차이로 인해 `It works on my machine` 문제가 발생할 수 있습니다.
- 위와 같은 문제점들을 개선하고자 도커 컴포즈와 오토 스케일링을 도입하기로 결정했습니다.
- **도커 컴포즈를 도입함으로써 다음과 같은 장점이 있었습니다.**
   1. 환경 일관성: 도커 컨테이너를 사용하면 개발부터 프로덕션까지 일관된 환경을 보장할 수 있습니다. 이는 배포 시 발생할 수 있는 문제를 최소화할 수 있었습니다.
   2. 빠른 배포와 롤백: 컨테이너화된 애플리케이션은 배포와 롤백이 빠르고 쉽습니다. 이는 유지보수 시간을 단축하고 가용성을 향상시킬 수 있었습니다.
- 아래 이미지는 새로운 버전의 애플리케이션 컨테이너가 배포되는 과정의 시퀀스 다이어그램입니다.
![deployDockerCompose](https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/f17a7d75-7fe7-45f4-91c7-2d1556727bbe)


- **오토 스케일링을 도입함으로써 다음과 같은 장점이 있었습니다.**
   1. 자동 리소스 조정: 트래픽이 증가하면 자동으로 추가 인스턴스를 배포하여 부하를 분산시키고, 트래픽이 감소하면 인스턴스를 줄여 비용을 절감합니다.
   2. 고가용성: 여러 인스턴스가 분산 배치되므로, 하나의 인스턴스에 문제가 생겨도 서비스 전체에 영향을 미치지 않을 수 있었습니다.
   3. 부하 분산: 로드 밸런서와 함께 사용하여 트래픽을 RR 방식을 통해 효율적으로 관리할 수 있었고, 서비스의 안정성을 높일 수 있었습니다.

- 아래 이미지는 오토 스케일링에 의해 Scale Out 되는 과정을 나타낸 시퀀스 다이어그램입니다.
![autoscaling](https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/d43faf42-e248-4169-a131-c66a84ebb08f)
- 아래 이미지는 `/board/list` 게시글 목록 조회 API에 대해 Scale Out 전후의 성능을 비교한 것으로, 한 대의 서버일 때보다 두 대의 서버일 때 성능이 올라간 것을 확인한 결과입니다.
  <img width="1424" alt="scaleoutperformance" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/c7942c20-b4bd-47c5-bb82-bacf989c32aa">

<br/>

## Database Replication & ProxySQL 쿼리 분산
- 트래픽이 증가함에 따라 오토 스케일링으로 애플리케이션 서버의 확장을 구축했지만, 데이터베이스는 여전히 과부하로 인한 병목지점이 될 수 있을 것이라 생각했습니다. 이는 EC2 인스턴스가 아무리 늘어난다 하더라도, DB 병목으로 서비스 전체 성능에 악영향이 될 것입니다.
- 우선, DB의 부하를 나누고자 AWS RDS의 읽기 복제본 생성을 통해 Replica를 쉽게 생성할 수 있었습니다. 해당 복제본은 기본적으로 `Asynchronous Replication`을 통해 데이터의 무결성을 제공합니다.
- 처음에는 애플리케이션 레벨에서 DataSource를 두 개로 나누어 읽기는 Slave(=Replica), 쓰기는 Master(=Source)로 보내도록 구현하려 했습니다.
- 하지만 해당 방식은 읽기 쿼리를 RR 방식으로 분산시키는 로직을 직접 구현해야 했으며, 이는 애플리케이션 코드가 상당히 데이터베이스에 의존적으로 변하게 되어 유연성이 떨어질 것이라 생각했습니다.
- 또한, Master가 다운되게 된다면 가용성이 떨어져 대처가 어렵다는 단점이 있었습니다.
- 이러한 단점들과 확장성 및 유연성을 고려하여 ProxySQL이라는 프록시 서버를 도입해서 쿼리의 타입(읽기, 쓰기)별로 쿼리를 분산 처리하기로 결정했습니다.
- ProxySQL의 설정과 datasource의 URL만 수정해주면 ProxySQL을 통해 읽기 쿼리가 RR 방식으로 Master와 Slave에 분산 처리되고, 쓰기 쿼리는 Master에서만 처리됩니다.
- 또한 ProxySQL은 Master DB의 장애가 생겨 쿼리를 처리할 수 없게 되는 경우, 자동으로 `Failover`를 수행하여 Slave 호스트 그룹 중 하나를 자동으로 Master DB로 승격하는 기능을 지원합니다.
- 결과적으로 이 작업을 통해서 데이터베이스의 부하를 분산할 수 있었고, 서비스 전체의 고가용성을 좀 더 보장할 수 있게 되었습니다.
- 아래 이미지는 애플리케이션에 임의의 부하를 가했을 때, Master/Slave DB 커넥션 개수를 비교한 그래프입니다. 커넥션의 개수가 분산됨을 확인할 수 있었고 부하로 인한 DB 병목의 완화를 기대할 수 있게 됐습니다.
  <img width="1511" alt="스크린샷 2023-11-05 오전 10 58 24" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/349395fe-1368-48e3-9c0c-f98faa95b05f">

- 하지만 한계점으로는 ProxySQL을 거쳐서 DB로 가기 때문에 이로 인한 네트워크 지연을 고려해야 했습니다.
- 한계점을 극복하고자 Connection Pool을 고려하여 적절한 커넥션 풀 개수를 조정해주면 커넥션을 열고 닫는데 드는 비용을 줄일 수 있고, 성능에 좀 더 좋은 영향을 줄 수 있을 것이라 생각했습니다.
- Spring Boot와 ProxySQL 간의 커넥션 풀, 그리고 ProxySQL과 Database 간의 커넥션 풀을 모두 고려하여, 앞으로 최적의 커넥션 풀을 찾는 작업을 할 계획입니다.

<br/>

## Kafka & ELK 스택을 이용한 로그 모니터링 시스템 구축

1. 로그 모니터링 부재로 인한 문제점과 불편한 점
- 이벤트가 발생할 때마다 그에 대한 실시간 감지가 불가능하여, 문제가 발생하면 신속하게 파악하고 해결하기 어려웠습니다.
- 컨테이너에서 실행되는 애플리케이션들의 로그 데이터를 효과적으로 수집하고 관리하기 어려웠으며, 로그 데이터의 중앙 집계 및 검색이 불가능한 환경이었습니다.

2. 로그 모니터링 시스템 구축
- 앞서 언급한 문제를 해결하고 향후 유사한 문제를 미리 예방하기 위해 로그 모니터링 시스템을 구축하기로 결정하였습니다.
- Spring Boot 애플리케이션에서 쓰기(Write) 작업에 대한 이벤트 로그를 생성하여 Kafka 브로커에 전송하고, Logstash에서 Consume하여 Elasticsearch에 저장합니다. 최종적으로 Kibana에서 로그를 모니터링 할 수 있도록 했습니다.

3. 기술 스택 선정
- 로그 모니터링 구축을 위해 Kafka와 ELK 스택을 아래와 같은 이유로 선택했습니다.
  - Kafka: 대규모 데이터 스트리밍 처리와 이벤트 기반 로그 모니터링에 적합하며, 실시간 이벤트 감지와 고급 데이터 스트리밍을 제공합니다.
  - ELK 스택: 중앙 집계, 강력한 검색 및 시각화 능력을 통해 로그 데이터 관리 및 분석에 용이합니다.
  - 이러한 조합은 대용량 데이터 처리, 중앙 집계, 강력한 검색 및 시각화를 통합하여 제공하며, 확장성과 커뮤니티 지원 측면에서도 강점을 가지고 있었습니다.

4. 결과
- 실시간 로그 데이터 수집, 중앙 집계, 검색 및 시각화를 통해 시스템 모니터링을 향상시키고, 대응 시간을 단축하며, 문제를 빠르게 파악할 수 있게 되었습니다.
- Kafka와 ELK 스택을 구축하고 운영하면서 이러한 기술에 대한 이해도를 크게 향상시키는 경험이었습니다.

아래 이미지는 Spring Boot에서 발생한 이벤트 로그를 Kafka -> Logstash -> Elasticsearch 을 거쳐서 최종적으로 Kibana 대시보드에서 확인한 결과입니다.
<img width="1375" alt="스크린샷 2023-11-24 오후 4 08 43" src="https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/8859a402-328a-4195-a37a-453ba37d19f7">




<br/>

## OAuth2 & JWT 기반 로그인 기능
- OAuth2와 JWT 기반의 로그인 기능을 구현했습니다.
- OAuth2 도입 이유는 간편 로그인을 통해 사용자 정보를 직접 관리할 필요가 없어지므로 보안에 신경을 줄일 수 있습니다. 이로 인해 다른 기능 개발에 더 몰입할 수 있기 때문입니다.
- 세션 방식과 JWT 방식의 장단점을 비교하여 JWT 기반으로 결정하게 됐습니다.
- 세션 기반은 Stateful 한 방식으로 서버에서 사용자 정보를 저장해야 하므로 확장성에 제한이 있습니다. Redis를 활용하여 세션 클러스터링을 구현할 수 있지만, Redis는 이미 캐시 저장소로 사용되고 있으므로 추가적인 리소스 비용 발생의 부담이 생깁니다. 그리고 많은 사용자가 접속할 경우 서버의 부하가 증가한다는 단점이 있습니다.
- 반면에, JWT 기반 방식은 Stateless 한 방식으로 서버는 사용자의 상태를 저장하지 않기 때문에 확장성이 높아집니다. 추후 분산 서버 환경을 구축할 때 JWT 방식이 세션 클러스터링을 하지 않고 서버를 빠르게 확장해나갈 수 있다고 판단했습니다.
- 또한 세션은 탈취 당하면 보안에 문제가 생길 수 있지만, JWT는 자체적으로 정보를 암호화할 수 있어 데이터의 무결성을 보장할 수 있습니다.
- 물론, JWT 방식도 완전히 안전한 것은 아닙니다. 클라이언트 측에서 토큰을 안전하게 보관해야 하며, 매 요청마다 토큰을 보내야 하므로 헤더의 크기가 커지는 단점도 있습니다.
- 그리고 토큰 만료시 새로운 토큰을 발급해줘야 하는 과정도 필요하게 됩니다.
- 결과적으로, 세션과 토큰 방식의 장단점을 비교했을 때, 서버의 확장성 측면과 프리티어인 EC2 인스턴스의 서버 부담을 덜어주기 위해 JWT 방식을 선택하게 됐습니다.

- 아래 이미지는 구현된 기능을 바탕으로 사용자가 OAuth2 로그인을 통해 Access Token을 전달받고 권한이 필요한 API를 호출하기 까지의 시나리오를 그린 시퀀스 다이어그램입니다.

![OAuth2JWT](https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/b463333c-7690-4f61-88ab-370999080a44)

- 권한이 필요한 API는 `/auth/**`경로를 거치게 되며 Access Token을 검증 과정이 진행됩니다. SecurityFilterChain 내부에 토큰을 검증하는 filter를 추가하여 검증 역할 수행을 맡겼습니다.
- 사용자는 토큰이 만료된 것을 알 필요가 없으므로 클라이언트와 서버에서 자동으로 재발급 과정을 거치고 재발급된 토큰을 가지고 API를 재요청하는 `Slient Refresh` 로직을 구현했습니다.
- 아래 이미지는 Access Token이 만료 시 Refresh Token을 가지고 재발급 및 API 재요청 하는 `Slient Refresh` 과정을 나타내는 시퀀스 다이어그램입니다.

![refreshToken](https://github.com/Seung-IL-Bang/MyCodeReview_backend/assets/87510898/c3d96268-1850-4bb8-8409-8e1dfd7ac6e1)


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

