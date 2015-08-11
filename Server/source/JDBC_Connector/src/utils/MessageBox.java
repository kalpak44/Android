package utils;
import java.util.LinkedList;
import org.json.simple.JSONObject;
import models.Message;


public class MessageBox {
	private LinkedList<Message> msgBox = null;
	public MessageBox(){
		this.msgBox = new LinkedList<Message>();
	}
	
	public void addMsg(String from, String msgText, String dateTime){
		if(msgBox.size() != 0){
			for(int i=0;i<msgBox.size();i++){
				if(msgBox.get(i).getFrom().equals(from)){
					msgBox.get(i).addMsgToList(msgText, dateTime);
					return;
				}
			}
			msgBox.add(new Message(from,msgText, dateTime));
		}else{
			msgBox.add(new Message(from,msgText,dateTime));
		}
	}
	
	public LinkedList<String> getMsgListByUser(String user){
		if (msgBox.size()==0){
			return null;
		}else{
			for(int i=0;i<msgBox.size();i++){
				if(msgBox.get(i).getFrom().equals(user)){
					return msgBox.get(i).getMsgTextList();
				}
			}
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJsonObject(){
		if (msgBox.size()==0){
			return null;
		}
		JSONObject result  = new JSONObject();
		for(int i=0; i<msgBox.size(); i++){
			String curientUser = msgBox.get(i).getFrom();
			result.put(curientUser, getMsgListByUser(curientUser));
		}
		return result;
	}

	
}
