package com.intel.c3.okconnect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.intel.stc.lib.AppRegisterService;
import com.intel.stc.lib.GadgetRegistration;
import com.intel.stc.utility.StcApplicationId;
import com.intel.stc.utility.StcCloudAppRole;

public class OkConnectRegisterApplication extends AppRegisterService {

	public static final String		APP_UUID		= "29109FB7-3352-4077-B426-F86C6BB10D61";
	public static final String		LAUNCH_INTENT	= "com.intel.c3.okconnect";

	static final UUID				appUuid			= UUID.fromString(APP_UUID);

	private static final UUID		apiKeyId		= UUID.fromString("0AC047D0-8FBA-48BF-A02D-631C238405A8");
	private static final String		apiSecret		= "ZK4N7p5CEhWzq2kQk1k/MOup4/wMq5CUcW3o3Ixmciw=";
	
	private static final String		appTypeID		= "29109FB7-3352-4077-B426-F86C6BB10D61";
	private static final String		appTypeName		= "okConnect";

	static final StcApplicationId	id				= new StcApplicationId(appUuid, apiKeyId, apiSecret, appTypeID,
															appTypeName, StcCloudAppRole.BOTH);

	@Override
	protected List<GadgetRegistration> getGadgetList(Context context)
	{
		String appName = context.getString(R.string.app_name);
		String appDescription = context.getString(R.string.app_description);

		ArrayList<GadgetRegistration> list = new ArrayList<GadgetRegistration>();
		list.add(new GadgetRegistration(appName, R.drawable.ic_launcher, APP_UUID, appDescription, LAUNCH_INTENT, 2,
				R.string.okConnect_inv_text, R.string.timeout_toast_text, 0, context));
		return list;
	}
}
