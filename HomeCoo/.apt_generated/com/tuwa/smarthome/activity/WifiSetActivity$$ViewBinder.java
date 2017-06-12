// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class WifiSetActivity$$ViewBinder<T extends com.tuwa.smarthome.activity.WifiSetActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231880, "field 'tvtitle'");
    target.tvtitle = finder.castView(view, 2131231880, "field 'tvtitle'");
    view = finder.findRequiredView(source, 2131231881, "field 'tvExit'");
    target.tvExit = finder.castView(view, 2131231881, "field 'tvExit'");
  }

  @Override public void unbind(T target) {
    target.tvtitle = null;
    target.tvExit = null;
  }
}
