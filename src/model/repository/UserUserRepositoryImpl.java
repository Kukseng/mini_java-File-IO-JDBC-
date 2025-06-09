package model.repository;

import model.entities.User;
import model.service.PasswordUtil;
import model.utils.DatabaseConfigure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class UserUserRepositoryImpl implements UserRepository<User, Integer> {
    @Override
    public User save(User user) {
        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        try(Connection con = DatabaseConfigure.getDatabaseConnection()){
            String sql = """
                INSERT INTO users (u_uuid, user_name, email, password, is_deleted)
                VALUES (?,?,?,?,?)
                """;
            PreparedStatement pre = con.prepareStatement(sql);
            pre.setString(1,user.getUuid());
            pre.setString(2, user.getUsername());
            pre.setString(3, user.getEmail());
            pre.setString(4, hashedPassword);
            pre.setBoolean(5, user.getIsDeleted());
            int rowAffected = pre.executeUpdate();
            if(rowAffected>0){
                System.out.println("User has been inserted successfully");
                return user;
            }

        }catch (Exception exception){
            System.err.println("Error during insert data to table user: " + exception.getMessage());
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public Integer delete(Integer id) {
        return 0;
    }
}
