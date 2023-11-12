package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;

@Slf4j
public class CheckedTest {

    @Test
    void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throw() {
        Service service = new Service();

        Assertions.assertThatThrownBy(() ->
            service.callThrow()).isInstanceOf(MyCheckedException.class);
    }


    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }


    /**
     * Checked 예외는 잡거나 던져야함
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 예외를 잡는 메서드
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        /**
         * 예외를 던지는 메서드
         * @throws MyCheckedException
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository {
        public void call() throws MyCheckedException { // 체크 예외는 던지거나 잡아야함
            throw new MyCheckedException("ex");
        }
    }
}
