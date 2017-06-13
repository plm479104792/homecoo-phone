// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class TimeTaskActivity$$ViewBinder<T extends com.tuwa.smarthome.activity.TimeTaskActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231878, "field 'tvBack' and method 'back'");
    target.tvBack = finder.castView(view, 2131231878, "field 'tvBack'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.back();
        }
      });
    view = finder.findRequiredView(source, 2131231074, "field 'gvTimetasks'");
    target.gvTimetasks = finder.castView(view, 2131231074, "field 'gvTimetasks'");
    view = finder.findRequiredView(source, 2131231880, "field 'tvtitle'");
    target.tvtitle = finder.castView(view, 2131231880, "field 'tvtitle'");
    view = finder.findRequiredView(source, 2131231881, "field 'tvSubmit'");
    target.tvSubmit = finder.castView(view, 2131231881, "field 'tvSubmit'");
  }

  @Override public void unbind(T target) {
    target.tvBack = null;
    target.gvTimetasks = null;
    target.tvtitle = null;
    target.tvSubmit = null;
  }
}
