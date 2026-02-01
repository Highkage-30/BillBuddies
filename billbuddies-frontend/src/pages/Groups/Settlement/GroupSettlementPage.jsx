import { useEffect, useState } from "react";
import {
  Container,
  Alert,
  Snackbar,
  Button,
  Box,
  CircularProgress,
} from "@mui/material";
import { useParams } from "react-router-dom";
import SettlementTable from "./SettlementTable";
import {
  fetchGroupSettlement,
  addSettlementExpense,
  executeSettlement,
} from "../../../api/settlementApi";
import "./Settlement.css";

function GroupSettlementPage() {
  const { groupId } = useParams();

  const [settlements, setSettlements] = useState([]); // always array
  const [loading, setLoading] = useState(true);
  const [settlingRowIndex, setSettlingRowIndex] = useState(null);
  const [globalLoading, setGlobalLoading] = useState(false);
  const [toast, setToast] = useState(null);

  useEffect(() => {
    loadSettlement();
  }, [groupId]);

  const loadSettlement = async () => {
    try {
      setLoading(true);
      const res = await fetchGroupSettlement(groupId);

      // 🔒 CRITICAL: always extract array
      setSettlements(res.settlements || []);
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Failed to load settlement data",
      });
    } finally {
      setLoading(false);
    }
  };

  // 🔹 Row-level settle
  const handleRowSettle = async (row, index) => {
    try {
      setSettlingRowIndex(index);

      // 1️⃣ Add settlement as expense
      await addSettlementExpense(groupId, {
        paidByName: row.fromMemberName,
        paidToName: row.toMemberName,
        amount: row.amount,
        expenseDate: new Date().toISOString().slice(0, 10),
        description: "Settlement via BillBuddy",
      });

      // 2️⃣ Execute settlement
      await executeSettlement(groupId);

      // 3️⃣ Refresh settlement view
      await loadSettlement();

      setToast({
        type: "success",
        msg: "Settlement completed successfully",
      });
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Settlement failed",
      });
    } finally {
      setSettlingRowIndex(null);
    }
  };

  // 🔹 Global settlement (always available)
  const handleExecuteSettlement = async () => {
    try {
      setGlobalLoading(true);

      await executeSettlement(groupId);
      await loadSettlement();

      setToast({
        type: "success",
        msg: "Settlement recalculated successfully",
      });
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Failed to execute settlement",
      });
    } finally {
      setGlobalLoading(false);
    }
  };

  if (loading) {
    return (
      <Container className="settlement-loading">
        <CircularProgress />
      </Container>
    );
  }

  return (
    <Container className="settlement-container">
      {/* Info banner */}
      <Box className="settlement-info">
        All settlements are routed via{" "}
        <strong>BillBuddy (Central Counter Party)</strong>.
      </Box>

      {/* Settlement content */}
      {settlements.length === 0 ? (
        <Alert severity="info">
          All balances are currently settled.  
          You can execute settlement again after adding new expenses.
        </Alert>
      ) : (
        <SettlementTable
          settlements={settlements}
          settlingRowIndex={settlingRowIndex}
          onRowSettle={handleRowSettle}
        />
      )}

      {/* ✅ ALWAYS VISIBLE */}
      <Box className="settlement-footer">
        <Button
          variant="contained"
          disabled={globalLoading}
          onClick={handleExecuteSettlement}
        >
          {globalLoading
            ? "Executing..."
            : "Execute Settlement"}
        </Button>
      </Box>

      {/* Toasts */}
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
    </Container>
  );
}

export default GroupSettlementPage;
