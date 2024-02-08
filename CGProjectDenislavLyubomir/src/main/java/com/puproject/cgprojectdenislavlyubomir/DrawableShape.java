package com.puproject.cgprojectdenislavlyubomir;

import javafx.scene.paint.Color;

import javafx.scene.paint.Color;

public class DrawableShape {
    private String type;
    private double x;
    private double y;
    private double endX;
    private double endY;
    private Color color;
    private double opacity;
    private boolean selected;

    public DrawableShape(String type, double x, double y, Color color, double opacity) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.color = color;
        this.opacity = opacity;
    }

    public String getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(double deltaX, double deltaY) {
        System.out.println("Start");
        // Update the position of the shape
        this.x += deltaX;
        this.y += deltaY;

        this.endX += deltaX;
        this.endY += deltaY;
        System.out.println("End");
    }
}
