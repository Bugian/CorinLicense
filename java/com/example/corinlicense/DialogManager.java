package com.example.corinlicense;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.math.BigDecimal;
import java.util.function.Consumer;


public class DialogManager {

    private final Context context;

    public DialogManager(Context context) {
        this.context = context;
    }

    public interface SavingsDialogListener {
        void onDialogResult(boolean success, String message);
    }

    public void showSavingsDialog(final FinancialManager financialManager, final SavingsDialogListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Action Savings")
                .setItems(new CharSequence[]{"Add to Savings", "Withdraw from Savings", "Cancel"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // Add to Savings
                            promptForAmount("Enter amount to add to Savings", amount -> {
                                if (financialManager.addToSavings(amount)) {
                                    listener.onDialogResult(true, "");
                                } else {
                                    listener.onDialogResult(false, "Insufficient funds in Balance!");
                                }
                            });
                            break;
                        case 1: // Withdraw from Savings
                            promptForAmount("Enter amount to withdraw from Savings", amount -> {
                                if (financialManager.withdrawFromSavings(amount)) {
                                    listener.onDialogResult(true, "");
                                } else {
                                    listener.onDialogResult(false, "Insufficient funds in Savings!");
                                }
                            });
                            break;
                        case 2: // Cancel
                            dialog.dismiss();
                            break;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showWithdrawDialog(final FinancialManager financialManager, Consumer<BigDecimal> withdrawAction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Withdraw Funds")
                .setMessage("Enter the amount to withdraw")
                .setPositiveButton("Withdraw", (dialog, which) -> {
                    // Prompt pentru introducerea sumei de retras
                    promptForAmount("Enter amount to withdraw", amount -> {
                        withdrawAction.accept(amount);
                        boolean success = financialManager.withdrawFromBalance(amount);
                        if (success) {
                            Toast.makeText(context, "Withdraw successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Insufficient funds!", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showAddDialog(final FinancialManager financialManager, Consumer<BigDecimal> addAction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Funds")
                .setMessage("Enter the amount to add")
                .setPositiveButton("Add", (dialog, which) -> {
                    // Prompt pentru introducerea sumei de adaugat
                    promptForAmount("Enter amount to Add", amount -> {
                        addAction.accept(amount);
                       financialManager.addToBalance(amount);
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void promptForAmount(String title, Consumer<BigDecimal> onAmountEntered) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
            try {
                BigDecimal amount = new BigDecimal(input.getText().toString());
                    onAmountEntered.accept(amount);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "@string/enter_valid_amount", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
