package com.healthme.app.ui;

import com.healthme.app.common.UpdateManager;

import com.healthme.app.R;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 关于我们
 * @author ecg team (http://healthme.com.cn)
 * @version 1.0
 * @created 2012-3-21
 */
public class About extends BaseActivity{
	
	private TextView mVersion;
	private Button mUpdate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		//获取客户端版本信息
        try { 
        	PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
        	mVersion = (TextView)findViewById(R.id.about_version);
    		mVersion.setText("版本："+info.versionName);
        } catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
        
        mUpdate = (Button)findViewById(R.id.about_update);
        mUpdate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UpdateManager.getUpdateManager().checkAppUpdate(About.this, true);
			}
		});        
	}
}
