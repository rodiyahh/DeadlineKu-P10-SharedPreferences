package com.example.deadlineku;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Satu-satunya pintu akses ke SharedPreferences.
 * Semua key dan nilai default dikumpulkan di sini supaya rapi.
 */
public class PrefsManager {

    private static final String PREF_NAME = "deadlineku_prefs";

    private static final String KEY_NAMA = "nama";
    private static final String KEY_JAM = "jam_pengingat";
    private static final String KEY_MENIT = "menit_pengingat";
    private static final String KEY_SORT = "urutan_sortir";   // 0 = terdekat, 1 = terjauh
    private static final String KEY_GELAP = "mode_gelap";

    public static final int SORT_TERDEKAT = 0;
    public static final int SORT_TERJAUH = 1;

    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ---------- BACA ----------
    public String getNama() {
        return prefs.getString(KEY_NAMA, "Mahasiswa");
    }

    public int getJam() {
        return prefs.getInt(KEY_JAM, 19);
    }

    public int getMenit() {
        return prefs.getInt(KEY_MENIT, 0);
    }

    public int getSort() {
        return prefs.getInt(KEY_SORT, SORT_TERDEKAT);
    }

    public boolean isGelap() {
        return prefs.getBoolean(KEY_GELAP, false);
    }

    public String getJamFormatted() {
        return String.format("%02d:%02d", getJam(), getMenit());
    }

    // ---------- TULIS ----------
    /** Simpan semua pengaturan sekaligus. apply() menulis di background (asynchronous). */
    public void simpan(String nama, int jam, int menit, int sort, boolean gelap) {
        prefs.edit()
                .putString(KEY_NAMA, nama)
                .putInt(KEY_JAM, jam)
                .putInt(KEY_MENIT, menit)
                .putInt(KEY_SORT, sort)
                .putBoolean(KEY_GELAP, gelap)
                .apply();
    }

    /** Terapkan mode gelap/terang sesuai nilai yang tersimpan. */
    public void terapkanTema() {
        AppCompatDelegate.setDefaultNightMode(
                isGelap() ? AppCompatDelegate.MODE_NIGHT_YES
                          : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
