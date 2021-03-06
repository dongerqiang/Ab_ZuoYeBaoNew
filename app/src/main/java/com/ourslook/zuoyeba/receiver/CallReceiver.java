/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ourslook.zuoyeba.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;
import com.ourslook.zuoyeba.activity.em.VideoCallActivity;
import com.ourslook.zuoyeba.activity.em.VideoCallNewActivity;
import com.ourslook.zuoyeba.activity.em.VoiceCallActivity;
import com.ourslook.zuoyeba.activity.em.VoiceCallNewActivity;

public class CallReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if(!EMClient.getInstance().isLoggedInBefore())
		    return;
		//拨打方username
		String from = intent.getStringExtra("from");
		//call type
		String type = intent.getStringExtra("type");
		if("video".equals(type)){ //视频通话
		    context.startActivity(new Intent(context, VideoCallNewActivity.class).
//                    putExtra("username", from).putExtra("isComingCall", true).
                    putExtra("from", from).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}else{ //音频通话
		    context.startActivity(new Intent(context, VoiceCallNewActivity.class).
//		            putExtra("username", from).putExtra("isComingCall", true).
		            putExtra("from", from).putExtra("isComingCall", true).
		            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
		Log.d("CallReceiver", "app received a incoming call");
	}

}
