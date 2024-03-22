package com.example.corinlicense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<FinancialTransaction> transactionList;

    public TransactionAdapter(List<FinancialTransaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FinancialTransaction transaction = transactionList.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        String dateStr = dateFormat.format(transaction.getDate());
        holder.textViewTransactionDate.setText(dateStr);
        holder.textViewTransactionDate.setText(dateStr);
        holder.textViewBalanceCount.setText(transaction.getBalanceCount());
        holder.textViewSpentTodayCount.setText(transaction.getSpentTodayCount());
        holder.textViewSavingsCount.setText(transaction.getSavingsCount());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTransactionDate;
        public TextView textViewBalanceCount;
        public TextView textViewSpentTodayCount;
        public TextView textViewSavingsCount;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTransactionDate = itemView.findViewById(R.id.textViewTransactionDate);
            textViewBalanceCount = itemView.findViewById(R.id.textViewBalanceCount);
            textViewSpentTodayCount = itemView.findViewById(R.id.textViewSpentTodayCount);
            textViewSavingsCount = itemView.findViewById(R.id.textViewSavingsCount);
        }
    }
}
