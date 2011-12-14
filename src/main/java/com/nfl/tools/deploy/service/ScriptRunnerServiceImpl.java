package com.nfl.tools.deploy.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

@Service
public class ScriptRunnerServiceImpl implements ScriptRunnerService {
	
	/* (non-Javadoc)
	 * @see com.nfl.tools.deploy.service.ScriptRunnerService#createPatchFile(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void createPatchFile(String fileName, String source, String version, String files) throws Exception {
		StringBuilder sb = new StringBuilder("/export/home/bamboo/bin/buildPatchFile.bash ");
		sb.append("-s ").append(source);
		if (source.equals("branch")) {
			sb.append(" -v ").append(version);
		}
		sb.append(" -n ").append(fileName).append(" -f ").append(files);
		ProcessBuilder pb = new ProcessBuilder("bash", "-c", sb.toString());
		pb.redirectErrorStream(true); // capture messages sent to stderr
		Process shell = pb.start();
		InputStream shellIn = shell.getInputStream(); // captures output from the command
		int shellExitStatus = shell.waitFor();
		
		try {
			shellIn.close();
		} catch (IOException ignore) {}
		
	}

}
