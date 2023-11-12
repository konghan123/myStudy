package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    @Test
    void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    /**
     * 예외가 뜨면 자동으로 밖을 던져짐
     */
    @Test
    void unchecked_throw() {
        Service service = new Service();
        service.callThrow();
    }

    /**
     * RuntimeException을 상속받은 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * UnCheked 예외는
     * 예외를 잡거나 던지지 않아도 된다.
     * 예외를 잡지 않으면 자동으로 밖으로 던져짐
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 예외를 잡는 메서드
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        /**
         * 예외를 던지는 메서드
         * @throws MyUncheckedException
         */
        public void callThrow() throws MyUncheckedException {
            Assertions.assertThatThrownBy(() -> repository.call())
                    .isInstanceOf(MyUncheckedException.class);

        }
    }



    static class Repository {
        public void call() {
            throw new MyUncheckedException("ex");
        }
    }
}
