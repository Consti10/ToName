package com.lrz;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.samples.gltf.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class LRZActivity extends AppCompatActivity {
    private static final String TAG = LRZActivity.class.getSimpleName();

    private MarkerBasedARFragment arFragment;
    private SceneView sceneView;
    private static final int N_RENDERABLES=3;
    private final ModelRenderable[] renderables=new ModelRenderable[N_RENDERABLES];

    private final Map<AugmentedImage,LRZARImageNode> augmentedImageMap = new HashMap<>();
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

        loadModel(0,R.raw.chamaeleon_low);
        loadModel(1,R.raw.bunge_1200_140mio);
        loadModel(2,R.raw.avocado);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);
        arFragment.getArSceneView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (LRZActivity.this){
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
                    if(!augmentedImageMap.containsKey(image)){
                        final LRZARImageNode node=new LRZARImageNode(image,renderables[1]);
                        arFragment.getArSceneView().getScene().addChild(node);
                        augmentedImageMap.put(image,node);
                    }
                    break;
                case STOPPED:
                    //remove if still contained
                    final LRZARImageNode tmp=augmentedImageMap.get(image);
                    if(tmp!=null){
                        System.out.println("Removed AR image:"+image.getName());
                        arFragment.getArSceneView().getScene().removeChild(tmp);
                        augmentedImageMap.remove(image);
                    }
                    break;
            }
        }
        if(!augmentedImageMap.isEmpty()){
            arFragment.getPlaneDiscoveryController().hide();
            arFragment.getPlaneDiscoveryController().setInstructionView(null);
            synchronized (LRZActivity.this){
                final LRZARImageNode node=augmentedImageMap.get(augmentedImageMap.keySet().iterator().next());
                node.setRenderable(renderables[mode]);
            }
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
