import {
  TableRow,
  TableCell,
  IconButton,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";

function ExpenseRow({
  expense,
  onOpen,
  onDelete,
}) {
  return (
    <TableRow>
      {/* 🔹 CLICKABLE DESCRIPTION */}
      <TableCell
        sx={{
          ...cellStyle,
          cursor: "pointer",
          color: "#1976d2",
          textDecoration: "underline",
        }}
        onClick={() =>
          onOpen(expense.originalExpenseId)
        }
      >
        {expense.description || "-"}
      </TableCell>

      <TableCell sx={cellStyle}>
        {expense.paidByName || "-"}
      </TableCell>

      <TableCell sx={cellStyle}>
        {expense.paidToName || "-"}
      </TableCell>

      <TableCell sx={cellStyle}>
        ₹ {expense.amount}
      </TableCell>

      <TableCell sx={cellStyle}>
        {expense.expenseDate}
      </TableCell>

      {/* 🗑 DELETE ACTION */}
      <TableCell sx={cellStyle}>
        <IconButton
          size="small"
          color="error"
          onClick={(e) => {
            e.stopPropagation(); // 🚫 prevent row click
            onDelete(expense);   // pass full expense
          }}
        >
          <DeleteIcon fontSize="small" />
        </IconButton>
      </TableCell>
    </TableRow>
  );
}

const cellStyle = {
  borderRight: "1px solid #e0e0e0",
  borderBottom: "1px solid #e0e0e0",
};

export default ExpenseRow;
