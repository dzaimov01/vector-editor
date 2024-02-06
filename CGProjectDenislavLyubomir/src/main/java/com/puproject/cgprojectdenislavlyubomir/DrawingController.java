package com.puproject.cgprojectdenislavlyubomir;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class DrawingController {
    @FXML
    private Canvas drawingCanvas;

    private double startX;
    private double startY;

    private double rotationAngle = 0.0;

    private List<Point2D> polygonPoints = new ArrayList<>();
    private GraphicsContext graphicsContext;
    private Timeline drawTimeline;

    @FXML
    private ComboBox<String> shapeComboBox;

    @FXML
    public void initialize() {
        // Initialize UI components and event handlers
        drawingCanvas.setOnMousePressed(this::handleMousePressed);
        drawingCanvas.setOnMouseDragged(this::handleMouseDragged);
        drawingCanvas.setOnMouseReleased(this::handleMouseReleased);

        // Get the graphics context for drawing on the canvas
        graphicsContext = drawingCanvas.getGraphicsContext2D();

        // Set up a timeline for periodic drawing (adjust the interval as needed)
        drawTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), event -> drawPartialVector())
        );
        drawTimeline.setCycleCount(Timeline.INDEFINITE);

        shapeComboBox.getItems().addAll("Line", "Square", "Triangle", "Circle", "Ellipse", "Polygon");
        shapeComboBox.getSelectionModel().selectFirst(); // Select the first shape by default
    }

    private void handleMousePressed(MouseEvent event) {
        this.startX = event.getX();
        this.startY = event.getY();
        String selectedShape = shapeComboBox.getValue();
        if(selectedShape.equals("Polygon")){
            polygonPoints.add(new Point2D(this.startX, this.startY));
        }

        // Start the timeline when the user starts drawing
        drawTimeline.playFromStart();
    }

    private void handleMouseDragged(MouseEvent event) {
        // Add logic for drawing on the canvas while dragging, if needed...
    }

    private void handleMouseReleased(MouseEvent event) {
        double endX = event.getX();
        double endY = event.getY();
        String selectedShape = shapeComboBox.getValue();
        if(selectedShape.equals("Polygon")){
            polygonPoints.add(new Point2D(startX, startY));
        }

        // Stop the timeline when the user releases the mouse
        drawTimeline.stop();
        drawSelectedShape(startX, startY, endX, endY);
    }

    private void drawSelectedShape(double startX, double startY, double endX, double endY) {
        String selectedShape = shapeComboBox.getValue();

        switch (selectedShape) {
            case "Line":
                drawLine(startX, startY, endX, endY);
                break;
            case "Square":
                drawSquare(startX, startY, endX, endY);
                break;
            case "Triangle":
                drawTriangle(startX, startY, endX, endY);
                break;
            case "Circle":
                drawCircle(startX, startY, endX, endY);
                break;
            case "Ellipse":
                drawEllipse(startX, startY, endX, endY);
                break;
            case "Polygon":
                drawPolygon();
                break;
        }
    }

    private void drawPartialVector() {
        // Draw the partial vector while the user is still drawing
        if (polygonPoints.size() >= 2) {
            graphicsContext.clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
            drawPolygon();
        }
    }

    private Point2D calculateVector(double startX, double startY, double endX, double endY) {
        double vectorX = endX - startX;
        double vectorY = endY - startY;

        return new Point2D(vectorX, vectorY);
    }

    private void drawLine(double startX, double startY, double endX, double endY) {
        graphicsContext.setStroke(javafx.scene.paint.Color.BLACK);
        graphicsContext.setLineWidth(2);
        graphicsContext.strokeLine(startX, startY, endX, endY);
    }

    private void drawSquare(double startX, double startY, double endX, double endY) {
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);
        double squareSize = Math.min(width, height);

        double squareX = (startX < endX) ? startX : startX - squareSize;
        double squareY = (startY < endY) ? startY : startY - squareSize;

        graphicsContext.setFill(javafx.scene.paint.Color.BLUE);
        graphicsContext.fillRect(squareX, squareY, squareSize, squareSize);
    }

    private void drawTriangle(double startX, double startY, double endX, double endY) {
        double midX = (startX + endX) / 2;
        double midY = (startY + endY) / 2;

        double triangleSize = Math.min(Math.abs(endX - startX), Math.abs(endY - startY)) / 2;

        double x1 = midX - triangleSize;
        double y1 = midY + triangleSize;

        double x2 = midX + triangleSize;
        double y2 = midY + triangleSize;

        graphicsContext.setFill(javafx.scene.paint.Color.GREEN);
        graphicsContext.fillPolygon(new double[]{midX, x1, x2}, new double[]{midY - triangleSize, y1, y2}, 3);
    }

    private void drawCircle(double startX, double startY, double endX, double endY) {
        double radius = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)) / 2;

        double centerX = (startX + endX) / 2;
        double centerY = (startY + endY) / 2;

        graphicsContext.setFill(javafx.scene.paint.Color.RED);
        graphicsContext.fillOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
    }

    private void drawEllipse(double startX, double startY, double endX, double endY) {
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        double centerX = (startX + endX) / 2;
        double centerY = (startY + endY) / 2;

        graphicsContext.setFill(javafx.scene.paint.Color.YELLOW);
        graphicsContext.fillOval(centerX - width / 2, centerY - height / 2, width, height);
    }

    private void drawPolygon() {
        if (polygonPoints.size() >= 3) {
            graphicsContext.setFill(javafx.scene.paint.Color.GREEN);
            graphicsContext.setStroke(javafx.scene.paint.Color.BLACK);
            graphicsContext.setLineWidth(2);

            double[] xPoints = new double[polygonPoints.size()];
            double[] yPoints = new double[polygonPoints.size()];

            for (int i = 0; i < polygonPoints.size(); i++) {
                Point2D point = polygonPoints.get(i);
                xPoints[i] = point.getX();
                yPoints[i] = point.getY();
            }

            graphicsContext.fillPolygon(xPoints, yPoints, polygonPoints.size());
            graphicsContext.strokePolygon(xPoints, yPoints, polygonPoints.size());
        }
    }

    private void rotateShape() {
        graphicsContext.translate(drawingCanvas.getWidth() / 2, drawingCanvas.getHeight() / 2);
        graphicsContext.rotate(rotationAngle);
        graphicsContext.translate(-drawingCanvas.getWidth() / 2, -drawingCanvas.getHeight() / 2);
    }
}