import { useEffect, useState } from "react";
import {
  Box,
  Button,
  CircularProgress,
  Typography,
  Snackbar,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  Divider,
  DialogActions,
} from "@mui/material";
import UploadFileIcon from "@mui/icons-material/UploadFile";
import {
  useNavigate,
  useParams,
  useOutletContext,
} from "react-router-dom";
import {
  fetchGroupExpenses,
  deleteExpense,
  uploadExpensesFile,
} from "../../../api/expenseApi";
import ExpenseTable from "./components/ExpenseTable";

function ExpensesPage() {
  const { groupId } = useParams();
  const { group } = useOutletContext();
  const navigate = useNavigate();

  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  // delete flow
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [undoExpense, setUndoExpense] = useState(null);
  const [snackbarOpen, setSnackbarOpen] = useState(false);

  // upload
  const [uploading, setUploading] = useState(false);

  // toast
  const [toast, setToast] = useState(null);

  useEffect(() => {
    loadExpenses();
  }, [groupId]);

  const loadExpenses = async () => {
    try {
      setLoading(true);
      const data = await fetchGroupExpenses(groupId);
      setExpenses(data);
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  };

  /* ---------------- NAVIGATION ---------------- */

  const openExpense = (expenseId) => {
    navigate(`/groups/${groupId}/expense/${expenseId}`);
  };

  /* ---------------- UPLOAD ---------------- */

  const handleUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    const allowedTypes = [
      "text/csv",
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "application/vnd.ms-excel", // older excel
    ];

    if (!allowedTypes.includes(file.type)) {
      setToast({
        type: "error",
        msg: "Only CSV or Excel files are allowed",
      });
      event.target.value = "";
      return;
    }

    try {
      setUploading(true);

      await uploadExpensesFile(groupId, file);

      setToast({
        type: "success",
        msg: "Expenses uploaded successfully",
      });

      await loadExpenses();
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Failed to upload expenses",
      });
    } finally {
      setUploading(false);
      event.target.value = "";
    }
  };

  /* ---------------- DELETE FLOW ---------------- */

  const requestDelete = (expense) => {
    setDeleteTarget(expense);
  };

  const confirmDelete = () => {
    if (!deleteTarget) return;

    setExpenses((prev) =>
      prev.filter(
        (e) =>
          e.originalExpenseId !==
          deleteTarget.originalExpenseId
      )
    );

    setUndoExpense(deleteTarget);
    setDeleteTarget(null);
    setSnackbarOpen(true);
  };

  const handleUndo = () => {
    if (undoExpense) {
      setExpenses((prev) => [undoExpense, ...prev]);
    }
    setUndoExpense(null);
    setSnackbarOpen(false);
  };

  const handleSnackbarClose = async () => {
    setSnackbarOpen(false);

    if (undoExpense) {
      try {
        await deleteExpense(
          groupId,
          undoExpense.originalExpenseId
        );

        setToast({
          type: "success",
          msg: "Expense deleted successfully",
        });
      } catch (err) {
        await loadExpenses();

        setToast({
          type: "error",
          msg:
            err?.response?.data?.message ||
            "Failed to delete expense",
        });
      } finally {
        setUndoExpense(null);
      }
    }
  };

  /* ---------------- RENDER ---------------- */

  if (loading) return <CircularProgress />;

  if (error)
    return (
      <Typography color="error">
        Failed to load expenses
      </Typography>
    );

  return (
    <Box>
    {/* FOOTER ACTIONS */}
      <Box
        mb={3}
        mt={2}
        display="flex"
        justifyContent="flex-end"
        gap={1}
      >
        {/* Upload CSV / Excel */}
        <Button
          variant="outlined"
          component="label"
          disabled={uploading}
          startIcon={
            uploading ? (
              <CircularProgress size={18} />
            ) : (
              <UploadFileIcon />
            )
          }
        >
          {uploading ? "Uploading..." : "Upload CSV / Excel"}
          <input
            type="file"
            hidden
            accept=".csv,.xlsx"
            onChange={handleUpload}
          />
        </Button>

        {/* Add Expense */}
        <Button
          variant="contained"
          onClick={() =>
            navigate(
              `/groups/${group.groupId}/expense/new`
            )
          }
        >
          Add Expense
        </Button>
      </Box>
      
      {/* <Divider sx={{ mb: 3 }} /> */}

      {expenses.length === 0 ? (
        <Typography>No expenses yet</Typography>
      ) : (
        <ExpenseTable
          expenses={expenses}
          onOpenExpense={openExpense}
          onDeleteExpense={requestDelete}
        />
      )}

      

      {/* CONFIRM DELETE */}
      <Dialog
        open={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
      >
        <DialogTitle>Delete Expense</DialogTitle>
        <DialogContent>
          Are you sure you want to delete this expense?
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteTarget(null)}>
            Cancel
          </Button>
          <Button
            color="error"
            variant="contained"
            onClick={confirmDelete}
          >
            Delete
          </Button>
        </DialogActions>
      </Dialog>

      {/* UNDO SNACKBAR */}
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={5000}
        onClose={handleSnackbarClose}
      >
        <Alert
          severity="info"
          action={
            <Button
              color="inherit"
              size="small"
              onClick={handleUndo}
            >
              UNDO
            </Button>
          }
        >
          Expense deleted
        </Alert>
      </Snackbar>

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

export default ExpensesPage;
