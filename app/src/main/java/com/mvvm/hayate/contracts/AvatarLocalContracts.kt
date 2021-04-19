package com.mvvm.hayate.contracts

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract

class AvatarLocalContracts : ActivityResultContract<Intent, ActivityResult>() {

	override fun createIntent(context: Context, intent: Intent): Intent {
		return intent
	}

	override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult {
		return ActivityResult(ContractsConst.AVATAR_LOCAL, intent)
	}
}