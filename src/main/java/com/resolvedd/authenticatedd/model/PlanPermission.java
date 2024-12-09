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
public class PlanPermission {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Column(name = "permission_limit", nullable = false)
    private Integer limit;

    public PlanPermission(Plan plan, Permission permission, Integer limit) {
        this.plan = plan;
        this.permission = permission;
        this.limit = limit;
    }
}
