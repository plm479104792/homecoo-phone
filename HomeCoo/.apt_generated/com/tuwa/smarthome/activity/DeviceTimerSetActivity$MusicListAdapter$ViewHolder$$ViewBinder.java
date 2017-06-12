// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceTimerSetActivity$MusicListAdapter$ViewHolder$$ViewBinder<T extends com.tuwa.smarthome.activity.DeviceTimerSetActivity.MusicListAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231787, "field 'tvmusicname'");
    target.tvmusicname = finder.castView(view, 2131231787, "field 'tvmusicname'");
  }

  @Override public void unbind(T target) {
    target.tvmusicname = null;
  }
}
