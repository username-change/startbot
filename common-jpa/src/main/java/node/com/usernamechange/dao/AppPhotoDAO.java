package node.com.usernamechange.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import node.com.usernamechange.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {

}
