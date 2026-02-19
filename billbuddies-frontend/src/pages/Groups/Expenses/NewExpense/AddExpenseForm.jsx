import {
  TextField,
  Button,
  Autocomplete,
} from "@mui/material";

function AddExpenseForm({
  values,
  errors,
  members,
  loading,
  onChange,
  onSubmit,
}) {
  return (
    <form className="add-expense-panel" onSubmit={onSubmit}>
      <div className="add-expense-header">
        <h2>Add Expense</h2>
      </div>

      {/* DESCRIPTION */}
      <div className="add-expense-row">
        <label>Description</label>
        <TextField
          size="small"
          fullWidth
          value={values.description}
          onChange={(e) =>
            onChange("description", e.target.value)
          }
        />
      </div>

      {/* PAID BY (SEARCHABLE) */}
      <div className="add-expense-row">
        <label>Paid By</label>
        <Autocomplete
          options={members}
          getOptionLabel={(option) => option.memberName}
          value={values.paidBy}
          onChange={(_, value) =>
            onChange("paidBy", value)
          }
          renderInput={(params) => (
            <TextField
              {...params}
              size="small"
              placeholder="Select member"
              error={!!errors.paidBy}
              helperText={errors.paidBy}
            />
          )}
        />
      </div>

      {/* PAID TO */}
      <div className="add-expense-row">
        <label>Paid To</label>
        <TextField
          size="small"
          fullWidth
          value={values.paidToName}
          onChange={(e) =>
            onChange("paidToName", e.target.value)
          }
        />
      </div>

      {/* AMOUNT */}
      <div className="add-expense-row">
        <label>Amount</label>
        <TextField
          size="small"
          type="number"
          fullWidth
          value={values.amount}
          onChange={(e) =>
            onChange("amount", e.target.value)
          }
          error={!!errors.amount}
          helperText={errors.amount}
          inputProps={{ min: 0, step: "any" }}
        />
      </div>

      {/* DATE */}
      <div className="add-expense-row">
        <label>Date</label>
        <TextField
          size="small"
          type="date"
          fullWidth
          value={values.expenseDate}
          onChange={(e) =>
            onChange("expenseDate", e.target.value)
          }
          error={!!errors.expenseDate}
          helperText={errors.expenseDate}
        />
      </div>

      <div className="add-expense-footer">
        <Button
          type="submit"
          variant="contained"
          disabled={loading}
        >
          {loading ? "Saving..." : "Add Expense"}
        </Button>
      </div>
    </form>
  );
}

export default AddExpenseForm;
