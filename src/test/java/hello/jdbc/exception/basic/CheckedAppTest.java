package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

public class CheckedAppTest
{
    @Test
    void checked()
    {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(controller::request)
                .isInstanceOf(Exception.class);
    }


    static class Controller
    {
        Service service = new Service();

        //Service로 부터 올라온 예외들이 너무 거슬린다.
        //어차피 Controller에서 해결 못하는 문제들인데.
        //그리고 Controller가 SQLException, ConnectException에 의존하게 된다.
        //SQLException은 JDBC인터페이스의 기술이다. 이후에 JPA인터페이스로 교체 시 모두 수정해야 한다.
        //기술을 바꾸면, 해당 예외도 모두 수정해야한다.
        public void request() throws SQLException, ConnectException
        {
            service.logic();
        }
    }

    //Repository, NetClient로 부터 올라온 예외들이 너무 거슬린다.
    //어차피 Service에서 해결 못하는 문제들인데.
    //그리고 Service가 SQLException, ConnectException에 의존하게 된다.
    static class Service
    {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException
        {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient
    {
        public void call() throws ConnectException
        {
            throw new ConnectException("연결 실패");
        }
    }

    static class Repository
    {
        public void call() throws SQLException
        {
            throw new SQLException("ex");
        }
    }
}
