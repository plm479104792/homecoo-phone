// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DevWidetypeAdapter$ViewHolder$$ViewBinder<T extends com.tuwa.smarthome.adapter.DevWidetypeAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231869, "field 'devwideImg'");
    target.devwideImg = finder.castView(view, 2131231869, "field 'devwideImg'");
    view = finder.findRequiredView(source, 2131231870, "field 'devwideTitle'");
    target.devwideTitle = finder.castView(view, 2131231870, "field 'devwideTitle'");
  }

  @Override public void unbind(T target) {
    target.devwideImg = null;
    target.devwideTitle = null;
  }
}
