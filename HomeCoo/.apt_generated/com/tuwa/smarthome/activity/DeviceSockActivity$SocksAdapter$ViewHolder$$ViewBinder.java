// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceSockActivity$SocksAdapter$ViewHolder$$ViewBinder<T extends com.tuwa.smarthome.activity.DeviceSockActivity.SocksAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231850, "field 'tgSock'");
    target.tgSock = finder.castView(view, 2131231850, "field 'tgSock'");
    view = finder.findRequiredView(source, 2131231848, "field 'tvDevSite'");
    target.tvDevSite = finder.castView(view, 2131231848, "field 'tvDevSite'");
    view = finder.findRequiredView(source, 2131231849, "field 'tvDevName'");
    target.tvDevName = finder.castView(view, 2131231849, "field 'tvDevName'");
  }

  @Override public void unbind(T target) {
    target.tgSock = null;
    target.tvDevSite = null;
    target.tvDevName = null;
  }
}
