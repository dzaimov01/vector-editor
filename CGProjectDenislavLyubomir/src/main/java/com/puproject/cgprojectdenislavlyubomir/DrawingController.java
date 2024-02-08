package com.puproject.cgprojectdenislavlyubomir;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Slider;
import javafx.scene.shape.*;
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
    private List<DrawableShape> shapes = new ArrayList<>();
    private List<DrawableShape> selectedShapes = new ArrayList<>();
    private GraphicsContext graphicsContext;
    private Timeline drawTimeline;

    private DrawableShape selectedShapeForDrag = null;

    private double lastMouseX;
    private double lastMouseY;

    @FXML
    private ComboBox<String> shapeComboBox;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Slider opacitySlider;

    @FXML
    public void initialize() {
        // Initialize UI components and event handlers
        drawingCanvas.setOnMousePressed(this::handleMousePressedForDraw);
        drawingCanvas.setOnMouseDragged(this::handleMouseDraggedForDraw);
        drawingCanvas.setOnMouseReleased(this::handleMouseReleasedForDraw);

        // Get the graphics context for drawing on the canvas
        graphicsContext = drawingCanvas.getGraphicsContext2D();

        // Set up a timeline for periodic drawing (adjust the interval as needed)
        drawTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), event -> drawPartialVector())
        );
        drawTimeline.setCycleCount(Timeline.INDEFINITE);

        colorPicker.setValue(javafx.scene.paint.Color.BLACK);

        shapeComboBox.getItems().addAll("Line", "Square", "Triangle", "Circle", "Ellipse", "Polygon");
        shapeComboBox.getSelectionModel().selectFirst();

        double initialOpacity = opacitySlider.getValue();
        for (DrawableShape shape : shapes) {
            shape.setOpacity(initialOpacity);
        }
    }

    @FXML
    private void switchToDrawMode(ActionEvent event) {
        // Set up the canvas to handle drawing events
        drawingCanvas.setOnMousePressed(this::handleMousePressedForDraw);
        drawingCanvas.setOnMouseDragged(this::handleMouseDraggedForDraw);
        drawingCanvas.setOnMouseReleased(this::handleMouseReleasedForDraw);
    }

    @FXML
    private void switchToDragMode(ActionEvent event) {
        // Set up the canvas to handle dragging events
        drawingCanvas.setOnMousePressed(this::handleMousePressedForDrag);
        drawingCanvas.setOnMouseDragged(this::handleMouseDraggedForDrag);
        drawingCanvas.setOnMouseReleased(this::handleMouseReleasedForDrag);
    }

    private void handleMouseReleasedForDrag(MouseEvent mouseEvent) {
    }

    private void handleMouseDraggedForDrag(MouseEvent mouseEvent) {
        if (!selectedShapes.isEmpty()) {
            System.out.println("Test 1: ");
            double deltaX = mouseEvent.getX() - lastMouseX;
            double deltaY = mouseEvent.getY() - lastMouseY;
            System.out.println(deltaX);
            System.out.println(deltaY);
            // Move all selected shapes by the same delta
            for (DrawableShape shape : selectedShapes) {
                System.out.println(shape.getType());
                shape.move(deltaX, deltaY);
            }

            // Redraw the canvas
            redrawCanvas();
        }

        lastMouseX = mouseEvent.getX();
        lastMouseY = mouseEvent.getY();
        System.out.println(lastMouseX);
        System.out.println(lastMouseY);
    }

    private void handleMousePressedForDrag(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        // Check if mouse click is inside any shape
        for (DrawableShape shape : shapes) {
            System.out.println("pressed " + mouseX + mouseY);
            if (isPointInsideShape(mouseX, mouseY, shape)) {
                System.out.println("inside isPoint in shape");
                // Toggle selection of the shape
                if (selectedShapes.contains(shape)) {
                    selectedShapes.remove(shape);
                } else {
                    selectedShapes.add(shape);
                }
            }
        }
    }

    private void handleMousePressedForDraw(MouseEvent event) {
        this.startX = event.getX();
        this.startY = event.getY();
        String selectedShape = shapeComboBox.getValue();
        if(selectedShape.equals("Polygon")){
            polygonPoints.add(new Point2D(this.startX, this.startY));
        }
        DrawableShape shape = new DrawableShape(shapeComboBox.getValue(), startX, startY, colorPicker.getValue(), opacitySlider.getValue());
        shapes.add(shape);

        // Start the timeline when the user starts drawing
        drawTimeline.playFromStart();
        System.out.println(opacitySlider.getValue());
    }

    private void handleMouseDraggedForDraw(MouseEvent event) {
        double endX = event.getX();
        double endY = event.getY();

        // Update the position of the shape being dragged
        for (DrawableShape shape : shapes) {
            if (shape.isSelected()) { // Assuming you have a method to check if the shape is selected
                shape.setPosition(endX, endY);
                break;
            }
        }
    }

    private void handleMouseReleasedForDraw(MouseEvent event) {
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

        graphicsContext.setFill(colorPicker.getValue());
        graphicsContext.setStroke(colorPicker.getValue());

        validate_shape(selectedShape, startX, startY, endX, endY);
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
        graphicsContext.setGlobalAlpha(opacitySlider.getValue());
        graphicsContext.setLineWidth(2);
        graphicsContext.strokeLine(startX, startY, endX, endY);

    }

    private void drawSquare(double startX, double startY, double endX, double endY) {
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);
        double squareSize = Math.min(width, height);

        double squareX = (startX < endX) ? startX : startX - squareSize;
        double squareY = (startY < endY) ? startY : startY - squareSize;

        graphicsContext.setGlobalAlpha(opacitySlider.getValue());
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

        graphicsContext.setGlobalAlpha(opacitySlider.getValue());
        graphicsContext.fillPolygon(new double[]{midX, x1, x2}, new double[]{midY - triangleSize, y1, y2}, 3);
    }

    private void drawCircle(double startX, double startY, double endX, double endY) {
        double radius = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)) / 2;

        double centerX = (startX + endX) / 2;
        double centerY = (startY + endY) / 2;

        graphicsContext.setGlobalAlpha(opacitySlider.getValue());
        graphicsContext.fillOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
    }

    private void drawEllipse(double startX, double startY, double endX, double endY) {
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        double centerX = (startX + endX) / 2;
        double centerY = (startY + endY) / 2;

        graphicsContext.setGlobalAlpha(opacitySlider.getValue());
        graphicsContext.fillOval(centerX - width / 2, centerY - height / 2, width, height);
    }

    private void drawPolygon() {
        if (polygonPoints.size() >= 3) {
            graphicsContext.setLineWidth(2);

            double[] xPoints = new double[polygonPoints.size()];
            double[] yPoints = new double[polygonPoints.size()];

            for (int i = 0; i < polygonPoints.size(); i++) {
                Point2D point = polygonPoints.get(i);
                xPoints[i] = point.getX();
                yPoints[i] = point.getY();
            }

            graphicsContext.setGlobalAlpha(opacitySlider.getValue());
            graphicsContext.fillPolygon(xPoints, yPoints, polygonPoints.size());
            graphicsContext.strokePolygon(xPoints, yPoints, polygonPoints.size());
        }
    }

    private void validate_shape(String selectedShape, double startX, double startY, double endX, double endY) {
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

    private void redrawCanvas() {
        // Clear the canvas
        graphicsContext.clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());

        // Redraw all shapes
        for (DrawableShape shape : shapes) {
            // Set color and opacity
            graphicsContext.setGlobalAlpha(shape.getOpacity());
            graphicsContext.setFill(shape.getColor());
            graphicsContext.setStroke(shape.getColor());
            validate_shape(shape.getType(), shape.getX(), shape.getY(), shape.getEndX(), shape.getEndY());
        }
    }



    private void rotateShape() {
        graphicsContext.translate(drawingCanvas.getWidth() / 2, drawingCanvas.getHeight() / 2);
        graphicsContext.rotate(rotationAngle);
        graphicsContext.translate(-drawingCanvas.getWidth() / 2, -drawingCanvas.getHeight() / 2);
    }

    private boolean isPointInsideShape(double x, double y, DrawableShape shape) {
        switch (shape.getType()) {
            case "Line":
                // For a line, check if the point is near the line segment within a threshold
                return isPointNearLine(x, y, shape.getX(), shape.getY(), shape.getEndX(), shape.getEndY());
            case "Square":
                // For a square, check if the point is within the bounds of the square
                return x >= shape.getX() && x <= shape.getEndX() && y >= shape.getY() && y <= shape.getEndY();
            case "Triangle":
                // For a triangle, check if the point is within the bounds of the triangle
                return isPointInsideTriangle(x, y, shape.getX(), shape.getY(), shape.getEndX(), shape.getEndY());
            case "Circle":
                // For a circle, check if the distance from the center to the point is less than the radius
                double radius = Math.sqrt(Math.pow(shape.getEndX() - shape.getX(), 2) + Math.pow(shape.getEndY() - shape.getY(), 2)) / 2;
                double centerX = (shape.getX() + shape.getEndX()) / 2;
                double centerY = (shape.getY() + shape.getEndY()) / 2;
                return Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2)) <= radius;
            case "Ellipse":
                // For an ellipse, check if the point is within the bounds of the ellipse
                return isPointInsideEllipse(x, y, shape.getX(), shape.getY(), shape.getEndX(), shape.getEndY());
            case "Polygon":
                // For a polygon, check if the point is inside the polygon
                return isPointInsidePolygon(x, y, shape);
            default:
                return false; // Invalid shape type
        }
    }

    private boolean isPointNearLine(double x, double y, double x1, double y1, double x2, double y2) {
        // Calculate distance from the point to the line using the formula for the distance from a point to a line
        double distance = Math.abs((x2 - x1) * (y1 - y) - (x1 - x) * (y2 - y1)) /
                Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        // Define a threshold for proximity
        double threshold = 5.0; // Adjust as needed
        // Return true if the distance is within the threshold
        return distance <= threshold;
    }

    private boolean isPointInsideTriangle(double x, double y, double x1, double y1, double x2, double y2) {
        // Calculate area of triangle ABC
        double A = area(x1, y1, x2, y2, x, y);
        // Calculate area of triangle ABP
        double A1 = area(x1, y1, x2, y2, x, y);
        // Calculate area of triangle ACP
        double A2 = area(x1, y1, x, y, x2, y2);
        // Calculate area of triangle BCP
        double A3 = area(x, y, x2, y2, x1, y1);
        // Check if sum of A1, A2, A3 is equal to A
        return A == A1 + A2 + A3;
    }

    private double area(double x1, double y1, double x2, double y2, double x3, double y3) {
        return Math.abs((x1*(y2-y3) + x2*(y3-y1) + x3*(y1-y2))/2.0);
    }

    private boolean isPointInsideEllipse(double x, double y, double centerX, double centerY, double radiusX, double radiusY) {
        // Check if the point is inside the ellipse equation (x - centerX)^2 / radiusX^2 + (y - centerY)^2 / radiusY^2 <= 1
        return Math.pow((x - centerX) / radiusX, 2) + Math.pow((y - centerY) / radiusY, 2) <= 1;
    }

    private boolean isPointInsidePolygon(double x, double y, DrawableShape shape) {
        int intersectCount = 0;

        for (int i = 0; i < polygonPoints.size() - 1; i++) {
            if ((polygonPoints.get(i).getY() > y) != (polygonPoints.get(i + 1).getY() > y) &&
                    x < (polygonPoints.get(i + 1).getX() - polygonPoints.get(i).getX()) * (y - polygonPoints.get(i).getY()) /
                            (polygonPoints.get(i + 1).getY() - polygonPoints.get(i).getY()) + polygonPoints.get(i).getX()) {
                intersectCount++;
            }
        }

        return intersectCount % 2 == 1;
    }


    public void clearCanvas(ActionEvent actionEvent) {
        graphicsContext.clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
    }

    public Shape convertToJavaFXShape(DrawableShape drawingShape) {
        String type = drawingShape.getType();
        double startX = drawingShape.getX();
        double startY = drawingShape.getY();
        double endX = drawingShape.getEndX();
        double endY = drawingShape.getEndY();
        javafx.scene.paint.Color color = drawingShape.getColor();

        switch (type) {
            case "Line":
                return new Line(startX, startY, endX, endY);
            case "Square":
                double width = Math.abs(endX - startX);
                double height = Math.abs(endY - startY);
                double squareSize = Math.min(width, height);
                return new Rectangle(startX, startY, squareSize, squareSize);
            case "Triangle":
                 //Implement triangle creation logic
                 Polygon triangle = new Polygon();
                 triangle.getPoints().addAll(startX, startY, endX, startY, (startX + endX) / 2, endY);
                 return triangle;
            case "Circle":
                double radius = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)) / 2;
                return new Circle(startX + radius, startY + radius, radius);
            case "Ellipse":
                return new Ellipse((startX + endX) / 2, (startY + endY) / 2, Math.abs(endX - startX) / 2, Math.abs(endY - startY) / 2);
            case "Polygon":
//                 Polygon polygon = new Polygon();
//                 for (Point2D point : polygonPoints)) {
//                     polygon.getPoints().addAll(point, point.getY());
//                 }
//                 return polygon;
                break;
        }
        return null;
    }
}