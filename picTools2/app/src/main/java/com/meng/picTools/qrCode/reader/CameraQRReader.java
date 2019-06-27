package com.meng.picTools.qrCode.reader;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.SurfaceHolder.*;
import android.widget.*;

import com.google.zxing.*;
import com.meng.picTools.*;
import com.meng.picTools.libAndHelper.zxing.camera.*;
import com.meng.picTools.libAndHelper.zxing.decoding.*;
import com.meng.picTools.libAndHelper.zxing.view.*;

import java.util.*;

import com.meng.picTools.R;

public class CameraQRReader extends Fragment implements Callback {

    private final int REQUEST_PERMISSION_CAMERA = 1000;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private boolean flashLightOpen = false;

    private ImageButton flashIbtn;

    private AlertDialog mDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.qr_camera, container, false);
	  }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(getActivity());
        CameraManager.init(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
								   REQUEST_PERMISSION_CAMERA);
			  }
		  }

        viewfinderView = (ViewfinderView) view.findViewById(R.id.viewfinder_view);
        flashIbtn = (ImageButton) view.findViewById(R.id.flash_ibtn);
        flashIbtn.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				  if (flashLightOpen) {
					  flashIbtn.setImageResource(R.drawable.ic_flash_off_white_24dp);
					} else {
					  flashIbtn.setImageResource(R.drawable.ic_flash_on_white_24dp);
					}
				  toggleFlashLight();
				}
			});
	  }


    @Override
    public void onResume() {
        super.onResume();
	//	LogTool.t("camera open");
		View sv=this.getView();
		if(sv==null){
		  return;
		}
        SurfaceView surfaceView =(SurfaceView) sv.findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
		  } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		  }
        decodeFormats = null;
        characterSet = null;
	  }

    @Override
    public void onPause() {
        super.onPause();
		//LogTool.t("camera close");
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
		  }
        if (flashIbtn != null) {
            flashIbtn.setImageResource(R.drawable.ic_flash_off_white_24dp);
		  }
		CameraManager cm=CameraManager.get();
		if (cm != null) {
			cm.closeDriver();
		  }
	  }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
	  }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 未获得Camera权限
                new AlertDialog.Builder(getActivity())
				  .setTitle("提示")
				  .setMessage("请在系统设置中为App开启摄像头权限后重试")
				  .setPositiveButton("确定", new DialogInterface.OnClickListener() {
					  @Override
					  public void onClick(DialogInterface dialog, int which) {
						  //       mActivity.finish();
						}
					}).show();
			  }
		  }
	  }

    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        MainActivity2.instence.doVibrate(200L);
        handleResult(result.getText(), result.getBarcodeFormat().toString());
	  }

    public void handleResult(final String resultString, String format) {
        if (TextUtils.isEmpty(resultString)) {
            LogTool.t("scan_failed");
            restartPreview();
		  } else {
            if (mDialog == null) {
                mDialog = new AlertDialog.Builder(getActivity())
				  .setTitle("二维码类型:" + format)
				  .setMessage(resultString)
				  .setPositiveButton("确定", null)
				  .setNeutralButton("复制文本", new DialogInterface.OnClickListener() {

					  @Override
					  public void onClick(DialogInterface p1, int p2) {
						  android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
						  ClipData clipData = ClipData.newPlainText("text", resultString);
						  clipboardManager.setPrimaryClip(clipData);
						}
					})
				  .setNegativeButton("生成AwesomeQR", new DialogInterface.OnClickListener() {

					  @Override
					  public void onClick(DialogInterface p1, int p2) {
						  MainActivity2.instence.showAwesomeFragment(true);
						  MainActivity2.instence.awesomeCreatorFragment.setDataStr(resultString);
						}
					}).create();
                mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					  @Override
					  public void onDismiss(DialogInterface dialog) {
						  mDialog = null;
						  restartPreview();
						}
					});
			  }
            if (!mDialog.isShowing()) {
                mDialog.show();
			  }
		  }
	  }


    protected void setViewfinderView(ViewfinderView view) {
        viewfinderView = view;
	  }

    public void toggleFlashLight() {
        if (flashLightOpen) {
            setFlashLightOpen(false);
		  } else {
            setFlashLightOpen(true);
		  }
	  }

    public void setFlashLightOpen(boolean open) {
        if (flashLightOpen == open) return;
        flashLightOpen = !flashLightOpen;
        CameraManager.get().setFlashLight(open);
	  }

    public boolean isFlashLightOpen() {
        return flashLightOpen;
	  }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
		  } catch (Exception e) {
            return;
		  }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		  }
	  }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	  }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
		  }

	  }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
	  }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
	  }

    public Handler getHandler() {
        return handler;
	  }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
	  }

    protected void restartPreview() {
        // 当界面跳转时 handler 可能为null
        if (handler != null) {
            Message restartMessage = Message.obtain();
            restartMessage.what = R.id.restart_preview;
            handler.handleMessage(restartMessage);
		  }
	  }


  }
