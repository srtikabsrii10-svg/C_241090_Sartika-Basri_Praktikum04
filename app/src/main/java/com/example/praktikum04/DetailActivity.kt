package com.example.praktikum04

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var mahasiswaId = -1

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        db = DatabaseHelper(this)

        mahasiswaId = intent.getIntExtra(EXTRA_ID, -1)

        if (mahasiswaId == -1) {
            finish()
            return
        }

        tampilData()

        findViewById<Button>(R.id.btnUbah)
            .setOnClickListener {
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra(EXTRA_ID, mahasiswaId)
                intent.putExtra("sumber", "detail")
                startActivity(intent)
            }

        findViewById<Button>(R.id.btnHapus)
            .setOnClickListener {
                konfirmasiHapus()
            }

        findViewById<Button>(R.id.btnKembali)
            .setOnClickListener {
                finish()
            }
    }

    private fun tampilData() {
        val mhs = db.getMahasiswaById(mahasiswaId)

        if (mhs == null) {
            finish()
            return
        }

        findViewById<TextView>(R.id.tvNim).text = mhs.nim
        findViewById<TextView>(R.id.tvNama).text = mhs.nama
        findViewById<TextView>(R.id.tvAlamat).text = mhs.alamat
        findViewById<TextView>(R.id.tvJurusan).text = mhs.jurusan
        findViewById<TextView>(R.id.tvNomorHp).text = mhs.nomorHp
    }

    override fun onResume() {
        super.onResume()

        if (::db.isInitialized && mahasiswaId != -1) {
            tampilData()
        }
    }

    private fun konfirmasiHapus() {
        val mhs = db.getMahasiswaById(mahasiswaId) ?: return

        AlertDialog.Builder(this)
            .setTitle("Hapus data")
            .setMessage("Hapus ${mhs.nama} (${mhs.nim})?")
            .setPositiveButton("Hapus") { _, _ ->

                val hasil = db.deleteMahasiswa(mahasiswaId)

                if (hasil > 0) {
                    Toast.makeText(
                        this,
                        "Data berhasil dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Gagal menghapus data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
