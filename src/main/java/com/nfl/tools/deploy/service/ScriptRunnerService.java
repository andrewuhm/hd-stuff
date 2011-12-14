package com.nfl.tools.deploy.service;

public interface ScriptRunnerService {

	public void createPatchFile(String fileName, String source, String version,
			String files) throws Exception;

}