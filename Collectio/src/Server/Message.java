package Server;

import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Ahmed Ali
 */
public class Message  implements Serializable  {
      private String msg, msgColor,  msgFontFamily;
    private int msgFontSize;
    
//    public Message(){
//        msgColor = Color.BROWN.toString();
//        msgFontFamily = "Courier New";
//        msgFontSize = 60;
//    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setMsgColor(String msgColor) {
        this.msgColor = msgColor;
    }

    public void setMsgFontFamily(String msgFontFamily) {
        this.msgFontFamily = msgFontFamily;
    }

    public void setMsgFontSize(int msgFontSize) {
        this.msgFontSize = msgFontSize;
    }

    public String getMsg() {
        return msg;
    }

    public String getMsgColor() {
        return msgColor;
    }

    public String getMsgFontFamily() {
        return msgFontFamily;
    }

    public int getMsgFontSize() {
        return msgFontSize;
    }
    
}
