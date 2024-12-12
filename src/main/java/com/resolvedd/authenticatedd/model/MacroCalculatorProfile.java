package com.resolvedd.authenticatedd.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class MacroCalculatorProfile extends UserApplicationProfile {

    private Integer calorieGoal;
    private Integer proteinGoal;
    private Integer carbGoal;
    private Integer fatGoal;

    public MacroCalculatorProfile(User user, Application application, Plan plan, Integer calorieGoal, Integer proteinGoal, Integer carbGoal, Integer fatGoal) {
        super(user, application, plan);
        this.calorieGoal = calorieGoal;
        this.proteinGoal = proteinGoal;
        this.carbGoal = carbGoal;
        this.fatGoal = fatGoal;
    }
}
