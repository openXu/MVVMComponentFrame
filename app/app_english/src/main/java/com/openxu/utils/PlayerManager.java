package com.openxu.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

public class PlayerManager {
	private String TAG = "PlayerManager";
	private static PlayerManager player;
	private MediaPlayer mediaPlayer;
	private String path;

	private PlayerManager() {
	}

	public static PlayerManager getInstance() {
		if (player == null) {
			player = new PlayerManager();
		}
		return player;
	}

	/**
	 * 播放音乐
	 */
	public void play(String path) {
		MyUtil.LOG_I(TAG, "播放:" + path);
		try {
			if(path.equalsIgnoreCase(this.path)&&mediaPlayer != null && mediaPlayer.isPlaying()){
				mediaPlayer.seekTo(0);
			} else {
				this.path = path;
				if(null==mediaPlayer){
					mediaPlayer = new MediaPlayer();
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				}
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
					mediaPlayer.release();
					mediaPlayer = null;
					mediaPlayer = new MediaPlayer();
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				}

				mediaPlayer.setDataSource(path);
				mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						mp.start();
					}
				});
				// Prepare to async playing
				mediaPlayer.prepareAsync();
			
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						/*
						 * 因为我本地java的mp对象是定义的全局变量，所以通过类名.this.mp的方式得到我的对象，
						 * 而非操作onCompletion(MediaPlayer
						 * mp)参数传给我的native对象，这样一来，本地java对象就被销毁了，native对象自然也被销毁了
						 */
						PlayerManager.this.mediaPlayer.release();
						PlayerManager.this.mediaPlayer = null;
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
			MyUtil.LOG_I(TAG, "播放失败");
		}
	}

	/**
	 * 停止
	 */
	public void stop() {
		MyUtil.LOG_I(TAG, "停止");
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	/**
	 * 重新播放
	 * 
	 * @param path
	 */
	public void replay() {
		MyUtil.LOG_I(TAG, "重新播放");
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.seekTo(0);
		}
	}

	/**
	 * 暂停
	 */
	public void pause() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		} else if (mediaPlayer != null && (!mediaPlayer.isPlaying())) {
			mediaPlayer.start();
		}
	}

}
