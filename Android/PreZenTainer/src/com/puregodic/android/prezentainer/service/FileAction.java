package com.puregodic.android.prezentainer.service;

public interface FileAction {

	void onFileActionError();

	void onFileActionProgress(long progress);

	void onFileActionTransferComplete();

	void onFileActionTransferRequested(int transId, String path);

}
