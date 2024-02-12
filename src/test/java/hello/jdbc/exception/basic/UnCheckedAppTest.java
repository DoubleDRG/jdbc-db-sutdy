package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

public class UnCheckedAppTest
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

        public void request() throws SQLException, ConnectException
        {
            service.logic();
        }
    }

    static class Service
    {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        //RunTimeException으로 한번 변환해서 던졌기 때문에 throw를 적지 않아도 된다.
        //코드도 깔끔해지고, 특정 기술에 의존하지 않게 된다.
        public void logic()
        {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient
    {
        public void call()
        {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    static class Repository
    {
        public void call()
        {
            try
            {
                runSQL();
            }
            catch (SQLException e)
            {
                throw new RuntimeSQLException(e);
            }
        }

        public void runSQL() throws SQLException
        {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException
    {
        public RuntimeConnectException(String message)
        {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException
    {
        public RuntimeSQLException(Throwable cause)
        {
            super(cause);
        }
    }
}
