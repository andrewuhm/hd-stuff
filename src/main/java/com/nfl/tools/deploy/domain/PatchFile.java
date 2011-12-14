package com.nfl.tools.deploy.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "PATCH_FILE")
public class PatchFile {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "SS")
	private Date creationDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "SS")
	private Date lastModifiedDate;
	
	private String userName;
	
	private String fileName;
	
	private String source;
	
	private String branchVersion;
	
	private String fileList;
	
	private String note;
	
	private String environment;
	
	private String testDeploy;
	private String stageDeploy;
	private String prodDeploy;
	private Date testDeployDate;
	private Date stageDeployDate;
	private Date prodDeployDate;
	
	@SuppressWarnings("unchecked")
	@Transient
	private List<String> files = LazyList.decorate(new ArrayList<String>(), FactoryUtils.instantiateFactory(String.class));

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFileList() {
		return fileList;
	}

	public void setFileList(String fileList) {
		this.fileList = fileList;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getBranchVersion() {
		return branchVersion;
	}

	public void setBranchVersion(String branchVersion) {
		this.branchVersion = branchVersion;
	}

	public List<String> getFiles() {
		if (StringUtils.hasText(fileList)) {
			String[] splitFiles = fileList.split(",");
			for (String str : splitFiles) {
				if (StringUtils.hasText(str)) {
					files.add(str);
				}
			}
		}
		
		return files;
	}
	
	public void setFiles(List<String> files) {
		this.files = files;
	}
	
	@Transient
	public String getFilesAsString() {
		StringBuilder sb = new StringBuilder();
		if (files.size() > 0) {
			for (String str : files) {
				if (StringUtils.hasText(str)) {
					sb.append(str).append(",");
				}
			}
		}
		return sb.toString();
	}

	public String getTestDeploy() {
		return testDeploy;
	}

	public void setTestDeploy(String testDeploy) {
		this.testDeploy = testDeploy;
	}

	public String getStageDeploy() {
		return stageDeploy;
	}

	public void setStageDeploy(String stageDeploy) {
		this.stageDeploy = stageDeploy;
	}

	public String getProdDeploy() {
		return prodDeploy;
	}

	public void setProdDeploy(String prodDeploy) {
		this.prodDeploy = prodDeploy;
	}

	public Date getTestDeployDate() {
		return testDeployDate;
	}

	public void setTestDeployDate(Date testDeployDate) {
		this.testDeployDate = testDeployDate;
	}

	public Date getStageDeployDate() {
		return stageDeployDate;
	}

	public void setStageDeployDate(Date stageDeployDate) {
		this.stageDeployDate = stageDeployDate;
	}

	public Date getProdDeployDate() {
		return prodDeployDate;
	}

	public void setProdDeployDate(Date prodDeployDate) {
		this.prodDeployDate = prodDeployDate;
	}

}
