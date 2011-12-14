package com.nfl.tools.deploy.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nfl.tools.deploy.domain.PatchFile;

@Repository
public class PatchFileDaoImpl implements PatchFileDao {
	
	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public List<PatchFile> listPatchFiles() {
		return sessionFactory.getCurrentSession().createQuery("from PatchFile order by creationDate desc").list();
	}

	public PatchFile find(Integer id) {
		return (PatchFile) sessionFactory.getCurrentSession().load(PatchFile.class, id);
	}

	public void addPatchFile(PatchFile patchFile) {
		if (patchFile.getCreationDate() == null) {
			if (patchFile.getId() == null)
				patchFile.setCreationDate(new Date());
		}
		
		patchFile.setLastModifiedDate(new Date());
		sessionFactory.getCurrentSession().save(patchFile);
	}

	public void deletePatchFile(Integer id) {
		PatchFile file = (PatchFile) sessionFactory.getCurrentSession().load(PatchFile.class, id);
		if (file != null)
			sessionFactory.getCurrentSession().delete(file);
	}

}
