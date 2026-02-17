import {
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
} from "@mui/material";
import ExpenseRow from "./ExpenseRow";

function ExpenseTable({
  expenses,
  onOpenExpense,
  onDeleteExpense,
}) {
  return (
    <Table
      sx={{
        border: "1px solid #bdbdbd",
        borderCollapse: "collapse",
      }}
    >
      <TableHead>
        <TableRow
          sx={{
            backgroundColor: "#f5f5f5",
          }}
        >
          <TableCell sx={headerCellStyle}>
            Description
          </TableCell>
          <TableCell sx={headerCellStyle}>
            Paid By
          </TableCell>
          <TableCell sx={headerCellStyle}>
            Paid To
          </TableCell>
          <TableCell sx={headerCellStyle}>
            Amount
          </TableCell>
          <TableCell sx={headerCellStyle}>
            Date
          </TableCell>
          <TableCell sx={headerCellStyle}>
            Action
          </TableCell>
        </TableRow>
      </TableHead>

      <TableBody>
        {expenses.map((expense) => (
          <ExpenseRow
            key={expense.originalExpenseId}
            expense={expense}
            onOpen={onOpenExpense}
            onDelete={onDeleteExpense}
          />
        ))}
      </TableBody>
    </Table>
  );
}

const headerCellStyle = {
  fontWeight: 600,
  color: "#212121",
  borderRight: "1px solid #bdbdbd",
};

export default ExpenseTable;
