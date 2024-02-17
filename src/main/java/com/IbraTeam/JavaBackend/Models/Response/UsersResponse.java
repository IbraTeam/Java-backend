package com.IbraTeam.JavaBackend.Models.Response;

import com.IbraTeam.JavaBackend.Models.User.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersResponse {
    private List<UserDto> users;
    private PageResponse page;
}
