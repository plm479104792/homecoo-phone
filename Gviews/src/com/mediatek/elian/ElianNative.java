package com.mediatek.elian;

public class ElianNative {
	public static boolean LoadLib()
	  {
	    try
	    {
	      System.loadLibrary("elianjni");
	      return true;
	    }
	    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
	    {
	      System.err.println("WARNING: Could not load elianjni library!");
	    }
	    return false;
	  }

	  public native int GetLibVersion();

	  public native int GetProtoVersion();

	  public native int InitSmartConnection(String paramString, int paramInt1, int paramInt2);

	  public native int StartSmartConnection(String paramString1, String paramString2, String paramString3, byte paramByte);

	  public native int StopSmartConnection();

}
