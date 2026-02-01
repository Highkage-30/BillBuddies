import { useEffect, useState } from "react";
import {
  Box,
  Button,
  CircularProgress,
  Typography,
} from "@mui/material";
import {
  useNavigate,
  useParams,
  useOutletContext,
} from "react-router-dom";
import { fetchGroupExpenses } from "../../../api/expenseApi";
import ExpenseTable from "./components/ExpenseTable";

function ExpensesPage() {
  const { groupId } = useParams();
  const { group } = useOutletContext();
  const navigate = useNavigate();

  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

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

  if (loading) return <CircularProgress />;
  if (error)
    return (
      <Typography color="error">
        Failed to load expenses
      </Typography>
    );

  return (
    <Box>
      {expenses.length === 0 ? (
        <Typography>No expenses yet</Typography>
      ) : (
        <ExpenseTable expenses={expenses} />
      )}

      <Box mt={2} display="flex" justifyContent="flex-end">
        <Button
          variant="contained"
          onClick={() =>
            navigate(`/groups/${group.groupId}/expense/new`)
          }
        >
          Add Expense
        </Button>
      </Box>
    </Box>
  );
}

export default ExpensesPage;
