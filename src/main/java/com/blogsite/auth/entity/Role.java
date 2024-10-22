package com.blogsite.auth.entity;



import com.blogsite.auth.enums.RoleEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "userroles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   // @JsonIgnore
    private Long id;
   // @JsonIgnore
    @Enumerated(EnumType.STRING)
    private RoleEnum name;

    public Role() {

    }

    public Role(RoleEnum name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleEnum getName() {
        return name;
    }

    public void setName(RoleEnum name) {
        this.name = name;
    }
}
