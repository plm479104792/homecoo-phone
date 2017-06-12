// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SpaceManegeActivity$SpaceAdapter$ViewHolder$$ViewBinder<T extends com.tuwa.smarthome.activity.SpaceManegeActivity.SpaceAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231902, "field 'tvSpacename'");
    target.tvSpacename = finder.castView(view, 2131231902, "field 'tvSpacename'");
    view = finder.findRequiredView(source, 2131231847, "field 'imSetting'");
    target.imSetting = finder.castView(view, 2131231847, "field 'imSetting'");
  }

  @Override public void unbind(T target) {
    target.tvSpacename = null;
    target.imSetting = null;
  }
}
