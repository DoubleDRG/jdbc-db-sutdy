package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest
{
    @Test
    void checked_catch()
    {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throw()
    {
        Service service = new Service();
        Assertions.assertThatThrownBy(service::callThrow).isInstanceOf(MyCheckedException.class);
    }

    //Exception을 상속받은 예외는 컴파일러가 체크하는 체크예외
    static class MyCheckedException extends Exception
    {
        public MyCheckedException(String message)
        {
            super(message);
        }
    }

    //체크 예외는 예외를 잡아서 처리하거나, 던지거나 둘 중 하나를 필수로 선택해야 한다.
    //이때, 체크예외는 throw하거나, catch해야 한다.
    static class Service
    {
        Repository repository = new Repository();

        //예외를 잡아서 처리하는 코드
        public void callCatch()
        {
            try
            {
                repository.call();
            }
            catch (MyCheckedException e)
            {
                //예외 처리 로직
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        //예외를 밖으로 던지는 코드
        public void callThrow() throws MyCheckedException
        {
            repository.call();
        }
    }

    static class Repository
    {
        //체크 예외는 던지는 것을 반드시 선언해야 한다.
        //던지거나, 받는 것을 컴파일러가 체크해준다.
        public void call() throws MyCheckedException
        {
            throw new MyCheckedException("ex");
        }
    }
}
