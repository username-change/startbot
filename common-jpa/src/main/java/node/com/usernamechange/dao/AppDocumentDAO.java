package node.com.usernamechange.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import node.com.usernamechange.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long>{

}
