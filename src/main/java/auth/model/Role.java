package auth.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;   // e.g. "USER", "ADMIN"

    public Role() {}
    public Role(String name) { this.name = name; }

    public Long   getId()   { return id; }
    public String getName() { return name; }
}