package com.google.ar.sceneform.samples.gltf;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Session;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.collision.Sphere;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CUtil {

    //Check if Node is inside view by checking its transformed centre point against the bounds of the viewport
    public static boolean isCentreInsideViewport(final Node node, final SceneView sceneView){
        final Camera camera=sceneView.getScene().getCamera();
        final Vector3 pos=node.getLocalPosition();
        final Vector3 posInScreenSpace=camera.worldToScreenPoint(pos);
        return isInRange(0,sceneView.getWidth(),posInScreenSpace.x)&&
                isInRange(0,sceneView.getHeight(),posInScreenSpace.y);
    }

    private static boolean isInRange(float low,float high,float val){
        return val>=low && val<=high;
    }

    public static boolean isPointInsideSphere(final Vector3 pos, final Sphere sphere){
        final Vector3 distanceBetween= Vector3.subtract(sphere.getCenter(),pos);
        return distanceBetween.length()<sphere.getRadius();
    }

    //Show the collision box of the renderable
    public static void addCollisionShapeVisualizer(final Context c, final Node parentNode){
        Node boundsNode = new Node();
        boundsNode.setParent(parentNode);
        MaterialFactory.makeTransparentWithColor(c,
                new  com.google.ar.sceneform.rendering.Color(0.8f, 0.8f, 0.8f, 0.5f))
                .thenAccept(
                        material -> {
                            Box box = (Box) parentNode.getRenderable().getCollisionShape();
                            Renderable renderable2 =
                                    ShapeFactory.makeCube(box.getSize(), box.getCenter(), material);
                            renderable2.setCollisionShape(null);
                            boundsNode.setRenderable(renderable2);
                        });
    }

    public static void addSimpleSphereAsRenderable(final Context c, final Node node, final float sphereRadius){
        MaterialFactory.makeOpaqueWithColor(c,new com.google.ar.sceneform.rendering.Color(1,0,0)).thenAccept((material)->{
            final Renderable renderable =
                    ShapeFactory.makeSphere(sphereRadius, new Vector3(0,0,0), material);
            node.setRenderable(renderable);
        });
    }



    //Place a sphere at 0,0,0 in the coordinate system, for debugging
    public static void placeSphereAtWorldOrigin(final Context c, final SceneView sceneView, final float sphereRadius){
        Node node=new Node();
        sceneView.getScene().addChild(node);
        MaterialFactory.makeTransparentWithColor(c,
                new  com.google.ar.sceneform.rendering.Color(0.0f, 0.0f, 0.0f, 1.0f))
                .thenAccept(
                        material -> {
                            final Renderable debug =
                                    ShapeFactory.makeSphere(sphereRadius, new Vector3(0,0,0), material);
                            debug.setCollisionShape(null);
                            node.setRenderable(debug);
                        });
    }

    //return the size of the renderable attached to the node (e.g. the bounding box)
    //also taking the node's local scale into account
    /*public static Vector3 getSizeOfNodeWithRenderable(final Node node){
        final Box collisionShape=(Box)node.getRenderable().getCollisionShape();
        final Vector3 sizeUnScaled=collisionShape.getSize();

        final float yOffset=collisionShape.getExtents().y*displayNode.getLocalScale().y;
        displayNode.setWorldPosition(new Vector3(0, -yOffset, 0));
    }*/

    public static MediaPlayer MediaPlayerFromAssets(final Context c, final String fileName){
        MediaPlayer p=null;
        try {
            AssetFileDescriptor afd = c.getAssets().openFd(fileName);
            p = new MediaPlayer();
            p.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            p.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    public static @NonNull
    AugmentedImageDatabase createAugmentedImageDatabase(final Context c, final Session session, final String databaseName){
        InputStream inputStream = null;
        try {
            inputStream = c.getAssets().open(databaseName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AugmentedImageDatabase imageDatabase = null;
        try {
            imageDatabase = AugmentedImageDatabase.deserialize(session,inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageDatabase;
    }


    //Avoid the 'concurrent lis' exception
    public static void removeAllChildren(Node node){
        ArrayList<Node> children = new ArrayList<>(node.getChildren());
        for(Node child : children){
            node.removeChild(child);
        }
    }

    /*public static void removeAllBoneAttachments(SkeletonNode skeletonNode){
        final ModelRenderable renderable=(ModelRenderable)skeletonNode.getRenderable();
        ArrayList<Node> attachedNodes=new ArrayList<>();
        for(int i=0;i<renderable.getBoneCount();i++){
            final Node node=skeletonNode.getBoneAttachment(renderable.getBoneName(i));
            if(node!=null)attachedNodes.add(node);
        }
        for(Node attachedNode : attachedNodes){
            skeletonNode.removeChild(attachedNode);
        }
    }*/

}
