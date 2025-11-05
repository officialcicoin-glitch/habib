package com.example.cicoinminer // Ganti com.example.cicoinminer sesuai nama paket Anda

// Data class ini mewakili satu item transaksi yang Anda dapatkan dari server
data class TransactionHistory(
    val txid: String,
    val amount: Double,
    val date: String, // Server Anda harus memformat tanggalnya
    val status: String // Misal: "Pending", "Selesai", "Gagal"
)