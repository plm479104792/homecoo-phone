package com.tuwa.smarthome.entity;



public class SocketPacket {
	
	private String header;
    private String stamp;
    private String gatewayId;
    private String devId; 
    private Short devType;                   //devType 为Short类型
    private Short dataType;
    private Short dataLen;
    private Object data;
    

    public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId;
	}

	public Short getDevType() {
		return devType;
	}

	public void setDevType(Short devType) {
		this.devType = devType;
	}

	public Short getDataType() {
		return dataType;
	}

	public void setDataType(Short dataType) {
		this.dataType = dataType;
	}

	public Short getDataLen() {
		return dataLen;
	}

	public void setDataLen(Short dataLen) {
		this.dataLen = dataLen;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
    
    
    //字节装转报文string
    public String getString(byte [] socketBytes){
        String headStr = this.bytesToString(socketBytes, 0, 4);
        String stampStr = this.bytesToString(socketBytes, 4, 4+4);
        String gwIdStr = this.bytesToString(socketBytes, 8, 8+8);
        String devIdStr = this.bytesToString(socketBytes, 16, 16+8);
        String devTypeStr = this.bytesToString(socketBytes, 24, 24+2);
        String dataTypeStr = this.bytesToString(socketBytes, 26, 26+2);
        String datalenStr = this.bytesToString(socketBytes, 28, 28+2);
        String dataStr = this.bytesToString(socketBytes, 30, socketBytes.length-30);
      
        return headStr+stampStr+gwIdStr+devIdStr+devTypeStr+dataTypeStr+datalenStr+dataStr;
    }
    
  ///将字节数组转化为string
    public String bytesToString(byte [] bytes,int start,int end){
        String str = "";
        if(bytes.length<end-start){
            return str;
        }
        byte [] bs = new byte[end-start];
        for(int i = 0;i<end-start;i++){
            bs[i] = bytes[start++];
        }
        str = new String(bs);
        return str;
    }
    

    
    
    /** 
     * ��byte[2]ת����short 
     * @param b 
     * @return 
     */  
    public static short byte2Short(byte[] b){  
        return (short) (((b[0] & 0xff) << 8) | (b[1] & 0xff));  
    }  
    
    public String toString(){
        return this.header+this.stamp+this.gatewayId+this.devId
        		+this.devType+this.dataType+this.dataLen+this.data;
    }
    
      
    
}
