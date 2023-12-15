package hello.proxy.common.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcreteService { // 인터페이스가 없는 구체 클래스
    public void call() {
        log.info("ConcreteService 호출");
    }
}
