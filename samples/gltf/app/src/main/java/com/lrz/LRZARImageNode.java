package com.lrz;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.Renderable;

public class LRZARImageNode extends AnchorNode {
    private Renderable renderable;
    private final AugmentedImage image;
    private final Node modelNode1=new Node();

    public LRZARImageNode(final AugmentedImage image, final Renderable renderable){
        this.image=image;
        this.renderable=renderable;

        // Set the anchor based on the center of the image.
        setAnchor(image.createAnchor(image.getCenterPose()));

        modelNode1.setParent(this);
        modelNode1.setRenderable(renderable);
    }

    public void setRenderable(final Renderable renderable){
        this.renderable=renderable;
        modelNode1.setRenderable(renderable);
    }
}
