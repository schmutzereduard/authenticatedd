package com.resolvedd.authenticatedd.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Data
@NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "application_id"})
)
public class UserApplicationPlan {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    public UserApplicationPlan(User user, Application application, Plan plan) {
        this.user = user;
        this.application = application;
        this.plan = plan;
    }
}
