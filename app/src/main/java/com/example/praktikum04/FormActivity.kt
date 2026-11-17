
package com.example.praktikum04

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FormActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    private lateinit var etNim: EditText
    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etJurusan: EditText
    private lateinit var etNomorHp: EditText

    private lateinit var btnSimpan: Button

    private var editId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        db = DatabaseHelper(this)

        // Menghubungkan komponen XML
        etNim = findViewById(R.id.etNim)
        etNama = findViewById(R.id.etNama)
        etAlamat = findViewById(R.id.etAlamat)
        etJurusan = findViewById(R.id.etJurusan)
        etNomorHp = findViewById(R.id.etNomorHp)

        btnSimpan = findViewById(R.id.btnSimpan)

        val tvJudulForm = findViewById<TextView>(R.id.tvJudulForm)
        val btnBatal = findViewById<Button>(R.id.btnBatal)

        // Mengambil ID dari Intent
        val id = intent.getIntExtra(DetailActivity.EXTRA_ID, -1)

        if (id != -1) {
            val mahasiswa = db.getMahasiswaById(id)

            if (mahasiswa != null) {
                editId = id
                isiForm(mahasiswa)
            } else {
                Toast.makeText(
                    this,
                    "Data mahasiswa tidak ditemukan",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return
            }
        }

        // Mengambil asal FormActivity
        val sumber = intent.getStringExtra("sumber")
            ?: if (editId == null) "daftar" else "detail"

        // Mengatur judul dan tombol berdasarkan mode
        if (editId == null) {
            tvJudulForm.text = "Tambah Data - $sumber"
            btnSimpan.text = "Simpan"
        } else {
            tvJudulForm.text = "Ubah Data - $sumber"
            btnSimpan.text = "Perbarui"
        }

        // Tombol Simpan atau Perbarui
        btnSimpan.setOnClickListener {
            simpan()
        }

        // Tombol Batal
        btnBatal.setOnClickListener {
            finish()
        }
    }

    // Mengisi form dengan data mahasiswa lama
    private fun isiForm(mhs: Mahasiswa) {
        etNim.setText(mhs.nim)
        etNama.setText(mhs.nama)
        etAlamat.setText(mhs.alamat)
        etJurusan.setText(mhs.jurusan)
        etNomorHp.setText(mhs.nomorHp)
    }

    // Menyimpan data mahasiswa
    private fun simpan() {

        val nim = etNim.text.toString().trim()
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val jurusan = etJurusan.text.toString().trim()
        val nomorHp = etNomorHp.text.toString().trim()

        if (!validasi(nim, nama, nomorHp)) return

        val mhs = Mahasiswa(
            id = editId ?: 0,
            nim = nim,
            nama = nama,
            alamat = alamat,
            jurusan = jurusan,
            nomorHp = nomorHp
        )

        val berhasil = if (editId == null) {
            db.insertMahasiswa(mhs) != -1L
        } else {
            db.updateMahasiswa(mhs) > 0
        }

        if (berhasil) {
            val aksi = if (editId == null) {
                "ditambahkan"
            } else {
                "diperbarui"
            }

            Toast.makeText(
                this,
                "Data berhasil $aksi",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        } else {
            Toast.makeText(
                this,
                "Gagal menyimpan data",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Validasi input mahasiswa
    private fun validasi(
        nim: String,
        nama: String,
        nomorHp: String
    ): Boolean {

        if (nim.isEmpty()) {
            etNim.error = "NIM wajib diisi"
            etNim.requestFocus()
            return false
        }

        if (nim.length < 5 || !nim.all { it.isDigit() }) {
            etNim.error = "NIM minimal 5 digit angka"
            etNim.requestFocus()
            return false
        }

        if (db.isNimTerdaftar(nim, editId ?: 0)) {
            etNim.error = "NIM sudah terdaftar"
            etNim.requestFocus()
            return false
        }

        if (nama.isEmpty()) {
            etNama.error = "Nama wajib diisi"
            etNama.requestFocus()
            return false
        }

        // Nomor HP wajib diisi
        if (nomorHp.isEmpty()) {
            etNomorHp.error = "Nomor HP wajib diisi"
            etNomorHp.requestFocus()
            return false
        }

        // Memeriksa format nomor HP
        if (!hpValid(nomorHp)) {
            etNomorHp.error = "Nomor HP tidak valid"
            etNomorHp.requestFocus()
            return false
        }

        return true
    }

    // Memeriksa format nomor HP
    private fun hpValid(hp: String): Boolean {
        return hp.startsWith("08") &&
                hp.length in 10..13 &&
                hp.all { it.isDigit() }
    }
}
