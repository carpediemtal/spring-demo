package eternal.fire.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class User {
    private Long id;
    private String email;
    private String password;
    private String name;
    private Long createdAt;

    public User(String email, String password, String name, Long createdAt) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.createdAt = createdAt;
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getCreatedDateTime() {
        return Instant.ofEpochMilli(this.createdAt).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String getImageUrl() {
        return "http://ww1.sinaimg.cn/large/005VT09Qly1gh02xd46ckj30aa08w3yq.jpg";
    }
}
