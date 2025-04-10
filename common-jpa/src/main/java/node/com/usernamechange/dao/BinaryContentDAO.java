package node.com.usernamechange.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import node.com.usernamechange.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {

}
