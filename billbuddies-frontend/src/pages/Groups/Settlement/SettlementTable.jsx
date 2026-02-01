import {
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Button,
  CircularProgress,
} from "@mui/material";

function SettlementTable({
  settlements,
  settlingRowIndex,
  onRowSettle,
}) {
  return (
    <Table className="settlement-table">
      <TableHead>
        <TableRow className="settlement-header-row">
          <TableCell className="settlement-header-cell">
            From
          </TableCell>
          <TableCell className="settlement-header-cell">
            To
          </TableCell>
          <TableCell className="settlement-header-cell">
            Amount
          </TableCell>
          <TableCell className="settlement-header-cell">
            Action
          </TableCell>
        </TableRow>
      </TableHead>

      <TableBody>
        {settlements.map((row, index) => (
          <TableRow
            key={`${row.fromMemberName}-${row.toMemberName}`}
            className="settlement-body-row"
          >
            <TableCell className="settlement-cell">
              {row.fromMemberName}
            </TableCell>

            <TableCell className="settlement-cell">
              {row.toMemberName}
            </TableCell>

            <TableCell className="settlement-cell">
              ₹{row.amount}
            </TableCell>

            <TableCell className="settlement-cell">
              <Button
                variant="contained"
                size="small"
                disabled={settlingRowIndex === index}
                onClick={() => onRowSettle(row, index)}
              >
                {settlingRowIndex === index ? (
                  <CircularProgress size={18} />
                ) : (
                  "Settle"
                )}
              </Button>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}

export default SettlementTable;
