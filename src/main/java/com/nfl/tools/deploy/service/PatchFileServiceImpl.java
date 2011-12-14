package com.nfl.tools.deploy.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nfl.tools.deploy.dao.PatchFileDao;
import com.nfl.tools.deploy.domain.PatchFile;

@Service
@Transactional
public class PatchFileServiceImpl implements PatchFileService {
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private PatchFileDao patchFileDao;
	
	@Autowired
	private ScriptRunnerService scriptRunnerService;
	
	@Autowired
	private MailSender mailSender;
	
	@Resource
	private Map<String, String> resources;

	public void addPatchFile(PatchFile patchFile, String user) {
		if (patchFile.getId() != null && patchFile.getId() > 0) {
			logger.warn("Modifying patch file: " + patchFile.getFileName() + " by " + user);
		} else {
			logger.warn("Patch File Created: " + patchFile.getFileName() + " by " + user);
		}
		try {
			scriptRunnerService.createPatchFile(patchFile.getFileName(), patchFile.getSource(), patchFile.getBranchVersion(), patchFile.getFileList());
		} catch (Exception e) {
			logger.error("something happened here", e);
		}
		patchFileDao.addPatchFile(patchFile);

	}

	public List<PatchFile> list() {
		return patchFileDao.listPatchFiles();
	}

	public void removePatchFile(Integer id, String user) {
		logger.warn("Removing patch file: " + id);
		patchFileDao.deletePatchFile(id);
	}

	/* (non-Javadoc)
	 * @see com.nfl.tools.deploy.service.PatchFileService#deployPatchToEnvironment(java.lang.Integer, java.lang.String)
	 */
	public void deployPatchToEnvironment(Integer id, String env, String user) throws Exception {
		PatchFile file = patchFileDao.find(id);
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom("hotdeploy@nfl.com");
		StringBuilder msgText = new StringBuilder("Deploying ");
		msgText.append(file.getFileName()).append(" to the ").append(env).append( " environment. Details about this patch: \n\n")
				.append(file.getNote()).append("\n\n").append("Patch File Contents: \n");
		for (String filecontent : file.getFiles()) {
			msgText.append("\t").append(filecontent).append("\n");
		}
		msgText.append("\nOrigin may take up to 5 minutes to reflect the change.");
		
		msg.setText(msgText.toString());
		File srcFile = new File("/NFL/deploy/patchfiles/source/" + file.getFileName() + ".tar");
		String dest = "/NFL/deploy/patchfiles/" + env;
		if (!srcFile.isFile()) {
			throw new FileNotFoundException("File does not exist");
		}
		
		String[] aliases = null;
		if (env.equals("prod")) {
			logger.warn("Deploying patch to PROD: " + file.getFileName() + ", initiated by " + user);
			msg.setSubject("[No Reply] Deploying to prod.  Kicked off by " + user);
			file.setProdDeploy(user);
			file.setProdDeployDate(new Date());
			
			aliases = resources.get("prod_email_alias").split(",");
			// aliases = "andrewuhm@gmail.com,uhmazing@gmail.com".split(",");
		} else if (env.equals("stage")) {
			logger.info("[No Reply] Deploying to stage: " + file.getFileName() + ", initiated by " + user);
			msg.setSubject("[No Reply] Deploying to stage.  Kicked off by " + user);
			file.setStageDeploy(user);
			file.setStageDeployDate(new Date());
			// aliases = "andrewuhm@gmail.com,uhmazing@gmail.com".split(",");
			aliases = resources.get("stage_email_alias").split(",");
		} else if (env.equals("test")) {
			logger.info("[No Reply] Deploying to test: " + file.getFileName() + ", initiated by " + user);
			msg.setSubject("Deploying to test.  Kicked off by " + user);
			file.setTestDeploy(user);
			file.setTestDeployDate(new Date());
			aliases = resources.get("test_email_alias").split(",");
		}
		msg.setTo(aliases);
		
		File destFile = new File(dest + File.separator + file.getFileName() + ".tar");
		FileUtils.copyFile(srcFile, destFile);
		
		file.setEnvironment(env);
		patchFileDao.addPatchFile(file);
		
		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			logger.error("Error sending notification email", ex);
		}
	}

}
