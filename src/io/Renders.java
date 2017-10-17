package io;

import java.awt.Color;

import universe.Point;

public interface Renders {
	void renderCircle(Point c, double radius, double minRadius, Color col, boolean fill);
	void renderCircle(ScreenPoint sc, double radius, double minRadius, Color col, boolean fill);
	void renderText(Point c, String text, Color col, boolean small);
	void renderText(ScreenPoint c, String text, Color col, boolean small);
	void renderRect(Point tl, Point br, Color col, boolean fill);
	void renderRect(ScreenPoint tl, ScreenPoint br, Color col, boolean fill);
	void renderRect(ScreenPoint tl, int width, int height, Color col, boolean fill);
	int getScreenHeight();
	int getScreenWidth();
	void renderTriangle(ScreenPoint one, ScreenPoint two, ScreenPoint three, Color col, boolean fill);
	void renderEllipse(Point center, double width, double height, Color col, boolean fill);
	void renderEllipse(ScreenPoint center, double width, double height, Color col, boolean fill);
	void renderArc(Point c, double radius, double minRadius, double extent, double start, Color col, boolean fill);
	void renderArc(ScreenPoint c, double radius, double minRadius, double extent, double start, Color col, boolean fill);
}
