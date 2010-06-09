package com.interdevinc.efiling.shared.view;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;

public class InformationDialogBox extends DialogBox {

    private Timer loadingTimer;
    
    public void loadingDialogBox(String titleBarText) {
	clear();
	setGlassEnabled(true);
	setAnimationEnabled(true);
	setText(titleBarText);
	//TODO Change before compiling...
	//HTML html = new HTML("<div align='center'><img align='center' src='Efiling/images/loading.gif' border=0 /></div><p>Please wait momentarily while the necessary component(s) finish loading.");
	HTML html = new HTML("<div align='center'><img align='center' src='/images/loading.gif' border=0 /></div><p>Please wait momentarily while the necessary component(s) finish loading.");
	add(html);
	center();
	show();
	startTimer();
    }

    public void messageDialogBox(String titleBarText, String message) {
	clear();
	HTML html = new HTML(message);
	setGlassEnabled(true);
	setAnimationEnabled(true);
	setAutoHideEnabled(true);
	setText(titleBarText);
	add(html);
	center();
	show();
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
