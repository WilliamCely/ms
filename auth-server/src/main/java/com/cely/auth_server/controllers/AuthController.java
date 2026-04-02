package com.cely.auth_server.controllers;

import com.cely.auth_server.dtos.TokenDto;
import com.cely.auth_server.dtos.UserDto;
import com.cely.auth_server.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "login")
    public ResponseEntity<TokenDto>jwtCreate(@RequestBody UserDto user){
        return ResponseEntity.ok(this.authService.login(user));
    }

    @PostMapping(path = "jwt")
    public ResponseEntity<TokenDto>jwtValidator(@RequestHeader String accessToken){
        return
            ResponseEntity.ok(
                    this.authService.validateToken(TokenDto.builder().accessToken(accessToken).build()));
    }
}
