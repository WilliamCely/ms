package com.cely.auth_server.services;

import com.cely.auth_server.dtos.TokenDto;
import com.cely.auth_server.dtos.UserDto;

public interface AuthService {

    TokenDto login(UserDto user);
    TokenDto validateToken(TokenDto token);
}
