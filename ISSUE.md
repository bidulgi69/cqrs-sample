## issue-1
> `embedded-debezium` 과 `spring-cloud-function` 라이브러리 동시 사용 시, `StreamBridge` 클래스가 spring bean 으로 정상적으로 등록되지 않음.<br>
아래 깃허브 링크에 동일한 에러에 대한 이슈가 등록돼 있음.<br>
>
> When using `embedded-debezium` library with `spring-cloud-function` library, `StreamBridge` class is not successfully registered as a spring bean.<br>
Same issue is registered in the GitHub link below.<br>
> 
> <a href="https://github.com/spring-cloud/spring-cloud-function/issues/920">https://github.com/spring-cloud/spring-cloud-function/issues/920 </a>

## issue-2
>`jasync-r2dbc-mysql:[2.0.8, 2.0.7]` 과 `spring-webflux 2.7.3` 버전과 호환이 되지 않는 문제가 있음.<br>
깃허브 이슈를 확인해보니, 각 라이브러리가 동일하게 사용하는 `r2dbc-spi` dependency 의 버전 불일치 문제였음.<br>
> 
> There is an incompatible issue with versions `jasync-r2dbc-mysql:[2.0.8, 2.0.7]` and `spring-webflux 2.7.3`.<br>
When I checked the GitHub issue below, it was a version mismatch problem with the `r2dbc-spi` dependency that each library uses the same.<br>
> 
><a href="https://github.com/jasync-sql/jasync-sql/issues/296">https://github.com/jasync-sql/jasync-sql/issues/296 </a>