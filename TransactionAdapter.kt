package com.example.cicoinminer // Ganti com.example.cicoinminer sesuai nama paket Anda

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class TransactionAdapter(
    private val transactionList: List<TransactionHistory>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    // ViewHolder untuk menampung view dari item_transaction.xml
    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootLayout: LinearLayout = view.findViewById(R.id.transactionRoot)
        val tvAmount: TextView = view.findViewById(R.id.tvTransactionAmount)
        val tvStatus: TextView = view.findViewById(R.id.tvTransactionStatus)
        val tvDate: TextView = view.findViewById(R.id.tvTransactionDate)
        val tvTxid: TextView = view.findViewById(R.id.tvTransactionTxid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int = transactionList.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        val context = holder.itemView.context

        // Atur data ke TextViews
        holder.tvAmount.text = String.format(Locale.US, "%.8f Cicoin", transaction.amount)
        holder.tvStatus.text = transaction.status
        holder.tvDate.text = transaction.date
        holder.tvTxid.text = "TxID: ${transaction.txid}"

        // Atur warna status
        when (transaction.status.lowercase()) {
            "selesai" -> holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_completed))
            "pending" -> holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending))
            "gagal" -> holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_failed))
        }

        // --- LOGIKA KLIK UNTUK MEMBUKA EXPLORER ---
        holder.rootLayout.setOnClickListener {
            val url = "https://explorer.taobot.org/tx/${transaction.txid}"
            
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Tidak dapat membuka browser", Toast.LENGTH_SHORT).show()
            }
        }
    }
}