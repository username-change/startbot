package node.com.usernamechange.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import node.com.usernamechange.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {

}
