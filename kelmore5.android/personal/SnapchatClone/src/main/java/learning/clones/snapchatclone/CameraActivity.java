package learning.clones.snapchatclone;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;

/**
 * This program is a clone of the popular app Snapchat.
 * It will not include all of Snapchat's features, but
 * will be used as a learning tool/basic diagram.
 *
 * @author Kyle Elmore
 * @version alpha
 * @since 2016-03-25
 */
public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    //The camera object
    private Camera mCamera;

    //The view objects to hold the camera
    CameraView cameraView;        //The actual view
    SurfaceHolder surfaceHolder;    //Used for editing the view

    /**
     * {@inheritDoc}
     *
     * On startup: Initialize mCamera, surfaceView, and surfaceHolder
     *             Add layout to surfaceView
     *             Add events to surfaceHolder (defined by this activity)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraView = (CameraView) findViewById(R.id.cameraView);
        mCamera = cameraView.getCamera();
        surfaceHolder = cameraView.getHolder();

        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Used on startup or when entering/exiting activity to
     * create the camera preview in our SurfaceView object
     */
    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        }

        catch (Exception e) {
        }

        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        }
        catch (Exception e) {
        }
    }

    /**
     * {@inheritDoc}
     *
     * Starts the camera upon surface creation
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        }

        catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    /**
     * {@inheritDoc}
     *
     * Refreshes the camera when the surface changes
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();
    }

    /**
     * {@inheritDoc}
     *
     * Stops the camera and sets to null
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}
