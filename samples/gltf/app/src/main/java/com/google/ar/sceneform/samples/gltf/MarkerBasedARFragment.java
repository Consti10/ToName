package com.google.ar.sceneform.samples.gltf;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class MarkerBasedARFragment extends ArFragment {
    //Loads a specific Image database before configuring the AR Fragment
    private static final String MODEL_DB="sample_database.imgdb";

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = new Config(session);
        //config.setAugmentedImageDatabase(CUtil.createAugmentedImageDatabase(requireActivity(),session,MODEL_DB));
        //Disable plane finding (only search for augmeneted images)
        //config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL);
        //from maze example
        //Default focus mode seems to be 'fixed'
        //but auto does not really achieve better results
        //System.out.println("Focus mode:"+config.getFocusMode());
        //config.setFocusMode(Config.FocusMode.AUTO);
        //https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/Config.FocusMode
        //don't mess around,just leave default

        session.configure(config);
        getArSceneView().setupSession(session);
        return config;
    }

    //override and don't call super to avoid going full screen
    @Override
    protected void onWindowFocusChanged (boolean hasFocus){
    }



}
