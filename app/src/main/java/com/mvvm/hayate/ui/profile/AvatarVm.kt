package com.mvvm.hayate.ui.profile

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.core.content.FileProvider
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.mvvm.component.BaseApplication
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.utils.FileUtils
import com.mvvm.component.view.BaseVm
import com.mvvm.hayate.BuildConfig
import com.mvvm.hayate.R
import com.mvvm.hayate.contracts.ContractsConst
import com.mvvm.hayate.manager.PathManager
import com.mvvm.hayate.manager.ProfileManager
import com.orhanobut.logger.Logger
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class AvatarVm : BaseVm() {

	var avatarPath = ObservableField("")
	var avatarErrorRes = ObservableInt(R.drawable.ic_avatar)

	val selectAvatarEvent = MutableLiveData<LiveDataEvent<Unit>>()
	val uploadAvatarEvent = MutableLiveData<LiveDataEvent<String>>()
	val registerActivityResultEvent = MutableLiveData<LiveDataEvent<Pair<Intent, Int>>>()

	lateinit var tempProfileIconUri: Uri

	init {
		updateAvatar()
	}

	fun updateAvatar() {
		avatarPath.set(ProfileManager.avatarPath ?: "")
	}

	/**
	 * 点击事件 -> 头像修改
	 */
	var onAvatarClick = View.OnClickListener {
		selectAvatarEvent.value = LiveDataEvent(Unit)
	}

	/**
	 * 拍照
	 */
	fun takePhoto() {
		val tempFile = File(PathManager.avatarTempPath)
		val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				val uriForFile = FileProvider.getUriForFile(BaseApplication.context, "${BuildConfig.APPLICATION_ID}.fileProvider", tempFile)
				putExtra(MediaStore.EXTRA_OUTPUT, uriForFile)
				addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
				addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
			} else {
				putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile))
			}
		}
		registerActivityResultEvent.value = LiveDataEvent(intent to ContractsConst.AVATAR_CAMERA)
	}

	/**
	 * 本地图片
	 */
	fun pickImage() {
		val intent = Intent().apply {
			type = "image/*"
			action = Intent.ACTION_PICK
		}
		registerActivityResultEvent.value = LiveDataEvent(intent to ContractsConst.AVATAR_LOCAL)
	}

	fun handleAvatarActivityResult(activityResult: ActivityResult) {
		when (activityResult.resultCode) {
			ContractsConst.AVATAR_CAMERA -> cropImageBySdk()
			ContractsConst.AVATAR_LOCAL -> activityResult.data?.data?.apply { cropImage(this) }
			ContractsConst.AVATAR_CROP -> saveCropImage()
			else -> Logger.e("handleAvatarActivityResult error")
		}
	}

	/**
	 * 根据手机的SDK进行不同方式的裁剪
	 */
	private fun cropImageBySdk() {
		File(PathManager.avatarTempPath).apply {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				cropImage(FileProvider.getUriForFile(BaseApplication.context, "${BuildConfig.APPLICATION_ID}.fileProvider", this))// 设置输入类型
			} else {
				cropImage(Uri.fromFile(this))
			}
		}
	}

	private fun saveCropImage() {
		// 针对Android11以上系统先将公共目录下保存的图片拷贝至私有目录下
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			val inputStream = BaseApplication.context.contentResolver.openInputStream(tempProfileIconUri)!!
			val outputStream = BufferedOutputStream(FileOutputStream(PathManager.avatarPath))
			android.os.FileUtils.copy(inputStream, outputStream)
			outputStream.flush()
			outputStream.close()
			inputStream.close()
			// 拷贝完后直接删除公有目录的图片
			BaseApplication.context.contentResolver.delete(tempProfileIconUri, null)
		}
		BitmapFactory.decodeFile(PathManager.avatarPath)?.apply {
			try {
				val profileIconTempFile = File("${PathManager.profileIconDir}photo${System.currentTimeMillis()}.jpg")
				val outputStream = BufferedOutputStream(FileOutputStream(profileIconTempFile))
				compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
				outputStream.flush()
				outputStream.close()
				uploadAvatarEvent.value = LiveDataEvent(profileIconTempFile.path)
			} catch (e: Exception) {
				Logger.e("saveCropImage => ${e.message}")
			}
		}
	}

	/**
	 * 裁剪图片方法实现
	 *
	 * @param uri
	 */
	private fun cropImage(uri: Uri) {
		FileUtils.createDirectory(PathManager.profileIconDir)
		val outputUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			val resolver = BaseApplication.context.contentResolver
			val icon = ContentValues().apply {
				put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}${File.separator}${BaseApplication.context.packageName}")
				put(MediaStore.Images.Media.DISPLAY_NAME, "icon.jpg")
				put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
			}
			resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, icon)!!.also { tempProfileIconUri = it }
		} else {
			Uri.fromFile(File(PathManager.avatarPath))
		}
		val intent = Intent("com.android.camera.action.CROP").apply {
			addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
			setDataAndType(uri, "image/*")
			putExtra("crop", "true")
			putExtra("aspectX", 1)
			putExtra("aspectY", 1)
			putExtra("outputX", 160)
			putExtra("outputY", 160)
			putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())// 返回格式
			putExtra("return-data", false)
			putExtra(MediaStore.EXTRA_OUTPUT, outputUri) // 保存到原文件
		}
		registerActivityResultEvent.value = LiveDataEvent(intent to ContractsConst.AVATAR_CROP)
	}
}