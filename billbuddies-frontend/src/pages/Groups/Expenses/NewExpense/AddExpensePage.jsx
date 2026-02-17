import { useEffect, useState } from "react";
import {
  Container,
  Snackbar,
  Alert,
  
} from "@mui/material";
import { useNavigate, useParams } from "react-router-dom";
import AddExpenseForm from "./AddExpenseForm";
import { createExpense } from "../../../../api/expenseApi";
import { fetchGroupMembers } from "../../../../api/groupMemberApi";
import "./AddExpense.css";

function AddExpensePage() {
  const { groupId } = useParams();
  const navigate = useNavigate();

  const [members, setMembers] = useState([]);
  const [loadingMembers, setLoadingMembers] = useState(true);
  
  const today = new Date().toLocaleDateString("en-CA");

  const [values, setValues] = useState({
    paidBy: null,
    paidToName: "",
    amount: "",
    expenseDate: today,
    description: "",
  });

  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState(null);

  useEffect(() => {
    loadMembers();
  }, [groupId]);

  const loadMembers = async () => {
    try {
      const data = await fetchGroupMembers(groupId);
      setMembers(data);
    } catch {
      setToast({
        type: "error",
        msg: "Failed to load group members",
      });
    } finally {
      setLoadingMembers(false);
    }
  };

  const onChange = (field, value) => {
    setValues((p) => ({ ...p, [field]: value }));
  };

  const validate = () => {
    const e = {};

    if (!values.paidBy)
      e.paidBy = "Paid By is required";

    if (
      values.amount === "" ||
      isNaN(values.amount) ||
      Number(values.amount) <= 0
    ) {
      e.amount = "Amount must be a positive number";
    }

    if (!values.expenseDate)
      e.expenseDate = "Date is required";

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    if (loading) return;
    if (!validate()) return;

    try {
      setLoading(true);

      await createExpense(groupId, {
        paidByName: values.paidBy.memberName,
        paidToName: values.paidToName,
        amount: Number(values.amount),
        expenseDate: values.expenseDate,
        description: values.description,
      });

      setToast({
        type: "success",
        msg: "Expense added successfully",
      });

      setTimeout(
        () => navigate(`/groups/${groupId}/expense`),
        800
      );
    } catch (err) {
      let message =
        "Something went wrong. Please try again.";

      if (err.response) {
        const status = err.response.status;
        const backendMessage =
          err.response.data?.message;

        if (backendMessage) {
          message = backendMessage;
        } else if (status >= 500) {
          message =
            "Server error occurred. Please try again later.";
        } else if (status >= 400) {
          message =
            "Invalid request. Please check the entered details.";
        }
      } else if (err.request) {
        message =
          "Unable to reach server. Check your internet connection.";
      }

      setToast({ type: "error", msg: message });
    } finally {
      setLoading(false); // 🔒 NEVER STUCK
    }
  };

  if (loadingMembers) {
    return <Container>Loading members...</Container>;
  }

  return (
    <Container>
      <AddExpenseForm
        values={values}
        errors={errors}
        members={members}
        loading={loading}
        onChange={onChange}
        onSubmit={onSubmit}
      />

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

export default AddExpensePage;
