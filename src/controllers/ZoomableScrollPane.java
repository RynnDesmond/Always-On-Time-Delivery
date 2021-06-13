package controllers;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class ZoomableScrollPane extends ScrollPane {

    private double scaleValue = 5;
    private final Node content;
    private final Node zoomNode;

    /**
     * Constructor for class ZoomableScrollPane
     *
     * @param content :Nodes to put on this pane
     */
    public ZoomableScrollPane(Node content) {
        super();
        this.content = content;
        this.zoomNode = new Group(content);
        setContent(outerNode(zoomNode));
        setPannable(true);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setFitToHeight(true);
        setFitToWidth(true);
        updateScale();
    }

    private Node outerNode(Node node) {
        Node outerNode = centeredNode(node);
        outerNode.setOnScroll(e -> {
            e.consume();
            onScroll(e.getTextDeltaY(), new Point2D(e.getX(), e.getY()));
        });
        return outerNode;
    }

    private Node centeredNode(Node node) {
        VBox vBox = new VBox(node);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    private void updateScale() {
        content.setScaleX(scaleValue);
        content.setScaleY(scaleValue);
    }

    private void onScroll(double wheelDelta, Point2D mousePoint) {
        double zoomIntensity = 0.02;
        double zoomFactor = Math.exp(wheelDelta * zoomIntensity);
        Bounds innerBounds = zoomNode.getLayoutBounds();
        Bounds viewportBounds = getViewportBounds();

        //Calculate pixel offsets from [0, 1] range
        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        scaleValue = scaleValue * zoomFactor;
        updateScale();
        this.layout(); //Refresh ScrollPane scroll positions & target bounds

        //Convert target coordinates to zoomTarget coordinates
        Point2D posInZoomTarget = content.parentToLocal(zoomNode.parentToLocal(mousePoint));

        //Calculate adjustment of scroll position (pixels)
        Point2D adjustment = content.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        //Convert back to [0, 1] range
        //Too large/small values are automatically corrected by ScrollPane
        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        this.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
        this.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
    }

    /**
     * Mouse drag context used for scene and nodes.
     */
    static class DragContext {
        double mouseAnchorX;
        double mouseAnchorY;

        double translateAnchorX;
        double translateAnchorY;
    }

    /**
     * Listeners for making the scene's canvas draggable
     */
    static class SceneGestures {

        private final DragContext sceneDragContext = new DragContext();

        ZoomableScrollPane pane;

        //Constructor
        public SceneGestures(ZoomableScrollPane pane) {
            this.pane = pane;
        }

        public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
            return onMousePressedEventHandler;
        }

        public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
            return onMouseDraggedEventHandler;
        }

        private final EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<>() {

            public void handle(MouseEvent event) {

                // right mouse button => panning
                if (!event.isSecondaryButtonDown())
                    return;

                sceneDragContext.mouseAnchorX = event.getSceneX();
                sceneDragContext.mouseAnchorY = event.getSceneY();

                sceneDragContext.translateAnchorX = pane.getTranslateX();
                sceneDragContext.translateAnchorY = pane.getTranslateY();

            }

        };

        private final EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<>() {
            public void handle(MouseEvent event) {

                // right mouse button => panning
                if (!event.isSecondaryButtonDown())
                    return;

                pane.setTranslateX(sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX);
                pane.setTranslateY(sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY);

                event.consume();
            }
        };
    }
}