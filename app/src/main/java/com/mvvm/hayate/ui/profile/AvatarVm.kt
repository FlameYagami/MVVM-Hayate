package com.mvvm.hayate.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
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
    val startActivityForResultEvent = MutableLiveData<LiveDataEvent<Pair<Intent, Int>>>()

    enum class AvatarType(private val value: Int) {
        TYPE_CAMERA(1), TYPE_LOCAL(2), TYPE_CROP(3);

        fun value(): Int {
            return this.value
        }
    }

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
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val uriForFile = FileProvider.getUriForFile(BaseApplication.context, "${BuildConfig.APPLICATION_ID}.fileProvider", tempFile)
                putExtra(MediaStore.EXTRA_OUTPUT, uriForFile)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            } else {
                putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile))
            }
            startActivityForResultEvent.value = LiveDataEvent(this to AvatarType.TYPE_CAMERA.value())
        }
    }

    /**
     * 本地图片
     */
    fun chooseLocal() {
        Intent().apply {
            type = "image/*"
            action = Intent.ACTION_PICK
            startActivityForResultEvent.value = LiveDataEvent(this to AvatarType.TYPE_LOCAL.value())
        }
    }

    fun handleAvatarRequestCode(requestCode: Int, data: Intent?) {
        when (requestCode) {
            AvatarType.TYPE_CAMERA.value() -> cropImageBySdk()
            AvatarType.TYPE_LOCAL.value() -> data?.data?.apply { cropImage(this) }
            AvatarType.TYPE_CROP.value() -> saveCropImage()
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
        BitmapFactory.decodeFile(PathManager.avatarPath)?.apply {
            try {
                val profileIconTempFile = File("${PathManager.profileIconDir}photo${System.currentTimeMillis()}.jpg")
                val bos = BufferedOutputStream(FileOutputStream(profileIconTempFile))
                compress(Bitmap.CompressFormat.JPEG, 100, bos)
                bos.flush()
                bos.close()
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
        Intent("com.android.camera.action.CROP").apply {
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, "image/*")
            putExtra("crop", "true")
            putExtra("aspectX", 1)
            putExtra("aspectY", 1)
            putExtra("outputX", 160)
            putExtra("outputY", 160)
            putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(PathManager.avatarPath)))// 保存到原文件
            putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())// 返回格式
            putExtra("return-data", false)
            startActivityForResultEvent.value = LiveDataEvent(this to AvatarType.TYPE_CROP.value())
        }
    }
}