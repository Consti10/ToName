package com.google.ar.sceneform.samples.gltf;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Renderable;

public class BayernARImageNode extends AnchorNode {

    private final Renderable renderable;
    private final Renderable renderable2;
    private final Renderable renderable3;
    private final AugmentedImage image;

    private final Node modelNode1=new Node();
    private final Node modelNode2=new Node();
    private final Node modelNode3=new Node();

    public BayernARImageNode(final AugmentedImage image,final Renderable renderable,final Renderable renderable2,final Renderable renderable3){
        this.image=image;
        this.renderable=renderable;
        this.renderable2=renderable2;
        this.renderable3=renderable3;
        // Set the anchor based on the center of the image.
        setAnchor(image.createAnchor(image.getCenterPose()));

        final float scale=0.01f;

        modelNode1.setParent(this);
        modelNode1.setRenderable(renderable);
        //modelNode1.setLocalScale(new Vector3(scale,scale,scale));

        modelNode2.setParent(this);
        modelNode2.setRenderable(renderable2);
        modelNode2.setLocalPosition(new Vector3(0,0,0));
        modelNode2.setLocalScale(new Vector3(scale,scale,scale));

        modelNode3.setParent(this);
        modelNode3.setRenderable(renderable3);
        modelNode3.setLocalPosition(new Vector3(0,0,0));
        modelNode3.setLocalScale(new Vector3(scale,scale,scale));
    }

    public void setMode(final int mode){
        if(mode==0){
            modelNode2.setEnabled(true);
            modelNode3.setEnabled(false);
        }else if(mode==1){
            modelNode2.setEnabled(false);
            modelNode3.setEnabled(true);
        }else if(mode==2){
            modelNode2.setEnabled(true);
            modelNode3.setEnabled(true);
        }else{
            System.out.println("Unknown mode");
        }
    }
}
