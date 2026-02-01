import { TableRow, TableCell } from "@mui/material";

function ExpenseRow({ expense }) {
  return (
    <TableRow>
      <TableCell sx={cellStyle}>
        {expense.description || "-"}
      </TableCell>
      <TableCell sx={cellStyle}>
        {expense.paidBy}
      </TableCell>
      <TableCell sx={cellStyle}>
        {expense.paidTo}
      </TableCell>
      <TableCell sx={cellStyle}>
        ₹ {expense.amount}
      </TableCell>
      <TableCell sx={cellStyle}>
        {expense.expenseDate}
      </TableCell>
    </TableRow>
  );
}

const cellStyle = {
  borderRight: "1px solid #e0e0e0",
  borderBottom: "1px solid #e0e0e0",
};

export default ExpenseRow;
