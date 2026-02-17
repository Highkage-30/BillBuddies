import { useEffect, useState } from "react";
import {
  Container,
  Alert,
  Snackbar,
  Button,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Typography,
  Box,
} from "@mui/material";
import { useParams } from "react-router-dom";
import SettlementTable from "./SettlementTable";
import {
  fetchGroupSettlement,
  executeSettlementRow,
  executeSettlement,
  downloadSettlementReport,
} from "../../../api/settlementApi";
import "./Settlement.css";

function GroupSettlementPage() {
  const { groupId } = useParams();

  const [settlements, setSettlements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState(null);

  const [confirmOpen, setConfirmOpen] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);
  const [groupName, setGroupName] = useState("");
  const [executingGlobal, setExecutingGlobal] = useState(false);
  const [executingRow, setExecutingRow] = useState(false);
  const [downloading, setDownloading] = useState(false);

  useEffect(() => {
    loadSettlement();
  }, [groupId]);

  const loadSettlement = async () => {
    try {
      setLoading(true);
      const res = await fetchGroupSettlement(groupId);
      setSettlements(res.settlements || []);
      setGroupName(res.groupName || "Group");
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

  /* ---------------- DOWNLOAD REPORT ---------------- */

  const handleDownloadReport = async () => {
    try {
      setDownloading(true);

      const response = await downloadSettlementReport(groupId);

      const blob = new Blob([response.data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });

      const url = window.URL.createObjectURL(blob);

      const link = document.createElement("a");
      link.href = url;

      const today = new Date().toISOString().split("T")[0];
      const safeGroupName =
        groupName.replace(/\s+/g, "-");

      const filename =
        `Settlement-${safeGroupName}-${today}.xlsx`;
      link.setAttribute("download", filename);
      document.body.appendChild(link);
      link.click();
      link.remove();

      window.URL.revokeObjectURL(url);

      setToast({
        type: "success",
        msg: "Settlement report downloaded",
      });
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Failed to download report",
      });
    } finally {
      setDownloading(false);
    }
  };

  /* ---------------- ROW LEVEL ---------------- */

  const askConfirmRow = (row) => {
    setSelectedRow(row);
    setConfirmOpen(true);
  };

  const confirmRowSettlement = async () => {
    if (!selectedRow) return;

    const snapshot = [...settlements];

    setSettlements((prev) =>
      prev.filter(
        (r) =>
          !(
            r.fromMemberId === selectedRow.fromMemberId &&
            r.toMemberId === selectedRow.toMemberId
          )
      )
    );

    setConfirmOpen(false);
    setExecutingRow(true);

    try {
      await executeSettlementRow(groupId, {
        fromMemberId: selectedRow.fromMemberId,
        toMemberId: selectedRow.toMemberId,
        amount: selectedRow.amount,
      });

      await loadSettlement();

      setToast({
        type: "success",
        msg: "Settlement completed successfully",
      });
    } catch (err) {
      setSettlements(snapshot);
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Settlement failed",
      });
    } finally {
      setExecutingRow(false);
      setSelectedRow(null);
    }
  };

  /* ---------------- GLOBAL ---------------- */

  const handleExecuteSettlement = async () => {
    try {
      setExecutingGlobal(true);
      await executeSettlement(groupId);
      await loadSettlement();

      setToast({
        type: "success",
        msg: "Settlement executed successfully",
      });
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Failed to execute settlement",
      });
    } finally {
      setExecutingGlobal(false);
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
      {settlements.length === 0 ? (
        <Alert severity="info">
          All balances are settled 🎉
        </Alert>
      ) : (
        <SettlementTable
          settlements={settlements}
          onRowSettle={askConfirmRow}
          disabled={executingRow || executingGlobal}
        />
      )}

      {/* FOOTER BUTTONS */}
      <Box
        className="settlement-footer"
        display="flex"
        justifyContent="space-between"
        mt={3}
      >
        <Button
          variant="outlined"
          onClick={handleDownloadReport}
          disabled={settlements.length === 0 || downloading}
        >
          {downloading
            ? "Downloading..."
            : "Download Settlement Report"}
        </Button>

        <Button
          variant="contained"
          onClick={handleExecuteSettlement}
          disabled={executingGlobal}
        >
          {executingGlobal
            ? "Executing..."
            : "Execute Settlement"}
        </Button>
      </Box>

      {/* CONFIRM MODAL */}
      <Dialog
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
      >
        <DialogTitle>Confirm Settlement</DialogTitle>
        <DialogContent>
          <Typography>
            <strong>{selectedRow?.fromMemberName}</strong>{" "}
            will pay{" "}
            <strong>{selectedRow?.toMemberName}</strong>
          </Typography>
          <Typography mt={1}>
            Amount: ₹{selectedRow?.amount}
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmOpen(false)}>
            Cancel
          </Button>
          <Button
            variant="contained"
            color="success"
            onClick={confirmRowSettlement}
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
    </Container>
  );
}

export default GroupSettlementPage;
