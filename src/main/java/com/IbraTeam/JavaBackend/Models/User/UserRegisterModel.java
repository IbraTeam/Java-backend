package com.IbraTeam.JavaBackend.Models.User;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterModel {
    @Size(min = 1, message = "Длина имени должна быть не менее 1 символа")
    @NotNull
    private String name;

    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+", message = "Неверный адрес почты")
    private String email;

    @NotNull
    @Pattern(regexp = "^(?=.*\\d).{6,}$", message = "Пароль должен содержать не менее 6 символов и 1 цифры")
    private String password;
}
