package com.app;

import com.app.persistence.entity.PermissionEntity;
import com.app.persistence.entity.RoleEntity;
import com.app.persistence.entity.RoleEnum;
import com.app.persistence.entity.UserEntity;
import com.app.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class SpringSecurityAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityAppApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository) {
		return args -> {
			/* Create PERMISSIONS */
			PermissionEntity createPermission = PermissionEntity.builder()
					.name("CREATE")
					.build();

			PermissionEntity readPermission = PermissionEntity.builder()
					.name("READ")
					.build();

			PermissionEntity updatePermission = PermissionEntity.builder()
					.name("UPDATE")
					.build();

			PermissionEntity deletePermission = PermissionEntity.builder()
					.name("DELETE")
					.build();

			/* Create ROLES */
			RoleEntity roleAdmin = RoleEntity.builder()
					.rol(RoleEnum.ADMIN)
					.permissionList(Set.of(createPermission, readPermission, updatePermission, deletePermission))
					.build();

			RoleEntity roleUser = RoleEntity.builder()
					.rol(RoleEnum.USER)
					.permissionList(Set.of(createPermission, readPermission))
					.build();

			RoleEntity roleInvited = RoleEntity.builder()
					.rol(RoleEnum.INVITED)
					.permissionList(Set.of(readPermission))
					.build();

			/* CREATE USERS */
			UserEntity userJuan = UserEntity.builder()
					.username("Juan")
					.password("$2a$10$maAuYuoeGNVPB8DA4Gq4hOMq4tD6r7S1szVp6UThp0uVN9oWVYiPG")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleAdmin))
					.build();

			UserEntity userDaniel = UserEntity.builder()
					.username("Daniel")
					.password("$2a$10$maAuYuoeGNVPB8DA4Gq4hOMq4tD6r7S1szVp6UThp0uVN9oWVYiPG")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleUser))
					.build();

			UserEntity userAndrea = UserEntity.builder()
					.username("Andrea")
					.password("$2a$10$maAuYuoeGNVPB8DA4Gq4hOMq4tD6r7S1szVp6UThp0uVN9oWVYiPG")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleInvited))
					.build();

			userRepository.saveAll(List.of(userJuan, userDaniel, userAndrea));
		};
	}
}
