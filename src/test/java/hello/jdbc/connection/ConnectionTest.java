package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest
{
    //DriverManager 인터페이스의 커넥션
    @Test
    void driverManager() throws SQLException
    {
        //class org.h2.jdbc.JdbcConnection
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection1 = {}, class = {}", con1, con1.getClass());
        log.info("connection2 = {}, class = {}", con2, con2.getClass());
    }

    //spring - DataSource 인터페이스 ->  DriverManager 인터페이스로 커넥션
    @Test
    void dataSourceDriverManager() throws SQLException
    {
        //class org.h2.jdbc.JdbcConnection
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    //Spring - DataSource 인터페이스 -> ConnectionPool로 커넥션
    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException
    {
        //HikariProxyConnection@575715826
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        Thread.sleep(1000);
    }

    private void useDataSource(DataSource dataSource) throws SQLException
    {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        Connection con3 = dataSource.getConnection();
        Connection con4 = dataSource.getConnection();
        Connection con5 = dataSource.getConnection();
        Connection con6 = dataSource.getConnection();
        Connection con7 = dataSource.getConnection();
        Connection con8 = dataSource.getConnection();
        Connection con9 = dataSource.getConnection();
        Connection con10 = dataSource.getConnection();
//        Connection con11 = dataSource.getConnection();
        log.info("connection1 = {}, class = {}", con1, con1.getClass());
        log.info("connection2 = {}, class = {}", con2, con2.getClass());
    }
}
