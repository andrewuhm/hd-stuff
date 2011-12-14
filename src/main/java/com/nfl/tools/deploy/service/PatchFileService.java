package com.nfl.tools.deploy.service;

import java.util.List;

import com.nfl.tools.deploy.domain.PatchFile;

public interface PatchFileService {

	public void addPatchFile(PatchFile patchFile, String user);
	public List<PatchFile> list();
	public void removePatchFile(Integer id, String user);
	public void deployPatchToEnvironment(Integer id, String env, String user) throws Exception;
}
