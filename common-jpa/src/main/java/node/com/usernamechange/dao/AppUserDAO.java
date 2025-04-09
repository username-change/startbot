package node.com.usernamechange.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import node.com.usernamechange.entity.AppUser;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);
}
