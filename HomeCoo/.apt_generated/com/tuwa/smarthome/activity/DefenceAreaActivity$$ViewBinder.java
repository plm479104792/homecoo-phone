// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DefenceAreaActivity$$ViewBinder<T extends com.tuwa.smarthome.activity.DefenceAreaActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230912, "field 'tvOutdoorBufang' and method 'outdoorBufangOnClick'");
    target.tvOutdoorBufang = finder.castView(view, 2131230912, "field 'tvOutdoorBufang'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.outdoorBufangOnClick();
        }
      });
    view = finder.findRequiredView(source, 2131231830, "field 'tvbttomNetwork' and method 'networkSwitchClick'");
    target.tvbttomNetwork = finder.castView(view, 2131231830, "field 'tvbttomNetwork'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.networkSwitchClick();
        }
      });
    view = finder.findRequiredView(source, 2131231881, "field 'tvExit' and method 'systemExit'");
    target.tvExit = finder.castView(view, 2131231881, "field 'tvExit'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.systemExit();
        }
      });
    view = finder.findRequiredView(source, 2131230911, "field 'tvIndoorChefang' and method 'indoorChefangOnClick'");
    target.tvIndoorChefang = finder.castView(view, 2131230911, "field 'tvIndoorChefang'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.indoorChefangOnClick();
        }
      });
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
    view = finder.findRequiredView(source, 2131231827, "field 'rg_navi_tab'");
    target.rg_navi_tab = finder.castView(view, 2131231827, "field 'rg_navi_tab'");
    view = finder.findRequiredView(source, 2131230913, "field 'tvOutdoorChefang' and method 'outdoorChefangOnClick'");
    target.tvOutdoorChefang = finder.castView(view, 2131230913, "field 'tvOutdoorChefang'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.outdoorChefangOnClick();
        }
      });
    view = finder.findRequiredView(source, 2131231880, "field 'tvTitle'");
    target.tvTitle = finder.castView(view, 2131231880, "field 'tvTitle'");
    view = finder.findRequiredView(source, 2131230910, "field 'tvIndoorBufang' and method 'indoorBufangOnClick'");
    target.tvIndoorBufang = finder.castView(view, 2131230910, "field 'tvIndoorBufang'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.indoorBufangOnClick();
        }
      });
    view = finder.findRequiredView(source, 2131231832, "method 'systemSet'");
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.systemSet();
        }
      });
    view = finder.findRequiredView(source, 2131231829, "method 'sceneMode'");
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.sceneMode();
        }
      });
    view = finder.findRequiredView(source, 2131231828, "method 'spaceDeviceShow'");
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.spaceDeviceShow();
        }
      });
  }

  @Override public void unbind(T target) {
    target.tvOutdoorBufang = null;
    target.tvbttomNetwork = null;
    target.tvExit = null;
    target.tvIndoorChefang = null;
    target.tvBack = null;
    target.rg_navi_tab = null;
    target.tvOutdoorChefang = null;
    target.tvTitle = null;
    target.tvIndoorBufang = null;
  }
}
