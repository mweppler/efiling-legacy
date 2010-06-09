package com.interdevinc.efiling.shared.view;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;

public class InformationDialogBox extends DialogBox {

    private final String ERROR_ICON = new String("images/error-icon-16.png");
    private final String INFO_ICON = new String("images/information-icon-16.png");
    private final String OK_ICON = new String("images/ok-icon-16.png");
    private final String QUESTION_ICON = new String("images/question-icon-16.png");
//    private final Image LOADING_IMAGE = new Image(GWT.getHostPageBaseURL() + "/images/loading.gif");
    private Timer loadingTimer;

    public void destroyTimer() {
	hide();
	loadingTimer.cancel();
    }

    public void loadingDialogBox(String titleBarText) {
	clear();
	setGlassEnabled(true);
	setAnimationEnabled(true);
	setText(titleBarText);
	HTML html = new HTML("<div align='center'><img align='center' src='/images/loading.gif' border=0 /></div><p>Please wait momentarily while the necessary component(s) finish loading.");
	add(html);
	center();
	show();
	startTimer();
    }

    public void messageDialogBox(int icon, String titleBarText, String message) {
	clear();
	HTML html = new HTML("<img border='0' src='" + addIconToMessage(icon) + "'/>&nbsp;" + message);
	setGlassEnabled(true);
	setAnimationEnabled(true);
	setAutoHideEnabled(true);
	setText(titleBarText);
	add(html);
	center();
	show();
    }

    private String addIconToMessage(int iconIndex) {

	switch (iconIndex) {
	case 0:
	    return ERROR_ICON;
	case 1:
	    return INFO_ICON;
	case 2:
	    return OK_ICON;
	case 3:
	    return QUESTION_ICON;
	default:
	    return null;
	}

    }

    private void startTimer() {
	loadingTimer = new Timer() {
	    public void run() {
	    }
	};
	loadingTimer.scheduleRepeating(2000);
    }

}
