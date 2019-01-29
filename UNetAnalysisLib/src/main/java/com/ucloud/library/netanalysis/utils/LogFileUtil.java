package com.ucloud.library.netanalysis.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogFileUtil {
	private static final String TAG = "LogFileUtil";
	private static final String LOG_FILE_SUFFIX = ".log";
	private static String sLogBasePath;

	/**
	 * 读写文件的线程池，单线程模型
	 */
	private static ExecutorService sExecutorService;

	static {
		sExecutorService = Executors.newSingleThreadExecutor();
	}

	/**
	 * 设置Log存放位置，同时删除超过存放时长的Log
	 *
	 * @param basePath
	 */
	public static void initBasePath(String basePath, int maxSaveDays) {
		sLogBasePath = basePath;
		if (!new File(basePath).exists()) {
			new File(basePath).mkdirs();
		}
		delOldFiles(new File(basePath), maxSaveDays);
	}

	/**
	 * 删除文件夹下所有的 N 天前创建的文件
	 * 注意: 由于拿不到文件的创建时间，这里暂且拿最后修改时间比较
	 *
	 * @param dir
	 * @param days
	 */
	public static void delOldFiles(File dir, int days) {
		int daysMillis = days * 24 * 60 * 60 * 1000;
		if (dir.exists()) {
			File[] files = dir.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile() && System.currentTimeMillis() - files[i].lastModified() > daysMillis) {
						files[i].delete();
					}
				}
			}
		}
	}

	/**
	 * 把文本写入文件中
	 *
	 * @param file       目录文件
	 * @param content    待写内容
	 * @param isOverride 写入模式，true - 覆盖，false - 追加
	 */
	public static void write(@NonNull final File file, @NonNull final String content, final boolean isOverride) {
		sExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				FileOutputStream fos = null;
				try {
					boolean isExist = file.exists();
					fos = new FileOutputStream(file, !(!isExist || isOverride));
					fos.write(content.getBytes("UTF-8"));
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							Log.e(TAG, e.getMessage());
						}
					}
				}
			}
		});
	}

	public static void writeLog(String content) {
        content = content.replaceFirst("]", "] \n");
		write(getLogFile(), "\n[" + getFormattedSecond() + "]" + content + "\n\n", false);
	}

	/**
	 * 拿到最新的Log文件
	 *
	 * @return
	 */
	public static File getLogFile() {
		File dir = new File(sLogBasePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File logFile = new File(dir, getFormattedDay() + LOG_FILE_SUFFIX);
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return logFile;
	}

	//==================================== TimeUtil =============================================//
	public static final String FORMATTER_DAY = "yy_MM_dd";
	public static final String FORMATTER_SECOND = "yy-MM-dd HH:mm:ss";

	public static SimpleDateFormat sSecondFormat = new SimpleDateFormat(FORMATTER_SECOND);

	public static String getFormattedDay() {
		return new SimpleDateFormat(FORMATTER_DAY).format(new Date());
	}

	public static String getFormattedSecond() {
		return sSecondFormat.format(new Date());
	}
}
