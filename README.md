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
  - [단위 테스트 & 테스트 환경 최적화](#단위-테스트-작성--테스트-환경-최적화)
  - [AWS Pipeline & Nginx를 활용한 무중단 배포](#aws-pipeline--nginx를-활용한-무중단-배포)
  - [Swagger를 사용한 API 문서화](#swagger를-사용한-api-문서화)

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

## Swagger를 사용한 API 문서화
- 예전에 진행했던 팀프로젝트의 경우 초기에 API 문서를 수동으로 관리했었습니다. 수동으로 관리하게 되면 기능의 추가나 수정이 생겼을 경우 매번 API 문서도 직접 수정해야 했습니다. 반복적인 수정 작업은 비효율적이며 실수가 생길 가능성이 컸습니다.
- 이런 문제를 예방하기 위해 해당 프로젝트에서는 API 문서 자동화를 도입하기로 결정했습니다.
- API 자동 문서화를 지원하는 라이브러리로는 Spring Rest Docs와 Swagger 두 개의 후보가 있었고, 각각 장단점을 비교하여 결정하기로 했습니다.
- Spring Rest Docs는 테스트 코드와 함께 사용되어 정확한 문서를 생성하지만, 사용하는데 학습 난이도가 꽤 있는 편이고 테스트 코드를 구성하는 데 복잡할 수 있습니다. 반면에 Swagger는 진입장벽이 낮을뿐더러 UI가 사용자 친화적이며 빠르게 문서를 생성할 수 있고, API 테스트도 UI에서 바로 가능한 이유로 개인 프로젝트에서는 Spring Rest Docs보다 Swagger가 더욱 적합하다고 판단하여 Swagger를 도입하기로 했습니다.
- 간단한 의존성 추가와 설정 클래스만으로 Swagger를 도입할 수 있었고 API 문서화를 자동으로 잘 정리할 수 있었습니다. 결국 API 문서 자동화 덕분에 개발 생산성을 높일 수 있었습니다.

