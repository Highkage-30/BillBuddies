import { useState } from "react";
import {
  Box,
  TextField,
  Button,
  Snackbar,
  Alert,
  Typography,
} from "@mui/material";
import { withdrawFromPool } from "../../../api/poolApi";

function PoolWithdrawForm({
  groupId,
  onSuccess,
}) {
  const today =
    new Date().toLocaleDateString("en-CA");

  const [amount, setAmount] = useState("");
  const [paidToName, setPaidToName] =
    useState("");
  const [description, setDescription] =
    useState("");
  const [expenseDate, setExpenseDate] =
    useState(today);

  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState(null);

  const submit = async (e) => {
    e.preventDefault();

    if (!paidToName.trim()) {
      setToast({
        type: "error",
        msg: "Paid To name is required",
      });
      return;
    }

    if (Number(amount) <= 0) {
      setToast({
        type: "error",
        msg: "Amount must be positive",
      });
      return;
    }

    try {
      setLoading(true);

      await withdrawFromPool(groupId, {
        amount: Number(amount),
        paidToName,
        description,
        expenseDate,
      });

      setAmount("");
      setPaidToName("");
      setDescription("");
      setExpenseDate(today);

      onSuccess();

      setToast({
        type: "success",
        msg: "BillBuddy withdrawal successful",
      });
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Withdraw failed",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box component="form" onSubmit={submit}>
      <Typography variant="h6" mb={2}>
        Withdraw from BillBuddy
      </Typography>

      <Box display="flex" gap={2} flexWrap="wrap">
        <TextField
          label="Paid To "
          size="small"
          value={paidToName}
          onChange={(e) =>
            setPaidToName(e.target.value)
          }
        />

        <TextField
          label="Amount"
          size="small"
          type="number"
          value={amount}
          onChange={(e) =>
            setAmount(e.target.value)
          }
        />

        <TextField
          label="Date"
          size="small"
          type="date"
          value={expenseDate}
          onChange={(e) =>
            setExpenseDate(e.target.value)
          }
        />

        <TextField
          label="Description"
          size="small"
          value={description}
          onChange={(e) =>
            setDescription(e.target.value)
          }
        />

        <Button
          type="submit"
          variant="contained"
          color="warning"
          disabled={loading}
        >
          Withdraw
        </Button>
      </Box>

      {toast && (
        <Snackbar
          open
          autoHideDuration={4000}
          onClose={() => setToast(null)}
        >
          <Alert severity={toast.type} variant="filled">
            {toast.msg}
          </Alert>
        </Snackbar>
      )}
    </Box>
  );
}

export default PoolWithdrawForm;
