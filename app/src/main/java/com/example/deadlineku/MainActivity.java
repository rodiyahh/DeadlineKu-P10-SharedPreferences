package com.example.deadlineku;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private PrefsManager prefs;
    private DBHelper db;

    private TextView tvSapaan, tvInfo;
    private EditText etJudul, etMatkul;
    private Button btnTanggal;
    private ListView lvTugas;

    private String tanggalDipilih = "";
    private final List<DBHelper.Tugas> data = new ArrayList<>();
    private ArrayAdapter<DBHelper.Tugas> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new PrefsManager(this);
        prefs.terapkanTema();               // tema dipasang sebelum layout dibuat
        setContentView(R.layout.activity_main);
        UiUtil.applyInsets(this, findViewById(R.id.root), findViewById(R.id.body));

        db = new DBHelper(this);

        tvSapaan = findViewById(R.id.tvSapaan);
        tvInfo = findViewById(R.id.tvInfo);
        etJudul = findViewById(R.id.etJudul);
        etMatkul = findViewById(R.id.etMatkul);
        btnTanggal = findViewById(R.id.btnTanggal);
        lvTugas = findViewById(R.id.lvTugas);
        Button btnTambah = findViewById(R.id.btnTambah);
        Button btnPengaturan = findViewById(R.id.btnPengaturan);

        lvTugas.setEmptyView(findViewById(R.id.tvKosong));

        // satu baris daftar = kartu berisi judul, mata kuliah, dan label sisa hari
        adapter = new ArrayAdapter<DBHelper.Tugas>(this, R.layout.item_tugas, data) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView != null
                        ? convertView
                        : getLayoutInflater().inflate(R.layout.item_tugas, parent, false);
                DBHelper.Tugas t = getItem(position);

                ((TextView) v.findViewById(R.id.tvJudul)).setText(t.judul);
                ((TextView) v.findViewById(R.id.tvMatkul)).setText(t.matkul + " · " + t.deadline);

                long sisa = sisaHari(t.deadline);
                String teks;
                int warna;
                if (sisa < 0) {
                    teks = "Terlewat " + (-sisa) + " hari";
                    warna = 0xFFD32F2F;
                } else if (sisa == 0) {
                    teks = "Hari ini!";
                    warna = 0xFFE64A19;
                } else if (sisa <= 3) {
                    teks = "Sisa " + sisa + " hari";
                    warna = 0xFFF57C00;
                } else {
                    teks = "Sisa " + sisa + " hari";
                    warna = 0xFF2E7D32;
                }
                TextView chip = v.findViewById(R.id.tvSisa);
                chip.setText(teks);
                GradientDrawable bg = new GradientDrawable();
                bg.setColor(warna);
                bg.setCornerRadius(12 * getResources().getDisplayMetrics().density);
                chip.setBackground(bg);
                return v;
            }
        };
        lvTugas.setAdapter(adapter);

        btnTanggal.setOnClickListener(v -> pilihTanggal());

        btnTambah.setOnClickListener(v -> {
            String judul = etJudul.getText().toString().trim();
            String matkul = etMatkul.getText().toString().trim();
            if (judul.isEmpty() || matkul.isEmpty() || tanggalDipilih.isEmpty()) {
                Toast.makeText(this, "Lengkapi judul, mata kuliah, dan deadline", Toast.LENGTH_SHORT).show();
                return;
            }
            db.tambah(judul, matkul, tanggalDipilih);   // data banyak -> database
            etJudul.setText("");
            etMatkul.setText("");
            tanggalDipilih = "";
            btnTanggal.setText("Pilih Deadline");
            muatData();
        });

        btnPengaturan.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        // tekan lama untuk menghapus tugas yang sudah selesai
        lvTugas.setOnItemLongClickListener((parent, view, position, id) -> {
            db.hapus(data.get(position).id);
            muatData();
            Toast.makeText(this, "Tugas dihapus", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    /** Setiap kembali dari layar Pengaturan, pengaturan terbaru dibaca lagi di sini. */
    @Override
    protected void onResume() {
        super.onResume();
        prefs.terapkanTema();
        tvSapaan.setText("Halo, " + prefs.getNama() + "!");
        String urut = prefs.getSort() == PrefsManager.SORT_TERDEKAT ? "deadline terdekat" : "deadline terjauh";
        tvInfo.setText("Pengingat jam " + prefs.getJamFormatted() + " · urut: " + urut);
        muatData();
    }

    private void muatData() {
        data.clear();
        data.addAll(db.semua(prefs.getSort() == PrefsManager.SORT_TERDEKAT));
        adapter.notifyDataSetChanged();
    }

    private void pilihTanggal() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            tanggalDipilih = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
            btnTanggal.setText(tanggalDipilih);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** Selisih hari antara hari ini dan deadline (negatif = sudah lewat). */
    private long sisaHari(String deadline) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(deadline);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            return Math.round((d.getTime() - c.getTimeInMillis()) / 86400000.0);
        } catch (Exception e) {
            return 999;
        }
    }
}
