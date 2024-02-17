package com.IbraTeam.JavaBackend.Models.User;

import com.IbraTeam.JavaBackend.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest {
    private Role role;
}
