package auth.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String phone;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns        = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public User() {}

    public User(String username, String password, String email, String phone) {
        this.username = username;
        this.password = password;
        this.email    = email;
        this.phone    = phone;
    }

    public Long    getId()        { return id; }
    public String  getUsername()  { return username; }
    public String  getPassword()  { return password; }
    public String  getEmail()     { return email; }
    public String  getPhone()     { return phone; }
    public Set<Role> getRoles()   { return roles; }
    public Instant getCreatedAt() { return createdAt; }
    public void setPassword(String p) { this.password = p; }
}