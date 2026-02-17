import {
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
} from "@mui/material";

function PoolTransactionTable({
  transactions,
}) {
  if (!transactions?.length) {
    return <div>No pool transactions</div>;
  }

  return (
    <Table>
      <TableHead>
        <TableRow>
          <TableCell>Date</TableCell>
          <TableCell>Member</TableCell>
          <TableCell>Type</TableCell>
          <TableCell>Amount</TableCell>
        </TableRow>
      </TableHead>

      <TableBody>
        {transactions.map((tx) => (
          <TableRow key={tx.transactionId}>
            <TableCell>
              {tx.expenseDate}
            </TableCell>

            <TableCell>
              {tx.memberName || "-"}
            </TableCell>

            <TableCell>
              {tx.type}
            </TableCell>

            <TableCell>
              {tx.type === "DEPOSIT" ? (
                <span
                  style={{ color: "green" }}
                >
                  + ₹{tx.amount}
                </span>
              ) : (
                <span
                  style={{ color: "red" }}
                >
                  - ₹{tx.amount}
                </span>
              )}
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}

export default PoolTransactionTable;
