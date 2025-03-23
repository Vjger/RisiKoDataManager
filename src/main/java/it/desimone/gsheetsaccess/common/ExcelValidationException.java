package it.desimone.gsheetsaccess.common;

import java.util.List;

import it.desimone.risiko.torneo.batch.ExcelValidator.ExcelValidatorMessages;

public class ExcelValidationException extends Exception {


	private static final long serialVersionUID = -8195956945396703673L;

	private List<ExcelValidatorMessages> messages;
	
	public ExcelValidationException(List<ExcelValidatorMessages> messages){
		this.messages = messages;
	}

	public List<ExcelValidatorMessages> getMessages() {
		return messages;
	}

	public void setMessages(List<ExcelValidatorMessages> messages) {
		this.messages = messages;
	}
	
	
}
