package com.homecoolink.global;

import java.io.Serializable;

import com.homecoolink.widget.NormalDialog;

public class Dialogse implements Serializable{

	private static final long serialVersionUID = 9140307315052131386L;
	public NormalDialog dialog_nd;
	public NormalDialog getDialog_nd() {
		return dialog_nd;
	}
	public void setDialog_nd(NormalDialog dialog_nd) {
		this.dialog_nd = dialog_nd;
	}
	
	
	
}