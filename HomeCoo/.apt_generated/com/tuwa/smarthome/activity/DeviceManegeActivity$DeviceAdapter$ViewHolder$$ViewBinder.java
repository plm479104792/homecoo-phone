// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceManegeActivity$DeviceAdapter$ViewHolder$$ViewBinder<T extends com.tuwa.smarthome.activity.DeviceManegeActivity.DeviceAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231844, "field 'tvDevSite'");
    target.tvDevSite = finder.castView(view, 2131231844, "field 'tvDevSite'");
    view = finder.findRequiredView(source, 2131231847, "field 'imSetting'");
    target.imSetting = finder.castView(view, 2131231847, "field 'imSetting'");
    view = finder.findRequiredView(source, 2131231845, "field 'tvDevName'");
    target.tvDevName = finder.castView(view, 2131231845, "field 'tvDevName'");
  }

  @Override public void unbind(T target) {
    target.tvDevSite = null;
    target.imSetting = null;
    target.tvDevName = null;
  }
}
