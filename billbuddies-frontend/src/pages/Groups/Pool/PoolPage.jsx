import { useEffect, useState } from "react";
import {
  Box,
  Typography,
  CircularProgress,
  Snackbar,
  Alert,
  Divider,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from "@mui/material";
import { useParams } from "react-router-dom";
import {
  fetchGroupPool,
  fetchPoolTransactions,
  settlePool,
} from "../../../api/poolApi";
import { fetchGroupMembers } from "../../../api/groupMemberApi";
import PoolDepositForm from "./PoolDepositForm";
import PoolWithdrawForm from "./PoolWithdrawForm";
import PoolTransactionTable from "./PoolTransactionTable";

function PoolPage() {
  const { groupId } = useParams();

  const [pool, setPool] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [executing, setExecuting] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [toast, setToast] = useState(null);

  const loadData = async () => {
    try {
      setLoading(true);

      const poolData = await fetchGroupPool(groupId);
      const txnData = await fetchPoolTransactions(groupId);
      const membersData = await fetchGroupMembers(groupId);

      setPool(poolData);
      setTransactions(txnData.transactions || []);
      setMembers(membersData);
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Failed to load pool data",
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [groupId]);

  const handleSettle = async () => {
    try {
      setExecuting(true);
      await settlePool(groupId);
      await loadData();

      setToast({
        type: "success",
        msg: "BillBuddy balance distributed successfully",
      });
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Settlement failed",
      });
    } finally {
      setExecuting(false);
      setConfirmOpen(false);
    }
  };

  if (loading) return <CircularProgress />;

  return (
    <Box>
      {/* BALANCE + DISTRIBUTE BUTTON */}
      <Box
        mb={3}
        display="flex"
        justifyContent="space-between"
        alignItems="center"
      >
        <Typography variant="h5">
          BillBuddy Balance: ₹{pool?.balance || 0}
        </Typography>

        {pool?.balance > 0 && (
          <Button
            variant="contained"
            color="success"
            disabled={executing}
            onClick={() => setConfirmOpen(true)}
          >
            {executing
              ? "Distributing..."
              : "Distribute Balance"}
          </Button>
        )}
      </Box>

      <Divider sx={{ mb: 3 }} />

      {/* DEPOSIT */}
      <PoolDepositForm
        groupId={groupId}
        members={members}
        onSuccess={loadData}
      />

      <Divider sx={{ my: 3 }} />

      {/* WITHDRAW */}
      <PoolWithdrawForm
        groupId={groupId}
        onSuccess={loadData}
      />

      <Divider sx={{ my: 3 }} />

      {/* TRANSACTIONS */}
      <PoolTransactionTable
        transactions={transactions}
      />

      {/* CONFIRM DIALOG */}
      <Dialog
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
      >
        <DialogTitle>
          Confirm BillBuddy Settlement
        </DialogTitle>
        <DialogContent>
          Are you sure you want to distribute
          remaining BillBuddy balance to members?
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => setConfirmOpen(false)}
          >
            Cancel
          </Button>
          <Button
            variant="contained"
            color="success"
            onClick={handleSettle}
          >
            Confirm
          </Button>
        </DialogActions>
      </Dialog>

      {/* TOAST */}
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

export default PoolPage;
