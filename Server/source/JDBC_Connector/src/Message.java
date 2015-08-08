import java.util.LinkedList;


public class Message {
	private String from = null;
	private LinkedList<String> msgTextList = null;
	
	public Message(String from, String msgText, String dateTime){
		this.msgTextList = new LinkedList<String>();
		this.from = from;
		this.msgTextList.add("["+dateTime+"]: " +msgText);
	}
	
	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public void addMsgToList(String msgText, String dateTime){
		this.msgTextList.add("["+dateTime+"]: "+msgText);
	}

	public LinkedList<String> getMsgTextList() {
		return msgTextList;
	}
	
}
