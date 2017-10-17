package io;

import surface.FeatureInteractor;

public interface HasUIButton{
	void UIClickAt(FeatureInteractor fi, ScreenPoint sp);
	void UIReleaseAt(FeatureInteractor fi, ScreenPoint sp);
	Renderable[] getButtonVisual();
}
