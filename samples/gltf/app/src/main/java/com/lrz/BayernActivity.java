package com.lrz;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.samples.gltf.R;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class BayernActivity extends AppCompatActivity {
    private static final String TAG = BayernActivity.class.getSimpleName();

    private MarkerBasedARFragment arFragment;
    private SceneView sceneView;
    private static final int N_RENDERABLES=3;
    private final ModelRenderable[] renderables=new ModelRenderable[N_RENDERABLES];

    //private final Map<AugmentedImage, BayernARImageNode> augmentedImageMap = new HashMap<>();
    private BayernARImageNode augmentedImageNode=null;
    private int mode=0;


    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sceneView=findViewById(R.id.sceneform_ar_scene_view);

        setContentView(R.layout.activity_ux);
        arFragment = (MarkerBasedARFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        loadModel(0,R.raw.bayern_low);
        loadModel(1,R.raw.autobahn_textures_unwrapped);
        loadModel(2,R.raw.kraftwerke_textures_transforms_reset);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);
        arFragment.getArSceneView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (BayernActivity.this){
                    mode++;
                    mode=mode % 3;
                }
            }
        });
    }

    private void onUpdate(FrameTime frameTime) {
        final Frame frame = arFragment.getArSceneView().getArFrame();
        if(frame==null)return;
        final Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);
        System.out.println("N images"+augmentedImages.size());
        if(!CUtil.done(renderables)){
            return;
        }
        //Add and remove 3D renderables accordingly
        for(final AugmentedImage image:augmentedImages) {
            System.out.println("Tracking state of "+image.getName()+" is:"+image.getTrackingState());
            //add 3d renderable to world first time ar image is detected
            //create one MediaPlayer instance for each renderable
            switch (image.getTrackingState()){
                case PAUSED:

                    break;
                case TRACKING:
                    //add if new image
                    if(augmentedImageNode==null){
                        System.out.println("New AR image:"+image.getName());
                        final BayernARImageNode mArImageNode=new BayernARImageNode(image,renderables[0],renderables[1],renderables[2]);
                        arFragment.getArSceneView().getScene().addChild(mArImageNode);
                        augmentedImageNode=mArImageNode;
                    }
                    break;
                case STOPPED:
                    //remove if still contained
                    final BayernARImageNode tmp=augmentedImageNode;
                    if(tmp!=null){
                        System.out.println("Removed AR image:"+image.getName());
                        arFragment.getArSceneView().getScene().removeChild(tmp);
                        augmentedImageNode=null;
                    }
                    break;
            }
        }
        if(augmentedImageNode!=null){
            synchronized (BayernActivity.this){
                augmentedImageNode.setMode(mode);
            }
            arFragment.getPlaneDiscoveryController().hide();
            arFragment.getPlaneDiscoveryController().setInstructionView(null);
        }
    }

    //Asynchronous !
    private void loadModel(final int index,final int resourceID){
        CompletableFuture<ModelRenderable> future =
                ModelRenderable.builder()
                        .setSource(this, resourceID)
                        .setIsFilamentGltf(true)
                        .build()
                        .thenApply(renderable -> {
                            System.out.println("Loaded renderable "+index);
                            return (renderables[index] = renderable);
                        })
                        .exceptionally(throwable ->{
                            Log.d(TAG, "Unable to load renderable: " + resourceID,throwable);
                            Toast.makeText(this,"Unable to load renderbale"+resourceID,Toast.LENGTH_LONG).show();
                            return null;});
    }

}
