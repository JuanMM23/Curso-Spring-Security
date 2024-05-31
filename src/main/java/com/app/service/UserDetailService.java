package com.app.service;

import com.app.controller.dto.AuthCreateUserRequest;
import com.app.controller.dto.AuthLoginRequest;
import com.app.controller.dto.AuthResponse;
import com.app.persistence.entity.RoleEntity;
import com.app.persistence.entity.UserEntity;
import com.app.persistence.repository.RoleRepository;
import com.app.persistence.repository.UserRepository;
import com.app.util.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        user.getRoles()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRol().name()))));

        user.getRoles().stream()
                .flatMap(rol -> rol.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNoExpired(), user.isCredentialNoExpired(), user.isAccountNoLocked(), authorityList);
    }

    public AuthResponse login(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);

        return new AuthResponse(username, "User loged successfuly", accessToken, true);
    }

    public AuthResponse create(AuthCreateUserRequest authCreateUserRequest) throws IllegalAccessException {
        String username = authCreateUserRequest.username();
        String password = authCreateUserRequest.password();
        List<String> roleRequest = authCreateUserRequest.authCreateRoleRequest().roleListName();
        Set<RoleEntity> roleEntitySet = new HashSet<>(roleRepository.findAllByRolIn(roleRequest));

        if (roleEntitySet.isEmpty()) {
            throw new IllegalAccessException("The roles specified does not exists");
        }

        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roleEntitySet)
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();
        UserEntity userCreated = userRepository.save(userEntity);

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userCreated.getRoles().forEach(roleEntity -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(roleEntity.getRol().name()))));
        userCreated.getRoles().stream()
                .flatMap(roleEntity -> roleEntity.getPermissionList().stream())
                .forEach(permissionEntity -> authorityList.add(new SimpleGrantedAuthority(permissionEntity.getName())));


        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(), userCreated.getPassword(), authorityList);

        String accesToken = jwtUtils.createToken(authentication);

        return new AuthResponse(userCreated.getUsername(), "User created successfully", accesToken, true);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails user = this.loadUserByUsername(username);

        if (user == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(username, user.getPassword(), user.getAuthorities());
    }
}
