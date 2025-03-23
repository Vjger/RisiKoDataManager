package it.desimone.gsheetsaccess.gdrive.file;

import java.util.List;

public class ReportDriveData {

	private String parentFolderId;
	private String parentFolderName;
	private String idGoogleDrive;
	private String fileName;
	private List<String> emailContacts;
	public String getParentFolderId() {
		return parentFolderId;
	}
	public void setParentFolderId(String parentFolderId) {
		this.parentFolderId = parentFolderId;
	}
	public String getParentFolderName() {
		return parentFolderName;
	}
	public void setParentFolderName(String parentFolderName) {
		this.parentFolderName = parentFolderName;
	}
	public String getIdGoogleDrive() {
		return idGoogleDrive;
	}
	public void setIdGoogleDrive(String idGoogleDrive) {
		this.idGoogleDrive = idGoogleDrive;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<String> getEmailContacts() {
		return emailContacts;
	}
	public void setEmailContacts(List<String> emailContacts) {
		this.emailContacts = emailContacts;
	}
	@Override
	public String toString() {
		return "ReportDriveData [parentFolderId=" + parentFolderId + ", parentFolderName=" + parentFolderName
				+ ", idGoogleDrive=" + idGoogleDrive + ", fileName=" + fileName + "]";
	}

}
