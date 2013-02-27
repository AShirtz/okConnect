package com.intel.c3.okconnect;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ServiceUsingActivity extends Activity {

	public static final String ServiceIntent = "com.intel.c3.okconnect.OkConnectApplicationService";
	protected OkConnectApplicationService service;
	private OkConnectApplicationServiceConnection mConnection = new OkConnectApplicationServiceConnection(this);
	boolean isBound = false;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void doStartService () {
		Intent servIntent = new Intent(ServiceIntent);
		startService(servIntent);
	}
	
	@Override
	protected void onResume () {
		doBindService();
		super.onResume();
	}
	
	@Override
	protected void onPause () {
		doUnbindService();
		super.onPause();
	}
	
	private void doBindService () {
		if (!isBound) {
			Intent servIntent = new Intent(ServiceIntent);
			isBound = bindService(servIntent, mConnection, 0);
		}
	}
	
	private void doUnbindService () {
		if (isBound) {
			isBound = false;
			unbindService(mConnection);
		}
	}
	
	public void onServiceConnected () {
		//TODO: anything requiring to be run first after service connected
	}
	
	public void onServiceDisconnected () {
		//TODO: anything to be done after service disconnected
	}
	
	public class OkConnectApplicationServiceConnection implements ServiceConnection {
		
		ServiceUsingActivity activity;
		boolean serviceStopped = false;
		
		OkConnectApplicationServiceConnection (ServiceUsingActivity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			synchronized (this) {
				service = (OkConnectApplicationService) ((OkConnectApplicationService.StcServInetBinder)arg1).getService();
				if (serviceStopped) {
					service.exitService();
				}
				else {
					activity.onServiceConnected();
				}
			}
		}
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			activity.onServiceDisconnected();
			service = null;
			
		}
		
	}
}
