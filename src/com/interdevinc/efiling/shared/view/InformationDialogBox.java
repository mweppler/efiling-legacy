package com.interdevinc.efiling.shared.view;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;

public class InformationDialogBox extends DialogBox {

    private Timer loadingTimer;

    public void errorMessageDialogBox(String titleBarText, String error) {
	setGlassEnabled(true);
	setAnimationEnabled(true);
	setAutoHideEnabled(true);
	setTitle(titleBarText);
	HTML html = new HTML(error);
	add(html);
	center();
	show();
    }
    
    public void loadingDialogBox(String text) {
	setGlassEnabled(true);
	setAnimationEnabled(true);
	setText(text);
	HTML html = new HTML("<div align='center'><img align='center' src='images/loading.gif' border=0 /></div><p>Please wait momentarily while the necessary component(s) finish loading.");
	add(html);
	center();
	show();
	startTimer();
    }

    private void startTimer() {
	loadingTimer = new Timer() {
	    public void run() {
	    }
	};
	loadingTimer.scheduleRepeating(2000);
    }

    public void destroyTimer() {
	hide();
	loadingTimer.cancel();
    }

}
