package com.cj.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.homecoolink.R;
import com.homecoolink.activity.AddContactNextActivity;
import com.homecoolink.activity.ModifyContactActivity;
import com.homecoolink.data.Contact;
import com.homecoolink.entity.LocalDevice;
import com.homecoolink.global.FList;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.widget.NormalDialog;
import com.homecoolink.widget.NormalDialog.OnButtonCancelListener;
import com.homecoolink.widget.NormalDialog.OnButtonOkListener;

public class PwdErrorUtil {

	Context context = MyApp.app;

	public PwdErrorUtil(Context c) {
		context = c;
	}

	public void PwdError(String cid, final DialogHandler handler) {
		LocalDevice localDevice = FList.getInstance().isContactUnSetPassword(
				cid);
		if (null != localDevice) {
			NormalDialog dialog = new NormalDialog(context, context
					.getResources().getString(R.string.confirm_pwdreset_title),
					context.getResources().getString(
							R.string.confirm_pwdreset_content), context
							.getResources().getString(R.string.confirm),
					context.getResources().getString(R.string.cancel));
			Contact saveContact = new Contact();
			saveContact.contactId = localDevice.contactId;
			saveContact.contactType = localDevice.type;
			saveContact.messageCount = 0;
			saveContact.activeUser = NpcCommon.mThreeNum;
			final Intent modify = new Intent();
			modify.setClass(context, AddContactNextActivity.class);
			modify.putExtra("isCreatePassword", true);
			modify.putExtra("contact", saveContact);
			String mark = localDevice.address.getHostAddress();
			Log.e("343", "mark==" + mark);
			modify.putExtra("ipFlag",
					mark.substring(mark.lastIndexOf(".") + 1, mark.length()));
			dialog.setOnButtonOkListener(new OnButtonOkListener() {
				@Override
				public void onClick() {
					context.startActivity(modify);
					handler.onClose();
					return;
				}
			});
			dialog.setOnButtonCancelListener(new OnButtonCancelListener() {

				@Override
				public void onClick() {
					handler.onClose();
					return;
				}
			});
			dialog.showNormalDialog();
			dialog.setCanceledOnTouchOutside(false);
		} else {
			NormalDialog dialog = new NormalDialog(context, context
					.getResources().getString(R.string.confirm_pwderror_title),
					context.getResources().getString(
							R.string.confirm_pwderror_content), context
							.getResources().getString(R.string.confirm),
					context.getResources().getString(R.string.cancel));

			dialog.showNormalDialog();
			dialog.setCanceledOnTouchOutside(false);

			final Intent modify = new Intent();
			modify.setClass(context, ModifyContactActivity.class);
			modify.putExtra("contact", FList.getInstance().isContact(cid));
			modify.putExtra("showpass", "1");
			dialog.setOnButtonOkListener(new OnButtonOkListener() {

				@Override
				public void onClick() {
					context.startActivity(modify);
					handler.onClose();
					return;
				}
			});
			dialog.setOnButtonCancelListener(new OnButtonCancelListener() {

				@Override
				public void onClick() {
					handler.onClose();
					return;
				}
			});
			
		}
	}

	public void PwdError(String cid) {
		PwdError(cid, new DialogHandler() {

			@Override
			public void onClose() {

			}
		});

	}

	public interface DialogHandler {
		void onClose();

	}
}
