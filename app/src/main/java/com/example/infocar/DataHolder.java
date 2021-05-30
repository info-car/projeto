package com.example.infocar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class DataHolder {
    private static String data;
    private static String origem;
    private static String destino;
    public static String getData() {return data;}
    public static String getOrigem() {return origem;}
    public static String getDestino() {return destino;}
    public static void setData(String data) {DataHolder.data = data;}
    public static void setOrigem(String data) {DataHolder.origem = data;}
    public static void setDestino(String data) {DataHolder.destino = data;}

}