// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class GatewayManegeActivity$GateWayAdapter$ViewHolder$$ViewBinder<T extends com.tuwa.smarthome.activity.GatewayManegeActivity.GateWayAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231847, "field 'imSetting'");
    target.imSetting = finder.castView(view, 2131231847, "field 'imSetting'");
    view = finder.findRequiredView(source, 2131231876, "field 'tgBtnSwitch'");
    target.tgBtnSwitch = finder.castView(view, 2131231876, "field 'tgBtnSwitch'");
    view = finder.findRequiredView(source, 2131231873, "field 'tvGatewayNO'");
    target.tvGatewayNO = finder.castView(view, 2131231873, "field 'tvGatewayNO'");
    view = finder.findRequiredView(source, 2131231874, "field 'tvGatewayIP'");
    target.tvGatewayIP = finder.castView(view, 2131231874, "field 'tvGatewayIP'");
  }

  @Override public void unbind(T target) {
    target.imSetting = null;
    target.tgBtnSwitch = null;
    target.tvGatewayNO = null;
    target.tvGatewayIP = null;
  }
}
