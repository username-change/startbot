package node.com.usernamechange.impl;


import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;
import node.com.usernamechange.dao.AppDocumentDAO;
import node.com.usernamechange.dao.AppPhotoDAO;
import node.com.usernamechange.entity.AppDocument;
import node.com.usernamechange.entity.AppPhoto;
import node.com.usernamechange.service.FileService;
import node.com.usernamechange.utils.CryptoTool;

@Log4j
@Service
public class FileServiceImpl implements FileService {
	private final AppDocumentDAO appDocumentDAO;
	private final AppPhotoDAO appPhotoDAO;
	private final CryptoTool cryptoTool;

	public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, CryptoTool cryptoTool) {
		this.appDocumentDAO = appDocumentDAO;
		this.appPhotoDAO = appPhotoDAO;
		this.cryptoTool = cryptoTool;
	}

	@Override
	public AppDocument getDocument(String hash) {
		var id = cryptoTool.idOf(hash);
		if (id == null) {
			return null;
		}
		return appDocumentDAO.findById(id).orElse(null);
	}

	@Override
	public AppPhoto getPhoto(String hash) {
		var id = cryptoTool.idOf(hash);
		if (id == null) {
			return null;
		}
		return appPhotoDAO.findById(id).orElse(null);
	}

}