package br.com.jhegner.labs.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    public CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        connection.createStatement().execute(
                "create table users (uuid varchar (200) primary key, email varchar(200))");
    }

    public static void main(String[] args) throws SQLException {

        var createUserService = new CreateUserService();
        try (var service = new KafkaService(
                CreateUserService.class.getName(),
                "ECOMMERCE_NEW_ORDER", createUserService::parse, Order.class, Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Order> record) throws SQLException {

        System.out.println("---------------------------------------");
        System.out.println("Processando nova ordem, verificando novo usuario");
        System.out.println(record.value());

        var order = record.value();

        if(isNovoUsuario(order.getEmail())) {
            insereNovoUsuario(order.getEmail());
        }
    }

    private void insereNovoUsuario(String email) throws SQLException {

        var sqlInsertStatement = connection.prepareStatement("insert into users (uuid, email) values (?, ?)");
        var uuid = UUID.randomUUID().toString();
        sqlInsertStatement.setString(1, uuid);
        sqlInsertStatement.setString(2, email);
        sqlInsertStatement.execute();

        System.out.println("O usuario foi adicionado: " + uuid + " - " + email);
    }

    private boolean isNovoUsuario(String email) throws SQLException {
        var sqlSelectStatement = connection.prepareStatement("select uuid from  where email = ? limit 1");
        sqlSelectStatement.setString(1, email);
        var result = sqlSelectStatement.executeQuery();
        return !result.next();
    }

}
