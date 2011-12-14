package com.nfl.tools.deploy.web;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nfl.tools.deploy.domain.PatchFile;
import com.nfl.tools.deploy.service.PatchFileService;

@Controller
public class PatchFileController {
	private final Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private PatchFileService patchFileService;
	
	@Resource
	private SecurityContextHolderStrategy securityContextHolderStrategy;

	@RequestMapping("/index")
	public String listContacts(Map<String, Object> map) {
		if (logger.isDebugEnabled()) {
			logger.info("in here");
			logger.debug("name; " +securityContextHolderStrategy.getContext().getAuthentication().getName());
		}
		map.put("patchFileList", patchFileService.list());
		map.put("patchFile", new PatchFile());
		map.put("user", securityContextHolderStrategy.getContext().getAuthentication().getName());
		return "listFiles";
	}
	
	@RequestMapping("/delete/{id}")
	public String deleteContact(@PathVariable("id") Integer id) {
		logger.info("in here");
		patchFileService.removePatchFile(id, securityContextHolderStrategy.getContext().getAuthentication().getName());
		return "redirect:/index";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addPatchFile(
			@RequestParam("source") String source, 
			@RequestParam("createDate") String createDate,
			@ModelAttribute("patchFile") PatchFile patchFile, 
			BindingResult result) {
		
		DateTimeFormatter fmt = DateTimeFormat.forStyle("SS");
		fmt.parseDateTime(createDate);
		patchFile.setCreationDate(fmt.parseDateTime(createDate).toDate());
		patchFile.setFileList(patchFile.getFilesAsString());
		patchFileService.addPatchFile(patchFile, securityContextHolderStrategy.getContext().getAuthentication().getName());
		return "redirect:/index";
	}
	
	@RequestMapping("/promote/{id}/{env}")
	public String deployPatchFile(@PathVariable("id") Integer id,
			@PathVariable("env") String env) {
		logger.info("Deploying file to env: " + env);
		
		String username = securityContextHolderStrategy.getContext().getAuthentication().getName();
		if (StringUtils.isEmpty(username)) 
			throw new RuntimeException("No User associated with this deploy request");
		
		String exc = null;
		
		try {
			patchFileService.deployPatchToEnvironment(id, env, username);
		} catch (Exception e) {
			logger.error("Could not deploy to environment " + env, e);
			exc = "Could not deploy to environment: " + e.getMessage();
		}
		
		return "redirect:/index" + (StringUtils.isEmpty(exc) ? "" : "?deployError=" + exc);
	}
}
