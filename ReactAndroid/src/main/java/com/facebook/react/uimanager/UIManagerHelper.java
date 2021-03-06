/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.react.uimanager;

import static com.facebook.react.uimanager.common.UIManagerType.FABRIC;
import static com.facebook.react.uimanager.common.ViewUtil.getUIManagerType;
import com.facebook.react.bridge.CatalystInstance;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.JSIModuleType;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactSoftException;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.uimanager.common.UIManagerType;
import com.facebook.react.uimanager.events.EventDispatcher;

/** Helper class for {@link UIManager}. */
public class UIManagerHelper {

  /** @return a {@link UIManager} that can handle the react tag received by parameter. */
  @Nullable
  public static UIManager getUIManagerForReactTag(ReactContext context, int reactTag) {
    return getUIManager(context, getUIManagerType(reactTag));
  }

  /** @return a {@link UIManager} that can handle the react tag received by parameter. */
  @Nullable
  public static UIManager getUIManager(ReactContext context, @UIManagerType int uiManagerType) {
    if (context.isBridgeless()) {
      return (UIManager) context.getJSIModule(JSIModuleType.UIManager);
    } else {
      if (!context.hasActiveCatalystInstance()) {
        ReactSoftException.logSoftException(
            "UIManagerHelper",
            new RuntimeException("Cannot get UIManager: no active Catalyst instance"));
        return null;
      }
      CatalystInstance catalystInstance = context.getCatalystInstance();
      return uiManagerType == FABRIC
          ? (UIManager) catalystInstance.getJSIModule(JSIModuleType.UIManager)
          : catalystInstance.getNativeModule(UIManagerModule.class);
    }
  }

  /**
   * @return the {@link EventDispatcher} that handles events for the reactTag received as a
   *     parameter.
   */
  @Nullable
  public static EventDispatcher getEventDispatcherForReactTag(ReactContext context, int reactTag) {
    return getEventDispatcher(context, getUIManagerType(reactTag));
  }

  /**
   * @return the {@link EventDispatcher} that handles events for the {@link UIManagerType} received
   *     as a parameter.
   */
  @Nullable
  public static EventDispatcher getEventDispatcher(
      ReactContext context, @UIManagerType int uiManagerType) {
    UIManager uiManager = getUIManager(context, uiManagerType);
    return uiManager == null ? null : (EventDispatcher) uiManager.getEventDispatcher();
  }
}
