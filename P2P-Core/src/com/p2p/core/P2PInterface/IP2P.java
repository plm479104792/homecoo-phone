package com.p2p.core.P2PInterface;

public interface IP2P {
	public void vCalling(boolean isOutCall,String threeNumber,int type);
	public void vReject(int reason_code);
	public void vAccept();
	public void vConnectReady();
	public void vAllarming(String srcId,int type,boolean isSupportExternAlarm,int iGroup,int iItem);
	public void vChangeVideoMask(int state);
	
	public void vRetPlayBackPos(int length,int currentPos);
	public void vRetPlayBackStatus(int state);
	
	public void vGXNotifyFlag(int flag);
	
	public void vRetPlaySize(int iWidth,int iHeight);
	public void vRetPlayNumber(int iNumber);
	
	
	public void vRecvAudioVideoData(byte[] AudioBuffer, int AudioLen, int AudioFrames, long AudioPTS,  byte[] VideoBuffer, int VideoLen, long VideoPTS);
}
