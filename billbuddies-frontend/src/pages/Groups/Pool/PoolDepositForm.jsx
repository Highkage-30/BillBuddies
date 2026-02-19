import { useState } from "react";
import {
  Box,
  TextField,
  Button,
  Autocomplete,
  Typography,
} from "@mui/material";
import { depositToPool } from "../../../api/poolApi";

function PoolDepositForm({
  groupId,
  members,
  onSuccess,
}) {
    const today =
    new Date().toLocaleDateString("en-CA");

  const [selectedMember, setSelectedMember] = useState(null);
  const [amount, setAmount] = useState("");
  const [expenseDate, setExpenseDate] = useState(today);
  const [description, setDescription] = useState("");

  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState(null);

  const submit = async (e) => {
    e.preventDefault();

    if (!selectedMember || amount <= 0) return;

    try {
      setLoading(true);

      await depositToPool(groupId, {
        memberName: selectedMember.memberName,
        amount: Number(amount),
        description,
        expenseDate,
      });

      setSelectedMember(null);
      setAmount("");
      setDescription("");
      onSuccess();

      setToast({
        type: "success",
        msg: "Deposit successful",
      });
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Deposit failed",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box component="form" onSubmit={submit}>
      <Typography variant="h6" mb={2}>
        Deposit to BillBuddy
      </Typography>

      <Box display="flex" gap={2} flexWrap="wrap">
        <Autocomplete
          options={members}
          getOptionLabel={(option) => option.memberName}
          value={selectedMember}
          onChange={(_, value) => setSelectedMember(value)}
          sx={{ width: 200 }}
          renderInput={(params) => (
            <TextField {...params} label="Member" size="small" />
          )}
        />

        <TextField
          label="Amount"
          size="small"
          type="number"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />

        <TextField
          label="Date"
          size="small"
          type="date"
          value={expenseDate}
          onChange={(e) => setExpenseDate(e.target.value)}
        />

        <TextField
          label="Description"
          size="small"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />

        <Button
          type="submit"
          variant="contained"
          disabled={loading}
        >
          Deposit
        </Button>
      </Box>
    </Box>
  );
}

export default PoolDepositForm;
