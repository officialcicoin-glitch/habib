package com.example.cicoinminer // Ganti com.example.cicoinminer sesuai nama paket Anda

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cicoinminer.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var miningTimer: CountDownTimer? = null
    private var mRewardedAd: RewardedAd? = null

    // Adapter untuk riwayat
    private lateinit var transactionAdapter: TransactionAdapter
    private var transactionList = mutableListOf<TransactionHistory>()

    // Konfigurasi
    private val MIN_WITHDRAW: Double = 0.01
    private val BONUS_ADS_REWARD: Double = 0.0002

    // Variabel ini HARUS didapat dari server Anda
    private var currentBalance: Double = 0.00000000
    private var miningEndTime: Long = 0 // Waktu (timestamp) kapan mining selesai

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi AdMob
        MobileAds.initialize(this) {}
        loadRewardedAd()

        // Setup RecyclerView
        setupRecyclerView()
        
        // Setup tombol
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        // Saat aplikasi dibuka/kembali, ambil status terbaru dari server
        fetchUserDataFromServer()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(transactionList)
        binding.rvHistory.adapter = transactionAdapter
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
    }

    private fun setupButtons() {
        binding.btnStartMining.setOnClickListener {
            // --- ⚠️ TODO WAJIB (BACKEND) ---
            // 1. Kirim perintah "start" ke server Anda (misal: /startMining).
            // 2. Server Anda akan mencatat waktu mulai dan merespons.
            // --------------------------------

            // (Simulasi) Anggap server merespons "sukses"
            Toast.makeText(this, "Mining dimulai...", Toast.LENGTH_SHORT).show()
            startMiningTimer(24 * 60 * 60 * 1000) // Mulai timer 24 jam
        }

        binding.btnShowAds.setOnClickListener {
            showRewardedAd()
        }

        binding.btnWithdraw.setOnClickListener {
            handleWithdraw()
        }
    }

    // --- LOGIKA MINING & DATA ---

    private fun fetchUserDataFromServer() {
        // --- ⚠️ TODO WAJIB (BACKEND) ---
        // 1. Buat panggilan API ke server Anda (misal: /getUserData).
        // 2. Server Anda akan mengembalikan data:
        //    a. Saldo (currentBalance) -> Tampilkan angka menghitung per detik seperti permintaan Anda
        //    b. Sisa waktu mining (miningEndTime)
        //    c. DAFTAR 5 TRANSAKSI TERAKHIR (historyList)
        // --------------------------------

        // (Simulasi) Anggap ini data dari server:
        currentBalance = 0.00512345
        miningEndTime = System.currentTimeMillis() + (5 * 60 * 60 * 1000) // Sisa 5 jam
        
        val historyFromServer = listOf(
            TransactionHistory("0x2a3b...", 0.012, "02 Nov 2025, 12:30", "Selesai"),
            TransactionHistory("0x4c5d...", 0.025, "01 Nov 2025, 08:15", "Selesai"),
            TransactionHistory("0x6e7f...", 0.010, "30 Okt 2025, 17:45", "Gagal")
        )

        // Update UI Saldo
        updateBalanceUI()
        
        // Update UI Timer
        val remainingTime = miningEndTime - System.currentTimeMillis()
        if (remainingTime > 0) {
            startMiningTimer(remainingTime)
        } else {
            stopMiningUI()
        }

        // Update UI Riwayat
        transactionList.clear()
        transactionList.addAll(historyFromServer)
        transactionAdapter.notifyDataSetChanged() // Memberi tahu adapter ada data baru

        if (transactionList.isEmpty()) {
            binding.tvHistoryTitle.visibility = View.GONE
            binding.rvHistory.visibility = View.GONE
        } else {
            binding.tvHistoryTitle.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.VISIBLE
        }
    }

    private fun startMiningTimer(duration: Long) {
        miningTimer?.cancel()
        miningTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val h = (millisUntilFinished / 3600000)
                val m = (millisUntilFinished % 3600000) / 60000
                val s = (millisUntilFinished % 60000) / 1000
                binding.tvTimer.text = String.format(Locale.getDefault(), "Sisa waktu: %02d:%02d:%02d", h, m, s)
                
                // Ini adalah simulasi visual "menghitung maju" seperti permintaan Anda
                // Saldo asli TETAP dihitung oleh server.
                currentBalance += 0.00000002 
                updateBalanceUI()
            }
            override fun onFinish() {
                stopMiningUI()
                Toast.makeText(this@MainActivity, "Sesi mining selesai", Toast.LENGTH_SHORT).show()
                // Panggil server untuk update status
                fetchUserDataFromServer() 
            }
        }.start()

        binding.btnStartMining.isEnabled = false
        binding.tvTimer.visibility = View.VISIBLE
    }
    
    private fun stopMiningUI() {
        miningTimer?.cancel()
        binding.btnStartMining.isEnabled = true
        binding.btnStartMining.text = "Start Mining (24 Jam)"
        binding.tvTimer.visibility = View.GONE
    }

    private fun updateBalanceUI() {
        // Format ke 8 angka desimal
        binding.tvBalance.text = String.format(Locale.US, "%.8f Cicoin", currentBalance)
    }


    // --- LOGIKA ADMOB ---

    private fun loadRewardedAd() {
        // GANTI "ca-app-pub-..." dengan Ad Unit ID Anda
        val adUnitId = "ca-app-pub-3940256099942544/5224354917" // Ini ID tes
        var adRequest = AdRequest.Builder().build()

        RewardedAd.load(this, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(rewardedAd: RewardedAd) {
                mRewardedAd = rewardedAd
                setupAdCallbacks()
            }
            override fun onAdFailedToLoad(adError: LoadAdError) { mRewardedAd = null }
        })
    }

    private fun setupAdCallbacks() {
        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() { loadRewardedAd() } // Load iklan baru
            override fun onAdFailedToShowFullScreenContent(adError: AdError) { Log.d("AdMob", "Ad failed to show.") }
        }
    }

    private fun showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd?.show(this) { rewardItem ->
                // Pengguna mendapat reward
                Toast.makeText(this, "Bonus +${BONUS_ADS_REWARD} Cicoin diterima!", Toast.LENGTH_SHORT).show()
                
                // --- ⚠️ TODO WAJIB (BACKEND) ---
                // 1. Kirim perintah ke SERVER Anda (misal: /claimAdReward).
                // 2. Server Anda akan memvalidasi dan menambahkan 0.0002 ke saldo pengguna di database.
                // --------------------------------
                
                // (Simulasi) Update UI sementara
                currentBalance += BONUS_ADS_REWARD
                updateBalanceUI()
            }
        } else {
            Toast.makeText(this, "Iklan belum siap. Coba lagi.", Toast.LENGTH_SHORT).show
            loadRewardedAd() // Coba load lagi
        }
    }


    // --- LOGIKA WITHDRAW (VERSI AMAN) ---

    private fun handleWithdraw() {
        if (currentBalance < MIN_WITHDRAW) {
            Toast.makeText(this, "Saldo minimum untuk withdraw adalah $MIN_WITHDRAW Cicoin", Toast.LENGTH_SHORT).show()
            return
        }

        // Tampilkan loading...
        Toast.makeText(this, "Memproses withdraw...", Toast.LENGTH_SHORT).show()

        // --- ⚠️ TODO WAJIB (BACKEND) ---
        // 1. Buat panggilan API ke SERVER Anda (misal: /requestWithdraw).
        // 2. Server Anda akan:
        //    a. Menerima permintaan.
        //    b. Mengecek saldo pengguna di database.
        //    c. Jika saldo cukup, server Anda-lah yang akan memanggil API Cicoin
        //       (https://explorer.taobot.org/ext/kirimcoin) secara AMAN.
        //    d. Mengurangi saldo pengguna di database.
        //    e. Mengirim respons "Sukses" atau "Gagal" ke aplikasi.
        // --------------------------------
        
        // (Simulasi) Anggap server merespons sukses
        // currentBalance = 0.0 
        // updateBalanceUI()
        // Toast.makeText(this, "Permintaan withdraw berhasil diproses!", Toast.LONG).SHOW
        
        // Refresh data dari server setelah withdraw
        // fetchUserDataFromServer()
    }
}