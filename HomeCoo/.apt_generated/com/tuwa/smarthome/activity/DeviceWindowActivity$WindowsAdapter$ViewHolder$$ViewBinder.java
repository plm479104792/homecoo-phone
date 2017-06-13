// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceWindowActivity$WindowsAdapter$ViewHolder$$ViewBinder<T extends com.tuwa.smarthome.activity.DeviceWindowActivity.WindowsAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231856, "field 'tvDevSite'");
    target.tvDevSite = finder.castView(view, 2131231856, "field 'tvDevSite'");
    view = finder.findRequiredView(source, 2131231859, "field 'imBtnPause'");
    target.imBtnPause = finder.castView(view, 2131231859, "field 'imBtnPause'");
    view = finder.findRequiredView(source, 2131231860, "field 'imBtnOff'");
    target.imBtnOff = finder.castView(view, 2131231860, "field 'imBtnOff'");
    view = finder.findRequiredView(source, 2131231858, "field 'imBtnOn'");
    target.imBtnOn = finder.castView(view, 2131231858, "field 'imBtnOn'");
    view = finder.findRequiredView(source, 2131231857, "field 'tvDevName'");
    target.tvDevName = finder.castView(view, 2131231857, "field 'tvDevName'");
  }

  @Override public void unbind(T target) {
    target.tvDevSite = null;
    target.imBtnPause = null;
    target.imBtnOff = null;
    target.imBtnOn = null;
    target.tvDevName = null;
  }
}
