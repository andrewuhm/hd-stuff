package com.nfl.tools.deploy.dao;

import java.util.List;

import com.nfl.tools.deploy.domain.PatchFile;

public interface PatchFileDao {
	
	public List<PatchFile> listPatchFiles();
	public PatchFile find(Integer id);
	public void addPatchFile(PatchFile patchFile);
	public void deletePatchFile(Integer id);

}
