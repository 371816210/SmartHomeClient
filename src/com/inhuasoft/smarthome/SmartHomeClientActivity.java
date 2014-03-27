package com.inhuasoft.smarthome;

import org.videolan.vlc.LibVLC;
import org.videolan.vlc.LibVlcException;
import org.videolan.vlc.WeakHandler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

public class SmartHomeClientActivity extends Activity implements SurfaceHolder.Callback {

	private SurfaceView mSurfaceView;
	private SurfaceHolder surfaceHolder = null;
	private Button btnConnect ;
	private EditText  mServerIP;
	
	private String mRtspURL =  null;
    private	LibVLC mLibVLC = null;
    
	private int mVideoHeight;
	private int mVideoWidth;
	private int mSarDen;
	private int mSarNum;
	
	
	
	private static final int SURFACE_SIZE = 3;

	private static final int SURFACE_BEST_FIT = 0;
	private static final int SURFACE_FIT_HORIZONTAL = 1;
	private static final int SURFACE_FIT_VERTICAL = 2;
	private static final int SURFACE_FILL = 3;
	private static final int SURFACE_16_9 = 4;
	private static final int SURFACE_4_3 = 5;
	private static final int SURFACE_ORIGINAL = 6;
	private int mCurrentSize = SURFACE_ORIGINAL;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smart_home_client);
		mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView1);
	//	mServerIP = (EditText)findViewById(R.id.editText1);
	//	btnConnect = (Button)findViewById(R.id.button1);
	    surfaceHolder = mSurfaceView.getHolder();
	    surfaceHolder.setFormat(PixelFormat.RGBX_8888);
		surfaceHolder.addCallback(this);
		
		try {
			LibVLC.useIOMX(true);
			mLibVLC = LibVLC.getInstance();
		    mRtspURL="rtsp://192.168.4.106:8086?h264&camera=front&amr";
			//String pathUri = "rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp";
			mLibVLC.readMedia(mRtspURL, false);
		} catch (LibVlcException e) {
			e.printStackTrace();
		}
		
		
		
	/*	btnConnect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mRtspURL = "rtsp://"+mServerIP.getText() ;
				try {
					LibVLC.useIOMX(true);
					mLibVLC = LibVLC.getInstance();
				
					if (mLibVLC != null) {
						
						String path = getIntent().getStringExtra("path");

						// String pathUri = LibVLC.getInstance().nativeToURI(path);
					       mRtspURL="rtsp://192.168.0.103:8086?h264&camera=front&amr";
						//String pathUri = "rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp";
						mLibVLC.readMedia(mRtspURL, false);
						//handler.sendEmptyMessageDelayed(0, 1000);
					}
				} catch (LibVlcException e) {
					e.printStackTrace();
				}

				
			}
		});  */
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	//	surfaceHolder = holder ;
	//	mheight = height ; 
	//	mwidth = width ;
		
		mLibVLC.attachSurface(holder.getSurface(), SmartHomeClientActivity.this,
				width, height);
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mLibVLC != null) {
			mLibVLC.stop();
		}
		super.onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//surfaceHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (mLibVLC != null)
		{
		  mLibVLC.detachSurface();
		}
		surfaceHolder = null ;
	}

	
	 public void setSurfaceSize(int width, int height, int sar_num, int sar_den) {
	        // store video size
	        mVideoHeight = height;
	        mVideoWidth = width;
	        mSarNum = sar_num;
	        mSarDen = sar_den;
	        Message msg = mHandler.obtainMessage(SURFACE_SIZE);
	        mHandler.sendMessage(msg);
	    } 
	 
	 
	 
	 
		private final Handler mHandler = new VideoPlayerHandler(this);

		private static class VideoPlayerHandler extends
				WeakHandler<SmartHomeClientActivity> {
			public VideoPlayerHandler(SmartHomeClientActivity owner) {
				super(owner);
			}

			@Override
			public void handleMessage(Message msg) {
				SmartHomeClientActivity activity = getOwner();
				if (activity == null) // WeakReference could be GC'ed early
					return;

				switch (msg.what) {
				case SURFACE_SIZE:
					activity.changeSurfaceSize();
					break;
				}
			}
		};

		
		private void changeSurfaceSize() {
			// get screen size
			int dw = getWindow().getDecorView().getWidth();
			int dh = getWindow().getDecorView().getHeight();

			// getWindow().getDecorView() doesn't always take orientation into
			// account, we have to correct the values
			boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
			if (dw > dh && isPortrait || dw < dh && !isPortrait) {
				int d = dw;
				dw = dh;
				dh = d;
			}
			if (dw * dh == 0)
				return;
			// compute the aspect ratio
					double ar, vw;
					double density = (double) mSarNum / (double) mSarDen;
					if (density == 1.0) {
						/* No indication about the density, assuming 1:1 */
						vw = mVideoWidth;
						ar = (double) mVideoWidth / (double) mVideoHeight;
					} else {
						/* Use the specified aspect ratio */
						vw = mVideoWidth * density;
						ar = vw / mVideoHeight;
					}

					// compute the display aspect ratio
					double dar = (double) dw / (double) dh;

//			// calculate aspect ratio
//			double ar = (double) mVideoWidth / (double) mVideoHeight;
//			// calculate display aspect ratio
//			double dar = (double) dw / (double) dh;

			switch (mCurrentSize) {
			case SURFACE_BEST_FIT:
			//	mTextShowInfo.setText(R.string.video_player_best_fit);
				if (dar < ar)
					dh = (int) (dw / ar);
				else
					dw = (int) (dh * ar);
				break;
			case SURFACE_FIT_HORIZONTAL:
			//	mTextShowInfo.setText(R.string.video_player_fit_horizontal);
				dh = (int) (dw / ar);
				break;
			case SURFACE_FIT_VERTICAL:
			//	mTextShowInfo.setText(R.string.video_player_fit_vertical);
				dw = (int) (dh * ar);
				break;
			case SURFACE_FILL:
				break;
			case SURFACE_16_9:
			//	mTextShowInfo.setText(R.string.video_player_16x9);
				ar = 16.0 / 9.0;
				if (dar < ar)
					dh = (int) (dw / ar);
				else
					dw = (int) (dh * ar);
				break;
			case SURFACE_4_3:
			//	mTextShowInfo.setText(R.string.video_player_4x3);
				ar = 4.0 / 3.0;
				if (dar < ar)
					dh = (int) (dw / ar);
				else
					dw = (int) (dh * ar);
				break;
			case SURFACE_ORIGINAL:
			//	mTextShowInfo.setText(R.string.video_player_original);
				//dh = mVideoHeight;
				//dw = mVideoWidth;
				dh = 450;
				dw = 600;
				break;
			}

			surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
			LayoutParams lp = mSurfaceView.getLayoutParams();
			lp.width = dw;
			lp.height = dh;
			mSurfaceView.setLayoutParams(lp);
			mSurfaceView.invalidate();
		}
		
		
		
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.smart_home_client, menu);
		return true;
	}

}
