package com.mvvm.component.utils

import com.orhanobut.logger.Logger
import java.io.*

object FileUtils {

	/**
	 * 创建文件夹
	 *
	 * @param outputPath 输出路径
	 * return 是否成功创建文件夹
	 */
	fun createDirectory(outputPath: String): Boolean {
		return File(outputPath).takeIf {
			!it.exists()
		}?.mkdirs()?.also {
			Logger.i("createDirectory => $outputPath:$it")
		} ?: false
	}

	/**
	 * 获取文件下的文件总数
	 *
	 * @param path 路径
	 */
	fun getDirectorySize(path: String): Int {
		val file = File(path)
		return takeIf {
			file.exists()
		}?.let {
			file.listFiles()?.size ?: 0
		} ?: 0
	}

	/**
	 * 获取文件名称（不包含扩展名）
	 *
	 * @param path 文件路径
	 * @return 文件名称
	 */
	fun getFileName(path: String): String {
		val file = File(path)
		if (file.exists()) {
			val fileName = file.name
			return fileName.substring(0, fileName.lastIndexOf("."))
		}
		return ""
	}

	/**
	 * 获取文本内容
	 *
	 * @param path 文件路径
	 * @return 文本内容
	 */
	fun getFileContent(path: String): String {
		val file = File(path)
		var content = ""
		if (!file.exists()) {
			return content
		}
		try {
			val fileInputStream = FileInputStream(file)
			val inputStreamReader = InputStreamReader(fileInputStream, "utf-8")
			val input = CharArray(fileInputStream.available())
			inputStreamReader.read(input)
			content = String(input)
		} catch (e: Exception) {
			e.printStackTrace()
		}

		return content
	}


	/**
	 * 重命名文件
	 *
	 * @param fileNamePath   原始文件路径
	 * @param renameFilePath 熊文件路径
	 * @return 操作结果
	 */
	fun renameFile(fileNamePath: String, renameFilePath: String): Boolean {
		val file = File(fileNamePath)
		val renameFile = File(renameFilePath)
		return !renameFile.exists() && file.renameTo(renameFile)
	}

	/**
	 * 删除文件
	 *
	 * @param filePath 文件路径
	 * @return 操作结果
	 */
	fun deleteFile(filePath: String): Boolean {
		return File(filePath).let { it.exists() && it.delete() }
	}

	/**
	 * 删除文件夹以及目录下的文件
	 *
	 * @param file 被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	fun deleteDirectory(file: File) {
		if (file.isFile) {
			file.delete()
			return
		}

		if (file.isDirectory) {
			val childFiles = file.listFiles()
			if (childFiles == null || childFiles.isEmpty()) {
				file.delete()
				return
			}

			for (childFile in childFiles) {
				deleteDirectory(childFile)
			}
			file.delete()
		}
	}

	/**
	 * 将内容写进文件
	 *
	 * @param content  文本
	 * @param filePath 文件路径
	 */
	fun writeFile(content: String, filePath: String): Boolean {
		return try {
			val file = File(filePath)
			if (!file.exists()) {
				file.parentFile?.mkdirs()
				file.createNewFile()
			}
			val fileOutputStream = FileOutputStream(file)
			val bytes = content.toByteArray()
			fileOutputStream.write(bytes)
			fileOutputStream.close()
			true
		} catch (e: Exception) {
			Logger.e("writeFile => ${e.message}")
			false
		}
	}

	/**
	 * 在指定的文件里面追加内容
	 *
	 * @param content    追加的内容
	 * @param parentPath 文件的父路径
	 * @param fileName   指定的文件名称
	 */
	@Synchronized
	fun appendFile(content: String, parentPath: String, fileName: String) {
		var fw: FileWriter? = null
		try {
			createDirectory(parentPath)

			// 创建文件 并获取文件写入流
			val file = File(parentPath, fileName)
			fw = FileWriter(file, true)// true代表往后追加的模式
			// 写入时间信息
			fw.write(TimeUtils.getCurrentTime() + " => " + content)
			fw.write("\r\n")//写入换行符
		} catch (ignored: Exception) {
			Logger.e("appendFile => ${ignored.message}")
		} finally {
			try {
				fw?.close()
			} catch (ignored: Exception) {
				Logger.e("appendFile => ${ignored.message}")
			}
		}
	}

	/**
	 * 在指定的文件里面追加内容
	 *
	 * @param content    追加的内容
	 */
	@Synchronized
	fun appendFile(content: ArrayList<String>, filePath: String) {
		File(filePath).parent?.apply { createDirectory(this) }
		var fw: FileWriter? = null
		try {
			fw = FileWriter(File(filePath), true).let { fileWriter ->
				content.forEach {
					fileWriter.write("$it \r\n")
				}
				fileWriter
			}
		} catch (ignored: Exception) {
			Logger.e("appendFile => ${ignored.message}")
		} finally {
			try {
				fw?.close()
			} catch (ignored: Exception) {
				Logger.e("appendFile => ${ignored.message}")
			}
		}
	}

	/**
	 * 获取文件夹大小(递归)
	 *
	 * @param file File实例
	 * @return long
	 */
	fun getFolderSize(file: File): Long {
		var size: Long = 0
		file.takeIf {
			it.exists()
		}?.listFiles()?.apply {
			for (i in indices) {
				size += if (this[i].isDirectory) getFolderSize(this[i])
				else this[i].length()
			}
		}
		return size
	}
}