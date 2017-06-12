// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class GatewayManegeActivity$$ViewBinder<T extends com.tuwa.smarthome.activity.GatewayManegeActivity> implements ViewBinder<T> {
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
    view = finder.findRequiredView(source, 2131230960, "field 'gvGateway'");
    target.gvGateway = finder.castView(view, 2131230960, "field 'gvGateway'");
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
    view = finder.findRequiredView(source, 2131231880, "field 'tvtitle'");
    target.tvtitle = finder.castView(view, 2131231880, "field 'tvtitle'");
    view = finder.findRequiredView(source, 2131231881, "field 'tvSubmit' and method 'systemExit'");
    target.tvSubmit = finder.castView(view, 2131231881, "field 'tvSubmit'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.systemExit();
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
    view = finder.findRequiredView(source, 2131231831, "method 'DefenceAreaClick'");
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.DefenceAreaClick();
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
    view = finder.findRequiredView(source, 2131230959, "method 'gatewayRefresh'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.gatewayRefresh();
        }
      });
  }

  @Override public void unbind(T target) {
    target.tvBack = null;
    target.gvGateway = null;
    target.tvbttomNetwork = null;
    target.tvtitle = null;
    target.tvSubmit = null;
  }
}
