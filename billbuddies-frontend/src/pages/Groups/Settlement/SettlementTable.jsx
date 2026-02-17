import {
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Button,
} from "@mui/material";

function SettlementTable({
  settlements,
  onRowSettle,
  disabled,
}) {
  return (
    <Table className="settlement-table">
      <TableHead>
        <TableRow>
          <TableCell>From</TableCell>
          <TableCell>To</TableCell>
          <TableCell>Amount</TableCell>
          <TableCell>Action</TableCell>
        </TableRow>
      </TableHead>

      <TableBody>
        {settlements.map((row) => (
          <TableRow
            key={`${row.fromMemberId}-${row.toMemberId}`}
          >
            <TableCell>{row.fromMemberName}</TableCell>
            <TableCell>{row.toMemberName}</TableCell>
            <TableCell>₹{row.amount}</TableCell>
            <TableCell>
              <Button
                variant="contained"
                size="small"
                disabled={disabled}
                onClick={() => onRowSettle(row)}
              >
                Settle
              </Button>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}

export default SettlementTable;
