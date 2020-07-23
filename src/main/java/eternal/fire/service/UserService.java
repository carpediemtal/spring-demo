package eternal.fire.service;

import eternal.fire.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Statement;
import java.util.Objects;

@Component
@Transactional
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class);

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User getUserById(long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", new Object[]{id}, userRowMapper);
    }

    public User getUserByEmail(String email) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email = ?", new Object[]{email}, userRowMapper);
    }

    public User singIn(String email, String password) {
        log.info("try to sign in by {},{}", email, password);
        User user = getUserByEmail(email);
        if (user.getPassword().equals(password)) {
            return user;
        } else {
            throw new RuntimeException("Login failed");
        }
    }

    public User register(String email, String password, String name) {
        log.info("try to register by {}", email);
        User user = new User(email, password, name, System.currentTimeMillis());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement("INSERT INTO users(email,password,name,createdAt) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, user.getEmail());
                ps.setObject(2, user.getPassword());
                ps.setObject(3, user.getName());
                ps.setObject(4, user.getCreatedAt());
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            throw new RuntimeException("Register failed");
        }
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    public void updateUserName(User user) {
        jdbcTemplate.update("UPDATE users SET name = ? WHERE id = ?", user.getName(), user.getId());
    }
}
