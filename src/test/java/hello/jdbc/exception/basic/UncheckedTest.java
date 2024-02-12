package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest
{
    @Test
    void unchecked_catch()
    {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw()
    {
        Service service = new Service();
        Assertions.assertThatThrownBy(service::callThrow)
                .isInstanceOf(MyUncheckedException.class);
    }

    //런타임 예외는 언체크 예외이다.
    //언체크 예외는 잡거나 던지지 않아도 된다.
    //잡거나 던지지 않으면, 밖으로 던진다.
    static class MyUncheckedException extends RuntimeException
    {
        public MyUncheckedException(String message)
        {
            super(message);
        }

    }

    static class Service
    {
        Repository repository = new Repository();

        public void callCatch()
        {
            try
            {
                repository.call();
            }
            catch (MyUncheckedException e)
            {
                //예외 처리 로직
                log.info("예외 처리 message={}", e.getMessage(), e);
            }
        }

        public void callThrow()
        {
            repository.call();
        }
    }

    static class Repository
    {
        public void call()
        {
            throw new MyUncheckedException("ex");
        }
    }
}
