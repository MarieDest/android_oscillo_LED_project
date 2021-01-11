package com.example.tp_eb03;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.google.android.material.badge.BadgeDrawable;

public class OscilloManager implements Transceiver.TransceiverListener {

    private Transceiver mTransceiver = new BTManager(); //ici le transceiver peut etre que BTManager puisque c'est la seule classe
    // qui a ce type.

    public void getInstance(){

    }

    public void setCalibrationDutyCycle(AppCompatSeekBar mSlider){
        FrameProcessor mFrameProcessor = new FrameProcessor();
        byte[] b=new byte[]{0x0A,(byte)mSlider.getAlpha()};
        mFrameProcessor.toFrame(b);
        mTransceiver.send(mFrameProcessor.getTxFrame());

    }
    public void getStatus(){

    }
    public void attachTransceiver(String adresse){
        mTransceiver.connect(adresse);
    }
    public void detachTransceiver(String adresse){
        mTransceiver.disconnect();
    }


    @Override
    public void onTransceiverDataReceiver() {

    }

    @Override
    public void onTransceiverStateChanged(int state) {

    }

    @Override
    public void onTransceiverConnectionLost() {

    }

    @Override
    public void onTransceiverUnableToConnect() {

    }

    public interface OscilloEventListener{

        void onOscilloStateChanged(int state);
        void onOscilloConnectionLost();
        void onOscilloUnableToConnect();
    }

}