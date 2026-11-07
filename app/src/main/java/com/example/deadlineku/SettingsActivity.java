package com.example.deadlineku;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private PrefsManager prefs;
    private EditText etNama;
    private TextView tvJam;
    private RadioButton rbTerdekat, rbTerjauh;
    private SwitchCompat swGelap;

    private int jam, menit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new PrefsManager(this);
        prefs.terapkanTema();
        setContentView(R.layout.activity_settings);
        UiUtil.applyInsets(this, findViewById(R.id.root), findViewById(R.id.body));

        etNama = findViewById(R.id.etNama);
        tvJam = findViewById(R.id.tvJam);
        rbTerdekat = findViewById(R.id.rbTerdekat);
        rbTerjauh = findViewById(R.id.rbTerjauh);
        swGelap = findViewById(R.id.swGelap);
        Button btnUbahJam = findViewById(R.id.btnUbahJam);
        Button btnSimpan = findViewById(R.id.btnSimpan);
        Button btnKembali = findViewById(R.id.btnKembali);

        // isi form dengan nilai yang tersimpan
        etNama.setText(prefs.getNama());
        jam = prefs.getJam();
        menit = prefs.getMenit();
        tvJam.setText(prefs.getJamFormatted());
        rbTerdekat.setChecked(prefs.getSort() == PrefsManager.SORT_TERDEKAT);
        rbTerjauh.setChecked(prefs.getSort() == PrefsManager.SORT_TERJAUH);
        swGelap.setChecked(prefs.isGelap());

        btnUbahJam.setOnClickListener(v ->
                new TimePickerDialog(this, (view, h, m) -> {
                    jam = h;
                    menit = m;
                    tvJam.setText(String.format(Locale.US, "%02d:%02d", h, m));
                }, jam, menit, true).show());

        btnSimpan.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            if (nama.isEmpty()) nama = "Mahasiswa";
            int sort = rbTerjauh.isChecked() ? PrefsManager.SORT_TERJAUH : PrefsManager.SORT_TERDEKAT;

            prefs.simpan(nama, jam, menit, sort, swGelap.isChecked());   // apply()
            prefs.terapkanTema();
            Toast.makeText(this, "Pengaturan disimpan", Toast.LENGTH_SHORT).show();
        });

        btnKembali.setOnClickListener(v -> finish());
    }
}
