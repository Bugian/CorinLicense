package com.example.corinlicense;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history); // Folosiți layout-ul pentru activitatea TransactionHistory

        recyclerView = findViewById(R.id.transactionHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this); // Inițializați dbHelper
        loadTransactions();
    }

    private void loadTransactions() {
        new AsyncTask<Void, Void, List<FinancialTransaction>>() {
            @Override
            protected List<FinancialTransaction> doInBackground(Void... voids) {
                // Perform database operation in background
                return dbHelper.getLastFiveTransactions();
            }

            @Override
            protected void onPostExecute(List<FinancialTransaction> transactions) {
                // Update UI on main thread
                TransactionAdapter adapter = new TransactionAdapter(transactions);
                recyclerView.setAdapter(adapter);
            }
        }.execute();
    }
}
