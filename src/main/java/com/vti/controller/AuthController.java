package com.vti.controller;

import com.vti.dto.ProfileDto;
import com.vti.entity.Account;
import com.vti.form.AuthCreateForm;
import com.vti.form.AuthUpdateForm;
import com.vti.repository.IAccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private IAccountRepository repository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/login")
    public ProfileDto login(Principal principal) {
        String username = principal.getName();
        Account account = repository.findByUsername(username);
        return mapper.map(account, ProfileDto.class);
    }

    @PostMapping("/register")
    public void register(@RequestBody AuthCreateForm form) {
        Account account = mapper.map(form, Account.class);
        String encodedPassword = encoder.encode(form.getPassword());
        account.setPassword(encodedPassword);
        repository.save(account);
    }

    @PutMapping("/change")
    public void changePassword(@RequestBody AuthUpdateForm form) {
        Account account = repository.findByUsername(form.getUsername());
        String encodedPassword = encoder.encode(form.getPassword());
        account.setPassword(encodedPassword);
        repository.save(account);
    }
}
